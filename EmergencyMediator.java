// Nguyen Thanh Dat - 202419040
import java.util.Objects;

// Mediator: nắm toàn bộ logic phối hợp giữa các thiết bị
final class EmergencyMediator implements IEmergencyMediator {
    public static final String FIRE_DETECTED = "FIRE_DETECTED";
    public static final String ALL_CLEAR = "ALL_CLEAR";

    private final Alarm alarm;
    private final DoorController door;
    private final VentilationController ventilation;

    public EmergencyMediator(Alarm alarm, DoorController door, VentilationController ventilation) {
        this.alarm = Objects.requireNonNull(alarm, "alarm");
        this.door = Objects.requireNonNull(door, "door");
        this.ventilation = Objects.requireNonNull(ventilation, "ventilation");
    }

    @Override
    public void notify(Object sender, String eventName) {
        System.out.println("[MEDIATOR] Nhận sự kiện '" + eventName
                + "' từ " + sender.getClass().getSimpleName());

        switch (eventName) {
            case FIRE_DETECTED -> {
                alarm.activate();
                door.unlockEmergencyExits();
                ventilation.startSmokeExhaust();
            }
            case ALL_CLEAR -> {
                alarm.deactivate();
                door.lockNormal();
                ventilation.stopSmokeExhaust();
            }
            default -> System.out.println("[MEDIATOR] Sự kiện không xác định: " + eventName);
        }
    }
}
