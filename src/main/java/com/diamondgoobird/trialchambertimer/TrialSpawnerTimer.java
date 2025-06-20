package com.diamondgoobird.trialchambertimer;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;

public class TrialSpawnerTimer implements ClientModInitializer {
    // Map for registry keys that contains a map for each blockpos to a starting time
    private static final HashMap<RegistryKey<World>, HashMap<BlockPos, Long>> timers = new HashMap<>();
    // List of Trial Spawner states that the block can switch to without resetting our timer
    private static final List<TrialSpawnerState> ACCEPTABLE_STATES = List.of(
            TrialSpawnerState.WAITING_FOR_REWARD_EJECTION,  // Occurs when timer is computed
            TrialSpawnerState.EJECTING_REWARD,              // Gives player reward
            TrialSpawnerState.COOLDOWN                      // The remaining 29:56 ish
    );

    @Override
    public void onInitializeClient() {

    }

    /**
     * Returns whether a Trial Spawner's timer should get deleted
     * when the block switches to this state
     *
     * @param state the state to test
     * @return true if changing to the state should delete its timer, false otherwise
     */
    public static boolean shouldReset(TrialSpawnerState state) {
        return !ACCEPTABLE_STATES.contains(state);
    }

    /**
     * Inserts the ending time (in milliseconds) of the trial spawner cooldown
     * at a specific position in a given world
     *
     * @param world the World in which the blockPos refers to (dimension)
     * @param pos   the position where the TrialSpawner is at
     * @param time  the time in milliseconds when the timer should end
     */
    public static void insertTime(World world, BlockPos pos, long time) {
        // Get the map or have a new one inserted
        HashMap<BlockPos, Long> t = timers.computeIfAbsent(world.getRegistryKey(), k -> new HashMap<>());
        t.put(pos, time);
    }

    /**
     * Fetches the time in milliseconds when the timer should end
     * for a specific block position in a given world, or returns 0
     *
     * @param world the World in which the blockPos refers to (dimension)
     * @param pos   the position where the TrialSpawner is at
     * @return      the time in milliseconds when the timer should end or 0 if nonexistent
     */
    public static long getTime(World world, BlockPos pos) {
        // Gets the timer map for the specific world
        HashMap<BlockPos, Long> t = timers.get(world.getRegistryKey());
        // If it doesn't exist yet just return 0
        if (t == null) {
            return 0;
        }
        // Return time or 0
        Long time = t.get(pos);
        if (time == null) {
            return 0;
        }
        return time;
    }

    /**
     * Deletes the timer tracking the trial spawner cooldown
     * at a specific position in a given world
     *
     * @param world the World in which the blockPos refers to (dimension)
     * @param pos   the position where the TrialSpawner is at
     */
    public static void deleteTime(World world, BlockPos pos) {
        // Gets the timer map for the specific world
        HashMap<BlockPos, Long> t = timers.get(world.getRegistryKey());
        // Can't delete if it's already null so we're done
        if (t == null) {
            return;
        }
        // It's not null so remove it
        t.remove(pos);
        // If it's empty then remove the hashmap since we're not using it
        if (t.isEmpty()) {
            timers.remove(world.getRegistryKey());
        }
    }
}
