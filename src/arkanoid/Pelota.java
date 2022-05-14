package arkanoid;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;

public class Pelota {

    //Declaración de variables
    float x, y, r;
    boolean visible;
    double angulo, velocidad;
    Color color;
    ImageIcon bola;

    //Constructor
    public Pelota(float posX, float posY, float diametro, double ang, Color col, double vel) {
        x = posX + 15;
        y = posY;
        r = diametro / 2;
        //Paso el angulo en grados a radianes para hacer el cambio inverso seria ang * 180 / Math.PI
        angulo = ang * Math.PI / 180;
        color = col;
        velocidad = vel;
        visible = true;
        try {
            bola = new ImageIcon(getClass().getResource("/images/Bola.png"));
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen de la bola.");
        }
    }

    public void pinta(Graphics2D g2) {
        if (visible) {
            g2.drawImage(bola.getImage(), (int) x + 15, (int) y, (int) r * 2, (int) r * 2, null);
        }
    }

    public float dameCentroX() {
        float centroX;
        centroX = x + r;
        return centroX;
    }

    public float dameCentroY() {
        float centroY;
        centroY = y + r;
        return centroY;
    }

    public float dameX() {
        return x;
    }

    public float dameY() {
        return y;
    }

    public float dameR() {
        return r;
    }

    public double dameAngulo() {
        return angulo;
    }
}
