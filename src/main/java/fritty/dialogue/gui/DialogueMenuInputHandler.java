package fritty.dialogue.gui;

import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public final class DialogueMenuInputHandler {

    private final DialogueChatInputManager inputManager;

    public DialogueMenuInputHandler(
            DialogueChatInputManager inputManager
    ) {
        this.inputManager = Objects.requireNonNull(
                inputManager,
                "Input manager cannot be null"
        );
    }

    public void requestText(
            Player player,
            String prompt,
            Consumer<String> callback
    ) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Objects.requireNonNull(callback, "Callback cannot be null");

        player.closeInventory();

        player.sendMessage(prompt);
        player.sendMessage("§7Введіть §fcancel §7для скасування.");

        inputManager.request(player, input -> {
            if (input == null) {
                return;
            }

            String value = input.trim();

            if (value.equalsIgnoreCase("cancel")) {
                player.sendMessage("§eВведення скасовано.");
                return;
            }

            if (value.isBlank()) {
                player.sendMessage("§cТекст не може бути порожнім.");
                return;
            }

            callback.accept(value);
        });
    }

    public void requestNumber(
            Player player,
            String prompt,
            int minimum,
            int maximum,
            Consumer<Integer> callback
    ) {
        if (minimum > maximum) {
            throw new IllegalArgumentException(
                    "Minimum cannot be greater than maximum"
            );
        }

        requestText(player, prompt, input -> {
            final int number;

            try {
                number = Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                player.sendMessage("§cПотрібно ввести ціле число.");
                return;
            }

            if (number < minimum || number > maximum) {
                player.sendMessage(
                        "§cДопустимий діапазон: "
                                + minimum
                                + "–"
                                + maximum
                );
                return;
            }

            callback.accept(number);
        });
    }

    public void requestDecimal(
            Player player,
            String prompt,
            double minimum,
            double maximum,
            Consumer<Double> callback
    ) {
        if (!Double.isFinite(minimum)
                || !Double.isFinite(maximum)
                || minimum > maximum) {
            throw new IllegalArgumentException(
                    "Invalid decimal range"
            );
        }

        requestText(player, prompt, input -> {
            final double number;

            try {
                number = Double.parseDouble(
                        input.replace(',', '.')
                );
            } catch (NumberFormatException exception) {
                player.sendMessage("§cПотрібно ввести число.");
                return;
            }

            if (!Double.isFinite(number)
                    || number < minimum
                    || number > maximum) {
                player.sendMessage(
                        "§cДопустимий діапазон: "
                                + minimum
                                + "–"
                                + maximum
                );
                return;
            }

            callback.accept(number);
        });
    }

    public void requestBoolean(
            Player player,
            String prompt,
            Consumer<Boolean> callback
    ) {
        requestText(player, prompt, input -> {
            String value = input.toLowerCase(Locale.ROOT);

            if (value.equals("true")
                    || value.equals("yes")
                    || value.equals("так")
                    || value.equals("1")) {
                callback.accept(true);
                return;
            }

            if (value.equals("false")
                    || value.equals("no")
                    || value.equals("ні")
                    || value.equals("0")) {
                callback.accept(false);
                return;
            }

            player.sendMessage(
                    "§cВведіть true або false."
            );
        });
    }
}
