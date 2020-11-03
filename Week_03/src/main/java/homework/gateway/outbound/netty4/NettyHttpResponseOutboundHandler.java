package homework.gateway.outbound.netty4;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpResponseStatus.GATEWAY_TIMEOUT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.apache.http.HttpHeaders.CONTENT_LENGTH;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public class NettyHttpResponseOutboundHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    Logger logger = LoggerFactory.getLogger(NettyHttpResponseOutboundHandler.class);
    /**
     * Is called for each message of type {@link FullHttpResponse}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param fullHttpResponse the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse fullHttpResponse) throws Exception {
        logger.info("后端服务响应处理");
        //TODO 添加后端服务response的过滤器
        ctx.writeAndFlush(fullHttpResponse);
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
            ctx.channel().writeAndFlush(response);
        }
    }
}