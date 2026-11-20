// Nguyen Thanh Dat - 202419040
public final class DoorController {
    private boolean unlocked;

    public boolean isUnlocked() {
        return unlocked;
    }

    public void unlockEmergencyExits() {
        unlocked = true;
        System.out.println("[DOOR] Đã mở khóa các cửa thoát hiểm.");
    }

    public void lockNormal() {
        unlocked = false;
        System.out.println("[DOOR] Đã khóa cửa về chế độ bình thường.");
    }
}
