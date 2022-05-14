package arkanoid;

import java.awt.Graphics2D;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

public class PowerUp {

    double x, y;
    int pow;
    boolean visible;
    ImageIcon gif;
    Nave nave;
    Puntuacion puntuacion;
    Pelota[] pelotas;
    boolean pegamento;

    public PowerUp(int posX, int posY, int powerUp, Nave nav, Puntuacion punt, Pelota[] pel) {
        x = posX;
        y = posY;
        pow = powerUp;
        visible = false;
        nave = nav;
        puntuacion = punt;
        pelotas = pel;
        pegamento = false;

        if (pow == 1) {
            try {
                gif = new ImageIcon(getClass().getResource("/images/powerups/vida.gif"));
            } catch (Exception e) {
                System.out.println("Error al cargar PowUp: Vida.");
            }
        }
        if (pow == 2) {
            try {
                gif = new ImageIcon(getClass().getResource("/images/powerups/grande.gif"));
            } catch (Exception e) {
                System.out.println("Error al cargar PowUp: Grande.");
            }
        }
        if (pow == 3) {
            try {
                gif = new ImageIcon(getClass().getResource("/images/powerups/pequeno.gif"));
            } catch (Exception e) {
                System.out.println("Error al cargar PowUp: Pequeño.");
            }
        }
        if (pow == 4) {
            try {
                gif = new ImageIcon(getClass().getResource("/images/powerups/pegamento.gif"));
            } catch (Exception e) {
                System.out.println("Error al cargar PowUp: Pegamento.");
            }
        }
        if (pow == 5) {
            try {
                gif = new ImageIcon(getClass().getResource("/images/powerups/slow.gif"));
            } catch (Exception e) {
                System.out.println("Error al cargar PowUp: Velocidad.");
            }
        }
    }

    public void pinta(Graphics2D g2) {
        if (visible) {
            if (y < 550) {
                g2.drawImage(gif.getImage(), (int) x + 10, (int) y + 5, 30, 15, null);
                y += 1.0;
            }
            consiguePowerUp();
        }
    }

    public void consiguePowerUp() {
        if ((y + gif.getIconHeight() >= nave.y)
                && (x + gif.getIconWidth() > nave.x - 15)
                && (x < nave.x - 15 + nave.ancho)
                && (y <= nave.y + nave.alto)) {
            switch (pow) {
                case 1://Vida
                    puntuacion.vidas++;
                    puntuacion.puntuacion += 50;
                    playSonido("/sonidos/Vida.wav");
                    break;
                case 2://Nave grande
                    nave.pegamento = false;
                        nave.nave = new ImageIcon(getClass().getResource("/images/NaveGrande.png"));
                        nave.ancho = 110;
                    puntuacion.puntuacion += 50;
                    playSonido("/sonidos/NaveGrande.wav");
                    break;
                case 3://Nave pequeña
                    nave.pegamento = false;
                        nave.nave = new ImageIcon(getClass().getResource("/images/NavePequena.png"));
                        nave.ancho = 45;
                    puntuacion.puntuacion += 50;
                    playSonido("/sonidos/Peque.wav");
                    break;
                case 4:// Pegamento
                    nave.pegamento = true;
                    nave.nave = new ImageIcon(getClass().getResource("/images/Nave.png"));
                    nave.ancho = 75;
                    puntuacion.puntuacion += 50;
                    playSonido("/sonidos/Pegamento.wav");
                    break;
                case 5://Reinicia velocidad
                    nave.pegamento = false;
                    for (int i = 0; i < pelotas.length; i++) {
                        pelotas[i].velocidad = 1;
                    }
                    puntuacion.puntuacion += 50;
                    playSonido("/sonidos/Despacio.wav");
                    break;
                case 6:
                    break;
            }
            visible = false;
        }
    }
    
    private static synchronized void playSonido(final String sonido) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    InputStream in = new BufferedInputStream(this.getClass().getResourceAsStream(sonido));
                    Clip clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(in));
                    clip.start();
                } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                    System.err.println(e);
                }
            }
        }).start();
    }
}
