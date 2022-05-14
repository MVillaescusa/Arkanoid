package arkanoid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

public class Ladrillo {

    final double ancho = 38.0;
    final double alto = 17.0;

    double x, y;
    int durezaInicial, durezaActual, powerUp;
    Color color, blanco;
    boolean visible;
    ImageIcon ladrillo;
    Image ladrilloPlata1, ladrilloPlata2, ladrilloOro;
    int red, green, blue;
    Toolkit t;

    public Ladrillo(double posX, double posY, int dur, Color col, int pow, boolean visib) {
        x = posX + 15;
        y = posY + 15;
        durezaInicial = dur;
        durezaActual = durezaInicial;
        color = col;
        powerUp = pow;
        visible = visib;
        //Divido el color en RGB
        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        t = Toolkit.getDefaultToolkit();
        //ASIGNO LAS IMAGENES A CADA LADRILLO DEPENDIENDO DE LA DUREZA
        if (durezaActual == 3) { //El cuadrado dorado tiene una capa diferente a los otros para dar sensacion de dorado
            //ladrillo = new ImageIcon(getClass().getResource("/images/oro.png"));
            ladrilloOro = t.createImage(getClass().getResource("/images/ladrillos/oro.png"));
        } else if (durezaInicial == 2) {
            //ladrillo = new ImageIcon(getClass().getResource("/images/plata1.png"));
            ladrilloPlata1 = t.createImage(getClass().getResource("/images/ladrillos/plata1.png"));
            ladrilloPlata2 = t.createImage(getClass().getResource("/images/ladrillos/plata2.gif"));
        } else { //ASIGNO LAS IMAGENES A CADA LADRILLO DEPENDIENDO DEL COLOR
            //red = color.getRed();
            //green = color.getGreen();
            //blue = color.getBlue();
            //Blanco
            if (red == 255 && green == 255 && blue == 255) {
                ladrillo = new ImageIcon(getClass().getResource("/images/ladrillos/blanco.png"));
            } //Amarillo
            else if (red == 255 && green == 255 && blue == 0) {
                ladrillo = new ImageIcon(getClass().getResource("/images/ladrillos/amarillo.png"));
            } //Morado
            else if (red == 255 && green == 0 && blue == 255) {
                ladrillo = new ImageIcon(getClass().getResource("/images/ladrillos/morado.png"));
            } //Azul
            else if (red == 0 && green == 0 && blue == 255) {
                ladrillo = new ImageIcon(getClass().getResource("/images/ladrillos/azul.png"));
            } //Rojo
            else if (red == 255 && green == 0 && blue == 0) {
                ladrillo = new ImageIcon(getClass().getResource("/images/ladrillos/rojo.png"));
            } //Verde
            else if (red == 0 && green == 255 && blue == 0) {
                ladrillo = new ImageIcon(getClass().getResource("/images/ladrillos/verde.png"));
            } //Cyan
            else if (red == 0 && green == 255 && blue == 255) {
                ladrillo = new ImageIcon(getClass().getResource("/images/ladrillos/cyan.png"));
            } //Naranja
            else if (red == 255 && green == 127 && blue == 0) {
                ladrillo = new ImageIcon(getClass().getResource("/images/ladrillos/naranja.png"));
            }
        }
    }

    public void pinta(Graphics2D g2) { //PONGO LOS LADRILLOS EN LA PANTALLA
        if (visible) {
            if (durezaActual == 3) { //El cuadrado dorado tiene una capa diferente a los otros para dar sensacion de dorado
                //ladrillo = new ImageIcon(getClass().getResource("/images/oro.png"));
                g2.drawImage(ladrilloOro, (int) x, (int) y, (int) ancho, (int) alto, null);
            } else if (durezaInicial == 2) {
                //g2.drawImage(ladrillo.getImage(), (int) x, (int) y, (int) ancho, (int) alto, null);
                g2.drawImage(ladrilloPlata1, (int) x, (int) y, (int) ancho, (int) alto, null);
                if (durezaActual == 1) {
                    g2.drawImage(ladrilloPlata2, (int) x, (int) y, (int) ancho, (int) alto, null);
                }
            } else {
                red = color.getRed();
                green = color.getGreen();
                blue = color.getBlue();
                //Blanco pero lo pinto en rosa
                if (red == 255 && green == 255 && blue == 255) {
                    g2.drawImage(ladrillo.getImage(), (int) x, (int) y, (int) ancho, (int) alto, null);
                } //Amarillo
                else if (red == 255 && green == 255 && blue == 0) {
                    g2.drawImage(ladrillo.getImage(), (int) x, (int) y, (int) ancho, (int) alto, null);
                } //Morado
                else if (red == 255 && green == 0 && blue == 255) {
                    g2.drawImage(ladrillo.getImage(), (int) x, (int) y, (int) ancho, (int) alto, null);
                } //Azul
                else if (red == 0 && green == 0 && blue == 255) {
                    g2.drawImage(ladrillo.getImage(), (int) x, (int) y, (int) ancho, (int) alto, null);
                } //Rojo
                else if (red == 255 && green == 0 && blue == 0) {
                    g2.drawImage(ladrillo.getImage(), (int) x, (int) y, (int) ancho, (int) alto, null);
                } //Verde
                else if (red == 0 && green == 255 && blue == 0) {
                    g2.drawImage(ladrillo.getImage(), (int) x, (int) y, (int) ancho, (int) alto, null);
                } //Cyan
                else if (red == 0 && green == 255 && blue == 255) {
                    g2.drawImage(ladrillo.getImage(), (int) x, (int) y, (int) ancho, (int) alto, null);
                } //Naranja
                else if (red == 255 && green == 127 && blue == 0) {
                    g2.drawImage(ladrillo.getImage(), (int) x, (int) y, (int) ancho, (int) alto, null);
                }
            }
        } else {
            this.x = -1000;
            this.y = -1000;
        }
    }

    public void resuelveChoque(Graphics2D g2) {
        if (durezaActual != 3) { //Si tiene dureza 3 el ladrillo no se rompe nunca
            durezaActual--;
        } else {
            ladrilloOro = t.createImage(getClass().getResource("/images/ladrillos/oro.gif"));
        }
        if (durezaActual <= 0) {
            visible = false;
        }
    }

    public String dimeTipo() {
        if (durezaInicial == 3) {
            return "Oro";
        } else if (durezaInicial == 2) {
            return "Plata";
        } else {
            return "Normal";
        }
    }
}
