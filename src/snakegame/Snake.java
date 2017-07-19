package snakegame;

import java.awt.*;
import java.util.LinkedList;

/**
 * Used as the player of _SnakeGame_
 *
 * @author Jeremy
 */
public class Snake
{

    private Color color;
    private Dimension direction;

    private int desiredLength;
    private LinkedList<GameObject> segments;
    private Point newSegmentPosition;

    /**
     * Creates a _Snake_ with a _position_, _color_, and _direction_;
     *
     * The _position_ is the position in cells of the _Snake_; The _color_ is
     * the color of the _segments_; The _direction_ is the initial direction of
     * the snake;
     *
     * @param position
     * @param color
     * @param direction
     */
    public Snake(Point position,
                 Color color,
                 Dimension direction)
    {
        this.newSegmentPosition = position;
        this.segments = new LinkedList();
        SetColor(color);
        SetDirection(direction);

        desiredLength = 1;
        AddSegment();
    }

    /**
     * Faces the _Snake_ in the _direction_ specified;
     *
     * @param direction
     */
    public final void SetDirection(Dimension direction)
    {
        if (this.direction != null)
        {
            int checkX = this.direction.width + direction.width;
            int checkY = this.direction.height + direction.height;
            if (checkX != 0 || checkY != 0)
            {
                this.direction = direction;
            }
        }
        else
        {
            this.direction = direction;
        }
    }

    /**
     * Colors the _Snake_ in the _color_ specified;
     *
     * @param color
     */
    public final void SetColor(Color color)
    {
        this.color = color;
    }

    /**
     * Moves the _Snake_ in whichever direction it's facing, and moves all
     * segments with it;
     */
    public void Move()
    {
        GameObject head = segments.get(0);
        assert (head != null);
        assert (direction != null);

        Point nextPosition = new Point();
        nextPosition.x = head.position.x + direction.width;
        nextPosition.y = head.position.y + direction.height;

        Point prevSegmentPosition = null;
        for (int i = 0;
             i < segments.size();
             i++)
        {
            GameObject segment = segments.get(i);

            prevSegmentPosition = new Point();
            prevSegmentPosition.x = segment.position.x;
            prevSegmentPosition.y = segment.position.y;

            segment.position = nextPosition;
            nextPosition = prevSegmentPosition;
        }

        assert (prevSegmentPosition != null);
        newSegmentPosition = prevSegmentPosition;
    }

    /**
     * Adds a new segment onto the tail of the _Snake_;
     */
    public final void AddSegment()
    {
        assert (newSegmentPosition != null);
        segments.add(new GameObject(newSegmentPosition, color));
        newSegmentPosition = null;
    }

    /**
     * Adds a new segment if there are more segments desired
     */
    public void AddSegmentIfNeeded()
    {
        if (segments.size() < desiredLength)
        {
            AddSegment();
        }
    }

    /**
     * Returns the _segments_ of the _Snake_
     *
     * @return
     */
    public LinkedList<GameObject> GetSegments()
    {
        return segments;
    }

    /**
     * Returns the position of the _Snake_'s head
     *
     * @return
     */
    public Point GetHeadPosition()
    {
        GameObject head = segments.get(0);

        Point result = new Point();

        result.x = head.position.x;
        result.y = head.position.y;

        return result;
    }

    /**
     * Returns the direction of the _Snake_'s heading
     *
     * @return
     */
    public Dimension GetDirection()
    {
        return direction;
    }

    /**
     * Returns true if the _Snake_ is hitting itself, false otherwise
     *
     * @return
     */
    public boolean CheckHittingItself()
    {
        boolean result = false;

        if (segments.size() >= 2)
        {
            Point headPosition = GetHeadPosition();

            for (int i = 1;
                 i < segments.size() && !result;
                 i++)
            {
                GameObject segment = segments.get(i);

                if (segment.GetPosition().equals(headPosition))
                {
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * Increases the _desiredLength_ of the _Snake_
     *
     */
    void IncreaseDesiredLength(int additionalLength)
    {
        desiredLength += additionalLength;
    }

    /**
     * Returns true if the _Snake_ is going to pass an edge, false otherwise
     *
     * @return
     */
    boolean HittingEdge(int columns,
                        int rows)
    {
        boolean result = false;

        Point headPosition = GetHeadPosition();

        if ((headPosition.x < 0 || headPosition.x >= columns)
                || (headPosition.y < 0 || headPosition.y >= rows))
        {
            result = true;
        }

        return result;
    }

}
