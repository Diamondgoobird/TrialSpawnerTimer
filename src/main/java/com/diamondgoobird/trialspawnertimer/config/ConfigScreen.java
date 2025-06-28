package com.diamondgoobird.trialspawnertimer.config;

import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.text.Text;

public class ConfigScreen extends GameOptionsScreen {
    public ConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.literal("TrialSpawnerTimer Options"));
    }

    @Override
    protected void addOptions() {
        if (this.body == null) {
            return;
        }
        this.body.addAll(TrialSpawnerTimer.getConfig().getOptions());
    }

    @Override
    public void removed() {
        TrialSpawnerTimer.getConfig().saveConfig();
    }
}