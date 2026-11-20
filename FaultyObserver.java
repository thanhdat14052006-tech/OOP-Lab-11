// Nguyen Thanh Dat - 202419040
public final class FaultyObserver implements IAlertObserver {
    @Override
    public void update(AlertEvent e) {
        throw new IllegalStateException("Observer cố tình gây lỗi");
    }
}
