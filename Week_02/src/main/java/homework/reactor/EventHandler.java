package homework.reactor;

/**
 * @author wyp
 * @version 1.0
 * @description description
 * @date in 19:37 04/11/2020
 * @since 1.0
 */
public abstract class EventHandler {
    private InputSource source;
    public abstract void handle(Event event);

    public InputSource getSource() {
        return source;
    }

    public void setSource(InputSource source) {
        this.source = source;
    }
}
