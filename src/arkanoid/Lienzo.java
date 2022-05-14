package arkanoid;

import java.applet.AudioClip;
import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Lienzo extends Canvas {

    //CONSTANTES
    final int numPelotas = 1; //Número de pelotas que se ponen en juego
    final double anguloInicial = 67.5; //Angulo en el que sale la bola de la nave la primera vez
    int nivelInicial = 1; //Nivel en el que se inicia la partida
    final int nivelMaximo = 9; //Númaro máximo de niveles implementados
    final double velocidadInicial = 2.5; //Velocidad con la que saldrá la pelota de la nave

    private static int aps = 0;
    private static int fps = 0;
    int sleep = 5;

    //DECLARACION DE VARIABLES
    Graphics2D g2;

    //Para poder pintar los objetos sin parpadeos
    private BufferStrategy strategy;

    ImageIcon fondo;
    ImageIcon borde;

    double velocidad = 0; //La pelota esta parada hasta hacer click
    double velocidadAux; //Capturo la velocidad de la pelota antes de que se pegue a la nave
    JFrame ventana;
    Nave nave;
    Pelota[] pelotas; //Array de pelotas
    Ladrillo[][] ladrillos; //Array de ladrillos
    Puntuacion puntuacion = new Puntuacion(nivelMaximo); //Canvas inferior, con la puntuacion, vidas y nivel
    PowerUp[][] power; //Array de powerups
    int numLadrillos; //Número de ladrillos que hay/quedan en el nivel actual
    Color color;
    int numeroPelotas; //Numero de pelotas que hay actualmente en juego
    boolean pause = false; //Para saber si la partida esta en pausa o no
    boolean pausaPintada = false; //Para pintar la pausa solo una vez
    boolean pantallaTopTen = false; //Cuando sea true muestra la pantalla con el top ten
    Font font; //Para el estilo de fuente
    NivelLadrillos level; //Nivel actual
    double xAntesChoque = 0; //Capturo la posicion en X de la pelota antes de que se quede pegada a la nave

    //char[][] nombre = new char[10][3];
    int[][] topTen = new int[10][4];
    int[] nombre = new int[4];
    char primeraLetra, segundaLetra, terceraLetra;
    char letraActiva;
    int posicion;
    ArrayList<String> listaPuntuaciones = new ArrayList<>();

    boolean derechaPulsada, izquierdaPulsada;

    //Para los sonidos
    String musicaFondo, reboteNave, reboteLadrillo, reboteLadrilloOro, sonidoMuerte, sonidoPegamento, sonidoDespacio,
            sonidoSiguienteNivel, sonidoGameOver;

    public Lienzo(JFrame vent, int nivelInicio) {
        //Asigno el tamaño del lienzo a la ventana
        this.setSize(500, 570);

        //Evito que el sistema llame a la funcion Repaint() por si mismo
        this.setIgnoreRepaint(true);

        //Añado el MouseListener para reconocer los eventos del raton
        this.addMouseListener(ml);

        //Añado el KeyListener para reconocer los eventos del teclado
        this.addKeyListener(kl);
        
        //Inicializo las variables de cada sonido
        musicaFondo = "/sonidos/MusicaFondo.wav";
        reboteNave = "/sonidos/ReboteNave.wav";
        reboteLadrillo = "/sonidos/ReboteLadrillo.wav";
        reboteLadrilloOro = "/sonidos/ReboteLadrilloOro.wav";
        sonidoMuerte = "/sonidos/Muerte.wav";
        sonidoPegamento = "/sonidos/Pegamento.wav";
        sonidoSiguienteNivel = "/sonidos/SiguienteNivel.wav";
        sonidoGameOver = "/sonidos/GameOver.wav";

        listaPuntuaciones = leerPuntuaciones();
        
        //Variables que mueven la nave
        derechaPulsada = false;
        izquierdaPulsada = false;
        
        //Variable para el jFrame
        ventana = vent;

        //Variables para introducir el nombre en las puntuaciones
        primeraLetra = 65; //Letra 'a' en Unicode = 65
        segundaLetra = 65;
        terceraLetra = 65;
        letraActiva = primeraLetra;
        posicion = 1;        

        //Creo la nave
        nave = new Nave();

        //Creo las pelotas
        numeroPelotas = numPelotas;
        creaPelotas(numeroPelotas);

        //Creo el primer nivel del juego seleccionado desde la pantalla de inicio
        puntuacion.nivel = nivelInicio;
        level = new NivelLadrillos(puntuacion.nivel, this, nave, puntuacion, pelotas);
        ladrillos = level.crearNivel(puntuacion.nivel);
        numLadrillos = level.numLadrillos;
        power = level.power;
        velocidadAux = velocidadInicial;

        //Pongo la imagen de fondo dependiendo del nivel inicial
        try {
            //fondo = new ImageIcon(getClass().getResource("/images/fondos/fondo" + puntuacion.nivel + ".jpg"));
            fondo = new ImageIcon(getClass().getResource("/images/fondos/fondo" + (int)(Math.random()*10) + ".jpg"));
            borde = new ImageIcon(getClass().getResource("/images/fondos/borde.png")); 
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        //Establezco el tipo de fuente
        try {
            String fName = "/fonts/joystix.ttf";
            InputStream is = getClass().getResourceAsStream(fName);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            //font = font.deriveFont(60f);
        } catch (FontFormatException | IOException ex) {
            System.out.println(ex.toString());
        }
        
        //Reproduzco el sonido de inicio de partida y la musica de fondo
        playSonido(sonidoSiguienteNivel);
        //playMusicaFondo(musicaFondo);
    }

    public void play() {
        final int NS_POR_SEGUNDO = 1000000000;
        final byte APS_OBJETIVO = 60;
        final double NS_POR_ACTUALIZACION = NS_POR_SEGUNDO / APS_OBJETIVO;

        long referenciaActualizacion = System.nanoTime();
        long referenciaContador = System.nanoTime();

        double tiempoTranscurrido;
        double delta = 0;

        while (!puntuacion.compruebaFinPartida()) {
            if (!pause) {
                if (numLadrillos == 0) {
                    siguienteNivel();
                }

                //PARA QUE REPINTE A 120 FPS/////////////////////////////////////////////////////////////////////////////////////
                final long inicioBucle = System.nanoTime();
                tiempoTranscurrido = inicioBucle - referenciaActualizacion;
                referenciaActualizacion = inicioBucle;

                delta += tiempoTranscurrido / NS_POR_ACTUALIZACION;

                while (delta >= 1) {
                    aps++;
                    delta--;
                }
                //DIBUJA CADA PASADA LA POSICIÓN DE CADA OBJETO
                pinta();
                if (System.nanoTime() - referenciaContador > NS_POR_SEGUNDO) {
                    ventana.setTitle("Arkanoid by Mario. " + "Aps: " + aps + " FPS: " + fps);
                    if (fps > 125) {
                        sleep++;
                    } else if (fps < 115) {
                        sleep--;
                    }

                    fps = 0;
                    aps = 0;
                    referenciaContador = System.nanoTime();
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                //RELENTIZA EL MOVIMIENTO PARA HACERLO FLUIDO
                try {
                    //Thread.sleep(1);
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            } else {
                pintaPausa();
            }
        }
        playSonido(sonidoGameOver);
        while (puntuacion.compruebaFinPartida() && !pantallaTopTen) {
            pintaFinPartida();
        }
    }

    public void pinta() {
        fps++;
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

        // Dibuja el fondo
        g2.drawImage(fondo.getImage(), 0, 0, getWidth(), getHeight(), this);
        g2.drawImage(borde.getImage(), 0, 0, getWidth(), getHeight(), this);

        //Dibujo el panel inferior
        ventana.add(puntuacion);
        puntuacion.pinta(g2);

        //Comprueba los rebotes con las paredes
        compruebaRebotes();
        //Comprueba los choques con los ladrillos
        compruebaChoque();
        //Comprueba los choque con la nave
        compruebaChoqueNave();
        //Comprueba si ha terminado la partida
        puntuacion.compruebaFinPartida();

        //Dibuja la nave
        nave.pinta(g2);
        //Controla el movimiento del raton y mueve la nave en funcion de su posición
        //nave.mueve (movimientoRaton());
        nave.mueve(this, derechaPulsada, izquierdaPulsada);
        for (int i = 0; i < pelotas.length; i++) {
            if (pelotas[i].visible) {
                if (pelotas[i].velocidad == 0) {

                    if (xAntesChoque != 0) {
                        pelotas[i].x = (int) nave.x + (float) xAntesChoque - 15;
                    } else {
                        pelotas[i].x = (int) nave.x + (int) (nave.ancho / 2) - pelotas[i].r - 15;
                    }
                }
            }
        }

        //Dibuja las pelotas
        for (int i = 0; i < numPelotas; i++) {
            pelotas[i].pinta(g2);
        }

        //Dibuja los ladrillos
        for (int j = 0; j < ladrillos.length; j++) {
            for (int i = 0; i < ladrillos[0].length; i++) {
                ladrillos[j][i].pinta(g2);
            }
        }

        //Dibuja powerups
        for (int j = 0; j < power.length; j++) {
            for (int i = 0; i < power[0].length; i++) {
                if (power[j][i] != null) {
                    power[j][i].pinta(g2);
                }
            }
        }

        //Si todo funciona correctamente muestra por pantalla el dibujo
        if (!strategy.contentsLost()) {
            strategy.show();
        }
    }

    public void pintaPausa() {
        //Juego en pausa
        if (pause) {
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

            g2.setFont(font.deriveFont(60f));
            //g2.setColor(new Color(0, 0, 0, 130));
            //g2.fillRect(0, 0, ventana.getWidth(), ventana.getHeight());
            g2.setColor(Color.red);
            g2.drawString("PAUSA", 130, getHeight() / 2);
            //Si todo funciona correctamente muestra por pantalla el dibujo
            if (!strategy.contentsLost()) {
                strategy.show();
            }
        }
    }

    public void pintaFinPartida() {
        //setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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

        if (puntuacion.vidas < 0) {
            g2.setFont(font.deriveFont(45f));
            g2.setColor(Color.black);
            g2.drawString("GAME OVER", 95, 195);
            g2.setColor(Color.red);
            g2.drawString("GAME OVER", 95, 190);
        }
        if (puntuacion.nivel > nivelMaximo) {
            font = font.deriveFont(Font.PLAIN, 35);
            g2.setFont(font);
            g2.setColor(Color.black);
            g2.drawString("ENHORABUENA", 115, 155);
            g2.drawString("¡HAS GANADO!", 105, 195);
            g2.setColor(Color.red);
            g2.drawString("ENHORABUENA", 115, 150);
            g2.drawString("¡HAS GANADO!", 105, 190);
        }

        g2.setColor(Color.BLACK);
        g2.setFont(font.deriveFont(20f));
        g2.drawString("Puntuación total:", 130, 253);
        g2.drawString("" + puntuacion.puntuacion, 235, 283);
        g2.setColor(Color.red);
        g2.setFont(font.deriveFont(20f));
        g2.drawString("Puntuación total:", 130, 250);
        g2.drawString("" + puntuacion.puntuacion, 235, 280);

        introduceNombre();

        //Si todo funciona correctamente muestra por pantalla el dibujo
        if (!strategy.contentsLost()) {
            strategy.show();
        }
    }

    public void introduceNombre() {
        String c1 = Character.toString(primeraLetra);
        String c2 = Character.toString(segundaLetra);
        String c3 = Character.toString(terceraLetra);
        g2.setColor(Color.BLACK);
        g2.fillRect(217, 310, 25, 25);
        g2.fillRect(252, 310, 25, 25);
        g2.fillRect(287, 310, 25, 25);
        g2.setFont(font.deriveFont(35f));
        if (posicion == 1) {
            g2.setColor(Color.white);
            g2.drawString(c1, 215, 335);
            g2.setColor(Color.red);
            g2.drawString(c2, 250, 335);
            g2.drawString(c3, 285, 335);

        } else if (posicion == 2) {
            g2.setColor(Color.white);
            g2.drawString(c2, 250, 335);
            g2.setColor(Color.red);
            g2.drawString(c1, 215, 335);
            g2.drawString(c3, 285, 335);
        } else {
            g2.setColor(Color.white);
            g2.drawString(c3, 285, 335);
            g2.setColor(Color.red);
            g2.drawString(c1, 215, 335);
            g2.drawString(c2, 250, 335);
        }
        g2.setColor(Color.red);
    }

    public int movimientoRaton() {
        int xRaton;
        //Calculo la posicion en x del raton en toda la pantalla y la ajusto al centro de la nave
        //para poder moverla y evito que se salga de mi ventana de juego
        //Toolkit.getDefaultToolkit().getScreenSize().width devuelve la resolucion de la pantalla (ej: 1280)
        int xAbsoluta = MouseInfo.getPointerInfo().getLocation().x - (Toolkit.getDefaultToolkit().getScreenSize().width / 2) + (this.getWidth() / 2) - ((int) nave.ancho / 2);

        if (xAbsoluta < 0) {
            xRaton = 0;
        } else if (xAbsoluta > 494 - nave.ancho) {
            xRaton = 494 - (int) nave.ancho;
        } else {
            xRaton = xAbsoluta;
        }
        for (int i = 0; i < pelotas.length; i++) {
            if (pelotas[i].visible) {
                if (pelotas[i].velocidad == 0) {
                    if (xAntesChoque != 0) {
                        pelotas[i].x = xRaton + (float) xAntesChoque;
                    } else {
                        pelotas[i].x = xRaton + (int) (nave.ancho / 2) - pelotas[i].r;
                    }
                }
            }
        }
        return xRaton;
    }

    public void compruebaRebotes() {
        //Creo un bucle para que compruebe los rebotes de todas las pelotas
        for (int i = 0; i < pelotas.length; i++) {
            //Para trabajar siempre con radianes positivos y evitar errores de rebote
            //en caso de resultar un angulo en radianes negativos lo pasamos
            //a positivo sumándole 2*Math.PI
            if (pelotas[i].dameAngulo() < 0) {
                pelotas[i].angulo = pelotas[i].dameAngulo() + 2 * Math.PI;
            }
            //Si la pelota se sale por arriba la vuelvo a colocar dentro del panel
            //Le restamos a 360 el angulo que lleva la pelota y el resultado 
            //es el angulo contrario después de golpear en el borde superior o inferior
            if (pelotas[i].dameY() < 0 + 15) {
                pelotas[i].y = 0 + 15 + (float) pelotas[i].velocidad;
                pelotas[i].angulo = (360 * Math.PI / 180) - pelotas[i].dameAngulo();
                //Aumenta un poco la velocidad de la pelota al chocar con la parte de arriba
                if (pelotas[i].velocidad < velocidadInicial) { //Si la pelota está relentizada el aumento de la
                    pelotas[i].velocidad += 0.5;               //velocidad es mayor para volver pronto a la velocidad
                } else {                                       //normal
                    pelotas[i].velocidad += 0.05;
                }
            }
            //Si la pelota se sale por abajo significa que has perdido y reinicializo la bola
            if ((pelotas[i].dameY() + pelotas[i].r * 2 > puntuacion.panelY)) {
                pelotas[i].velocidad = 0;
                pelotas[i].angulo = 0;
                pelotas[i].x = 250;//Dejo la pelota dentro del panel pero invisible
                pelotas[i].y = 650;//para que no me cambie el angulo al perder una vida
                pelotas[i].visible = false;
                numeroPelotas--;
                if (numeroPelotas < 1) {
                    pelotas[i].angulo = 0;
                    reiniciaBola();
                    puntuacion.vidas = puntuacion.vidas - 1;
                    playSonido(sonidoMuerte);
                    try {
                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
            }
            //Si la pelota se sale por la derecha o por la izquierda cambio el angulo
            //Le restamos a 180 el angulo que lleva la pelota y el resultado 
            //es el angulo contrario después de golpear en el borde derecho o izquierdo
            if ((pelotas[i].x + 30 + pelotas[i].r * 2 > getWidth()) || (pelotas[i].x < 0)) {
                pelotas[i].angulo = (180 * Math.PI / 180) - pelotas[i].angulo;
            }
            pelotas[i].x += pelotas[i].velocidad * Math.cos(pelotas[i].dameAngulo());
            pelotas[i].y -= pelotas[i].velocidad * Math.sin(pelotas[i].dameAngulo());
        }
    }

    public void compruebaChoque() {

        //Distancia entre el centro de la bola y cada una de las esquinas
        double distanciaArrIzq, distanciaAbIzq, distanciaArrDer, distanciaAbDer;
        boolean esquina = true;
        for (int i = 0; i < pelotas.length; i++) {
            if (numLadrillos != 0) {
                for (int j = 0; j < ladrillos.length; j++) {
                    for (int k = 0; k < ladrillos[0].length; k++) {

                        //Obtengo la distancia entre el centro de la bola y cada una de las esquinas del ladrillo
                        distanciaArrIzq = Math.sqrt(((ladrillos[j][k].x - 15 - pelotas[i].dameCentroX()) * (ladrillos[j][k].x - 15 - pelotas[i].dameCentroX()))
                                + ((ladrillos[j][k].y - pelotas[i].dameCentroY()) * (ladrillos[j][k].y - pelotas[i].dameCentroY())));
                        distanciaAbIzq = Math.sqrt(((ladrillos[j][k].x - 15 - pelotas[i].dameCentroX()) * (ladrillos[j][k].x - 15 - pelotas[i].dameCentroX()))
                                + ((pelotas[i].dameCentroY() - (ladrillos[j][k].y + ladrillos[j][k].alto)) * (pelotas[i].dameCentroY() - (ladrillos[j][k].y + ladrillos[j][k].alto))));
                        distanciaArrDer = Math.sqrt(((pelotas[i].dameCentroX() - (ladrillos[j][k].x - 15 + ladrillos[j][k].ancho)) * (pelotas[i].dameCentroX() - (ladrillos[j][k].x - 15 + ladrillos[j][k].ancho)))
                                + ((ladrillos[j][k].y - pelotas[i].dameCentroY()) * (ladrillos[j][k].y - pelotas[i].dameCentroY())));
                        distanciaAbDer = Math.sqrt(((pelotas[i].dameCentroX() - (ladrillos[j][k].x - 15 + ladrillos[j][k].ancho)) * (pelotas[i].dameCentroX() - (ladrillos[j][k].x - 15 + ladrillos[j][k].ancho)))
                                + ((pelotas[i].dameCentroY() - (ladrillos[j][k].y + ladrillos[j][k].alto)) * (pelotas[i].dameCentroY() - (ladrillos[j][k].y + ladrillos[j][k].alto))));

                        //Si choca por abajo
                        if ((((ladrillos[j][k].y + ladrillos[j][k].alto) >= (pelotas[i].dameY()))
                                && (pelotas[i].dameCentroX() >= ladrillos[j][k].x - 15 && pelotas[i].dameCentroX() <= ladrillos[j][k].x - 15 + ladrillos[j][k].ancho))
                                && (ladrillos[j][k].y < pelotas[i].dameY())) {
                            pelotas[i].angulo = (360 * Math.PI / 180) - pelotas[i].dameAngulo();
                            pelotas[i].y += pelotas[i].velocidad;
                            esquina = false;
                            ladrillos[j][k].resuelveChoque(g2);
                            if (ladrillos[j][k].dimeTipo().equals("Oro")) {
                                playSonido(reboteLadrilloOro);
                            } else {
                                playSonido(reboteLadrillo);
                            }
                            if (!ladrillos[j][k].visible) {
                                numLadrillos--;
                                if (power[j][k] != null) {
                                    power[j][k].visible = true;
                                }
                                puntuacion.puntuacion += 10;
                            }
                        }

                        //Si choca por arriba
                        if ((((ladrillos[j][k].y) <= (pelotas[i].dameY() + (pelotas[i].r * 2)))
                                && (pelotas[i].dameCentroX() >= ladrillos[j][k].x - 15 && pelotas[i].dameCentroX() <= ladrillos[j][k].x - 15 + ladrillos[j][k].ancho))
                                && ((ladrillos[j][k].y + ladrillos[j][k].alto) > (pelotas[i].dameY() + pelotas[i].r * 2))) {
                            pelotas[i].angulo = (360 * Math.PI / 180) - pelotas[i].dameAngulo();
                            esquina = false;
                            pelotas[i].y -= pelotas[i].velocidad;
                            ladrillos[j][k].resuelveChoque(g2);
                            if (ladrillos[j][k].dimeTipo().equals("Oro")) {
                                playSonido(reboteLadrilloOro);
                            } else {
                                playSonido(reboteLadrillo);
                            }
                            if (!ladrillos[j][k].visible) {
                                numLadrillos--;
                                if (power[j][k] != null) {
                                    power[j][k].visible = true;
                                }
                                puntuacion.puntuacion += 10;
                            }
                        }

                        //Si choca por la izquierda
                        if ((((pelotas[i].dameX() + (pelotas[i].r * 2)) >= (ladrillos[j][k].x - 15))
                                && (pelotas[i].dameCentroY() >= ladrillos[j][k].y && pelotas[i].dameCentroY() <= ladrillos[j][k].y + ladrillos[j][k].alto))
                                && ((pelotas[i].dameX() + pelotas[i].r * 2) < (ladrillos[j][k].x - 15 + ladrillos[j][k].ancho))) {
                            pelotas[i].angulo = (180 * Math.PI / 180) - pelotas[i].dameAngulo();
                            esquina = false;
                            pelotas[i].x -= pelotas[i].velocidad;
                            ladrillos[j][k].resuelveChoque(g2);
                            if (ladrillos[j][k].dimeTipo().equals("Oro")) {
                                playSonido(reboteLadrilloOro);
                            } else {
                                playSonido(reboteLadrillo);
                            }
                            if (!ladrillos[j][k].visible) {
                                numLadrillos--;
                                if (power[j][k] != null) {
                                    power[j][k].visible = true;
                                }
                                puntuacion.puntuacion += 10;
                            }
                        }

                        //Si choca por la derecha
                        if (((pelotas[i].dameX() <= (ladrillos[j][k].x - 15 + ladrillos[j][k].ancho))
                                && (pelotas[i].dameCentroY() >= ladrillos[j][k].y && pelotas[i].dameCentroY() <= ladrillos[j][k].y + ladrillos[j][k].alto))
                                && (pelotas[i].dameX() > ladrillos[j][k].x - 15)) {
                            pelotas[i].angulo = (180 * Math.PI / 180) - pelotas[i].dameAngulo();
                            esquina = false;
                            pelotas[i].x += pelotas[i].velocidad;
                            ladrillos[j][k].resuelveChoque(g2);
                            if (ladrillos[j][k].dimeTipo().equals("Oro")) {
                                playSonido(reboteLadrilloOro);
                            } else {
                                playSonido(reboteLadrillo);
                            }
                            if (!ladrillos[j][k].visible) {
                                numLadrillos--;
                                if (power[j][k] != null) {
                                    power[j][k].visible = true;
                                }
                                puntuacion.puntuacion += 10;
                            }
                        }

                        if (esquina) {
                            //En todas las comprobaciones de los choques con esquina he ampliado el radio de la bola
                            //para que el choque sea mas fluido y no devuelva angulos raros
                            //sería como chocar con otra bola con el mismo radio

                            //Si choca en la esquina Superior Izquierda
                            if (distanciaArrIzq < pelotas[i].r - 1.0) {
                                if (ladrillos[j][k].dimeTipo().equals("Oro")) {
                                    playSonido(reboteLadrilloOro);
                                } else {
                                    playSonido(reboteLadrillo);
                                }
                                //pelotas[i].angulo = (Math.PI) - (Math.sin((ladrillos[j][k].y - pelotas[i].dameCentroY()) / (pelotas[i].r * 2)));
                                //Pongo un angulo fijo para evitar que la pelota se quede rebotando horizontalmete
                                pelotas[i].angulo = 135 * Math.PI / 180;
                                //Alejo la pelota del cuadrado para evitar que entre dentro del ladrillo
                                pelotas[i].x = pelotas[i].x - 1;
                                pelotas[i].y = pelotas[i].y - 1;
                                ladrillos[j][k].resuelveChoque(g2);
                                if (!ladrillos[j][k].visible) {
                                    numLadrillos--;
                                    if (power[j][k] != null) {
                                        power[j][k].visible = true;
                                    }
                                    puntuacion.puntuacion += 10;
                                }
                            }

                            //Si choca en la esquina Inferior Izquierda
                            if (distanciaAbIzq < pelotas[i].r - 1.0) {
                                if (ladrillos[j][k].dimeTipo().equals("Oro")) {
                                    playSonido(reboteLadrilloOro);
                                } else {
                                    playSonido(reboteLadrillo);
                                }
                                //pelotas[i].angulo = (Math.PI) - (Math.sin((pelotas[i].dameCentroY() - (ladrillos[j][k].y + ladrillos[j][k].alto) / (pelotas[i].r * 2))));
                                //Pongo un angulo fijo para evitar que la pelota se quede rebotando horizontalmete
                                pelotas[i].angulo = 225 * Math.PI / 180;
                                //Alejo la pelota del cuadrado para evitar que entre dentro del ladrillo
                                pelotas[i].x = pelotas[i].x - 1;
                                pelotas[i].y = pelotas[i].y + 1;
                                ladrillos[j][k].resuelveChoque(g2);
                                if (!ladrillos[j][k].visible) {
                                    numLadrillos--;
                                    if (power[j][k] != null) {
                                        power[j][k].visible = true;
                                    }
                                    puntuacion.puntuacion += 10;
                                }
                            }

                            //Si choca en la esquina Inferior Derecha
                            if (distanciaAbDer < pelotas[i].r - 1.0) {
                                if (ladrillos[j][k].dimeTipo().equals("Oro")) {
                                    playSonido(reboteLadrilloOro);
                                } else {
                                    playSonido(reboteLadrillo);
                                }
                                //pelotas[i].angulo = (2 * Math.PI) - Math.sin(((pelotas[i].dameCentroY() - ladrillos[j][k].y + ladrillos[j][k].alto) / pelotas[i].r * 2));
                                //Pongo un angulo fijo para evitar que la pelota se quede rebotando horizontalmete
                                pelotas[i].angulo = 315 * Math.PI / 180;
                                //Alejo la pelota del cuadrado para evitar que entre dentro del ladrillo
                                pelotas[i].x = pelotas[i].x + 1;
                                pelotas[i].y = pelotas[i].y + 1;
                                ladrillos[j][k].resuelveChoque(g2);
                                if (!ladrillos[j][k].visible) {
                                    numLadrillos--;
                                    if (power[j][k] != null) {
                                        power[j][k].visible = true;
                                    }
                                    puntuacion.puntuacion += 10;
                                }
                            }
                            //Si choca en la esquina Superior Derecha
                            if (distanciaArrDer < pelotas[i].r - 1.0) {
                                if (ladrillos[j][k].dimeTipo().equals("Oro")) {
                                    playSonido(reboteLadrilloOro);
                                } else {
                                    playSonido(reboteLadrillo);
                                }
                                //pelotas[i].angulo = (2 * Math.PI) - Math.sin(((ladrillos[j][k].y - pelotas[i].dameCentroY()) / pelotas[i].r * 2));
                                //Pongo un angulo fijo para evitar que la pelota se quede rebotando horizontalmete
                                pelotas[i].angulo = 45 * Math.PI / 180;
                                //Alejo la pelota del cuadrado para evitar que entre dentro del ladrillo
                                pelotas[i].x = pelotas[i].x + 1;
                                pelotas[i].y = pelotas[i].y - 1;
                                ladrillos[j][k].resuelveChoque(g2);
                                if (!ladrillos[j][k].visible) {
                                    numLadrillos--;
                                    if (power[j][k] != null) {
                                        power[j][k].visible = true;
                                    }
                                    puntuacion.puntuacion += 10;
                                }
                            }
                        }
                        if (numLadrillos == 0) {
                            siguienteNivel();
                        }
                    }
                }
            }
        }
    }

    public void compruebaChoqueNave() {
        double distanciaArrIzq, distanciaArrDer;
        boolean esquina = true;
        for (int i = 0; i < pelotas.length; i++) {
            //Obtengo la distancia entre el centro de la bola y cada una de las esquinas de la nave
            distanciaArrIzq = Math.sqrt(((nave.x - 15 - pelotas[i].dameCentroX()) * (nave.x - 15 - pelotas[i].dameCentroX()))
                    + ((nave.y - pelotas[i].dameCentroY()) * (nave.y - pelotas[i].dameCentroY())));
            distanciaArrDer = Math.sqrt(((pelotas[i].dameCentroX() - (nave.x - 15 + nave.ancho)) * (pelotas[i].dameCentroX() - (nave.x - 15 + nave.ancho)))
                    + ((nave.y - pelotas[i].dameCentroY()) * (nave.y - pelotas[i].dameCentroY())));

            if ((nave.y <= pelotas[i].dameY() + pelotas[i].r * 2) && (nave.y + nave.alto > pelotas[i].dameY() + pelotas[i].r * 2)) {
                for (int z = 0; z < pelotas.length; z++) {
                    if (pelotas[i].velocidad < velocidadInicial) { //Si la pelota está relentizada el aumento de la
                        pelotas[i].velocidad += 0.5;               //velocidad es mayor para volver pronto a la velocidad
                    } else {                                       //normal
                        pelotas[i].velocidad += 0.02;
                    }
                }
                if (pelotas[i].dameCentroX() >= nave.x - 15 && pelotas[i].dameCentroX() <= nave.x - 15 + nave.ancho) {
                    esquina = false;
                    //Reproduzco el sonido del golpeo con la nave
                    if (nave.pegamento) {
                        playSonido(sonidoPegamento);
                    } else {
                        playSonido(reboteNave);
                    }
                }
                //Choca por 1
                if (pelotas[i].dameCentroX() >= nave.x - 15 && pelotas[i].dameCentroX() <= nave.x - 15 + (nave.ancho * 0.10)) {
                    pelotas[i].angulo = (157.5 * Math.PI / 180);
                    esquina = false;
                }
                //Choca por 2
                if (pelotas[i].dameCentroX() >= nave.x - 15 + (nave.ancho * 0.10) && pelotas[i].dameCentroX() <= nave.x - 15 + (nave.ancho * 0.24)) {
                    pelotas[i].angulo = (135 * Math.PI / 180);
                    esquina = false;
                }
                //Choca por 3
                if (pelotas[i].dameCentroX() >= nave.x - 15 + (nave.ancho * 0.24) && pelotas[i].dameCentroX() <= nave.x - 15 + (nave.ancho * 0.414)) {
                    pelotas[i].angulo = (112.5 * Math.PI / 180);
                    esquina = false;
                }
                //Choca por 4
                if (pelotas[i].dameCentroX() >= nave.x - 15 + (nave.ancho * 0.414) && pelotas[i].dameCentroX() <= nave.x - 15 + nave.ancho - (nave.ancho * 0.414)) {
                    pelotas[i].angulo = (360 * Math.PI / 180) - pelotas[i].dameAngulo();
                    esquina = false;
                }
                //Choca por 5
                if (pelotas[i].dameCentroX() >= nave.x - 15 + nave.ancho - (nave.ancho * 0.414) && pelotas[i].dameCentroX() <= nave.x - 15 + nave.ancho - (nave.ancho * 0.24)) {
                    pelotas[i].angulo = (67.5 * Math.PI / 180);
                    esquina = false;
                }
                //Choca por 6
                if (pelotas[i].dameCentroX() >= nave.x - 15 + nave.ancho - (nave.ancho * 0.24) && pelotas[i].dameCentroX() <= nave.x - 15 + nave.ancho - (nave.ancho * 0.10)) {
                    pelotas[i].angulo = (45 * Math.PI / 180);
                    esquina = false;
                }
                //Choca por 7
                if (pelotas[i].dameCentroX() >= nave.x - 15 + nave.ancho - (nave.ancho * 0.10) && pelotas[i].dameCentroX() <= nave.x - 15 + nave.ancho) {
                    pelotas[i].angulo = (22.5 * Math.PI / 180);
                    esquina = false;
                }
                //Choca en alguna esquina            
                if (esquina) {
                    //Si choca en la esquina Superior Izquierda
                    if (distanciaArrIzq < pelotas[i].r) {
                        pelotas[i].angulo = 150 * Math.PI / 180;
                        if (nave.pegamento) {
                            playSonido(sonidoPegamento);
                        } else {
                            playSonido(reboteNave);
                        }
                    }

                    //Si choca en la esquina Superior Derecha
                    if (distanciaArrDer < pelotas[i].r) {
                        pelotas[i].angulo = 30 * Math.PI / 180;
                        if (nave.pegamento) {
                            playSonido(sonidoPegamento);
                        } else {
                            playSonido(reboteNave);
                        }
                    }
                }

                xAntesChoque = (5 + pelotas[i].x + pelotas[i].r * 2) - nave.x;
                //Compruebo si tengo el PowerUp de pegamento y si lo tengo paro la pelota
                if (nave.pegamento && (pelotas[i].dameCentroX() >= nave.x - 15 && pelotas[i].dameCentroX() <= nave.x - 15 + nave.ancho)) {
                    velocidadAux = pelotas[i].velocidad;
                    pelotas[i].velocidad = 0;
                    pelotas[i].y = (int) nave.y - (pelotas[i].r * 2 + 1);
                }
            }
        }
    }

    MouseListener ml = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            /*if (!puntuacion.combruebaFinPartida()) {
                for (int i = 0; i < numPelotas; i++) {
                    if (pelotas[i].visible) {
                        pelotas[i].velocidad = velocidadAux;
                    }
                }
            }*/
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
            //Vuelvo a poner el raton dentro de la ventana cuando se sale por la derecha o la izquierda
            Robot robot = null;
            try {
                robot = new Robot();
            } catch (AWTException aWTException) {
            }
            if (e.getX() <= 0 && robot != null) {
                robot.mouseMove(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - (ventana.getWidth() / 2) + ((int) nave.ancho / 2), e.getYOnScreen());
            }
            if (e.getX() >= ventana.getWidth() / 2 && robot != null) {
                robot.mouseMove(Toolkit.getDefaultToolkit().getScreenSize().width / 2 + (ventana.getWidth() / 2) - ((int) nave.ancho / 2), e.getYOnScreen());
            }
        }
    };

    KeyListener kl = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (!puntuacion.compruebaFinPartida()) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    derechaPulsada = true;
                    //nave.x += 20;
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    izquierdaPulsada = true;
                    //nave.x -= 20;
                }
            }

            if (puntuacion.compruebaFinPartida()) {
                if ((e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode() == KeyEvent.VK_DOWN)) {
                    if (letraActiva >= 65 && letraActiva <= 90) {
                        if ((e.getKeyCode() == KeyEvent.VK_UP)) {
                            if (letraActiva == 90) {
                                letraActiva = 64;
                            }
                            letraActiva++;
                        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                            if (letraActiva == 65) {
                                letraActiva = 91;
                            }
                            letraActiva--;
                        }
                    }
                    switch (posicion) {
                        case 1:
                            primeraLetra = letraActiva;
                            break;
                        case 2:
                            segundaLetra = letraActiva;
                            break;
                        case 3:
                            terceraLetra = letraActiva;
                            break;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (posicion == 1) {
                        posicion = 2;
                        letraActiva = segundaLetra;
                    } else if (posicion == 2) {
                        posicion = 3;
                        letraActiva = terceraLetra;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (posicion == 2) {
                        posicion = 1;
                        letraActiva = primeraLetra;
                    } else if (posicion == 3) {
                        posicion = 2;
                        letraActiva = segundaLetra;
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if ((e.getKeyChar() == 'p' || e.getKeyChar() == 'P') && !pause) {
                pause = true;
            } else if ((e.getKeyChar() == 'p' || e.getKeyChar() == 'P') && pause) {
                pause = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_F1) {
                pelotas[0].angulo = anguloInicial;
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
            if (puntuacion.compruebaFinPartida()) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    nombre[0] = primeraLetra;
                    nombre[1] = segundaLetra;
                    nombre[2] = terceraLetra;
                    nombre[3] = (int) puntuacion.puntuacion;

                    guardaPuntuacion(nombre);
                    pantallaTopTen = true;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (!puntuacion.compruebaFinPartida()) {
                    for (int i = 0; i < numPelotas; i++) {
                        if (pelotas[i].visible) {
                            pelotas[i].velocidad = velocidadAux;
                            playSonido(reboteNave);
                        }
                    }
                }
            }
            if (!puntuacion.compruebaFinPartida()) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    derechaPulsada = false;
                    //nave.x += 20;
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    izquierdaPulsada = false;
                    //nave.x -= 20;
                }
            }
        }
    };

    public void guardaPuntuacion(int[] nomb) {
        int j = listaPuntuaciones.size();
        int p;
        while (j >= 0) {
            try {
                p = Integer.parseInt(listaPuntuaciones.get(j).substring(4));
            } catch (Exception e) {
                p = 0;
            }

            if (puntuacion.puntuacion > p) {
                j--;
                if (j == -1) {
                    listaPuntuaciones.add(0, "" + (char) nomb[0] + "" + (char) nomb[1] + "" + (char) nomb[2] + "-" + puntuacion.puntuacion);
                }
            } else {
                listaPuntuaciones.add(j + 1, "" + (char) nomb[0] + "" + (char) nomb[1] + "" + (char) nomb[2] + "-" + puntuacion.puntuacion);
                break;
            }
        }

        try {
            File miDir = new File(".");
            File archivo = new File(miDir.getCanonicalPath() + File.separator + "ficheros" + File.separator + "puntuaciones.txt");
            BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo));
            for (int i = 0; i < listaPuntuaciones.size(); i++) {
                escritor.write(listaPuntuaciones.get(i));
                escritor.newLine();
            }
            escritor.close();
        } catch (IOException e) {
        }
    }

    private void siguienteNivel() {
        playSonido(sonidoSiguienteNivel);
        puntuacion.nivel++;
        borraPowerUps();
        reiniciaBola();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        if (puntuacion.nivel <= nivelMaximo) {
            ladrillos = level.crearNivel(puntuacion.nivel);
            numLadrillos = level.numLadrillos;
        } else {
            if (puntuacion.compruebaFinPartida()) {
                numLadrillos = -1;
            }
        }
        power = level.power;
        //int numFondo = (int) Math.floor(Math.random() * 9); //9 es el numero de fondos distintos en la carpeta fondos
        fondo = fondo = new ImageIcon(getClass().getResource("/images/fondos/fondo" + (int)(Math.random()*10) + ".jpg"));
        //fondo = fondo = new ImageIcon(getClass().getResource("/images/fondos/fondo" + puntuacion.nivel + ".jpg"));
    }

    public void reiniciaBola() {
        //Paro la bola, la coloco encima de la nave y le asigno su angulo inicial
        numeroPelotas = numPelotas; //Asigno de nuevo al numero de pelotas actuales el de las iniciales
        velocidadAux = velocidadInicial;
        borraPowerUps();
        xAntesChoque = 0;
        for (int i = 0; i < numPelotas; i++) {
            pelotas[i].velocidad = 0;
            pelotas[i].angulo = 0;
            pelotas[i].visible = true;
            pelotas[i].y = (int) nave.y - (pelotas[i].r * 2 + 1);
            if (i == 0) {
                pelotas[i].angulo = (anguloInicial) * Math.PI / 180;
            } else {
                pelotas[i].angulo = (int) Math.floor(Math.random() * 180);
            }
        }
        nave.nave = new ImageIcon(getClass().getResource("/images/Nave.png"));
        nave.ancho = 75;
        power = level.power;
    }

    private void creaPelotas(int numero) {
        //Crea las pelotas (posicion inicial en x, posicion inicial en y, diametro, angulo inicial, color)
        //Math.rondom()*360 devuelve un numero entre 0.0 y 359.99 
        pelotas = new Pelota[numPelotas];
        for (int i = 0; i < numero; i++) {
            if (i == 0) {
                pelotas[i] = new Pelota(245 + 15, 514, 10, anguloInicial, Color.green, velocidad);
            } else {
                pelotas[i] = new Pelota(245 + 15, 514, 10, Math.random() * 180, Color.green, velocidad);
            }
        }
    }

    private void borraPowerUps() {
        nave.pegamento = false;

        for (int j = 0; j < power.length; j++) {
            for (int i = 0; i < power[0].length; i++) {
                if (power[j][i] != null) {
                    power[j][i].visible = false;
                }
            }
        }
    }

    public ArrayList<String> leerPuntuaciones() {
        File miDir = new File(".");
        File fichero = null;
        FileReader fr = null;
        BufferedReader br = null;
        String linea;

        try {
            fichero = new File(miDir.getCanonicalPath() + File.separator + "ficheros" + File.separator + "puntuaciones.txt");
            fr = new FileReader(fichero);
            br = new BufferedReader(fr);
            while ((linea = br.readLine()) != null) {
                listaPuntuaciones.add(linea);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println(e);
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e2) {
                System.out.println(e2);
            }
        }
        return listaPuntuaciones;
    }

    public static synchronized void playMusicaFondo(final String musica) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    InputStream in = new BufferedInputStream(this.getClass().getResourceAsStream(musica));
                    Clip clipFondo = AudioSystem.getClip();
                    clipFondo.open(AudioSystem.getAudioInputStream(in));
                    clipFondo.loop(Clip.LOOP_CONTINUOUSLY);
                    clipFondo.start();
                } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                    System.err.println(e);
                }
            }
        }).start();
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
