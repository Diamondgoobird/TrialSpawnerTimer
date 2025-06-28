package com.diamondgoobird.trialspawnertimer;

import com.diamondgoobird.trialspawnertimer.config.Config;
import com.diamondgoobird.trialspawnertimer.config.TrialSpawnerTimerCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static com.diamondgoobird.trialspawnertimer.TimerHandler.*;

public class TrialSpawnerTimer implements ClientModInitializer {
    public static String VERSION = "1.1.0";
    public static final Logger LOGGER = LoggerFactory.getLogger("trialspawnertimer");
    public static boolean showGui;
    private static Config CONFIG;

    // List of Trial Spawner states that the block can switch to without resetting our timer
    public static final List<TrialSpawnerState> ACCEPTABLE_STATES = List.of(
            TrialSpawnerState.WAITING_FOR_REWARD_EJECTION,  // Occurs when timer is computed
            TrialSpawnerState.EJECTING_REWARD,              // Gives player reward
            TrialSpawnerState.COOLDOWN                      // The remaining 29:56 ish
    );

    @Override
    public void onInitializeClient() {
        try {
            if (FabricLoaderImpl.INSTANCE.isModLoaded("fabric-command-api-v2")) {
                TrialSpawnerTimerCommand.register();
            }
            CONFIG = new Config("trialspawnertimer.properties");
            LOGGER.info("Initialized Trial Spawner Timer Version {}", VERSION);
        } catch (IOException e) {
            LOGGER.error("Error loading configuration file for Trial Spawner Timer");
        }
    }

    /**
     * Gets the mod config instance
     * @return instance of the mod config
     */
    public static Config getConfig() {
        return CONFIG;
    }

    /**
     * Handles Trial Spawner block updates, tests if the timer should be deleted
     * @param world the world the trial spawner is in
     * @param pos the position of the trial spawner
     * @param state the blockstate of the trial spawner
     */
    public static void onSpawnerBlockUpdate(World world, BlockPos pos, BlockState state) {
        // If the block was destroyed for some reason or updated to a different state then delete the timer
        TrialSpawnerState st = (TrialSpawnerState) state.getEntries().get(Properties.TRIAL_SPAWNER_STATE);
        if (shouldReset(st)) {
            deleteTime(world, pos);
        }
        if (!MinecraftClient.getInstance().isConnectedToLocalServer()) {
            onSpawnerStateUpdate(world, pos, st, TrialSpawnerLogic.FullConfig.DEFAULT.targetCooldownLength());
        }
    }

    /**
     * Handles TrialSpawnerState updates for Trial Spawners to
     * create and delete timers at the correct time
     * @param world the world the Trial Spawner is in
     * @param pos the position of the Trial Spawner
     * @param spawnerState the state of the Trial Spawner to check
     * @param cooldownLength the length of the cooldown of this Trial Spawner configuration
     */
    public static void onSpawnerStateUpdate(World world, BlockPos pos, TrialSpawnerState spawnerState, int cooldownLength) {
        // Reset the timer state if it changed to something we don't allow
        if (shouldReset(spawnerState)) {
            deleteTime(world, pos);
        }
        // Only insert the time at the state when the server calculates the time
        else if (spawnerState == TrialSpawnerState.WAITING_FOR_REWARD_EJECTION) {
            insertTime(world, pos, world.getTime(), cooldownLength);
        }
    }
}
