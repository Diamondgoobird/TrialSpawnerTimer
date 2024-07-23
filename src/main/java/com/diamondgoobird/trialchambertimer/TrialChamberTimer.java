package com.diamondgoobird.trialchambertimer;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class TrialChamberTimer implements ClientModInitializer {
    private static HashMap<BlockPos, Long> timers = new HashMap<>();

    @Override
    public void onInitializeClient() {

    }

    public static void insertTime(BlockPos data, long time) {
        timers.put(data, time);
    }

    public static long getTime(BlockPos data) {
        Long time = timers.get(data);
        if (time == null) {
            return 0;
        }
        return time;
    }
}
