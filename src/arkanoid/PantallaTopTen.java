package arkanoid;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PantallaTopTen extends Canvas {

    ArrayList<String> listaPuntuaciones;
    Graphics2D g2;
    private BufferStrategy strategy;
    Font font; //Para el estilo de fuente
    int strwid;
    boolean replay;

    public PantallaTopTen(ArrayList<String> lstPuntuaciones) {
        listaPuntuaciones = lstPuntuaciones;
        replay = false;
        this.addKeyListener(kl);
        //setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        //Establezco el tipo de fuente
        try {
            String fName = "/fonts/joystix.ttf";
            InputStream is = getClass().getResourceAsStream(fName);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException ex) {
            System.out.println(ex.toString());
        }
    }

    public PantallaTopTen() {
        //listaPuntuaciones = lstPuntuaciones;
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        //Establezco el tipo de fuente
        try {
            String fName = "/fonts/joystix.ttf";
            InputStream is = getClass().getResourceAsStream(fName);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException ex) {
            System.out.println(ex.toString());
        }
    }

    public void pintaPuntuaciones() {
        if (!replay) {
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.black);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.red);
            g2.setFont(font.deriveFont(35f));
            strwid = g2.getFontMetrics().stringWidth("TOP TEN") / 2;
            g2.setColor(Color.yellow);
            g2.drawString("TOP TEN", getWidth() / 2 - strwid, 90);
            g2.setFont(font.deriveFont(20f));
            g2.setColor(Color.red);
            strwid = g2.getFontMetrics().stringWidth("POS") / 2;
            g2.drawString("POS", 60 + strwid - strwid, 150);
            strwid = g2.getFontMetrics().stringWidth("PUNT") / 2;
            g2.drawString("PUNT", getWidth() / 2 - strwid, 150);
            strwid = g2.getFontMetrics().stringWidth("NOMB") / 2;
            g2.drawString("NOMB", getWidth() - strwid * 2 - 50, 150);
            int y = 190;
            int x = listaPuntuaciones.size();//Evito que salgan más de 10 puntuaciones
            if (x > 10){
                x = 10;
            }
            for (int i = 1; i < x+1; i++) {
                if (i == 1) {
                    g2.setColor(Color.yellow);
                } else {
                    g2.setColor(Color.red);
                }
                strwid = g2.getFontMetrics().stringWidth("" + i + "º") / 2;
                g2.drawString(" " + i + "º", 70 - strwid, y);
                strwid = g2.getFontMetrics().stringWidth(listaPuntuaciones.get(i - 1).substring(4)) / 2;
                g2.drawString(listaPuntuaciones.get(i - 1).substring(4), getWidth() / 2 - strwid, y);
                strwid = g2.getFontMetrics().stringWidth(listaPuntuaciones.get(i - 1).substring(0, 3)) / 2;
                g2.drawString(listaPuntuaciones.get(i - 1).substring(0, 3), getWidth() - strwid * 2 - 60, y);
                y += 40;

            }

            g2.setColor(Color.red);
            g2.setFont(font.deriveFont(25f));
            strwid = g2.getFontMetrics().stringWidth("PULSA ESPACIO") / 2;
            g2.drawString("PULSA ESPACIO", getWidth()/2-strwid, 640);
            strwid = g2.getFontMetrics().stringWidth("PARA REINICIAR") / 2;
            g2.drawString("PARA REINICIAR", getWidth()/2-strwid, 670);
            
        }
    }

    KeyListener kl = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                replay = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                System.exit(0);
            }
        }
    };
}
