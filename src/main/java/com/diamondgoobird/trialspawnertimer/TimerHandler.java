package com.diamondgoobird.trialspawnertimer;

import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

/**
 * Handles every cooldown timer for Trail Spawners
 * Uses nested HashMap of World then BlockPos to get the Long of when the timer ends
 */
public class TimerHandler {
    // Map for registry keys that contains a map for each blockpos to a timer
    private static final HashMap<RegistryKey<World>, HashMap<BlockPos, Timer>> timers = new HashMap<>();

    /**
     * Returns whether a Trial Spawner's timer should get deleted
     * when the block switches to this state
     *
     * @param state the state to test
     * @return true if changing to the state should delete its timer, false otherwise
     */
    public static boolean shouldReset(TrialSpawnerState state) {
        return !TrialSpawnerTimer.ACCEPTABLE_STATES.contains(state);
    }

    /**
     * Returns true if there's a cooldown being tracked
     * at a given position in a given world
     *
     * @param world the world to check the block of
     * @param pos the position where the trial spawner is
     * @return true if there is an active cooldown timer, false otherwise
     */
    public static boolean hasTimer(World world, BlockPos pos) {
        return getTimer(world, pos) != null;
    }

    /**
     * Inserts the ending time (in milliseconds) of the trial spawner cooldown
     * at a specific position in a given world
     *
     * @param world the World in which the blockPos refers to (dimension)
     * @param pos   the position where the TrialSpawner is at
     * @param time  the time in milliseconds when the timer should end
     */
    public static void insertTime(World world, BlockPos pos, long time, long cooldown) {
        // Get the map or have a new one inserted
        HashMap<BlockPos, Timer> t = timers.computeIfAbsent(world.getRegistryKey(), k -> new HashMap<>());
        t.put(pos, new Timer(time, cooldown));
        TrialSpawnerTimer.LOGGER.info("Timer added at block {} in {} ending {} minutes from now", pos, world.getRegistryKey().getValue(), Duration.of(cooldown, ChronoUnit.SECONDS).toMinutes() / 20);
    }

    /**
     * Fetches the time in milliseconds when the timer should end
     * for a specific block position in a given world, or returns 0
     *
     * @param world the World in which the blockPos refers to (dimension)
     * @param pos   the position where the TrialSpawner is at
     * @return      the time in milliseconds when the timer should end or 0 if nonexistent
     */
    public static Timer getTimer(World world, BlockPos pos) {
        // Gets the timer map for the specific world
        HashMap<BlockPos, Timer> t = timers.get(world.getRegistryKey());
        // If it doesn't exist yet just return 0
        if (t == null) {
            return null;
        }
        // Return time or 0
        return t.get(pos);
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
        HashMap<BlockPos, Timer> t = timers.get(world.getRegistryKey());
        // Can't delete if it's already null, so we're done
        if (t == null) {
            return;
        }
        // It's not null so remove it
        Timer ti = t.remove(pos);
        if (ti != null) {
            long timeLeft = Duration.of(ti.getTimerEnd() - world.getTime(), ChronoUnit.SECONDS).toMinutes() / 20;
            TrialSpawnerTimer.LOGGER.info("Timer removed at block {} in {} with {} minutes left", pos, world.getRegistryKey().getValue(), timeLeft);
        }
        // If it's empty then remove the hashmap since we're not using it
        if (t.isEmpty()) {
            timers.remove(world.getRegistryKey());
        }
    }
}
