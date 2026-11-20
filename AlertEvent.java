// Nguyen Thanh Dat - 202419040
import java.time.LocalDateTime;

// record: các trường đều final, không có setter => bất biến
record AlertEvent(
        String sensorName,
        double value,
        String unit,
        String severity,
        LocalDateTime occurredAt) {
}

// ---------------------------------------------------------------------
// 2. ADAPTER
// ---------------------------------------------------------------------
