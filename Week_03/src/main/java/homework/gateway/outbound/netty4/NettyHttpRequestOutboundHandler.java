package homework.gateway.outbound.netty4;

import homework.gateway.filter.HttpRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyHttpRequestOutboundHandler extends SimpleChannelInboundHandler<Object> {
    private HttpRequestFilter requestFilter;
    Logger logger = LoggerFactory.getLogger(NettyHttpRequestOutboundHandler.class);

    /**
     * Calls {@link ChannelHandlerContext#fireChannelActive()} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info(ctx.channel().id().asShortText());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) o;
        String trueUrl = "http://localhost:8808" + fullHttpRequest.uri();
        logger.info("gateway前置client request处理,true url:{}", trueUrl);
        fullHttpRequest.setUri(trueUrl);
        requestFilter.filter(fullHttpRequest, ctx);
        logger.info("channel id:{}",ctx.channel().id().asShortText());
        ctx.channel().writeAndFlush(fullHttpRequest);
    }

    public NettyHttpRequestOutboundHandler(HttpRequestFilter requestFilter) {
        this.requestFilter = requestFilter;
    }
}