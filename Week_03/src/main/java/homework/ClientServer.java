package homework;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/**
 * @author wyp
 * @version 1.0
 * @description description
 * @date in 19:33 28/10/2020
 * @since 1.0
 */
public class ClientServer {
    public static void main(String[] args) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://127.0.0.1:8888/test");
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = client.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            System.out.println("响应状态："+httpResponse.getStatusLine());
            if (entity != null){
                byte[] bytes = new byte[entity.getContent().available()];
                entity.getContent().read(bytes);

                System.out.println("响应内容："+new String(bytes));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //释放资源
            try {
                if (client != null){
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
