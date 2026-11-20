// Nguyen Thanh Dat - 202419040
// Các thiết bị KHÔNG tham chiếu lẫn nhau
final class Alarm {
    private boolean ringing;

    public boolean isRinging() {
        return ringing;
    }

    public void activate() {
        ringing = true;
        System.out.println("[ALARM] Còi báo động BẬT!");
    }

    public void deactivate() {
        ringing = false;
        System.out.println("[ALARM] Còi báo động TẮT.");
    }
}
