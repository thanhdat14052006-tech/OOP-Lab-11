// Nguyen Thanh Dat - 202419040
import java.util.Objects;

// Cầu nối Observer -> Mediator: chỉ cảnh báo CRITICAL mới thành sự kiện
final class EmergencyBridgeObserver implements IAlertObserver {
    private final IEmergencyMediator mediator;

    public EmergencyBridgeObserver(IEmergencyMediator mediator) {
        this.mediator = Objects.requireNonNull(mediator, "mediator");
    }

    @Override
    public void update(AlertEvent e) {
        if (e.severity().equals("CRITICAL")) {
            mediator.notify(this, EmergencyMediator.FIRE_DETECTED);
        }
    }
}
