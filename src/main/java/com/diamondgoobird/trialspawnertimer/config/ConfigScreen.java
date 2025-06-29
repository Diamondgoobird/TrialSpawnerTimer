package com.diamondgoobird.trialspawnertimer.config;

import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.text.Text;

/**
 * Represents the screen where the user can change the different settings for the TrialChamberTimer mod
 */
public class ConfigScreen extends GameOptionsScreen {
    public ConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.literal("TrialSpawnerTimer Options"));
    }

    /**
     * Initializes the buttons/config options to be displayed to the user
     */
    @Override
    protected void addOptions() {
        if (this.body == null) {
            return;
        }
        this.body.addAll(TrialSpawnerTimer.getConfig().getOptions());
    }

    /**
     * Runs when the screen is closed, simply saves the config once we've changed the values
     */
    @Override
    public void removed() {
        TrialSpawnerTimer.getConfig().saveConfig();
    }
}