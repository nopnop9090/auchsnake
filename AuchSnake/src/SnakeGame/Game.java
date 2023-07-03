package SnakeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("serial")
public class Game extends JPanel {

    private final int BOX_SIZE = 32;
    private final int GAME_SIZE = 16;
    private ArrayList<Point> snake;
    private Point fruit;
    private int direction;

    public Game() {
        setPreferredSize(new Dimension(BOX_SIZE * GAME_SIZE, BOX_SIZE * GAME_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        direction = 0;
                        break;
                    case KeyEvent.VK_RIGHT:
                        direction = 1;
                        break;
                    case KeyEvent.VK_DOWN:
                        direction = 2;
                        break;
                    case KeyEvent.VK_LEFT:
                        direction = 3;
                        break;
                }
            }
        });
        initGame();
    }

    private void initGame() {
        snake = new ArrayList<>();
        snake.add(new Point(GAME_SIZE / 2, GAME_SIZE / 2));
        spawnFruit();
        direction = -1;
        new Timer(200, e -> {
            if (!gameOver()) {
                updateGame();
                repaint();
            } else {
                ((Timer)e.getSource()).stop();
                JOptionPane.showMessageDialog(this, "Game Over!", "Game Over!", JOptionPane.ERROR_MESSAGE);
                initGame();
            }
        }).start();
    }

    private void spawnFruit() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(GAME_SIZE);
            y = random.nextInt(GAME_SIZE);
        } while (snake.contains(new Point(x, y)));
        fruit = new Point(x, y);
    }

    private boolean gameOver() {
        Point head = snake.get(snake.size() - 1);
        
        // Check if the head is out of bounds
        if (head.x < 0 || head.y < 0 || head.x >= GAME_SIZE || head.y >= GAME_SIZE) {
            return true;
        }
        
        // Check if the head collides with the body (excluding the last segment)
        for (int i = 0; i < snake.size() - 1; i++) {
            if (head.equals(snake.get(i))) {
                return true;
            }
        }
        
        return false;
    }

    private void updateGame() {
        if (direction != -1) {
            Point head = snake.get(snake.size() - 1);
            Point newPoint;
            switch (direction) {
                case 0: // UP
                    newPoint = new Point(head.x, head.y - 1);
                    break;
                case 1: // RIGHT
                    newPoint = new Point(head.x + 1, head.y);
                    break;
                case 2: // DOWN
                    newPoint = new Point(head.x, head.y + 1);
                    break;
                default: // LEFT
                    newPoint = new Point(head.x - 1, head.y);
            }
            if (snake.contains(newPoint)) {
                initGame();
                return;
            }
            snake.add(newPoint);
            if (newPoint.equals(fruit)) {
                spawnFruit();
            } else {
                snake.remove(0);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw the fruit
        g.setColor(getRandomFruitColor());
        g.fillRect(fruit.x * BOX_SIZE + (BOX_SIZE - 20) / 2, fruit.y * BOX_SIZE + (BOX_SIZE - 20) / 2, 20, 20);
        
        // Draw the snake
        g.setColor(Color.GREEN);
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            if (i == snake.size() - 1) {
                // Draw the head as a red circle
                g.setColor(Color.RED);
                g.fillOval(p.x * BOX_SIZE, p.y * BOX_SIZE, BOX_SIZE, BOX_SIZE);
            } else {
                // Draw the body as green circles
                g.setColor(Color.GREEN);
                g.fillOval(p.x * BOX_SIZE + (BOX_SIZE - 20) / 2, p.y * BOX_SIZE + (BOX_SIZE - 20) / 2, 20, 20);
            }
        }
    }

    private Color getRandomFruitColor() {
        Random random = new Random();
        int colorIndex = random.nextInt(6); // Generate a random number between 0 and 5
        
        switch (colorIndex) {
            case 0:
                return Color.YELLOW;
            case 1:
                return Color.ORANGE;
            case 2:
                return Color.CYAN;
            case 3:
                return Color.WHITE;
            case 4:
                return Color.PINK;
            default:
                return Color.MAGENTA;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Auch Snake.");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new Game());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
