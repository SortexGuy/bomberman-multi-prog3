package uneg.bombfx.components;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import uneg.bombfx.App;
import uneg.bombfx.components.InputHandler.InputOrder;
import uneg.bombfx.networking.Client;

/**
 * Player
 */
public class Player extends Object {
    private final double SPEED = 200.0;
    private final int SIZE = 28;
    private final float BOMB_TIMEOUT = 2.0f;
    private int id;
    private Point2D pos;
    private Point2D lastPos = new Point2D(0, 0);
    private Point2D lastDir = new Point2D(0, 0);
    private Point2D facing = new Point2D(0, 1);
    private double lastDelta = 0.0;
    private double bombTimer = 0.0;
    private InputHandler inputHandler;
    private LevelGrid levelGrid;

    public Player(int id, LevelGrid levelGrid) {
        this.id = id;
        this.levelGrid = levelGrid;
        this.pos = this.levelGrid.getStartPos(id).subtract(SIZE / 2, SIZE / 2);
        inputHandler = new InputHandler();
    }

    public void handleInputPressed(KeyEvent e) {
        inputHandler.inputPressed(e.getCode());
    }

    public void handleInputReleased(KeyEvent e) {
        inputHandler.inputReleased(e.getCode());
    }

    public void update(double delta) {
        boolean up = inputHandler.getInput(InputOrder.UP).pressed;
        boolean down = inputHandler.getInput(InputOrder.DOWN).pressed;
        boolean left = inputHandler.getInput(InputOrder.LEFT).pressed;
        boolean right = inputHandler.getInput(InputOrder.RIGHT).pressed;
        boolean bomb = inputHandler.getInput(InputOrder.BOMB).pressed;

        // Movement
        double x = (left) ? -1.0 : (right) ? 1.0 : 0;
        double y = (up) ? -1.0 : (down) ? 1.0 : 0;
        Point2D dir = new Point2D(x, y).normalize();
        lastDir = dir;
        lastDelta = delta;
        Point2D vel = dir.multiply(delta * SPEED);
        pos = pos.add(vel);
        if (dir.getX() == 0 || dir.getY() == 0) {
            facing = dir;
        }

        // Collision
        Point2D collFrom = levelGrid.checkPColl(new Rectangle(pos.getX(), pos.getY(), SIZE, SIZE));
        if (collFrom == Point2D.ZERO)
            return;

        Point2D collDir = collFrom.subtract(pos).normalize();
        if (lastDir.distance(collDir) <= 45.0f) {
            pos = pos.subtract(lastDir.multiply(lastDelta * SPEED));
            lastDir = Point2D.ZERO;
        }

        // Bomb Button Pressing
        if (bomb && bombTimer <= 0) {
            Point2D bombPos = pos.add(SIZE / 2.0, SIZE / 2.0).add(facing.multiply(32.0));
            if (levelGrid.getBombPos(bombPos) != Point2D.ZERO) {
                App.getGameEngine().addBomb(bombPos, id);
                bombTimer = BOMB_TIMEOUT;
            }
        }
    }

    public void draw(GraphicsContext gContext) {
        Color c;
        switch (id) {
            case 0:
                c = Color.ORANGE;
                break;
            case 1:
                c = Color.RED;
                break;
            case 2:
                c = Color.BLUE;
                break;
            case 3:
                c = Color.GREEN;
                break;
            default:
                c = Color.YELLOW;
                break;
        }
        gContext.setFill(c);
        // gContext.fillRect(realPos.getX(), realPos.getY(), size.getX(), size.getY());
        gContext.beginPath();
        gContext.rect(pos.getX(), pos.getY(), SIZE, SIZE);
        gContext.closePath();
        gContext.fill();
        gContext.setStroke(Color.BLACK);
        gContext.setLineWidth(1);
        gContext.stroke();

        // Facing debug
        Point2D center = pos.add(SIZE / 2.0, SIZE / 2.0);
        gContext.beginPath();
        gContext.moveTo(center.getX(), center.getY());
        gContext.lineTo(center.getX() + facing.getX() * 32.0, center.getY() + facing.getY() * 32.0);
        gContext.closePath();
        gContext.setStroke(Color.BLACK);
        gContext.setLineWidth(4);
        gContext.stroke();
    }

    public void sync(Client client) {
        if (lastPos != null && pos.distance(lastPos) > 0.5) {
            lastPos = pos;
            // client.sendSyncPPos(pos);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Point2D getPos() {
        return pos;
    }

    public void setPos(Point2D pos) {
        if (pos.getX() == 0)
            pos = new Point2D(this.pos.getX(), pos.getY());
        if (pos.getY() == 0)
            pos = new Point2D(pos.getX(), this.pos.getY());
        this.pos = pos;
    }
}
