package homework.reactor;

/**
 * @author wyp
 * @version 1.0
 * @description 负责启动reactor服务并启动相关服务接收请求
 * @date in 19:39 04/11/2020
 * @since 1.0
 */
public class Server {
    Selector selector = new Selector();
    Dispatcher eventLooper = new Dispatcher(selector);
    Acceptor acceptor;

    Server(int port) {
        acceptor = new Acceptor(selector, port);
    }

    public void start() {
        eventLooper.registEventHandler(EventType.ACCEPT, new AcceptEventHandler(selector));
        new Thread(acceptor, "Acceptor-" + acceptor.getPort()).start();
        eventLooper.handleEvents();
    }
}
