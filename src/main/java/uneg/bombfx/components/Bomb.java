package uneg.bombfx.components;

import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Bomb
 */
public class Bomb {
    private enum BombState {
        IDLE,
        EXPLODING,
        DELETING;
    }

    private final int SIZE = 28;
    // private static int COUNT = 0;
    private float secLeftToExpl = 3.0f;
    private BombState state = BombState.IDLE;
    // private int id;
    private int ownerID;
    private ArrayList<Player> players;
    private LevelGrid levelGrid;
    private Point2D pos;

    public Bomb(int ownerID, Point2D pos, ArrayList<Player> players, LevelGrid levelGrid) {
        this.ownerID = ownerID;
        this.players = players;
        this.levelGrid = levelGrid;
        Point2D center = levelGrid.getBombPos(pos);
        this.pos = center.subtract(SIZE / 2, SIZE / 2);
        // this.id = COUNT;
        // COUNT = (COUNT + 1) % 80;
    }

    public void update(double delta) {
        secLeftToExpl -= delta;
        if (secLeftToExpl <= 0)
            switch (state) {
                case IDLE:
                    state = BombState.EXPLODING;
                    secLeftToExpl = 1.0f;
                    break;
                case EXPLODING:
                    state = BombState.DELETING;
                default:
                    break;
            }
    }

    public void draw(GraphicsContext gContext) {
        switch (state) {
            case EXPLODING:
            case IDLE:
                gContext.setFill(Color.RED);
                gContext.fillOval(pos.getX(), pos.getY(), SIZE, SIZE);
                gContext.setStroke(Color.BLACK);
                gContext.setLineWidth(2);
                gContext.strokeOval(pos.getX(), pos.getY(), SIZE, SIZE);
            default:
                break;
        }
    }

    public boolean isDeleting() {
        return state == BombState.DELETING;
    }
}
