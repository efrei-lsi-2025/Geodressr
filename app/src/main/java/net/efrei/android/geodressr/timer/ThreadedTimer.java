package net.efrei.android.geodressr.timer;

public class ThreadedTimer {
    private TimerEventListener listener;
    private boolean running;
    private long secondsElapsed;

    public ThreadedTimer(TimerEventListener listener) {
        this.listener = listener;
    }

    public synchronized void start() {
        running = true;
        new Thread(() -> {
            while (running) {
                secondsElapsed++;
                listener.onTimerTick(secondsElapsed);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public synchronized void stop() {
        running = false;
    }
}