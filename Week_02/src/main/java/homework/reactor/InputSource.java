package homework.reactor;

/**
 * @author wyp
 * @version 1.0
 * @description description
 * @date in 19:36 04/11/2020
 * @since 1.0
 */
public class InputSource {
    private Object data;
    private long id;

    public InputSource(Object data, long id) {
        this.data = data;
        this.id = id;
    }

    @Override
    public String toString() {
        return "InputSource{" +
                "data=" + data +
                ", id=" + id +
                '}';
    }
}
