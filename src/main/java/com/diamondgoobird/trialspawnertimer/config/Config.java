package com.diamondgoobird.trialspawnertimer.config;

import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.SimpleOption;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Represents the config file for the TrialSpawnerTimer mod
 */
public class Config implements Serializable {
    // Whether the timer is visible through walls
    private boolean seeThroughWalls = false;
    // Whether the timer text is rainbow-colored based on the amount of time left
    private boolean chromaTimer = false;
    // Whether the timer should disappear after it reaches 00:00
    // private boolean deleteOnComplete = true;
    private final Path filePath;

    public Config(String path) throws IOException {
        this.filePath = FabricLoader.getInstance().getConfigDir().resolve(path);
        loadConfig();
    }

    /**
     * Loads the config from the config/trialspawnertimer.properties file
     * @throws IOException if there is an error writing to the config
     */
    private void loadConfig() throws IOException {
        if (!Files.exists(filePath)) {
            saveConfig();
            return;
        }
        FileInputStream fis = new FileInputStream(filePath.toFile());
        Properties p = new Properties();
        p.load(fis);
        seeThroughWalls = p.getProperty("seeThroughWalls").equals("true");
        chromaTimer = p.getProperty("chromaTimer").equals("true");
    }

    /**
     * Saves the config to the config/trialspawnertimer.properties file
     */
    public void saveConfig() {
        Properties p = new Properties();
        p.setProperty("seeThroughWalls", String.valueOf(seeThroughWalls));
        p.setProperty("chromaTimer", String.valueOf(chromaTimer));
        // p.setProperty("deleteOnComplete", String.valueOf(deleteOnComplete));
        try {
            if (!filePath.getParent().toFile().exists()) {
                filePath.getParent().toFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(filePath.toFile());
            p.store(fos, "TrialSpawnerTimer Config version " + TrialSpawnerTimer.VERSION);
        }
        catch (IOException e) {
            TrialSpawnerTimer.LOGGER.error("Failed to save config: {}", e.getMessage());
        }
    }

    /**
     * Gets the config properly whether the timer should be visible through walls
     * @return true if seeing through walls is enabled, false otherwise
     */
    public boolean getSeeThroughWalls() {
        return seeThroughWalls;
    }

    /**
     * Gets the config property whether the text color changes based on how long is left in the cooldown
     * @return true if the chroma timer is enabled, false otherwise
     */
    public boolean getChromaTimer() {
        return chromaTimer;
    }

    public SimpleOption<?>[] getOptions() {
        SimpleOption<?>[] options = new SimpleOption[2];
        options[0] = SimpleOption.ofBoolean("Visible through walls", seeThroughWalls, aBoolean -> TrialSpawnerTimer.getConfig().seeThroughWalls = aBoolean);
        options[1] = SimpleOption.ofBoolean("Rainbow timer text", chromaTimer, aBoolean -> TrialSpawnerTimer.getConfig().chromaTimer = aBoolean);
        return options;
    }
}
