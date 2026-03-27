package com.diamondgoobird.trialspawnertimer.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;

/**
 * Entrypoint for ModMenu so that players can get to the config screen easier
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<@org.jetbrains.annotations.NotNull Screen>) ConfigScreen::new;
    }
}
