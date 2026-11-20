public final class SelfDetachObserver implements IAlertObserver {
    private final SensorMonitor monitor;
    int callCount;

    SelfDetachObserver(SensorMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void update(AlertEvent e) {
        callCount++;
        monitor.detach(this); // tự gỡ mình ngay trong lúc được thông báo
    }
}
