package snakegame;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;

/**
 *
 * @author Jeremy
 */
public class Food extends GameObject
{

    static private int COUNT;

    /**
     * Resets the current food count
     *
     */
    public static void ResetCount()
    {
        Food.COUNT = 0;
    }

    /**
     * Gets the current food count
     *
     * @return
     */
    public static int GetCount()
    {
        return Food.COUNT;
    }

    /**
     * Chooses a valid _Point_ to spawn food in, and spawns it;
     *
     * @param snake
     * @param foodList
     * @param columns
     * @param rows
     * @return
     */
    static public Food Spawn(Snake snake,
                             LinkedList<Food> foodList,
                             int columns,
                             int rows)
    {
        LinkedList<Point> validPoints = new LinkedList();

        // Fills the _validPoints_ with all possible _Point_s in the game
        for (int x = 0;
             x < columns;
             x++)
        {
            for (int y = 0;
                 y < rows;
                 y++)
            {
                validPoints.add(new Point(x, y));
            }
        }

        // Removes any _Point_s used by all existing _Snake_ _segment_s
        LinkedList<GameObject> snakeSegments = snake.GetSegments();
        for (int i = 0;
             i < snakeSegments.size();
             i++)
        {
            Point segmentPosition = snakeSegments.get(i).position;

            if (validPoints.contains(segmentPosition))
            {
                validPoints.remove(segmentPosition);
            }
        }

        // Removes any _Point_s used by any existing _Food_
        for (int i = 0;
             i < foodList.size();
             i++)
        {
            Point foodPosition = foodList.get(i).position;

            if (validPoints.contains(foodPosition))
            {
                validPoints.remove(foodPosition);
            }
        }

        // Picks a random _Point_ from the list of valid _Point_s
        Point validPoint = validPoints.get(
                (int) (Math.random() * (validPoints.size() - 1)));

        // Spawns a _Food_ in at the chosen _validPoint_
        Food result = new Food(validPoint, Color.WHITE);

        // Returns the spawned _Food_
        return result;
    }

    boolean eaten;

    /**
     * Creates a _Food_ with a _position_, and _color_;
     *
     * The _position_ is the position in cells of the _Food_; The _color_ is the
     * color of the _Food_;
     *
     * @param position
     * @param color
     */
    public Food(Point position,
                Color color)
    {
        super(position, color);
        this.eaten = false;
        Food.COUNT++;
    }

    /**
     * Determines if the _Snake_ is overlapping any _Food_, and if yes eats the
     * food and returns true, otherwise returns false;
     *
     * @param snake
     * @return
     */
    public boolean CheckIfEaten(Snake snake)
    {
        if (snake.GetHeadPosition().equals(this.position))
        {
            eaten = true;
            Food.COUNT--;
        }

        return eaten;
    }

}
