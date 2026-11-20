// Nguyen Thanh Dat - 202419040
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + name);
        } else {
            failed++;
            System.out.println("  [FAIL] " + name);
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    public static void main(String[] args) {
        // In tiếng Việt có dấu đúng trên console
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        testAdapter();
        testThresholds();
        testObserverRegistration();
        testObserverRobustness();
        testImmutableEvent();
        testMediatorFlow();
        runFullDemo();

        System.out.println();
        System.out.println("KẾT QUẢ: " + passed + " đạt, " + failed + " lỗi.");
    }

    // --- Adapter ---
    private static void testAdapter() {
        section("TEST ADAPTER");

        LegacySmokeDetector legacy = new LegacySmokeDetector(735);
        ISensor sensor = new SmokeSensorAdapter(legacy);

        System.out.println(sensor.readValue()); // 73.5
        System.out.println(sensor.getUnit());   // % smoke

        check("735 -> 73.5", sensor.readValue() == 73.5);
        check("Đơn vị là '% smoke'", sensor.getUnit().equals("% smoke"));

        // Không copy trạng thái: đổi lớp cũ thì adapter thấy giá trị mới
        legacy.setSmokeLevel(200);
        check("Adapter đọc trực tiếp lớp cũ (200 -> 20.0)", sensor.readValue() == 20.0);

        legacy.setSmokeLevel(5000); // bị clamp về 1000
        check("Giá trị tối đa 1000 -> 100.0", sensor.readValue() == 100.0);

        boolean threw = false;
        try {
            new SmokeSensorAdapter(null);
        } catch (NullPointerException ex) {
            threw = true;
        }
        check("Constructor null ném NullPointerException", threw);
    }

    // --- Ngưỡng cảnh báo ---
    private static void testThresholds() {
        section("TEST NGƯỠNG CẢNH BÁO");

        SensorMonitor monitor = new SensorMonitor();
        RecorderObserver recorder = new RecorderObserver();
        monitor.attach(recorder);

        LegacySmokeDetector legacy = new LegacySmokeDetector(0);
        ISensor sensor = new SmokeSensorAdapter(legacy);

        legacy.setSmokeLevel(499); // 49.9%
        monitor.checkSensor("Lab-A", sensor);
        check("49.9% -> không cảnh báo", recorder.received.size() == 0);

        legacy.setSmokeLevel(500); // 50%
        monitor.checkSensor("Lab-A", sensor);
        check("50% -> WARNING",
                recorder.received.size() == 1
                        && recorder.received.get(0).severity().equals("WARNING"));

        legacy.setSmokeLevel(699); // 69.9%
        monitor.checkSensor("Lab-A", sensor);
        check("69.9% -> WARNING",
                recorder.received.size() == 2
                        && recorder.received.get(1).severity().equals("WARNING"));

        legacy.setSmokeLevel(700); // 70%
        monitor.checkSensor("Lab-A", sensor);
        check("70% -> CRITICAL",
                recorder.received.size() == 3
                        && recorder.received.get(2).severity().equals("CRITICAL"));

        AlertEvent last = recorder.received.get(2);
        check("AlertEvent chứa đúng dữ liệu",
                last.sensorName().equals("Lab-A")
                        && last.value() == 70.0
                        && last.unit().equals("% smoke"));
    }

    // --- Đăng ký / hủy đăng ký ---
    private static void testObserverRegistration() {
        section("TEST ATTACH / DETACH");

        SensorMonitor monitor = new SensorMonitor();
        RecorderObserver recorder = new RecorderObserver();
        ISensor sensor = new SmokeSensorAdapter(new LegacySmokeDetector(800));

        monitor.attach(recorder);
        monitor.attach(recorder); // đăng ký lần 2
        monitor.checkSensor("Lab-A", sensor);
        check("Đăng ký 2 lần chỉ nhận 1 thông báo", recorder.received.size() == 1);

        monitor.detach(recorder);
        monitor.checkSensor("Lab-A", sensor);
        check("Sau detach không nhận thêm", recorder.received.size() == 1);

        boolean threw = false;
        try {
            monitor.attach(null);
        } catch (NullPointerException ex) {
            threw = true;
        }
        check("attach(null) ném NullPointerException", threw);
    }

    // --- Độ bền của Observer ---
    private static void testObserverRobustness() {
        section("TEST OBSERVER LỖI / TỰ GỠ");

        ISensor sensor = new SmokeSensorAdapter(new LegacySmokeDetector(900));

        // Observer lỗi không ảnh hưởng observer khác
        SensorMonitor monitor1 = new SensorMonitor();
        RecorderObserver before = new RecorderObserver();
        RecorderObserver after = new RecorderObserver();
        monitor1.attach(before);
        monitor1.attach(new FaultyObserver());
        monitor1.attach(after);
        monitor1.checkSensor("Lab-A", sensor);
        check("Observer đứng trước nhận được cảnh báo", before.received.size() == 1);
        check("Observer đứng sau vẫn nhận được dù có observer lỗi", after.received.size() == 1);

        // Observer tự detach trong lúc notify
        SensorMonitor monitor2 = new SensorMonitor();
        SelfDetachObserver selfDetach = new SelfDetachObserver(monitor2);
        RecorderObserver other = new RecorderObserver();
        monitor2.attach(selfDetach);
        monitor2.attach(other);
        monitor2.checkSensor("Lab-A", sensor);
        monitor2.checkSensor("Lab-A", sensor);
        check("Observer tự gỡ chỉ được gọi 1 lần", selfDetach.callCount == 1);
        check("Observer còn lại vẫn nhận đủ 2 lần", other.received.size() == 2);
    }

    // --- Tính bất biến của AlertEvent ---
    private static void testImmutableEvent() {
        section("TEST ALERTEVENT BẤT BIẾN");

        // Hai observer nhận CÙNG một đối tượng AlertEvent
        SensorMonitor monitor = new SensorMonitor();
        RecorderObserver a = new RecorderObserver();
        RecorderObserver b = new RecorderObserver();
        monitor.attach(a);
        monitor.attach(b);
        monitor.checkSensor("Lab-A", new SmokeSensorAdapter(new LegacySmokeDetector(750)));

        check("Hai observer nhận cùng dữ liệu", a.received.get(0).equals(b.received.get(0)));

        // record không có setter: muốn "đổi" phải tạo đối tượng mới, bản gốc giữ nguyên
        AlertEvent original = a.received.get(0);
        AlertEvent changed = new AlertEvent(
                original.sensorName(), original.value(), original.unit(),
                "WARNING", original.occurredAt());
        check("Bản gốc không bị đổi khi tạo bản mới", original.severity().equals("CRITICAL"));
        check("Bản mới có giá trị mới", changed.severity().equals("WARNING"));
        // original.severity = "X";  // <- lỗi biên dịch vì trường là final
    }

    // --- Mediator ---
    private static void testMediatorFlow() {
        section("TEST MEDIATOR");

        Alarm alarm = new Alarm();
        DoorController door = new DoorController();
        VentilationController vent = new VentilationController();
        EmergencyMediator mediator = new EmergencyMediator(alarm, door, vent);

        LegacySmokeDetector legacy = new LegacySmokeDetector(0);
        ISensor sensor = new SmokeSensorAdapter(legacy);
        SensorMonitor monitor = new SensorMonitor();
        monitor.attach(new EmergencyBridgeObserver(mediator));

        legacy.setSmokeLevel(600); // WARNING
        monitor.checkSensor("Lab-A", sensor);
        check("WARNING chưa kích hoạt ứng phó khẩn cấp",
                !alarm.isRinging() && !door.isUnlocked() && !vent.isExhausting());

        legacy.setSmokeLevel(850); // CRITICAL
        monitor.checkSensor("Lab-A", sensor);
        check("CRITICAL -> alarm bật", alarm.isRinging());
        check("CRITICAL -> mở cửa thoát hiểm", door.isUnlocked());
        check("CRITICAL -> bật quạt hút", vent.isExhausting());

        mediator.notify(new Object(), EmergencyMediator.ALL_CLEAR);
        check("ALL_CLEAR -> tắt toàn bộ",
                !alarm.isRinging() && !door.isUnlocked() && !vent.isExhausting());
    }

    // --- Demo tổng hợp: ghép đủ các thành phần như sơ đồ ---
    private static void runFullDemo() {
        section("DEMO TỔNG HỢP CAMPUS SAFETY HUB");

        LegacySmokeDetector legacy = new LegacySmokeDetector(100);
        ISensor sensor = new SmokeSensorAdapter(legacy);

        EmergencyMediator mediator = new EmergencyMediator(
                new Alarm(), new DoorController(), new VentilationController());

        SensorMonitor monitor = new SensorMonitor();
        EmailAlertObserver email = new EmailAlertObserver();
        monitor.attach(new DashboardObserver());
        monitor.attach(email);
        monitor.attach(new AuditLogObserver());
        monitor.attach(new EmergencyBridgeObserver(mediator));

        System.out.println("\n-- Khói 10% (bình thường) --");
        monitor.checkSensor("Phòng máy 1", sensor);

        System.out.println("\n-- Khói 55% (WARNING) --");
        legacy.setSmokeLevel(550);
        monitor.checkSensor("Phòng máy 1", sensor);

        System.out.println("\n-- Hủy đăng ký Email, khói 82% (CRITICAL) --");
        monitor.detach(email);
        legacy.setSmokeLevel(820);
        monitor.checkSensor("Phòng máy 1", sensor);
    }
}
