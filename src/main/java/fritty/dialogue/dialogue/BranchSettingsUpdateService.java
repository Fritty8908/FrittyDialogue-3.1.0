package fritty.dialogue.dialogue;

import java.util.Objects;

public final class BranchSettingsUpdateService {

    public void set(
            Branch branch,
            SettingKey key,
            Object value
    ) {
        Objects.requireNonNull(branch, "Branch cannot be null");
        Objects.requireNonNull(key, "Setting key cannot be null");
        Objects.requireNonNull(value, "Setting value cannot be null");

        validateValue(key, value);
        branch.getSettings().set(key, value);
    }

    public void clear(Branch branch, SettingKey key) {
        Objects.requireNonNull(branch, "Branch cannot be null");
        Objects.requireNonNull(key, "Setting key cannot be null");

        branch.getSettings().clear(key);
    }

    private void validateValue(SettingKey key, Object value) {
        Class<?> expectedType = switch (key) {
            case HIDE_PLAYERS,
                 ONE_TIME,
                 SEE_THROUGH_BLOCKS,
                 SMOOTH_TELEPORT,
                 USE_SLOWNESS,
                 MAKE_INVISIBLE,
                 SAVE_START_LOCATION,
                 ACTIONBAR_HINTS -> Boolean.class;

            case CHAR_DELAY,
                 BACKGROUND_ALPHA,
                 LINE_WIDTH,
                 TEXT_SCALE,
                 CHAR_PITCH,
                 SOUND_VOLUME,
                 PER_CHAR -> Number.class;

            default -> String.class;
        };

        if (!expectedType.isInstance(value)) {
            throw new IllegalArgumentException(
                    "Неправильний тип значення для налаштування "
                            + key.name()
                            + ". Очікується: "
                            + expectedType.getSimpleName()
            );
        }
    }
}
