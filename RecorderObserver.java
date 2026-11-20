import java.util.ArrayList;
import java.util.List;

public final class RecorderObserver implements IAlertObserver {
    final List<AlertEvent> received = new ArrayList<>();

    @Override
    public void update(AlertEvent e) {
        received.add(e);
    }
}
