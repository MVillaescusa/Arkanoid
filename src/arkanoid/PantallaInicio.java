package arkanoid;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

public class PantallaInicio extends Canvas {

    Graphics2D g2;
    boolean inicioPartida;
    JFrame ventana;
    private BufferStrategy strategy;
    Image titulo, fondo;
    Toolkit t;
    File file;
    Font font;
    MouseEvent event = null;
    boolean clickComenzar;
    Color defaultColor;
    boolean nivelUp, nivelDown;
    int nivelInicial;
    String sonidoPantallaInicio = "/sonidos/SonidoPantallaInicio.wav";
    static Clip clip;

    public PantallaInicio(JFrame vent) {
        inicioPartida = false;
        clickComenzar = false;
        nivelUp = false;
        nivelDown = false;
        nivelInicial = 1;
        defaultColor = Color.white;
        ventana = vent;
        t = Toolkit.getDefaultToolkit();
        titulo = t.createImage(getClass().getResource("/images/arkanoid.png"));
        fondo = t.createImage(getClass().getResource("/images/fondos/pantallaInicio.jpg"));
        file = new File(getClass().getResource("/fonts/joystix.ttf").getPath(), "joystix.ttf");
        
        //Reproduzco el sonido de inicio del juego
        playSonido(sonidoPantallaInicio);

        //Establezco el tipo de Fuente
        try {
            String fName = "/fonts/joystix.ttf";
            InputStream is = getClass().getResourceAsStream(fName);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            font = font.deriveFont(Font.PLAIN, 22);
        } catch (FontFormatException | IOException e) {
            System.out.println("Exception de la Fuente: " + e);
        }

        this.setSize(500, 570);

        //Evito que el sistema llame a la funcion Repaint() por si mismo
        this.setIgnoreRepaint(true);

        //Añado el KeyListener para reconocer los eventos del teclado
        this.addKeyListener(kl);

        //Añado el KeyListener para reconocer los eventos del teclado
        //this.addMouseListener(ml);

        this.addMouseMotionListener(mml);
    }

    public void play() {
        pinta();
    }

    public void pinta() {
        try {
            if (strategy == null || strategy.contentsLost()) {
                // Crea BufferStrategy para el renderizado
                createBufferStrategy(2);
                strategy = getBufferStrategy();
                Graphics g = strategy.getDrawGraphics();
                g2 = (Graphics2D) g;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        // ANTIALIASING
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //IMAGEN ARKANOID
        g2.setColor(Color.black);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        g2.drawImage(titulo, getWidth() / 2 - 186, 50, 373, 135, null);

        //PULSA ESPACIO PARA COMENZAR
        font = font.deriveFont(Font.PLAIN, 22);
        g2.setFont(font);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("PULSA ESPACIO", 144, 474);
        g2.drawString("PARA COMENZAR", 144, 499);
        if (checkForHover(event) == "PULSA ESPACIO PARA COMENZAR") {
            g2.setColor(Color.white);
        } else {
            g2.setColor(defaultColor);
        }
        g2.drawString("PULSA ESPACIO", 140, 470);
        g2.drawString("PARA COMENZAR", 140, 495);
        g2.setColor(defaultColor);

        //SELECCIONE NIVEL INICIAL
        font = font.deriveFont(Font.PLAIN, 15);
        g2.setFont(font);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("SELECCIONE EL NIVEL INICIAL", 102, 602);
        g2.setColor(defaultColor);
        g2.drawString("SELECCIONE EL NIVEL INICIAL", 100, 600);
        font = font.deriveFont(Font.PLAIN, 25);
        g2.setFont(font);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("" + nivelInicial, getWidth() / 2 - 6, 584);
        g2.drawString("←", getWidth() / 2 - 36, 584);
        g2.drawString("→", getWidth() / 2 + 24, 584);
        if (checkForHover(event) == "FLECHAS") {
            g2.setColor(Color.white);
        } else {
            g2.setColor(defaultColor);
        }
        g2.drawString("" + nivelInicial, getWidth() / 2 - 10, 580);
        g2.drawString("←", getWidth() / 2 - 40, 580);
        g2.drawString("→", getWidth() / 2 + 20, 580);
        g2.setColor(defaultColor);

        //Si todo funciona correctamente muestra por pantalla el dibujo
        if (!strategy.contentsLost()) {
            strategy.show();
        }
    }

    KeyListener kl = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                inicioPartida = true;
                clip.stop();
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT){
                nivelInicial++;
                if (nivelInicial == 10) {
                    nivelInicial = 9;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT){
                nivelInicial--;
                if (nivelInicial == 0) {
                    nivelInicial = 1;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                System.exit(0);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };

    MouseListener ml = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (clickComenzar) {
                inicioPartida = true;
            }
            if (nivelUp) {
                nivelInicial++;
                if (nivelInicial == 10) {
                    nivelInicial = 9;
                }
            }
            if (nivelDown) {
                nivelInicial--;
                if (nivelInicial == 0) {
                    nivelInicial = 1;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    };

    MouseMotionListener mml = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            //checkForHover(e);
        }

    };

    //CONTROLA SI EL RATON ESTÁ ENCIMA DEL TEXTO PARA COMENZAR LA PARTIDA
    //Y DEVUELVE EL COLOR AL QUE TIENE QUE ESTAR EL TEXTO SI ESTÁ EL RATÓN ENCIMA
    public String checkForHover(MouseEvent even) {
        if (even != null) {
            event = even;
            FontMetrics metrics = getFontMetrics(font);

            Graphics g = getGraphics();
            Rectangle textBounds = metrics.getStringBounds("PULSA ESPACIO", g).getBounds();
            Rectangle textBounds2 = metrics.getStringBounds("PARA COMENZAR", g).getBounds();
            Rectangle textBounds3 = metrics.getStringBounds("←", g).getBounds();
            Rectangle textBounds4 = metrics.getStringBounds("→", g).getBounds();
            g.dispose();

            textBounds.translate(150, 250);
            textBounds2.translate(150, 275);
            textBounds3.translate(getWidth() / 2 - 40, 640);
            textBounds4.translate(getWidth() / 2 + 20, 640);

            if (textBounds.contains(event.getPoint())
                    || textBounds2.contains(event.getPoint())) {
                clickComenzar = true;
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                return "PULSA ESPACIO PARA COMENZAR";
            } else if (textBounds3.contains(event.getPoint())) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                nivelDown = true;
                return "FLECHAS";
            } else if (textBounds4.contains(event.getPoint())) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                nivelUp = true;
                return "FLECHAS";
            } else {
                clickComenzar = false;
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                nivelUp = false;
                nivelDown = false;
                return "";
            }
        }
        return "";
    }
    
    private static synchronized void playSonido(final String sonido) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    InputStream in = new BufferedInputStream(this.getClass().getResourceAsStream(sonido));
                    clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(in));
                    clip.start();
                } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                    System.err.println(e);
                }
            }
        }).start();
    }
}
