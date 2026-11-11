package fritty.dialogue.dialogue;

import java.util.Map;
import java.util.Objects;

public final class DialogueSettingsManager {

    public Object get(
            Dialogue dialogue,
            Branch branch,
            SettingKey key
    ) {
        return DialogueSettingsResolver.resolve(
                Objects.requireNonNull(dialogue),
                branch,
                Objects.requireNonNull(key)
        );
    }

    public void setDialogueSetting(
            Dialogue dialogue,
            SettingKey key,
            Object value
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        SettingsValueValidator.validate(key, value);
        dialogue.getSettings().set(key, value);
    }

    public void setBranchSetting(
            Branch branch,
            SettingKey key,
            Object value
    ) {
        Objects.requireNonNull(branch, "Branch cannot be null");

        SettingsValueValidator.validate(key, value);
        branch.getSettings().set(key, value);
    }

    public void resetDialogueSetting(
            Dialogue dialogue,
            SettingKey key
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        dialogue.getSettings().clear(key);
    }

    public void resetBranchSetting(
            Branch branch,
            SettingKey key
    ) {
        Objects.requireNonNull(branch, "Branch cannot be null");
        branch.getSettings().clear(key);
    }

    public Map<SettingKey, Object> resolveAll(
            Dialogue dialogue,
            Branch branch
    ) {
        return DialogueSettingsInheritanceResolver.resolve(
                dialogue,
                branch
        );
    }
}
