package homework.reactor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wyp
 * @version 1.0
 * @description reactor的事件接收类，负责初始化selector和接收缓冲队列
 * @date in 19:39 04/11/2020
 * @since 1.0
 */
public class Acceptor implements Runnable {
    private int port; // server socket port
    private Selector selector;

    // 代表 serversocket，通过LinkedBlockingQueue来模拟外部输入请求队列
    private BlockingQueue<InputSource> sourceQueue = new LinkedBlockingQueue<InputSource>();

    Acceptor(Selector selector, int port) {
        this.selector = selector;
        this.port = port;
    }

    //外部有输入请求后，需要加入到请求队列中
    public void addNewConnection(InputSource source) {
        sourceQueue.offer(source);
    }

    public int getPort() {
        return this.port;
    }

    public void run() {
        //1. 这个线程就只监听连接，然后将对应的事件放入处理缓冲队列，所以效率很高
        while (true) {
            System.out.println("acceptor阻塞等待");
            InputSource source = null;
            try {
                // 相当于 serversocket.accept()，接收输入请求，该例从请求队列中获取输入请求
                //take方法会阻塞等待，知道队列中有数据了
                source = sourceQueue.take();
            } catch (InterruptedException e) {
                // ignore it;
            }

            //接收到InputSource后将接收到event设置type为ACCEPT，并将source赋值给event
            if (source != null) {
                Event acceptEvent = new Event();
                acceptEvent.setSource(source);
                acceptEvent.setType(EventType.ACCEPT);

                selector.addEvent(acceptEvent);
            }

        }
    }
}
