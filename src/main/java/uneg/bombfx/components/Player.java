package uneg.bombfx.components;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import uneg.bombfx.components.InputHandler.InputOrder;
import uneg.bombfx.networking.Client;

/**
 * Player
 */
public class Player {
    private int id;
    private Point2D pos;
    private Point2D lastPos = new Point2D(0, 0);
    private Point2D size;
    private InputHandler inputHandler;

    public Player(int id) {
        this.id = id;
        switch (id) {
            case 0:
                pos = new Point2D(48 + 16, 48 + 16);
                break;
            case 1:
                pos = new Point2D(48 * 9 + 16, 48 + 16);
                break;
            case 2:
                pos = new Point2D(48 + 16, 48 * 9 + 16);
                break;
            case 3:
                pos = new Point2D(48 * 9 + 16, 48 * 9 + 16);
                break;
            default:
                pos = new Point2D(0, 0);
                break;
        }
        // pos = new Point2D((id % 2) * (48 * 8), (id / 3) * (48 * 8));
        size = new Point2D(32, 32);
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

        double x = (left) ? -1.0 : (right) ? 1.0 : 0;
        double y = (up) ? -1.0 : (down) ? 1.0 : 0;
        Point2D dir = new Point2D(x, y).normalize();
        pos = pos.add(dir.multiply(3.0));
    }

    public void draw(GraphicsContext gContext) {
        Point2D realPos = new Point2D(pos.getX() - size.getX() / 2, pos.getY() - size.getY() / 2);

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
        gContext.rect(realPos.getX(), realPos.getY(), size.getX(), size.getY());
        gContext.closePath();
        gContext.fill();
        gContext.setStroke(Color.BLACK);
        gContext.setLineWidth(1);
        gContext.stroke();
    }

    public void sync(Client client) {
        if (lastPos != null && pos.distance(lastPos) > 0.5) {
            lastPos = pos;
            client.sendSyncPPos(pos);
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

    public Point2D getSize() {
        return size;
    }

    public void setSize(Point2D size) {
        this.size = size;
    }
}
