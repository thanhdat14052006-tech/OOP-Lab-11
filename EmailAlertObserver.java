// Nguyen Thanh Dat - 202419040
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Giả lập gửi email: chỉ in ra console
final class EmailAlertObserver implements IAlertObserver {
    private final String recipient;

    public EmailAlertObserver() {
        this("security@campus.edu");
    }

    public EmailAlertObserver(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public void update(AlertEvent e) {
        System.out.println(String.format(Locale.US,
                "[EMAIL -> %s] Cảnh báo %s tại %s: %.1f%s lúc %s",
                recipient, e.severity(), e.sensorName(), e.value(), e.unit(),
                e.occurredAt().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
    }
}
