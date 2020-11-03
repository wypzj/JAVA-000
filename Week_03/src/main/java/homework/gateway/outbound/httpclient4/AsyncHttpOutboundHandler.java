package homework.gateway.outbound.httpclient4;

import homework.gateway.filter.HttpRequestFilter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.GATEWAY_TIMEOUT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.apache.http.HttpHeaders.CONTENT_LENGTH;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

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
    private HttpRequestFilter httpRequestFilter;

    public AsyncHttpOutboundHandler(String backendUrl) {
        //TODO backendUrl不应该只有一个
        this.backendUrl = backendUrl;
        //TODO httpClient暂不调优
        this.httpClient = HttpClientBuilder.create().build();
        this.httpRequestFilter = (fullRequest, ctx) -> {
            HttpHeaders headers = fullRequest.headers();
            headers.add("nio", "jim");
        };
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
                this.httpRequestFilter.filter(fullRequest, ctx);
                CloseableHttpResponse httpResponse = null;
                try {
                    HttpHeaders headers = fullRequest.headers();
                    Iterator<Map.Entry<String, String>> entryIterator = headers.iteratorAsString();
                    if (entryIterator.hasNext()) {
                        httpGet.setHeader(entryIterator.next().getKey(), entryIterator.next().getValue());
                    }
                    httpResponse = httpClient.execute(httpGet);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //处理response
                try {
                    handleResponse(fullRequest, ctx, httpResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        } else {
            logger.error("本网关只支持get请求");
        }
    }

    private void handleResponse(FullHttpRequest fullRequest, ChannelHandlerContext ctx, CloseableHttpResponse httpResponse) throws IOException {
        logger.info("response 处理");
        FullHttpResponse response = null;
        try {
            if (httpResponse == null) {
                logger.info("response为null");
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, GATEWAY_TIMEOUT, Unpooled.wrappedBuffer(new byte[0]));
            } else {
                byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
                logger.info("bytes size:{}", bytes.length);
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, Unpooled.wrappedBuffer(bytes));
                response.headers().set(CONTENT_TYPE, httpResponse.getFirstHeader(CONTENT_TYPE));
                response.headers().setInt(CONTENT_LENGTH, Integer.parseInt(httpResponse.getFirstHeader(CONTENT_LENGTH).getValue()));
            }

        } finally {
            if (httpResponse != null) {
                try {
                    logger.info("close httpResponse");
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ctx.writeAndFlush(response);
        }
    }
}
