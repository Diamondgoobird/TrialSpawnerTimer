package com.diamondgoobird.trialspawnertimer.config;

import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class TrialSpawnerTimerCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess) -> {
                    commandDispatcher.register(ClientCommandManager.literal("trialspawnertimer").executes(
                            context -> {
                                TrialSpawnerTimer.showGui = true;
                                return 1;
                            }
                    ));
                }
        );
    }
}
