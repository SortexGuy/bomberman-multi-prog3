package uneg.bombfx.components;

import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LevelGrid extends Object {
    private final int GRID_SIZE = 32;
    private final int GRID_NUM = 13;
    public ArrayList<GridCell> cells = new ArrayList<LevelGrid.GridCell>();
    private Rectangle gridRect;

    private class GridCell {
        public enum Type {
            CLEAR,
            SOLID,
            BREAKABLE,
            DANGER;
        }

        public Point2D pos;
        public Type type;
        private Color color = Color.BLACK;

        public GridCell(Point2D pos, Type type) {
            this.pos = pos;
            this.type = type;
            if (type == Type.CLEAR) {
                color = Color.GHOSTWHITE;
            }
            if (type == Type.BREAKABLE) {
                color = Color.BURLYWOOD;
            }
        }

        void draw(GraphicsContext gContext) {
            gContext.beginPath();
            gContext.rect(pos.getX(), pos.getY(), GRID_SIZE, GRID_SIZE);
            gContext.closePath();

            gContext.setFill(color);
            gContext.fill();
            gContext.setStroke(Color.WHITE);
            gContext.setLineWidth(1);
            gContext.stroke();
        }

        boolean colliding(Rectangle rect1) {
            if (type == Type.CLEAR)
                return false;

            Rectangle rect2 = getRect();
            boolean result = rect1.getBoundsInParent().intersects(rect2.getBoundsInParent());
            if (result) {
                color = Color.RED;
            } else {
                if (type == Type.BREAKABLE) {
                    color = Color.BURLYWOOD;
                } else if (type == Type.SOLID) {
                    color = Color.BLACK;
                }
            }
            return result;
        }

        boolean isInside(Point2D p) {
            Rectangle rect2 = getRect();
            return rect2.contains(p);
        }

        public Rectangle getRect() {
            return new Rectangle(pos.getX(), pos.getY(), GRID_SIZE, GRID_SIZE);
        }

        public Point2D getCenter() {
            return new Point2D(pos.getX() + GRID_SIZE / 2, pos.getY() + GRID_SIZE / 2);
        }

        // Getters and Setters
        public Point2D getPos() {
            return pos;
        }

        public void setPos(Point2D pos) {
            this.pos = pos;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }
    }

    public LevelGrid() {
        gridRect = new Rectangle(0, 0, GRID_SIZE * GRID_NUM, GRID_SIZE * GRID_NUM);
        Rectangle scrRect = new Rectangle(0, 0, 960, 540);
        gridRect.setX(scrRect.getWidth() - gridRect.getWidth() - 62);
        gridRect.setY(scrRect.getHeight() - gridRect.getHeight() - 62);

        for (int i = 0; i < GRID_NUM; i++) {
            for (int j = 0; j < GRID_NUM; j++) {
                Point2D pos = new Point2D(
                        gridRect.getX() + i * GRID_SIZE, gridRect.getY() + j * GRID_SIZE);
                GridCell.Type type = GridCell.Type.CLEAR;

                if (i == 0 || j == 0 || i == GRID_NUM - 1 || j == GRID_NUM - 1
                        || (i % 2 == 0 && j % 2 == 0)) {
                    type = GridCell.Type.SOLID;
                } else if (Math.random() < 0.4 && !isPlayerNear(pos)) {
                    type = GridCell.Type.BREAKABLE;
                }
                GridCell cell = new GridCell(pos, type);
                cells.add(cell);
            }
        }
    }

    private boolean isPlayerNear(Point2D pos) {
        for (int i = 0; i < 4; i++) {
            Point2D start = getStartPos(i);
            if (Math.abs(pos.getX() - start.getX()) <= GRID_SIZE
                    && Math.abs(pos.getY() - start.getY()) <= GRID_SIZE) {
                return true;
            }
        }
        return false;
    }

    public void update(double delta) {
    }

    public void draw(GraphicsContext gContext) {
        gContext.setStroke(Color.YELLOW);
        gContext.setLineWidth(2);
        gContext.strokeRect(
                gridRect.getX(), gridRect.getY(), gridRect.getWidth(), gridRect.getHeight());

        cells.forEach(cell -> {
            cell.draw(gContext);
        });
    }

    public Point2D checkPColl(Rectangle rect) {
        Point2D result = Point2D.ZERO;
        for (GridCell cell : cells) {
            if (cell.colliding(rect)) {
                result = cell.getCenter();
                break;
            }
        }
        return result;
    }

    public Point2D getStartPos(int id) {
        Point2D pos = new Point2D(0, 0);
        int idx = 0;
        switch (id) {
            case 0:
                idx = 1 + 1 * GRID_NUM;
                if (cells.size() <= idx)
                    return pos;
                pos = cells.get(idx).getCenter();
                break;
            case 1:
                // idx = (GRID_NUM - 2) + 1 * (GRID_NUM - 1);
                // pos = cells.get(idx).getCenter();
                break;
            case 2:
                // idx = 1 + (GRID_NUM - 2) * (GRID_NUM - 1);
                // pos = cells.get(idx).getCenter();
                break;
            case 3:
                // idx = (GRID_NUM - 2) + (GRID_NUM - 2) * (GRID_NUM - 1);
                // pos = cells.get(idx).getCenter();
            default:
                break;
        }
        return pos;
    }

    public Point2D getBombPos(Point2D pos) {
        Point2D ret_pos = Point2D.ZERO;
        for (GridCell cell : cells) {
            if (!cell.isInside(pos))
                continue;

            if (cell.getType() == GridCell.Type.CLEAR) {
                ret_pos = cell.getCenter();
                break;
            }
        }
        return ret_pos;
    }
}
