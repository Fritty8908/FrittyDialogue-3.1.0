package fritty.dialogue.gui;

import fritty.dialogue.dialogue.Dialogue;
import fritty.dialogue.dialogue.DialogueSettings;
import fritty.dialogue.dialogue.SettingKey;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Consumer;

public final class DialogueSettingsInputHandler {

    private final DialogueMenuInputHandler inputHandler;

    public DialogueSettingsInputHandler(
            DialogueMenuInputHandler inputHandler
    ) {
        this.inputHandler = Objects.requireNonNull(
                inputHandler,
                "Input handler cannot be null"
        );
    }

    public void requestString(
            Player player,
            String prompt,
            Consumer<String> callback
    ) {
        inputHandler.requestText(player, prompt, callback);
    }

    public void requestInteger(
            Player player,
            String prompt,
            int minimum,
            int maximum,
            Consumer<Integer> callback
    ) {
        inputHandler.requestNumber(
                player,
                prompt,
                minimum,
                maximum,
                callback
        );
    }

    public void requestDecimal(
            Player player,
            String prompt,
            double minimum,
            double maximum,
            Consumer<Double> callback
    ) {
        inputHandler.requestDecimal(
                player,
                prompt,
                minimum,
                maximum,
                callback
        );
    }

    public void requestBoolean(
            Player player,
            String prompt,
            Consumer<Boolean> callback
    ) {
        inputHandler.requestBoolean(
                player,
                prompt,
                callback
        );
    }

    public void requestSettingValue(
            Player player,
            Dialogue dialogue,
            SettingKey settingKey,
            String prompt,
            Consumer<String> callback
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        Objects.requireNonNull(settingKey, "Setting key cannot be null");

        inputHandler.requestText(
                player,
                prompt,
                value -> {
                    callback.accept(value);
                }
        );
    }
}
