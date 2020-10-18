package homework;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @describtion 自定义类加载器
 * @author wyp
 */
public class CustomClassLoader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] b = loadClassData(name);
        return defineClass(name, b, 0, b.length);
    }

    private byte[] loadClassData(String name) throws ClassNotFoundException {
        // load the class data from the connection
        try(InputStream inputStream = new FileInputStream(new File("src/main/java/homework/Hello.xlass"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            byte[] bytes = new byte[1];
            while (inputStream.available() != 0){
                inputStream.read(bytes);
                //System.out.println(bytes[0] + ":" + (255-bytes[0]));
                outputStream.write(255-bytes[0]);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ClassNotFoundException("未找到该类");
    }

    public static void main(String[] args) {
        try {
            //name填写需要加上包名
            Class<?> targetClass = new CustomClassLoader().findClass("Hello");
            Object instance = targetClass.newInstance();
            Method hello = targetClass.getDeclaredMethod("hello");
            hello.setAccessible(true);
            hello.invoke(instance);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
