package com.diamondgoobird.trialspawnertimer.config;

import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

/**
 * Represents the screen where the user can change the different settings for the TrialChamberTimer mod
 */
public class ConfigScreen extends OptionsSubScreen {
    public ConfigScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.literal("TrialSpawnerTimer Options"));
    }

    /**
     * Initializes the buttons/config options to be displayed to the user
     */
    @Override
    protected void addOptions() {
        if (this.list == null) {
            return;
        }
        this.list.addSmall(TrialSpawnerTimer.getConfig().getOptions());
    }

    /**
     * Runs when the screen is closed, simply saves the config once we've changed the values
     */
    @Override
    public void removed() {
        TrialSpawnerTimer.getConfig().saveConfig();
    }
}