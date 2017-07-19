package snakegame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author Jeremy
 */
public class GameWindow extends JFrame
{

    private LinkedList<Integer> pressedKeyCodes;

    KeyListener keyListener = new KeyListener()
    {

        @Override
        public void keyTyped(KeyEvent e)
        {
        }

        @Override
        public void keyReleased(KeyEvent e)
        {
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            pressedKeyCodes.add(e.getKeyCode());
        }
    };

    private boolean isOpen;
    private boolean isPaused;

    WindowListener windowListener = new WindowListener()
    {
        @Override
        public void windowOpened(WindowEvent e)
        {
        }

        @Override
        public void windowClosing(WindowEvent e)
        {
            isOpen = false;
        }

        @Override
        public void windowClosed(WindowEvent e)
        {
        }

        @Override
        public void windowIconified(WindowEvent e)
        {
        }

        @Override
        public void windowDeiconified(WindowEvent e)
        {
        }

        @Override
        public void windowActivated(WindowEvent e)
        {
            isPaused = false;
        }

        @Override
        public void windowDeactivated(WindowEvent e)
        {
            isPaused = true;
        }

    };

    private LinkedList<GameObject> gameObjects;
    private LinkedList<LinkedList> gameObjectLists;

    public GameWindow() throws HeadlessException
    {
        super.addKeyListener(keyListener);
        super.addWindowListener(windowListener);
        super.setResizable(false);

        pressedKeyCodes = new LinkedList();
        gameObjects = new LinkedList();
        gameObjectLists = new LinkedList();
        isOpen = true;
        isPaused = false;
    }

    public boolean GetIsOpen()
    {
        return isOpen;
    }

    public boolean GetIsPaused()
    {
        return isPaused;
    }

    public int PopPressedKeyCode()
    {
        int result = -1;
        if (pressedKeyCodes.size() > 0)
        {
            result = pressedKeyCodes.get(0);
            pressedKeyCodes.remove(0);
        }
        return result;
    }

    public int PeekPressedKeyCode()
    {
        int result = -1;
        if (pressedKeyCodes.size() > 0)
        {
            result = pressedKeyCodes.get(0);
        }

        return result;
    }

    public void AddGameObject(GameObject gameObject)
    {
        assert (gameObject != null);
        gameObjects.add(gameObject);
    }

    public void AddGameObjects(LinkedList gameObjects)
    {
        assert (gameObjects != null);
        gameObjectLists.add(gameObjects);
    }

    BufferedImage bufferedImage;
    Graphics2D graphics2d;
    Rectangle bounds;

    public void DrawGameObjects(int gamePanelWidth,
                                int gamePanelHeight,
                                int guiPanelHeight)
    {
        for (int i = 0;
             i < gameObjects.size();
             i++)
        {
            GameObject gameObject = gameObjects.get(i);
            gameObject.Paint(graphics2d);
        }

        for (int i = 0;
             i < gameObjectLists.size();
             i++)
        {
            LinkedList gameObjectList = gameObjectLists.get(i);
            for (int j = 0;
                 j < gameObjectList.size();
                 j++)
            {
                Object object = gameObjectList.get(j);
                GameObject gameObject = (GameObject) object;
                gameObject.Paint(graphics2d);
            }
        }
    }

    public void PaintToScreen()
    {
        Graphics g = getContentPane().getGraphics();
        g.drawImage(
                bufferedImage,
                bufferedImage.getMinX(), bufferedImage.getMinY(),
                bufferedImage.getWidth(), bufferedImage.getHeight(),
                null);
    }

    public void ClearScreen()
    {
        GraphicsConfiguration gc = getContentPane().getGraphicsConfiguration();
        assert (gc != null);
        bufferedImage = gc.createCompatibleImage(
                getContentPane().getWidth(),
                getContentPane().getHeight());

        graphics2d = bufferedImage.createGraphics();
        bounds = new Rectangle(
                bufferedImage.getMinX(),
                bufferedImage.getMinY(),
                bufferedImage.getWidth(),
                bufferedImage.getHeight());

        graphics2d.setColor(Color.black);
        graphics2d.clearRect(
                bounds.x, bounds.y,
                bounds.width, bounds.height);
    }

