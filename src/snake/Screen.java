/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Dawid
 */
public class Screen extends JPanel implements Runnable {

    //
    public static final int WIDTH = 800, HEIGHT = 800, blockSize = 20;
    private Thread thread;
    private int ticks = 0;
    private movementHandler key;

    // Game state
    private boolean running = false;

    // Variables that are defying snake
    private BodyPart b;
    private ArrayList<BodyPart> snakeBody;
    private int snakeSize = 4;
    private int xC, yC;

    // Snake directions
    private boolean right = true, left = false, up = false, down = false;

    //Food
    private Food food;
    private ArrayList<Food> foodList;
    private Random random;

    public Screen() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);

        key = new movementHandler();
        addKeyListener(key);

        snakeBody = new ArrayList<>();
        foodList = new ArrayList<>();
        random = new Random();
        // Random position of the snake
        xC = random.nextInt((WIDTH / blockSize) - 1);
        yC = random.nextInt((WIDTH / blockSize) - 1);
        start();
    }

    public void start() {
        running = true;
        thread = new Thread(this, "Game");
        thread.start();
    }

    public void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Screen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tick() {
        createSnake();
        createFood();
        checkFoodCollected();
        checkBodyCollisions();
        checkBorderCollisions();
        ticks++;
        if (ticks > 300000) {
            if (right) {
                xC++;
            }
            if (left) {
                xC--;
            }
            if (up) {
                yC--;
            }
            if (down) {
                yC++;
            }

            ticks = 0;
            b = new BodyPart(xC, yC, blockSize);
            snakeBody.add(b);

            if (snakeBody.size() > snakeSize) {
                snakeBody.remove(0);
            }
        }
    }

    public void createSnake() {
        if (snakeBody.size() == 0) {
            b = new BodyPart(xC, yC, blockSize);
            snakeBody.add(b);
        }
    }
    public void createFood(){
        if (foodList.size() == 0) {
            int x = random.nextInt((WIDTH / blockSize) - 1);
            int y = random.nextInt((WIDTH / blockSize) - 1);
            food = new Food(x, y, blockSize);
            foodList.add(food);
        }
    }
    public void checkFoodCollected(){
        for (int i = 0; i < foodList.size(); i++) {
            if (xC == foodList.get(i).getxCoordinate() && yC == foodList.get(i).getyCoordinate()) {
                snakeSize++;
                foodList.remove(i);
                i--;
            }
        }
    }
    public void checkBorderCollisions(){
        if (xC < 0 || xC > (WIDTH / blockSize) - 1 || yC < 0 || yC > (WIDTH / blockSize) - 1) {
            stop();
        }
    }
    public void checkBodyCollisions(){
        for (int i = 0; i < snakeBody.size(); i++) {
            if (xC == snakeBody.get(i).getxCoordinate() && yC == snakeBody.get(i).getyCoordinate()) {
                if (i != snakeBody.size() - 1) {
                    stop();
                }
            }
        }
    }
    public void paint(Graphics g) {
        g.clearRect(0, 0, WIDTH, HEIGHT);
        // Fill background
        g.setColor(new Color(3421752));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw snakeBody;
        g.setColor(new Color(16737792));
        for (int i = 0; i < snakeBody.size(); i++) {
            snakeBody.get(i).draw(g);
        }

        // Draw food
        for (int i = 0; i < foodList.size(); i++) {
            foodList.get(i).draw(g);
        }
        // Draw x-axis grid 
        g.setColor(new Color(1052945));
        for (int i = 0; i < WIDTH / blockSize; i++) {
            g.drawLine(i * blockSize, 0, i * blockSize, HEIGHT);
        }

        // Draw y-axis grid 
        for (int i = 0; i < HEIGHT / blockSize; i++) {
            g.drawLine(0, i * blockSize, WIDTH, i * blockSize);
        }

    }

    //Runnable class abstract method
    @Override
    public void run() {
        while (running) {
            tick();
            repaint();
        }
    }

    //Snake steering
    private class movementHandler implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    if (!up) {
                        down = true;
                        left = false;
                        right = false;
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (!down) {
                        up = true;
                        right = false;
                        left = false;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (!right) {
                        down = false;
                        up = false;
                        left = true;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (!left) {
                        down = false;
                        up = false;
                        right = true;
                    }
                    break;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

    }
}
