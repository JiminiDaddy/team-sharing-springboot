package event;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/05/10
 * Time : 4:02 PM
 */

public class SendMessageEvent {
    private String name;

    protected SendMessageEvent() {
    }

    public SendMessageEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
