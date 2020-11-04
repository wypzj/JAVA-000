package homework.reactor;

/**
 * @author wyp
 * @version 1.0
 * @description description
 * @date in 19:37 04/11/2020
 * @since 1.0
 */
public class Event {
    private InputSource source;
    private EventType type;

    public InputSource getSource() {
        return source;
    }

    public void setSource(InputSource source) {
        this.source = source;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }
}
