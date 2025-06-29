package com.diamondgoobird.trialspawnertimer.config;

import com.diamondgoobird.trialspawnertimer.TimerHandler;
import com.diamondgoobird.trialspawnertimer.TrialSpawnerTimer;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class TrialSpawnerTimerCommand {
    // Registers the TrialSpawnerTimer command on the client side
    public static void register() {
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
                    ).then(
                            // /trialspawnertimer debug
                            // Prints out all the active timers to your logs
                            ClientCommandManager.literal("debug").executes(
                                    context -> {
                                        context.getSource().sendFeedback(Text.literal(TimerHandler.getTimers().toString()));
                                        return 0;
                                    }
                            ).then(
                                    // /trialspawner debug ~ ~ ~
                                    // Prints out the list of all the states this spawner has gone through since it's last timer
                                    // Used for debugging
                                    ClientCommandManager.argument("checkhistory", BlockPosArgumentType.blockPos()).executes(
                                            context -> {
                                                testStorage(context);
                                                return 1;
                                            }
                                    ).requires(fabricClientCommandSource -> /*TrialSpawnerTimer.getConfig().isHighSensitivity()*/true)
                            )
                    ));
                }
        );
    }

    // TODO: add comment
    public static void testStorage(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource csc = context.getSource();
        Text name = csc.getPlayer().getName();
        ServerCommandSource scs = new ServerCommandSource(CommandOutput.DUMMY, csc.getPosition(), csc.getRotation(), null, 4, name.getString(), name, MinecraftClient.getInstance().getServer(), csc.getEntity());
        BlockPos test = context.getArgument("checkhistory", PosArgument.class).toAbsoluteBlockPos(scs);
        assert MinecraftClient.getInstance().world != null;
        context.getSource().sendFeedback(Text.of(TrialSpawnerTimer.getHistory(MinecraftClient.getInstance().world, test).toString()));
    }
}
