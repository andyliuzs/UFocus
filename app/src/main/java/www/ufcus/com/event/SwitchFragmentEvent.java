package www.ufcus.com.event;

/**
 * Created by andyliu on 16-7-6.
 */
public class SwitchFragmentEvent {
    String fragment;

    public SwitchFragmentEvent(String fragment) {
        this.fragment = fragment;
    }

    public String getFragment() {
        return fragment;
    }
}
