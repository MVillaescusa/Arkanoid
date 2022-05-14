package arkanoid;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.ImageIcon;

public class Puntuacion extends Canvas {

    final int panelY = 570; //Posicion en Y donde empieza el panel

    Graphics2D g2;
    long puntuacion;
    int nivel;
    int vidas;
    int nivelMaximo;
    ImageIcon nave;
    File file = new File(getClass().getResource("/fonts/joystix.ttf").getPath(), "joystix.ttf");
    Font font;
    BufferStrategy strategy;

    public Puntuacion(int nivelMax) {
        puntuacion = 0;
        vidas = 2;
        nivelMaximo = nivelMax;
        try {
            nave = new ImageIcon(getClass().getResource("/images/Nave.png"));
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        //Establezco el tipo de Fuente
        try {
            String fName = "/fonts/joystix.ttf";
            InputStream is = getClass().getResourceAsStream(fName);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            font = font.deriveFont(Font.PLAIN,15);
        } catch (FontFormatException | IOException e) {
            System.out.println("Exception de la Fuente: " + e);
        } 
    }

    public void pinta(Graphics2D g) {
        this.g2 = g;
        //Establezco el estilo de la Fuente
        g2.setFont(font);
        g2.setColor(Color.white);
        //Cambio la puntuacion
        g2.drawString("Puntuación: " + puntuacion, 25, 600);
        //Pinto el nivel actual
        g2.drawString("Nivel " + nivel + "/" + nivelMaximo, 350, 600);
        //Pinto las vidas
        g2.drawImage(nave.getImage(), 25, 625, 35, 10, null);
        g2.drawString("x " + vidas, 65, 635);
        g2.drawString("Desarrollado por Mario Villaescusa",60,665);
        g2.drawString("1º DAW IES Dos Mares",145,680);
    }

    public boolean compruebaFinPartida() {
        if ((vidas < 0) || nivel > nivelMaximo) {
            return true;
        }
        return false;
    }
}
