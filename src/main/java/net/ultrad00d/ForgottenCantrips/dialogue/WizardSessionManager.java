package net.ultrad00d.ForgottenCantrips.dialogue;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WizardSessionManager {
    private static final Map<String, SessionData> ACTIVE_TOKENS = new HashMap<>();

    public record SessionData(UUID playerUUID, DialogueChoice choice, String fromKey) {}

    public static void clearPlayerTokens(Player player) {
        ACTIVE_TOKENS.entrySet().removeIf(entry -> entry.getValue().playerUUID().equals(player.getUUID()));
    }

    public static void registerToken(String token, Player player, DialogueChoice choice, String fromKey) {
        ACTIVE_TOKENS.put(token, new SessionData(player.getUUID(), choice, fromKey));
    }

    public static SessionData consumeToken(String token, Player player) {
        SessionData session = ACTIVE_TOKENS.get(token);
        if (session != null && session.playerUUID().equals(player.getUUID())) {
            ACTIVE_TOKENS.remove(token);
            return session;
        }
        return null; // Invalid or fraudulent token
    }
}
