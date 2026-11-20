import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Subject: chỉ biết IAlertObserver; là nơi duy nhất quyết định ngưỡng
final class SensorMonitor {
    private final List<IAlertObserver> observers = new ArrayList<>();

    public void attach(IAlertObserver observer) {
        Objects.requireNonNull(observer, "observer");
        if (!observers.contains(observer)) { // không đăng ký 2 lần
            observers.add(observer);
        }
    }

    public void detach(IAlertObserver observer) {
        observers.remove(observer);
    }

    public void checkSensor(String sensorName, ISensor sensor) {
        double value = sensor.readValue();

        String severity;
        if (value >= 70) {
            severity = "CRITICAL";
        } else if (value >= 50) {
            severity = "WARNING";
        } else {
            return; // dưới 50%: bình thường, không phát cảnh báo
        }

        AlertEvent alertEvent = new AlertEvent(
                sensorName,
                value,
                sensor.getUnit(),
                severity,
                LocalDateTime.now());

        notifyObservers(alertEvent);
    }

    private void notifyObservers(AlertEvent alertEvent) {
        // Duyệt trên BẢN SAO để observer có thể detach trong lúc được thông báo
        for (IAlertObserver observer : new ArrayList<>(observers)) {
            try {
                observer.update(alertEvent);
            } catch (Exception ex) {
                // Observer lỗi không được làm hỏng observer khác
                System.out.println("[MONITOR-ERROR] "
                        + observer.getClass().getSimpleName()
                        + " lỗi: " + ex.getMessage());
            }
        }
    }
}
