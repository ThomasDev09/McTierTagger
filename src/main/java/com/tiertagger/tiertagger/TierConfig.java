package com.tiertagger.tiertagger;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TierConfig {

    private static final File CONFIG_FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("tiertagger.properties")
            .toFile();

    public static String dbHost = "localhost";
    public static int dbPort = 3306;
    public static String dbName = "tier_testing";
    public static String dbUser = "root";
    public static String dbPassword = "";
    public static boolean showOwnTag = true;
    public static boolean showUnranked = false;

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            saveDefaults();
            TierTaggerMod.LOGGER.info("TierTagger: Created default config at {}", CONFIG_FILE.getAbsolutePath());
            return;
        }
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
            dbHost = props.getProperty("db_host", dbHost);
            dbPort = Integer.parseInt(props.getProperty("db_port", String.valueOf(dbPort)));
            dbName = props.getProperty("db_name", dbName);
            dbUser = props.getProperty("db_user", dbUser);
            dbPassword = props.getProperty("db_password", dbPassword);
            showOwnTag = Boolean.parseBoolean(props.getProperty("show_own_tag", "true"));
            showUnranked = Boolean.parseBoolean(props.getProperty("show_unranked", "false"));
        } catch (IOException e) {
            TierTaggerMod.LOGGER.error("TierTagger: Failed to load config", e);
        }
    }

    private static void saveDefaults() {
        Properties props = new Properties();
        props.setProperty("db_host", dbHost);
        props.setProperty("db_port", String.valueOf(dbPort));
        props.setProperty("db_name", dbName);
        props.setProperty("db_user", dbUser);
        props.setProperty("db_password", dbPassword);
        props.setProperty("show_own_tag", "true");
        props.setProperty("show_unranked", "false");
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out,
                "TierTagger Config\n" +
                "Fill in your MySQL database details below.\n" +
                "show_own_tag: show the tag above your own head\n" +
                "show_unranked: show a tag for unranked players"
            );
        } catch (IOException e) {
            TierTaggerMod.LOGGER.error("TierTagger: Failed to save default config", e);
        }
    }
}
