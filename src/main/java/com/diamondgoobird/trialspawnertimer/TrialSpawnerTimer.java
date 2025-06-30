package com.diamondgoobird.trialspawnertimer;

import com.diamondgoobird.trialspawnertimer.config.Config;
import com.diamondgoobird.trialspawnertimer.config.TrialSpawnerTimerCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static com.diamondgoobird.trialspawnertimer.TimerHandler.*;

/**
 * Represents the TrialSpawnerTimer mod and its primary logic
 */
public class TrialSpawnerTimer implements ClientModInitializer {
    public static String VERSION = "1.1.0";
    public static final Logger LOGGER = LoggerFactory.getLogger("trialspawnertimer");
    public static boolean showGui;
    private static Config CONFIG;

    /**
     * List of Trial Spawner states that the block can switch to without resetting our timer
     */
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
            CONFIG = new Config();
            LOGGER.info("Initialized Trial Spawner Timer Version {}", VERSION);
        } catch (IOException e) {
            LOGGER.error("Error loading Trial Spawner Timer: {}", e.getMessage());
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
     * Handles Trial Spawner block updates, tests if timers should be created/deleted
     * @param world the world the trial spawner is in
     * @param pos the position of the trial spawner
     * @param state the blockstate of the trial spawner
     */
    public static void onSpawnerBlockUpdate(World world, BlockPos pos, BlockState state) {
        TrialSpawnerState st = (TrialSpawnerState) state.getEntries().get(Properties.TRIAL_SPAWNER_STATE);
        onSpawnerStateUpdate(world, pos, st);
    }

    /**
     * Handles Trial Spawner State updates, tests if timers should be created/deleted
     * @param world the world the trial spawner is in
     * @param pos the position of the trial spawner
     * @param state the TrialSpawnerState of the trial spawner
     */
    public static void onSpawnerStateUpdate(World world, BlockPos pos, TrialSpawnerState state) {
        // Only insert the time at the state when the server calculates the time
        if (shouldCreate(state) && !hasTimer(world, pos)) {
            insertTime(world, pos, world.getTime(), TrialSpawnerLogic.FullConfig.DEFAULT.targetCooldownLength());
            return;
        }
        // Reset the timer state if it changed to something we don't allow
        if (shouldReset(state)) {
            deleteTime(world, pos);
        }
    }
}
