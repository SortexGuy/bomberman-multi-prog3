package uneg.bombfx.components;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

/**
 * Player
 */
public class Player {
    private int id;
    private Point2D pos;
    private Point2D size;

    public Player(int id) {
        this.id = id;
        pos = new Point2D(0, 0);
        size = new Point2D(24, 24);
    }

    public void handleInput(KeyEvent e) {
        if (id != 0)
            return;
        double x = (e.getCode() == KeyCode.A) ? -1.0 : (e.getCode() == KeyCode.D) ? 1.0 : 0;
        double y = (e.getCode() == KeyCode.W) ? -1.0 : (e.getCode() == KeyCode.S) ? 1.0 : 0;
        Point2D dir = new Point2D(x, y).normalize();
        pos = pos.add(dir.multiply(5.0));
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
}
