// Nguyen Thanh Dat - 202419040
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AuditLogObserver implements IAlertObserver {
    private final List<String> logs = new ArrayList<>();

    public List<String> getLogs() {
        return List.copyOf(logs); // trả bản chỉ-đọc
    }

    @Override
    public void update(AlertEvent e) {
        String line = String.format(Locale.US, "%s | %s | %.1f%s | %s",
                e.occurredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                e.sensorName(), e.value(), e.unit(), e.severity());
        logs.add(line);
        System.out.println("[AUDIT] " + line);
    }
}
