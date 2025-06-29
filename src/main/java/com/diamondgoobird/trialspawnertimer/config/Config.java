package com.diamondgoobird.trialspawnertimer.config;

import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Represents the config file for the TrialSpawnerTimer mod
 */
public class Config {
    private static final String CONFIG_PATH = "trialspawnertimer.properties";
    private final Text seeThroughWallsDescriptor = Text.literal("Whether the timer is visible through walls\n").append(Text.literal("NOTE: The rendering can be glitchy around pots and particles").withColor(Colors.LIGHT_RED));
    private boolean seeThroughWalls = false;
    private final Text chromaTimerDescriptor = Text.literal("Whether the color of the timer changes through the rainbow as the timer decreases (from ").append(Text.literal("blue").withColor(Color.CYAN.getRGB())).append(Text.literal(" to ")).append(Text.literal("red").withColor(Color.RED.getRGB())).append(Text.literal(")")).append(Text.literal(" or if the color is a static ")).append(Text.literal("magenta").withColor(Color.MAGENTA.getRGB()));
    private boolean chromaTimer = false;
    private final Text highSensitivityDescriptor = Text.literal("More thorough detection that allows timers to be created if the client never receives a blockupdate for the trial spawner (ex: if there's lag during the window where the cooldown starts)\n").append(Text.literal("NOTE: Try turning this on if timers aren't showing up consistently").withColor(Colors.LIGHT_RED));
    private boolean highSensitivity = true;
    private final Text brighterTextDescriptor = Text.literal("If enabled, the text is always bright as opposed to the text brightness being based on the block light level");
    private boolean brighterText = true;
    private final Path filePath;
    private SimpleOption<?>[] options;

    public Config() throws IOException {
        this.filePath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_PATH);
        loadConfig();
        initOptions();
    }

    /**
     * Sets up the Minecraft options that contain the descriptors, the titles and the callbacks to change the variables
     */
    private void initOptions() {
        options = new SimpleOption[4];
        options[0] = SimpleOption.ofBoolean("Visible through walls", SimpleOption.constantTooltip(seeThroughWallsDescriptor), seeThroughWalls, aBoolean -> this.seeThroughWalls = aBoolean);
        options[1] = SimpleOption.ofBoolean("Rainbow timer text", SimpleOption.constantTooltip(chromaTimerDescriptor), chromaTimer, aBoolean -> this.chromaTimer = aBoolean);
        options[2] = SimpleOption.ofBoolean("Higher sensitivity", SimpleOption.constantTooltip(highSensitivityDescriptor), highSensitivity, aBoolean -> this.highSensitivity = aBoolean);
        options[3] = SimpleOption.ofBoolean("Brighter text", SimpleOption.constantTooltip(brighterTextDescriptor), brighterText, aBoolean -> this.brighterText = aBoolean);
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
        seeThroughWalls = getBooleanProperty(p, "seeThroughWalls", seeThroughWalls);
        chromaTimer = getBooleanProperty(p, "chromaTimer", chromaTimer);
        highSensitivity = getBooleanProperty(p, "highSensitivity", highSensitivity);
        brighterText = getBooleanProperty(p, "brighterText", brighterText);
    }

    /**
     * Gets the property of a given name in boolean form from a specific properties list
     * @param p the properties list to search in
     * @param name the name of the property to get the boolean value of
     * @param fallback the backup return value in case the property is not present
     * @return either the boolean property value, or fallback in case it's not present
     */
    private boolean getBooleanProperty(Properties p, String name, boolean fallback) {
        String s = p.getProperty(name);
        if (s != null) {
            return s.equals("true");
        }
        return fallback;
    }

    /**
     * Saves the config to the config/trialspawnertimer.properties file
     */
    public void saveConfig() {
        Properties p = new Properties();
        p.setProperty("seeThroughWalls", String.valueOf(seeThroughWalls));
        p.setProperty("chromaTimer", String.valueOf(chromaTimer));
        p.setProperty("highSensitivity", String.valueOf(highSensitivity));
        p.setProperty("brighterText", String.valueOf(brighterText));
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

    /**
     * Gets the config property whether timer creation is more sensitive
     * @return true if the setting is enabled, false otherwise
     */
    public boolean isHighSensitivity() {
        return highSensitivity;
    }

    /**
     * Gets the config property whether timer text is always bright
     * @return true if the setting is enabled, false otherwise
     */
    public boolean isBrighterText() {
        return brighterText;
    }

    /**
     * Gets the options that are used in a GameOptionsScreen to change and display our config
     * @return the array of simpleoptions that represent this config
     */
    public SimpleOption<?>[] getOptions() {
        return options;
    }
}
