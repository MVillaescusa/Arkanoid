package arkanoid;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;

public class Nave {

    final double y = 525;
    double x, ancho, alto;
    Graphics2D g2;
    ImageIcon nave;
    boolean pegamento;

    public Nave() {
        ancho = 75;
        alto = 15;
        x = 250 - (ancho / 2);
        try {
            nave = new ImageIcon(getClass().getResource("/images/Nave.png"));
        } catch (Exception ex) {  
            System.out.println(ex.toString());
        }
        pegamento = false;
    }

    public void pinta(Graphics2D g) {
        g2 = g;
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawImage(nave.getImage(), (int)x, (int)y, (int)ancho, (int)alto, null);
    }

    public void mueve(int posX) {
        x = posX+15;
        pinta(g2);
    }
    
    public void mueve (Lienzo lienzo, boolean derecha, boolean izquierda){
        if (x >= 15 && x + ancho <= lienzo.getWidth()-15) {
            if (derecha) {
                x = x + 3.0;
            }
            if (izquierda) {
                x = x - 3.0;
            }
        }
        else if (x < 15){
            x = 15;
        }
        else if (x + ancho > lienzo.getWidth()-15){
            x = lienzo.getWidth() - 16 - ancho;
        }
    }
}
