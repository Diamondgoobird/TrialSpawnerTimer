package com.diamondgoobird.trialchambertimer;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class TrialSpawnerTimer implements ClientModInitializer {
    private static final HashMap<BlockPos, Long> timers = new HashMap<>();

    @Override
    public void onInitializeClient() {

    }

    public static void insertTime(BlockPos pos, long time) {
        timers.put(pos, time);
    }

    public static long getTime(BlockPos pos) {
        Long time = timers.get(pos);
        if (time == null) {
            return 0;
        }
        return time;
    }

    public static void deleteTime(BlockPos pos) {
        timers.remove(pos);
    }
}
