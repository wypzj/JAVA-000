package homework.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wyp
 * @version 1.0
 * @description description
 * @date in 17:01 03/11/2020
 * @since 1.0
 */
public class HttpRequestHeaderFilter implements HttpRequestFilter {
    Logger logger = LoggerFactory.getLogger(HttpRequestHeaderFilter.class);

    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        logger.info("HttpRequestHeaderFilter过滤器");
        HttpHeaders headers = fullRequest.headers();
        headers.add("form", "gateway server");
    }
}
