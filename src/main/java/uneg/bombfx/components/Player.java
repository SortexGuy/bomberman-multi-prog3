package uneg.bombfx.components;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import uneg.bombfx.networking.Client;

/**
 * Player
 */
public class Player {
    private int id;
    private Point2D pos;
    private Point2D size;

    public Player(int id) {
        this.id = id;
        // pos = new Point2D((id % 2) * (48 * 8), (id / 3) * (48 * 8));
        pos = new Point2D(0, 0);
        size = new Point2D(32, 32);
    }

    public void handleInput(KeyEvent e, Client connection) {
        if (id != 0)
            return;
        double x = (e.getCode() == KeyCode.A) ? -1.0 : (e.getCode() == KeyCode.D) ? 1.0 : 0;
        double y = (e.getCode() == KeyCode.W) ? -1.0 : (e.getCode() == KeyCode.S) ? 1.0 : 0;
        Point2D dir = new Point2D(x, y).normalize();
        pos = pos.add(dir.multiply(5.0));
        connection.sendSyncPPos(pos);
    }

    public void draw(GraphicsContext gContext) {
        Point2D realPos = new Point2D(pos.getX() - size.getX() / 2, pos.getY() - size.getY() / 2);

        gContext.setFill(id == 1 ? Color.RED : Color.BLUE);
        gContext.fillRect(realPos.getX(), realPos.getY(), size.getX(), size.getY());
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
        this.pos = pos;
    }

    public Point2D getSize() {
        return size;
    }

    public void setSize(Point2D size) {
        this.size = size;
    }
}
