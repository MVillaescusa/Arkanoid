package arkanoid;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;

public class Arkanoid {

    static Lienzo lienzo;
    static JFrame ventana;
    static Puntuacion puntuacion;
    static PantallaInicio inicio;
    static PantallaTopTen topTen;

    public static void main(String[] args) /*throws InterruptedException*/ {
        //Creo la carpeta para almacenar ficheros si no existe
        creaDirectorioFicherosSiNoExiste();

        //Creo una ventana
        ventana = new JFrame("Arkanoid by Mario");

        //Ajustamos el tamaño de la ventana y evitamos que cambie de tamaño
        ventana.setSize(530, 730);
        ventana.setResizable(false);

        //La coloco en el centro de la pantalla
        ventana.setLocationRelativeTo(null);

        //Le digo que se cierre al pulsar la "X"
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        do {
            //Oculto el puntero
            ocultaPuntero();
            
            inicio = new PantallaInicio(ventana);
            ventana.getContentPane().add(inicio);

            //Hacemos visible la ventana
            ventana.setVisible(true);
            inicio.requestFocus();//Le otorgo el foco a la ventana de inicio

            while (!inicio.inicioPartida) {
                inicio.play();
            }
            //Quito de memoria la pantalla de inicio
            ventana.getContentPane().remove(inicio);

            //Creo un objeto lienzo (canvas) donde vamos poner el bucle que se va 
            //a repetir todo el rato dibujando constantemente.
            //Lo creo despues de hacer visible la ventana para que al salir de la
            //pantalla de inicio el nivel ya esté cargado en memoria
            lienzo = new Lienzo(ventana, inicio.nivelInicial);
            lienzo.setBackground(Color.black);

            //Se lo añadimos a la ventana
            ventana.getContentPane().add(lienzo);

            //Muestro la pantalla de juego cargada previamente
            ventana.setVisible(true);
            lienzo.requestFocus();//Le otrogo el foco a la ventana de juego

            //Inicia el programa
            lienzo.play();

            topTen = new PantallaTopTen(lienzo.listaPuntuaciones);
            ventana.getContentPane().add(topTen);

            ventana.remove(lienzo);
            ventana.setVisible(true);
            topTen.requestFocus();//Le otrogo el foco a la ventana de puntuaciones
            while (!topTen.replay) {
                topTen.pintaPuntuaciones();
            }
            ventana.remove(topTen);
        } while (topTen.replay);
    }

    public static void ocultaPuntero() {
        // Creo una imagen transparente de 16x16 pixeles
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Creo un nuevo cursor con la imagen anterior
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");

        // Asigno el cursor a la ventana
        ventana.setCursor(blankCursor);
    }

    public static void creaDirectorioFicherosSiNoExiste() {
        File directorio = new File("ficheros");
        directorio.mkdir(); //Crea un directorio si no existe, si existe no lo modifica

        try {
            File archivo = new File(directorio.getCanonicalPath() + File.separator + "puntuaciones.txt");
            if (!archivo.exists()) {
                archivo.createNewFile();
                System.out.println(directorio.getCanonicalPath() + File.separator + "puntuaciones.txt");
            }
        } catch (IOException iOException) {
        }
    }
}
