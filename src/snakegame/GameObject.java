package snakegame;

import java.awt.*;

/**
 * Used as a base class for all objects in the game;
 *
 * @author Jeremy
 */
public class GameObject
{

    static private Dimension CELL_SIZE = null;
    static private float CELL_INSET = 0.1f;

    static public void SetCellSize(int width,
                                   int height)
    {
        GameObject.CELL_SIZE = new Dimension(width, height);
    }

    static public void SetCellSize(Dimension cellSize)
    {
        GameObject.SetCellSize(cellSize.width, cellSize.height);
    }

    static public Dimension GetCellSize()
    {
        return GameObject.CELL_SIZE;
    }

    protected Point position;
    protected Dimension size;
    protected Color color;

    /**
     * Creates a _GameObject_ with a _position_, and _color_;
     *
     * The _position_ is the position in cells of the _GameObject_; The _color_
     * is the color of the rectangle drawn;
     *
     * @param position
     * @param color
     */
    public GameObject(Point position,
                      Color color)
    {
        this.position = position;
        this.size = CELL_SIZE;
        this.color = color;
    }

    /**
     * Returns the _position_ of the _GameObject_
     *
     * @return
     */
    public Point GetPosition()
    {
        return position;
    }

    /**
     * Returns the screen position of the _GameObject_;
     *
     * @return
     */
    public Point GetScreenPosition()
    {
        Point screenPosition = new Point();

        screenPosition.x = position.x * CELL_SIZE.width;
        screenPosition.y = position.y * CELL_SIZE.height;

        return screenPosition;
    }

    /**
     * Paints to the _Graphics_ of a screen;
     *
     * @param g
     */
    public void Paint(Graphics g)
    {
        Point screenPosition = GetScreenPosition();

        g.setColor(color);

        int insetWidth = Math.round(CELL_INSET * (float) CELL_SIZE.width);
        int insetHeight = Math.round(CELL_INSET * (float) CELL_SIZE.height);

        g.fillRect(screenPosition.x + insetWidth, screenPosition.y + insetHeight,
                size.width - insetWidth, size.height - insetHeight);
    }

}
