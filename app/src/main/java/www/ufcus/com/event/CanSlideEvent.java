package www.ufcus.com.event;

/**
 * Created by dongjunkun on 2016/2/19.
 */
//控制是否可以滑动
public class CanSlideEvent {
    private boolean b;

    public CanSlideEvent(boolean b) {
        this.b = b;
    }

    public boolean getB() {
        return b;
    }
}
