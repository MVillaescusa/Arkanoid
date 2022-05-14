package arkanoid;

import java.awt.Color;

public class NivelLadrillos {

    int nivel;
    Lienzo lienzo;
    Nave nave;
    Puntuacion puntuacion;
    int numLadrillos;
    Color color;
    PowerUp[][] power;
    Pelota[] pelotas;

    public NivelLadrillos(int lvl, Lienzo lien, Nave nav, Puntuacion punt, Pelota[] pelot) {
        nivel = lvl;
        lienzo = lien;
        puntuacion = punt;
        nave = nav;
        pelotas = pelot;
    }

    public Ladrillo[][] crearNivel(int nivel) {
        int y, posX, posY, dureza, powerUp;
        boolean visible;
        numLadrillos = 0;

        Ladrillo[][] arrayLadrillos;
        arrayLadrillos = new Ladrillo[16][13];
        power = new PowerUp[16][13];
        //Creo el array del nivel
        Nivel lvl = new Nivel();
        int[][][] arrayNivel = lvl.crearNivelTeorico(nivel);

        posX = 0;
        posY = 0;
        dureza = 0;
        visible = false;
        powerUp = 0;

        for (int i = 0; i < arrayNivel.length; i++) {
            //A los ladrillos de cada fila les asigno un color aleatorio
            //entre Blanco, Cyan, Verde, Rojo, Azul, Morado o Amarillo
            int rojo = (int) Math.floor(Math.random() * 2) * 255;
            int verde = (int) Math.floor(Math.random() * 2) * 255;
            int azul = (int) Math.floor(Math.random() * 2) * 255;
            //Si el color resultante es negro lo cambio a naranja
            if (rojo == 0 && verde == 0 && azul == 0) {
                rojo = 255;
                verde = 127;
            }
            color = new Color(rojo, verde, azul);
            //Paso el array del nivel al array de ladrillos para dibujarlos
            for (int j = 0; j < arrayNivel[0].length; j++) {
                //Para dejar las dos filas de arriba vacias y empezar a colocar ladrillos en la tercera
                //le sumo 2 a i
                y = (i + 2) * 17;
                for (int k = 0; k < arrayNivel[0][0].length; k++) {
                    switch (k) {
                        case 0:
                            if (arrayNivel[i][j][k] == 1) {
                                posX = j * 38;
                                posY = y;
                                dureza = 1;
                                visible = true;
                            } else if (arrayNivel[i][j][k] == 2) {
                                posX = j * 38;
                                posY = y;
                                dureza = 2;
                                visible = true;
                            } else if (arrayNivel[i][j][k] == 3) {
                                posX = j * 38;
                                posY = y;
                                dureza = 3;
                                visible = true;
                            } else {
                                posX = -1000;
                                posY = -1000;
                                dureza = 0;
                                visible = false;
                            }
                            break;
                        case 1:
                            switch (arrayNivel[i][j][k]) {
                                case 1:
                                    //Rosa
                                    color = new Color(255, 255, 255);
                                    break;
                                case 2:
                                    //Naranja
                                    color = new Color(255, 127, 0);
                                    break;
                                case 3:
                                    //Cyan
                                    color = new Color(0, 255, 255);
                                    break;
                                case 4:
                                    //Verde
                                    color = new Color(0, 255, 0);
                                    break;
                                case 5:
                                    //Rojo
                                    color = new Color(255, 0, 0);
                                    break;
                                case 6:
                                    //Azul
                                    color = new Color(0, 0, 255);
                                    break;
                                case 7:
                                    //Morado
                                    color = new Color(255, 0, 255);
                                    break;
                                case 8:
                                    //Amarillo
                                    color = new Color(255, 255, 0);
                                    break;
                                default:
                                    color = new Color(0, 0, 0);
                                    break;
                            }
                            break;
                        case 2:
                            if (arrayNivel[i][j][k] == 1) {
                                power[i][j] = new PowerUp(posX, posY, 1, nave, puntuacion, pelotas);
                                powerUp = 1;
                            } else if (arrayNivel[i][j][k] == 2) {
                                power[i][j] = new PowerUp(posX, posY, 2, nave, puntuacion, pelotas);
                                powerUp = 2;
                            } else if (arrayNivel[i][j][k] == 3) {
                                power[i][j] = new PowerUp(posX, posY, 3, nave, puntuacion, pelotas);
                                powerUp = 3;
                            } else if (arrayNivel[i][j][k] == 4) {
                                power[i][j] = new PowerUp(posX, posY, 4, nave, puntuacion, pelotas);
                                powerUp = 4;
                            } else if (arrayNivel[i][j][k] == 5) {
                                power[i][j] = new PowerUp(posX, posY, 5, nave, puntuacion, pelotas);
                                powerUp = 4;
                            } else {
                                powerUp = 0;
                            }
                            break;
                    }
                }
                switch (dureza) {
                    case 2:
                        arrayLadrillos[i][j] = new Ladrillo(posX, posY, dureza, new Color(150, 150, 150), powerUp, visible);
                        break;
                    case 3:
                        arrayLadrillos[i][j] = new Ladrillo(posX, posY, dureza, new Color(230, 150, 0), powerUp, visible);
                        break;
                    default:
                        arrayLadrillos[i][j] = new Ladrillo(posX, posY, dureza, color, powerUp, visible);
                        break;
                }
                if (visible && dureza != 3) {
                    numLadrillos++;
                }
            }
        }
        return arrayLadrillos;
    }
}
