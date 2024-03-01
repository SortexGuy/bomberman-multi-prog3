package uneg.bombfx.engine;

import javafx.animation.AnimationTimer;

public abstract class GameLoop extends AnimationTimer {
    private long lastNano = 0;
    private double max = -1;
    private double min = -1;
    private double timeToTick = 0;

    @Override
    public void start() {
        lastNano = 0;
        max = -1;
        min = -1;
        super.start();
    }

    @Override
    public void handle(long nowNano) {
        if (lastNano == 0) {
            lastNano = nowNano;
            return;
        }

        long deltaNano = nowNano - lastNano;
        double deltaS = ((double) deltaNano) / 1_000_000_000.0;
        lastNano = nowNano;

        double tickDur = 1 / 30.0;
        timeToTick += deltaS;
        if (timeToTick >= 0.0f) {
            if (timeToTick > max || max == -1)
                max = timeToTick;
            if (timeToTick < min || min == -1)
                min = timeToTick;

            tick(deltaS);

            sync(deltaS);

            // System.err.println("[DELTA> " + deltaS + " | TIMETODELTA> " + timeToTick +
            // " | MAX> " + max + " | MIN> " + min + "]");
            timeToTick -= tickDur;
        }
    }

    public abstract void tick(double deltaTime);

    public abstract void sync(double deltaTime);
}
