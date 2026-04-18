package com.tiertagger.tiertagger;

import net.minecraft.util.Formatting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TierCache {

    private static final Map<UUID, String> TIER_MAP = new ConcurrentHashMap<>();

    public static String getTier(UUID uuid) {
        return TIER_MAP.get(uuid);
    }

    public static void setTier(UUID uuid, String tier) {
        if (tier == null || tier.isBlank() || tier.equalsIgnoreCase("Unranked")) {
            TIER_MAP.remove(uuid);
        } else {
            TIER_MAP.put(uuid, tier);
        }
    }

    public static void refresh() {
        Thread thread = new Thread(() -> {
            String url = "jdbc:mysql://" + TierConfig.dbHost + ":" + TierConfig.dbPort
                    + "/" + TierConfig.dbName + "?useSSL=false&allowPublicKeyRetrieval=true";
            try (Connection conn = DriverManager.getConnection(url, TierConfig.dbUser, TierConfig.dbPassword)) {
                String query = "SELECT p.uuid, t.tier FROM players p LEFT JOIN tiers t ON p.discord_id = t.discord_id";
                try (PreparedStatement stmt = conn.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {
                    TIER_MAP.clear();
                    while (rs.next()) {
                        String rawUuid = rs.getString("uuid");
                        String tier = rs.getString("tier");
                        if (rawUuid == null || tier == null || tier.equalsIgnoreCase("Unranked")) continue;
                        try {
                            UUID uuid = toUUID(rawUuid);
                            TIER_MAP.put(uuid, tier);
                        } catch (Exception ignored) {}
                    }
                }
                TierTaggerMod.LOGGER.info("TierTagger: Refreshed {} tier entries.", TIER_MAP.size());
            } catch (Exception e) {
                TierTaggerMod.LOGGER.error("TierTagger: Failed to connect to database: {}", e.getMessage());
            }
        }, "TierTagger-DB-Refresh");
        thread.setDaemon(true);
        thread.start();
    }

    private static UUID toUUID(String raw) {
        String clean = raw.replace("-", "");
        if (clean.length() == 32) {
            return UUID.fromString(
                clean.substring(0, 8) + "-" +
                clean.substring(8, 12) + "-" +
                clean.substring(12, 16) + "-" +
                clean.substring(16, 20) + "-" +
                clean.substring(20)
            );
        }
        return UUID.fromString(raw);
    }

    public static Formatting getColor(String tier) {
        if (tier == null) return Formatting.GRAY;
        return switch (tier) {
            case "HT1" -> Formatting.RED;
            case "LT1" -> Formatting.GOLD;
            case "HT2" -> Formatting.GREEN;
            case "LT2" -> Formatting.AQUA;
            case "HT3" -> Formatting.YELLOW;
            case "LT3" -> Formatting.LIGHT_PURPLE;
            case "HT4" -> Formatting.BLUE;
            case "LT4" -> Formatting.DARK_AQUA;
            case "HT5" -> Formatting.DARK_GRAY;
            case "LT5" -> Formatting.GRAY;
            default -> Formatting.WHITE;
        };
    }
}
