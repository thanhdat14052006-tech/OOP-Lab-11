import java.util.Objects;

// Object Adapter: chứa lớp cũ, không copy trạng thái
final class SmokeSensorAdapter implements ISensor {
    private final LegacySmokeDetector legacy;

    public SmokeSensorAdapter(LegacySmokeDetector legacy) {
        this.legacy = Objects.requireNonNull(legacy, "legacy");
    }

    @Override
    public double readValue() {
        return legacy.getSmokeLevel() / 10.0; // 0..1000 -> 0..100 (%)
    }

    @Override
    public String getUnit() {
        return "% smoke";
    }
}
