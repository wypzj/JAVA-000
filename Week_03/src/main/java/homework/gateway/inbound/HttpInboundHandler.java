package homework.gateway.inbound;

import homework.gateway.outbound.httpclient4.AbstractHttpOutboundHandler;
import homework.gateway.outbound.httpclient4.AsyncHttpOutboundHandler;
import homework.gateway.outbound.httpclient4.HttpOutboundHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.GATEWAY_TIMEOUT;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
    private final String proxyServer;
    private AbstractHttpOutboundHandler handler;
    private Map<String, Channel> channelMap = new HashMap<>(2);

    public HttpInboundHandler(String proxyServer) {
        this.proxyServer = proxyServer;
        //handler = new AsyncHttpOutboundHandler(this.proxyServer);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel id:{}", ctx.channel().id().asShortText());
        channelMap.put(ctx.channel().id().asShortText(), ctx.channel());
        //ctx.fireChannelActive();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            //logger.info("channelRead流量接口请求开始，时间为{}", startTime);
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
//            String uri = fullRequest.uri();
//            //logger.info("接收到的请求url为{}", uri);
//            if (uri.contains("/test")) {
//                handlerTest(fullRequest, ctx);
//            }
            String uri = this.proxyServer + fullRequest.uri();
            logger.info("true url为{}", uri);
            fullRequest.setUri(uri);
            for (String key :
                    channelMap.keySet()) {
                channelMap.get(key).writeAndFlush(fullRequest);
            }
            //ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, GATEWAY_TIMEOUT, Unpooled.wrappedBuffer(new byte[0])));
            //handler.handle(fullRequest, ctx);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void handlerTest(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
//        FullHttpResponse response = null;
//        try {
//            String value = "hello,kimmking";
//            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(value.getBytes("UTF-8")));
//            response.headers().set("Content-Type", "application/json");
//            response.headers().setInt("Content-Length", response.content().readableBytes());
//
//        } catch (Exception e) {
//            logger.error("处理测试接口出错", e);
//            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
//        } finally {
//            if (fullRequest != null) {
//                if (!HttpUtil.isKeepAlive(fullRequest)) {
//                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
//                } else {
//                    response.headers().set(CONNECTION, KEEP_ALIVE);
//                    ctx.write(response);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        cause.printStackTrace();
//        ctx.close();
//    }

}
