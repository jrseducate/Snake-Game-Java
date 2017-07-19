package snakegame;

import javax.swing.*;

/**
 * The main Class for the _SnakeGame_
 *
 * @author Jeremy
 */
public class SnakeGame
{

    /**
     * The _main_ method called
     *
     * @param args
     */
    public static void main(String[] args)
    {
        // Seperate thread used to run the game
        Runnable runnable = new Runnable()
        {
            GameState gameState;

            @Override
            public void run()
            {
                gameState = new GameState();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }
}
