package homework.gateway.outbound.httpclient4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wyp
 * @version 1.0
 * @description description
 * @date in 19:16 02/11/2020
 * @since 1.0
 */
public abstract class AbstractHttpOutboundHandler {
    //固定大小线程池
    ExecutorService executorService = Executors.newFixedThreadPool(20);

    public abstract void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx);
}
