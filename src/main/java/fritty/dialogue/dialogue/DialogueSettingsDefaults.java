package fritty.dialogue.dialogue;

import java.util.EnumMap;
import java.util.Map;

public final class DialogueSettingsDefaults {

    private DialogueSettingsDefaults() {
    }

    public static Map<SettingKey, Object> create() {
        Map<SettingKey, Object> defaults =
                new EnumMap<>(SettingKey.class);

        defaults.put(SettingKey.HIDE_PLAYERS, true);
        defaults.put(SettingKey.ONE_TIME, false);
        defaults.put(SettingKey.SEE_THROUGH_BLOCKS, false);
        defaults.put(SettingKey.SMOOTH_TELEPORT, true);

        defaults.put(SettingKey.CHAR_DELAY, 2);
        defaults.put(SettingKey.CHAR_PITCH, 1.0);
        defaults.put(SettingKey.SOUND_VOLUME, 0.7);
        defaults.put(SettingKey.PER_CHAR, 1);

        defaults.put(SettingKey.BACKGROUND_COLOR, "#000000");
        defaults.put(SettingKey.BACKGROUND_ALPHA, 180);
        defaults.put(SettingKey.LINE_WIDTH, 300);
        defaults.put(SettingKey.TEXT_SCALE, 1.0);

        defaults.put(SettingKey.USE_SLOWNESS, false);
        defaults.put(SettingKey.MAKE_INVISIBLE, false);
        defaults.put(SettingKey.SAVE_START_LOCATION, true);
        defaults.put(SettingKey.ACTIONBAR_HINTS, true);

        return defaults;
    }
}
