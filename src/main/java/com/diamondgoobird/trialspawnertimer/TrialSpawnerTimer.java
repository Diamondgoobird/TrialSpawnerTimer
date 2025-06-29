package com.diamondgoobird.trialspawnertimer;

import com.diamondgoobird.trialspawnertimer.config.Config;
import com.diamondgoobird.trialspawnertimer.config.TrialSpawnerTimerCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.registry.RegistryKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.diamondgoobird.trialspawnertimer.TimerHandler.*;

/**
 * Represents the TrialSpawnerTimer mod and its primary logic
 */
public class TrialSpawnerTimer implements ClientModInitializer {
    public static String VERSION = "1.1.0";
    private static final HashMap<RegistryKey<World>, HashMap<BlockPos, LinkedList<TrialSpawnerState>>> stateLog = new HashMap<>();
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
            CONFIG = new Config("trialspawnertimer.properties");
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
     * Handles Trial Spawner block updates, tests if the timer should be deleted
     * @param world the world the trial spawner is in
     * @param pos the position of the trial spawner
     * @param state the blockstate of the trial spawner
     */
    public static void onSpawnerBlockUpdate(World world, BlockPos pos, BlockState state) {
        // If the block was destroyed for some reason or updated to a different state then delete the timer
        TrialSpawnerState st = (TrialSpawnerState) state.getEntries().get(Properties.TRIAL_SPAWNER_STATE);
        // Only add to the history of the
        if (/*CONFIG.isHighSensitivity()*/true) {
            getHistory(world, pos).add(st);
        }
        LOGGER.info("Blockupdate: {}, Coordinate: {}", st, pos);
        // Reset the timer state if it changed to something we don't allow
        if (shouldReset(st)) {
            deleteTime(world, pos);
        }
        // Only insert the time at the state when the server calculates the time
        if (TimerHandler.shouldCreate(st) && !hasTimer(world, pos)) {
            insertTime(world, pos, world.getTime(), TrialSpawnerLogic.FullConfig.DEFAULT.targetCooldownLength());
            return;
        }
        // Ensures that if we missed the reward states that we start the timer ASAP
        // if (TrialSpawnerTimer.CONFIG.isHighSensitivity()) {
        //     checkMissedTimer(world, pos, st);
        // }
    }

    /**
     * Checks if we missed the window to create a timer, and we're currently on cooldown in order to make one ASAP
     * @param world the world the trialspawner is in
     * @param pos the block position of the trialspawner
     * @param st the most recent state of that trialspawner
     */
    private static void checkMissedTimer(World world, BlockPos pos, TrialSpawnerState st) {
        // Only check in the cooldown stage and if we don't already have a timer
        if (TimerHandler.shouldCreate(st) || hasTimer(world, pos)) {
            return;
        }
        insertTime(world, pos, world.getTime(), TrialSpawnerLogic.FullConfig.DEFAULT.targetCooldownLength());
        LOGGER.info("CAUGHT RATE LIMIT EXCEPTION, STARTED TIMER");
        // Gets the most recent TrialSpawnerState of this block
        // TrialSpawnerState tss = getHistory(world, pos).getLast();
        // if (tss == TrialSpawnerState.ACTIVE || tss == TrialSpawnerState.EJECTING_REWARD) {
        // }
        // else {
        //     LOGGER.info("{} THEN {}", st, tss);
        // }
    }

    // TODO: add comment
    public static LinkedList<TrialSpawnerState> getHistory(World world, BlockPos pos) {
        HashMap<BlockPos, LinkedList<TrialSpawnerState>> posMap = stateLog.get(world.getRegistryKey());
        LinkedList<TrialSpawnerState> list = new LinkedList<>();
        if (posMap != null) {
            LinkedList<TrialSpawnerState> temp = posMap.get(pos);
            if (temp != null) {
                return temp;
            }
            posMap.put(pos, list);
            return list;
        }
        else {
            posMap = new HashMap<>();
            stateLog.put(world.getRegistryKey(), posMap);
        }
        posMap.put(pos, list);
        return list;
    }
}
