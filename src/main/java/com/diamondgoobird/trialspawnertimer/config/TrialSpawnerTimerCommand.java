package com.diamondgoobird.trialspawnertimer.config;

import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class TrialSpawnerTimerCommand {
    /**
     * Registers the TrialSpawnerTimer command on the client side
      */
    public static void register() {
        // Using the fabric command v2 api
        ClientCommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess) -> {
                    commandDispatcher.register(ClientCommandManager.literal("trialspawnertimer").executes(
                            // /trialspawnertimer
                            context -> {
                                /*
                                 Uses convoluted logic to display our GUI through
                                 a mixin called GameRendererMixin that changes the
                                 current screen right before the game tries to render it

                                 I had to do it this way because if I did:

                                 MinecraftClient.getInstance().setScreen()

                                 it would either crash or throw an exception because we're not on the render thread
                                */
                                TrialSpawnerTimer.showGui = true;
                                return 1;
                            }
                    ));
                }
        );
    }
}
