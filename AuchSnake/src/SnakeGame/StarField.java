package SnakeGame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Eine Klasse, die eine animierte Starfield-Darstellung in einem JPanel bereitstellt.
 */
@SuppressWarnings("serial")
public class StarField extends JPanel {
    private Star[] stars; // Array zur Speicherung der Sterne
    private final int starCount = 1024; // Anzahl der Sterne (final, da sich die Anzahl nicht ändert)
    private double velocity = -0.5; // Geschwindigkeit des Starfields
    private final int focalPoint = 256; // Fokuspunkt des Starfields (final, da sich der Fokuspunkt nicht ändert)
    private double cameraDepth = 0; // Kameratiefeneinstellung
    private int cX = 256; // X-Koordinate des Zentrums des Starfields
    private int cY = 256; // Y-Koordinate des Zentrums des Starfields
    
    /**
     * Konstruktor für das Starfield-Panel.
     * Initialisiert das Panel und die Sterne.
     */
    public StarField() {
        setPreferredSize(new Dimension(512, 512)); // Setzt die bevorzugte Größe des Panels
        setBackground(Color.BLACK); // Setzt den Hintergrund auf Schwarz
        stars = new Star[this.starCount]; // Initialisiert das Sterne-Array mit der angegebenen Anzahl
        for (int i = 0; i < this.starCount; i++) {
            stars[i] = new Star(this, i); // Erzeugt einen neuen Stern und fügt ihn dem Array hinzu
        }
        new Timer(20, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                move(); // Bewegt die Sterne
                repaint(); // Aktualisiert das Panel
            }
        }).start();
    }

    /**
     * Bewegt die Sterne entsprechend der aktuellen Geschwindigkeit.
     */
    private void move() {
        double dv = this.velocity - (-0.5); // Berechnet die Änderung der Geschwindigkeit
        this.velocity -= dv * 0.01; // Verringert die Geschwindigkeit schrittweise
        this.cameraDepth = (this.cameraDepth + this.velocity) % 1024; // Aktualisiert die Kameratiefeneinstellung
    }

    /**
     * Zeichnet das Starfield-Panel mit den aktuellen Sternpositionen.
     * @param g Der Graphics-Kontext zum Zeichnen.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Ruft die paintComponent-Methode der Elternklasse auf
        Graphics2D g2d = (Graphics2D) g; // Erzeugt einen Graphics2D-Kontext
        g2d.setColor(Color.WHITE); // Setzt die Zeichenfarbe auf Weiß
        for (Star star : this.stars) {
            star.move(this.velocity); // Bewegt den Stern
            star.draw(g2d); // Zeichnet den Stern
        }
    }

    /**
     * Eine innere Klasse, die einen Stern repräsentiert.
     */
    private class Star {
        private double x, y, z; // Position und Tiefe des Sterns
        private final double spread = 1.0; // Streuung des Sterns (final, da sich die Streuung nicht ändert)

        /**
         * Konstruktor für einen Stern.
         * @param context Das Starfield-Panel, dem der Stern gehört.
         * @param index Der Index des Sterns im Array.
         */
        public Star(StarField context, int index) {
            this.x = (Math.random() * 2048 - 1024) * this.spread; // Zufällige X-Position mit Streuung
            this.y = (Math.random() * 2048 - 1024) * this.spread; // Zufällige Y-Position mit Streuung
            this.z = ((StarField.this.starCount - 1) - index) / (StarField.this.starCount / 1024.0); // Berechnet die Tiefe des Sterns
        }

        /**
         * Hilfsmethode zur Berechnung des Modulo einer Gleitkommazahl. 
         * Modulo-Berechnung mit % hat irgendwie nicht das passende Ergebnis geliefert..
         * @param a Die zu berechnende Zahl.
         * @param b Der Modulo-Wert.
         * @return Das Modulo der Zahl.
         * 
         * Erklärung: 
         * Die Hilfsmethode modulo berechnet das Modulo einer 
         * Gleitkommazahl a mit einem Modulo-Wert b. 
         * Das Modulo einer Zahl gibt den Rest an, der übrig bleibt, 
         * wenn die Zahl durch den Modulo-Wert geteilt wird.
         * 
         * Die mathematische Formel a - b * Math.floor(a / b) implementiert den 
         * Modulo-Berechnungsalgorithmus. Hier ist eine schrittweise Erklärung:
         * 
         * Für das Starfield wird die modulo-Methode verwendet, um die "Tiefe" 
         * der Sterne im Starfield zu berechnen, indem sie die Modulo-Operation 
         * auf die Z-Koordinate und den Modulo-Wert 1024 anwendet. Dies hilft, die 
         * Sterne innerhalb des festgelegten Tiefenbereichs zu halten und eine 
         * kontinuierliche Bewegung zu erzeugen.
         * 
         */
        private double modulo(double a, double b) {
            return a - b * Math.floor(a / b);
        }

        /**
         * Bewegt den Stern entsprechend der aktuellen Geschwindigkeit.
         * @param velocity Die aktuelle Geschwindigkeit des Starfields.
         */
        public void move(double velocity) {
            this.z = modulo(this.z + velocity, 1024); // Aktualisiert die Tiefe des Sterns
            //this.z = this.z + (velocity % 1024); // wäre statt modulo funktion möglich  
        }

        /**
         * Zeichnet den Stern auf den Graphics2D-Kontext.
         * @param g2d Der Graphics2D-Kontext zum Zeichnen.
         */
        public void draw(Graphics2D g2d) {
            // modulo-berechnung mittels % hat seltsame ergebnisse, kommazahlen sind gruselig. 
            double depth = StarField.this.focalPoint / (this.modulo(this.z + StarField.this.cameraDepth, 1024) + 1); // Berechnet die Tiefe des Sterns
            
        	int x = (int) (this.x * depth + StarField.this.cX); // Berechnet die X-Koordinate des Sterns
            int y = (int) (this.y * depth + StarField.this.cY); // Berechnet die Y-Koordinate des Sterns
            
            int sz = (int) (5 * depth); // Berechnet die Größe des Sterns
            
            g2d.fillRect(x, y, sz, sz); // Zeichnet den Stern
        }
    }

    /**
     * Die Hauptmethode, die das Starfield-Panel in einem JFrame erzeugt und anzeigt.
     * @param args Die Kommandozeilenargumente (werden ignoriert).
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Starfield"); // Erzeugt ein neues JFrame mit dem Titel "Starfield"
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Setzt das Verhalten des Frames beim Schließen
        frame.add(new StarField()); // Fügt das Starfield-Panel zum Frame hinzu
        frame.pack(); // Passt die Größe des Frames an den Inhalt an
        frame.setLocationRelativeTo(null); // Zentriert den Frame auf dem Bildschirm
        frame.setVisible(true); // Macht den Frame sichtbar
    }
}
