package SnakeGame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;
import java.util.Random;
import SnakeGame.StarField;

@SuppressWarnings("serial")
public class Game extends StarField {

    private final int BOX_SIZE = 32;		// Grösse einer "Kiste" des Spielfelds
    private final int GAME_SIZE = 16;		// Anzahl Kisten horizontal und vertikal
    private ArrayList<Point> snake;			// Die Schlange, ein Array von Punkten
    private Point fruit;					// Position der Frucht auf dem Spielfeld
    private int direction;					// Aktuelle Bewegungsrichtung der Schlange
    private Color fruitColor;				// Farbe der aktuell aktiven Frucht
    private int score;						// Punktestand
    private Timer timer;					// Timer für den Spielablauf
    private boolean gameOver;				// Spielstatus
    
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
        
        timer = new Timer(200, e -> {
            if (!gameOver) {
                updateGame();
                repaint();
            } 
        });
        
        initGame();
    }

    /**
     * Initialisiert das Spiel.
     */    
    private void initGame() {
    	gameOver = false;
    	if(timer != null && timer.isRunning()) {
    		return;
    	}
        snake = new ArrayList<>();
        snake.add(new Point(GAME_SIZE / 2, GAME_SIZE / 2));	// Startposition der Schlange in der Mitte des Spielfelds
        spawnFruit();
        direction = -1;
        
        timer.start();
    }

    /**
     * Generiert eine neue Frucht an einer zufälligen Position auf dem Spielfeld.
     * Die Position darf nicht von der Schlange eingenommen werden.
     */
    private void spawnFruit() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(GAME_SIZE); // Zufällige x-Koordinate generieren
            y = random.nextInt(GAME_SIZE); // Zufällige y-Koordinate generieren
        } while (snake.contains(new Point(x, y))); // Überprüfen, ob die generierte Position bereits von der Schlange eingenommen wird
        fruit = new Point(x, y); // Position der Frucht festlegen
        fruitColor = getRandomFruitColor();
    }

    /**
     * Beendet das Spiel.
     * Stoppt den Timer und zeigt eine Meldung an, dass das Spiel vorbei ist.
     * Initialisiert dann ein neues Spiel.
     */    
    private void gameOver() {
    	gameOver = true;
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over!", "Game Over!", JOptionPane.ERROR_MESSAGE);
        initGame();
    }
    
    /**
     * Überprüft, ob ein Punkt außerhalb des Spielfelds liegt.
     *
     * @param point der zu überprüfende Punkt
     * @return true, wenn der Punkt außerhalb des Spielfelds liegt, ansonsten false
     */    
    private boolean isOutOfBounds(Point point) {
        return point.x < 0 || point.y < 0 || point.x >= GAME_SIZE || point.y >= GAME_SIZE;
    }

    /*private boolean checkBodyCollision(Point head) {
        for (int i = 0; i < snake.size() - 1; i++) {
            if (head.equals(snake.get(i))) {
                return true;
            }
        }
        return false;
    }*/

    /**
     * Überprüft, ob ein gegebener Punkt mit dem Körper der Schlange kollidiert.
     *
     * @param newPoint der zu überprüfende Punkt
     * @return true, wenn der Punkt mit dem Körper der Schlange kollidiert, ansonsten false
     */    
    private boolean checkSnakeCollision(Point newPoint) {
        return snake.contains(newPoint);
    }
    
    
    /**
     * Aktualisiert den Spielzustand.
     * Wird vom Timer in regelmäßigen Abständen aufgerufen.
     */    
    private void updateGame() {
    	if (!gameOver && direction != -1) {
    		Point head = snake.get(snake.size() - 1);	// Kopf der Schlange holen
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
            
            if (checkSnakeCollision(newPoint) || isOutOfBounds(newPoint)) {
                gameOver();
                return;
            }            
           
            snake.add(newPoint);	// Neuen Punkt (Kopf) zur Schlange hinzufügen
            if (newPoint.equals(fruit)) {	
            	score += getFruitScore();	// Punktestand erhöhen, wenn der Kopf die Frucht erreicht
                spawnFruit();	// Neue Frucht erzeugen
            } else {
                snake.remove(0);	// Schwanz der Schlange entfernen, da sie sich nicht zur Frucht bewegt hat
            }
        }
    }

    /**
     * Gibt den Punktwert der aktuellen Frucht zurück.
     *
     * @return der Punktwert der aktuellen Frucht
     */
    private int getFruitScore() {
        if (fruitColor.equals(Color.YELLOW)) {
            return 1;
        } else if (fruitColor.equals(Color.ORANGE)) {
            return 2;
        } else if (fruitColor.equals(Color.CYAN)) {
            return 3;
        } else if (fruitColor.equals(Color.WHITE)) {
            return 4;
        } else if (fruitColor.equals(Color.PINK)) {
            return 5;
        } else if (fruitColor.equals(Color.MAGENTA)) {
            return 6;
        } else {
            return 0;
        }
    }
    
    /**
     * Überschreibt die paintComponent-Methode der JPanel-Klasse.
     * Zeichnet das Spielfeld, die Frucht und die Schlange auf den Bildschirm.
     *
     * @param g der Graphics-Kontext zum Zeichnen
     */
    @Override
     public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Spielfeld-Kisten malen
		g.setColor(Color.DARK_GRAY);
		for (int x = 0; x < GAME_SIZE; x++) {
			for (int y = 0; y < GAME_SIZE; y++) {
				g.drawRect(x * BOX_SIZE, y * BOX_SIZE, BOX_SIZE, BOX_SIZE);
			}
		}

		// Frucht zeichnen
        g.setColor(fruitColor);
        g.fillRect(fruit.x * BOX_SIZE + (BOX_SIZE - 20) / 2, fruit.y * BOX_SIZE + (BOX_SIZE - 20) / 2, 20, 20);

        // Testweise den Punktewert der aktuellen Frucht ausgeben: 
        g.setColor(Color.YELLOW);
        String tmp = "" + getFruitScore();
        g.drawString(tmp, fruit.x * BOX_SIZE + (BOX_SIZE - 20) / 2, fruit.y * BOX_SIZE + (BOX_SIZE - 20) / 2);

        
        // Schlange zeichnen
        g.setColor(Color.GREEN);
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            if (i == snake.size() - 1) {
            	// Kopf als roten Kreis zeichnen
                g.setColor(Color.RED);
                g.fillOval(p.x * BOX_SIZE, p.y * BOX_SIZE, BOX_SIZE, BOX_SIZE);
            } else {
            	// Körper als grüne Kreise zeichnen
                g.setColor(Color.GREEN);
                g.fillOval(p.x * BOX_SIZE + (BOX_SIZE - 20) / 2, p.y * BOX_SIZE + (BOX_SIZE - 20) / 2, 20, 20);
            }
        }
        
        // Punktestand anzeigen
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
    }

    
    /**
     * Gibt eine zufällige Farbe für die Frucht zurück.
     *
     * @return eine zufällige Farbe für die Frucht
     */    
    private Color getRandomFruitColor() {
    	Color[] colors = { Color.YELLOW, Color.ORANGE, Color.CYAN, Color.WHITE, Color.PINK, Color.MAGENTA };
    	
        Random random = new Random();
        int colorIndex = random.nextInt(colors.length); 
        return colors[colorIndex];
    }

    /**
     * Der Einstiegspunkt des Programms.
     * Erzeugt ein JFrame, fügt das Game-Objekt hinzu und zeigt das Fenster an.
     *
     * @param args die Kommandozeilenargumente (werden ignoriert)
     */
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
