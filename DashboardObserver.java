// Nguyen Thanh Dat - 202419040
import java.util.Locale;

public final class DashboardObserver implements IAlertObserver {
    @Override
    public void update(AlertEvent e) {
        System.out.println(String.format(Locale.US,
                "[DASHBOARD] %s: %.1f%s [%s]",
                e.sensorName(), e.value(), e.unit(), e.severity()));
    }
}
