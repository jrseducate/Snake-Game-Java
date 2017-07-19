/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakegame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.Timer;

/**
 *
 * @author fuchs
 */
public class GameState
{

    private static GameState gameState = null;

    // Game _ActionListener_ used by the _Timer_ to update the game
    private static final ActionListener updateGameAction = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            gameState.UpdateGame();
        }
    };

    // _Timer_ used to update the game
    private static final Integer updateIntervalMs = 150;
    private static Timer updateTimer = new Timer(updateIntervalMs, updateGameAction);

    //The player's score
    private int score = 0;

    // Window used to display game
    private GameWindow window;

    // The player snake
    private Snake snake;

    // The food
    private Integer foodCountWanted;
    private LinkedList<Food> foodList;

    // Panel specifications to seperate the game from the gui
    private Integer gamePanelWidth;
    private Integer gamePanelHeight;
    private Integer guiPanelHeight;

    // The inner window size specifications
    private Integer innerWindowWidth;
    private Integer innerWindowHeight;

    private Integer rows = 20;
    private Integer columns = 20;

    private String songName = new String();

    @SuppressWarnings("LeakingThisInConstructor")
    public GameState()
    {
        gameState = this;
        InitializeGame();
        updateTimer.start();
        PlaySong();
    }

    private void InitializeGame()
    {

        // Window used to display game
        window = new GameWindow();
        window.setTitle("Snake");
        window.show();

        // Cells used to position _GameObject_s
        if (GameObject.GetCellSize() == null)
        {
            GameObject.SetCellSize(new Dimension(20, 20));
        }

        // Resizes window based on cell size
        ResizeWindow(window);

        // The player snake
        snake = new Snake(
                new Point(0, 0),
                Color.GREEN,
                new Dimension(1, 0));

        // The food
        Food.ResetCount();
        foodCountWanted = 1;
        foodList = new LinkedList();

        // Adds them to the window to be drawn
        window.AddGameObjects(snake.GetSegments());
        window.AddGameObjects(foodList);

        score = 0;

        UpdateTimerDelay(window);
    }

    /**
     * Resizes the _window_ based on the current cell size of _GameObject_
     *
     * @param window
     */
    private void ResizeWindow(GameWindow window)
    {

        // Panel specifications to seperate the game from the gui
        gamePanelWidth = columns * GameObject.GetCellSize().width;
        gamePanelHeight = rows * GameObject.GetCellSize().height;
        guiPanelHeight = gamePanelHeight / 4;

        // The inner window size specifications
        innerWindowWidth = gamePanelWidth;
        innerWindowHeight = gamePanelHeight + guiPanelHeight;

        // Sets the innter window size
        window.SetInnerWindowSize(
                innerWindowWidth,
                innerWindowHeight);
    }

    /**
     * Updates the _Timer_'s delay based on the current score
     *
     * @param window
     */
    private void UpdateTimerDelay(GameWindow window)
    {
        int newDelay = (int) Math.round((float) updateIntervalMs / (((float) score / 1000f) + 1f)) + 1;
        window.setTitle("Snake " + ((100 * updateIntervalMs / newDelay) + 1) + "% Speed");
        updateTimer.setDelay(newDelay);
    }

    /**
     * Plays any _wav_ file in the Music folder
     *
     */
    @SuppressWarnings("UseSpecificCatch")
    private void PlaySong()
    {
        // Gets the music folder
        File folder = new File("Music");

        // If the folder doesn't exist, make one
        if (!folder.isDirectory())
        {
            folder.mkdir();
        }
        else
        {
            //Filters out everything but the (.wav) files

            FilenameFilter filter = new FilenameFilter()
            {
                @Override
                public boolean accept(File dir,
                                      String name)
                {
                    String format = name.substring(name.length() - 4, name.length());
                    boolean isWaveFile = (format.equalsIgnoreCase(".wav"));

                    return isWaveFile;
                }
            };

            // Chooses a random wave file from the list of files
            File musicList[] = folder.listFiles(filter);
            if (musicList.length >= 1)
            {
                File musicChosen = musicList[(int) (Math.random() * (double) (musicList.length - 1))];
                songName = musicChosen.getName();

                // Tries to play the wave file
                try
                {
                    final Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));

                    // The listener to know where we are in the wave file
                    clip.addLineListener(new LineListener()
                    {
                        @Override
                        public void update(LineEvent event)
                        {
                            if (event.getType() == LineEvent.Type.STOP)
                            {
                                clip.close();

                                // When the wave file is over, if the window is
                                // still open, play another one
                                if (window.GetIsOpen())
                                {
                                    PlaySong();
                                }
                            }
                        }
                    });

                    // Adds the listener and starts playing
                    clip.open(AudioSystem.getAudioInputStream(musicChosen));
                    clip.start();
                }

                // Should it fail to play, it prints a stack trace
                catch (Exception exc)
                {
                    exc.printStackTrace(System.out);
                }
            }
        }
    }

    /**
     * Handles any food eaten, and spawns more if necessary
     *
     * @return
     */
    private int HandleFood()
    {
        int foodEaten = 0;

        LinkedList<Integer> foodToRemove = new LinkedList();

        // Adds any _Food_ that should be removed to the _foodToRemove_ list
        for (int i = 0;
             i < foodList.size();
             i++)
        {
            Food food = foodList.get(i);

            if (food.CheckIfEaten(snake))
            {
                snake.IncreaseDesiredLength(5);
                foodEaten++;
                foodToRemove.add(i);
            }
        }

        // Goes through and deletes anything in the _foodToRemove_ list
        for (int i = 0;
             i < foodToRemove.size();
             i++)
        {
            foodList.remove(foodToRemove.get(i).intValue());
        }

        // Adds _Food_ to the list to get it to the _foodCountWanted_
        int foodCountNeeded = foodCountWanted - Food.GetCount();
        for (int i = 0;
             i < foodCountNeeded;
             i++)
        {
            foodList.add(Food.Spawn(snake, foodList, columns, rows));
        }

        return foodEaten;
    }

    /**
     * Updates the game
     *
     */
    private void UpdateGame()
    {
        if (!window.GetIsPaused())
        {
            switch (window.PeekPressedKeyCode())
            {
                case KeyEvent.VK_ADD:
                {
                    // Increases cell size by 5
                    Dimension newCellSize = GameObject.GetCellSize();
                    newCellSize.width += 5;
                    newCellSize.height += 5;
                    // Resizes window based on cell size
                    ResizeWindow(window);
                }
                break;
                case KeyEvent.VK_SUBTRACT:
                {
                    if (GameObject.GetCellSize().width > 15
                            && GameObject.GetCellSize().height > 15)
                    {
                        // Decreases cell size by 5
                        Dimension newCellSize = GameObject.GetCellSize();
                        newCellSize.width -= 5;
                        newCellSize.height -= 5;
                        // Resizes window based on cell size
                        ResizeWindow(window);
                    }
                }
                break;
            }

            if (!snake.CheckHittingItself() && !snake.HittingEdge(columns, rows))
            {
                // Get input from the user
                switch (window.PopPressedKeyCode())
                {
                    case KeyEvent.VK_UP:
                    {
                        snake.SetDirection(new Dimension(0, -1));
                    }
                    break;

                    case KeyEvent.VK_DOWN:
                    {
                        snake.SetDirection(new Dimension(0, 1));
                    }
                    break;

                    case KeyEvent.VK_LEFT:
                    {
                        snake.SetDirection(new Dimension(-1, 0));
                    }
                    break;

                    case KeyEvent.VK_RIGHT:
                    {
                        snake.SetDirection(new Dimension(1, 0));
                    }
                    break;
                }

                // Checks if the input given will hit an edge, and if yes redirects them
                for (int i = 0; i < 2; i++)
                {
                    ;
                }

                // Moves the player
                snake.Move();

                // Handles any _Food_ eaten
                int foodEaten = HandleFood();

                snake.AddSegmentIfNeeded();

                // Updates the score by however much _Food_ was eaten
                score += foodEaten * 100;

                // Clears the _GameWindow_
                window.ClearScreen();

                // Draws all _GameObject_s in the _GameWindow_
                window.DrawGameObjects(
                        gamePanelWidth,
                        gamePanelHeight,
                        guiPanelHeight);

                // Draws the _score_ of the game
                window.DrawScore(
                        gamePanelWidth,
                        gamePanelHeight,
                        guiPanelHeight,
                        14, score, songName);

                // Paints all things drawn to the screen, and finalizes render
                window.PaintToScreen();

            }
            else
            {
                // Clears the _GameWindow_
                window.ClearScreen();

                // Draws all _GameObject_s in the _GameWindow_
                window.DrawGameObjects(
                        gamePanelWidth,
                        gamePanelHeight,
                        guiPanelHeight);

                // Draws the _score_ and death message of the game
                window.DrawScoreAndDeath(
                        gamePanelWidth,
                        gamePanelHeight,
                        guiPanelHeight,
                        14, score, songName);

                // Paints all things drawn to the screen, and finalizes render
                window.PaintToScreen();

                switch (window.PopPressedKeyCode())
                {
                    case KeyEvent.VK_SPACE:
                    {
                        System.exit(0);
                    }
                    break;

                    case KeyEvent.VK_ENTER:
                    {
                        window.dispose();
                        InitializeGame();
                    }
                }
            }
        }

        UpdateTimerDelay(window);

        // Checks if the window has been closed
        if (!window.GetIsOpen())
        {
            System.exit(0);
        }
    }

}
