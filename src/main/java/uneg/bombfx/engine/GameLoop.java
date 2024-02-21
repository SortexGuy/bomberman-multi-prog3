package uneg.bombfx.engine;

import javafx.animation.AnimationTimer;

public abstract class GameLoop extends AnimationTimer {
    int debugDelta = 0;
    long lastNano = 0;
    double max = -1;
    double min = -1;

    @Override
    public void start() {
        debugDelta = 0;
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
        double deltaMs = ((double) deltaNano) / 1_000_000.0;
        lastNano = nowNano;

        if (deltaMs > max || max == -1)
            max = deltaMs;
        if (deltaMs < min || min == -1)
            min = deltaMs;

        if (debugDelta <= 0) {
            System.err.println("[DELTA> " + deltaMs + " | MAX> " + max + " | MIN> " + min + "]");
            debugDelta = 30;
        }
        tick(deltaMs);
        sync(deltaMs);
        debugDelta--;
    }

    public abstract void tick(double deltaTime);

    public abstract void sync(double deltaTime);
}
