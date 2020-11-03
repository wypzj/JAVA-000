package homework.gateway.outbound.netty4;

import homework.gateway.filter.HttpRequestHeaderFilter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;

public class NettyHttpClient {
    public void connect(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler())
                .attr(AttributeKey.newInstance("client-name"), "client1")
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        HttpRequestHeaderFilter httpRequestHeaderFilter = new HttpRequestHeaderFilter();
                        ChannelPipeline pipeline = ch.pipeline();
                        //gateway 前置client 服务请求处理
                        pipeline.addLast(new NettyHttpRequestOutboundHandler(httpRequestHeaderFilter));
                        //后端服务响应处理
                        pipeline.addLast(new NettyHttpResponseOutboundHandler());
                        //请求编码
                        pipeline.addLast(new HttpRequestEncoder());
                        //响应解码
                        pipeline.addLast(new HttpResponseDecoder());
                    }
                }).bind("127.0.0.1", 8844);
        ChannelFuture sync = bootstrap.connect(host, port).sync();

    }

    public static void main(String[] args) throws Exception {
        NettyHttpClient client = new NettyHttpClient();
        client.connect("127.0.0.1", 8888);
    }
}