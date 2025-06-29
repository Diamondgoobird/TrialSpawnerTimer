package com.diamondgoobird.trialspawnertimer.config;

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

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess) -> {
                    commandDispatcher.register(ClientCommandManager.literal("trialspawnertimer").executes(
                            context -> {
                                TrialSpawnerTimer.showGui = true;
                                return 1;
                            }
                    ).then(
                            ClientCommandManager.literal("debug").executes(
                                    context -> {
                                        TrialSpawnerTimer.debug();
                                        return 0;
                                    }
                            )
                    ).then(
                            ClientCommandManager.argument("test", BlockPosArgumentType.blockPos()).executes(
                                    context -> {
                                        testStorage(context);
                                        return 1;
                                    }
                            )
                    ));
                }
        );
    }

    public static void testStorage(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource csc = context.getSource();
        Text name = csc.getPlayer().getName();
        ServerCommandSource scs = new ServerCommandSource(CommandOutput.DUMMY, csc.getPosition(), csc.getRotation(), null, 4, name.getString(), name, MinecraftClient.getInstance().getServer(), csc.getEntity());
        BlockPos test = context.getArgument("test", PosArgument.class).toAbsoluteBlockPos(scs);
        context.getSource().sendFeedback(Text.of(TrialSpawnerTimer.getHistory(MinecraftClient.getInstance().world, test).toString()));
    }
}
