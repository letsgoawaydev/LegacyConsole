package com.letsgoaway.legacyconsole;

import java.util.ArrayList;

public class TickTimer {
    public static ArrayList<TickTimer> timersToUpdate = new ArrayList<TickTimer>();
    public boolean finished = false;
    public boolean timerIsRunning = false;
    public int durationTicks;
    public int currentTicks = 0;
    public float secondsLeft;
    public int roundedSecondsLeft;
    public int ticksLeft;
    private int lastRoundedSeconds;
    private Runnable task;
    private Runnable eachSecondTask;
    public boolean finishOnRoundedZero = false;



    TickTimer(Float durationSeconds, Runnable task, Runnable eachSecondTask) {
        this.task = task;
        this.eachSecondTask = eachSecondTask;

        durationTicks = Math.round(20F * durationSeconds);
    };

    TickTimer(Integer durationTicks, Runnable task, Runnable eachSecondTask) {
        this.task = task;
        this.eachSecondTask = eachSecondTask;

        this.durationTicks = durationTicks;
    };

    public void stop() {
        if (timersToUpdate.contains(this))
        {
            timersToUpdate.remove(this);
            timerIsRunning = false;
        }
    }

    public void reset() {
        stop();
        currentTicks = 0;
        finished = false;
        start();
    }

    public void start() {
        if (!timerIsRunning)
        {
            if (finished)
            {
                reset();
                return;
            }
            timerIsRunning = true;
            timersToUpdate.add(this);
        }
    }


    public void tick() {
        currentTicks++;
        if (currentTicks == durationTicks && !finishOnRoundedZero)
        {
            stop();
            finished = true;
            if (task != null)
            {
                task.run();
            }
        }
        ticksLeft = currentTicks - durationTicks;
        secondsLeft = ticksLeft / 20;
        roundedSecondsLeft = (int) Math.abs(Math.floor(secondsLeft));
        if (lastRoundedSeconds != roundedSecondsLeft)
        {
            if (eachSecondTask != null)
            {
                eachSecondTask.run();
            }
            lastRoundedSeconds = roundedSecondsLeft;
        }
        else
        {
            lastRoundedSeconds = roundedSecondsLeft;
        }
        if (finishOnRoundedZero && roundedSecondsLeft == 0)
        {
            stop();
            finished = true;
            if (task != null)
            {
                task.run();
            }
        }
    }

    public static void update() {
        try
        {
            for (TickTimer t : timersToUpdate)
            {
                t.tick();
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }

    }
}
