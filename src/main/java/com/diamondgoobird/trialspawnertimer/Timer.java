package com.diamondgoobird.trialspawnertimer;

/**
 * Represents a cooldown timer with a starting time and long duration
 */
public class Timer {
    private final long time;
    private final long cooldown;

    public Timer(long time, long cooldown) {
        this.time = time;
        this.cooldown = cooldown;
    }

    /**
     * Get the time when the timer should be over
     * @return long as time in milliseconds when timer should end
     */
    public long getTimerEnd() {
        return time + cooldown;
    }

    /**
     * Gets the length of the cooldown of the timer
     * @return long as time in milliseconds for the length of the timer
     */
    public long getCooldown() {
        return cooldown;
    }
}
