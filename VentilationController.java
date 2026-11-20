public final class VentilationController {
    private boolean exhausting;

    public boolean isExhausting() {
        return exhausting;
    }

    public void startSmokeExhaust() {
        exhausting = true;
        System.out.println("[VENT] Bật quạt hút khói.");
    }

    public void stopSmokeExhaust() {
        exhausting = false;
        System.out.println("[VENT] Tắt quạt hút khói.");
    }
}
