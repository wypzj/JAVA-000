package homework.gateway.outbound.httpclient4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author wyp
 * @version 1.0
 * @description 异步HttpHandler
 * @date in 19:14 02/11/2020
 * @since 1.0
 */
public class AsyncHttpOutboundHandler extends AbstractHttpOutboundHandler {
    Logger logger = LoggerFactory.getLogger(AsyncHttpOutboundHandler.class);
    private String backendUrl;
    private CloseableHttpClient httpClient;

    public AsyncHttpOutboundHandler(String backendUrl) {
        //TODO backendUrl不应该只有一个
        this.backendUrl = backendUrl;
        //TODO httpClient暂不调优
        this.httpClient = HttpClientBuilder.create().build();
    }

    @Override
    public void handle(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        //后台服务地址
        String trueUrl = this.backendUrl + fullRequest.uri();
        if (HttpMethod.GET.name().equals(fullRequest.method().name())) {
            HttpGet httpGet = new HttpGet(trueUrl);
            //线程池发送request
            executorService.execute(() -> {
                logger.info("网关AsyncHttpOutboundHandler，url：{}", trueUrl);
                HttpResponse httpResponse = null;
                try {
                    httpResponse = httpClient.execute(httpGet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //处理response
                handleResponse(fullRequest, ctx, httpResponse);
            });
        } else {
            logger.error("本网关只支持get请求");
        }
    }

    private void handleResponse(FullHttpRequest fullRequest, ChannelHandlerContext ctx, HttpResponse httpResponse) {
        FullHttpResponse response = null;
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        try {
            if (httpResponse == null) {
                response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND, ByteBufAllocator.DEFAULT.buffer());
                response.headers().set("Content-Type", "application/json");
            } else {
                InputStream content = httpResponse.getEntity().getContent();
                byte[] bytes = new byte[content.available()];
                if (content.read(bytes) != 0){
                    logger.info("uuuuu:{}", new String(bytes));
                }
                //TODO 这里available获取到的不一定是完整的流的大小，需要优化
                buffer.setBytes(0,bytes);
                //logger.info("content:{}", buffer.readBytes(buffer.readableBytes()));
                response = new DefaultFullHttpResponse(HTTP_1_1, OK, buffer);
                response.headers().set("Content-Type", "application/json");
                response.headers().set("Content-Length", Integer.parseInt(httpResponse.getFirstHeader("Content-Length").getValue()));
                if (content != null){
                    content.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //异常处理
            ctx.close();
        } finally {
            ctx.writeAndFlush(response);
        }
    }
}