    void SetInnerWindowSize(int width,
                            int height)
    {
        Insets insets = getInsets();
        int insetWidth = insets.left + insets.right;
        int insetHeight = insets.top + insets.bottom;

        setBounds(0, 0, width + insetWidth, height + insetHeight);
    }

    void SetInnerWindowSize(Dimension size)
    {
        SetInnerWindowSize(size.width, size.height);
    }

    public void DrawScore(int gamePanelWidth,
                          int gamePanelHeight,
                          int guiPanelHeight,
                          int fontSize,
                          int score,
                          String songName)
    {
        Rectangle rect = new Rectangle(0, 0, gamePanelWidth, guiPanelHeight);
        Font font = Font.decode(null);
        String texta = "Score: " + Integer.toString(score);
        String textb = "Use the arrow keys to move, + and - to change size.";
        String textc = "Song Playing: '" + songName + "'";

        FontMetrics metrics = graphics2d.getFontMetrics(font);
        int xa = (rect.width - metrics.stringWidth(texta)) / 2;
        int xb = (rect.width - metrics.stringWidth(textb)) / 2;
        int xc = (rect.width - metrics.stringWidth(textc)) / 2;
        int ya = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        int yb = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        int yc = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        ya += gamePanelHeight - (guiPanelHeight / 3);
        yb += gamePanelHeight + (guiPanelHeight / 3);
        yc += gamePanelHeight + (guiPanelHeight / 8);

        graphics2d.setColor(Color.LIGHT_GRAY);
        graphics2d.fillRect(0, gamePanelWidth, gamePanelHeight, guiPanelHeight);

        graphics2d.setColor(Color.BLUE);
        graphics2d.setFont(font);
        graphics2d.drawString(texta, xa, ya);
        graphics2d.drawString(textb, xb, yb);
        graphics2d.drawString(textc, xc, yc);
    }

    public void DrawScoreAndDeath(int gamePanelWidth,
                                  int gamePanelHeight,
                                  int guiPanelHeight,
                                  int fontSize,
                                  int score,
                                  String songName)
    {
        Rectangle rect = new Rectangle(0, 0, gamePanelWidth, guiPanelHeight);
        Font font = Font.decode(null);
        String texta = "You have died...";
        String textb = "Final Score: " + Integer.toString(score);
        String textc = "Press SPACE to quit, or ENTER to restart.";
        String textd = "Song Playing: '" + songName + "'";

        FontMetrics metrics = graphics2d.getFontMetrics(font);
        int xa = (rect.width - metrics.stringWidth(texta)) / 2;
        int xb = (rect.width - metrics.stringWidth(textb)) / 2;
        int xc = (rect.width - metrics.stringWidth(textc)) / 2;
        int xd = (rect.width - metrics.stringWidth(textd)) / 2;
        int ya = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        int yb = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        int yc = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        int yd = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        ya += gamePanelHeight - (guiPanelHeight / 3);
        yb += gamePanelHeight;
        yc += gamePanelHeight + (guiPanelHeight / 3);
        yd += gamePanelHeight + (guiPanelHeight / 8);

        graphics2d.setColor(Color.LIGHT_GRAY);
        graphics2d.fillRect(0, gamePanelWidth, gamePanelHeight, guiPanelHeight);

        graphics2d.setColor(Color.BLUE);
        graphics2d.setFont(font);
        graphics2d.drawString(texta, xa, ya);
        graphics2d.drawString(textb, xb, yb);
        graphics2d.drawString(textc, xc, yc);
        graphics2d.drawString(textd, xd, yd);
    }
}
