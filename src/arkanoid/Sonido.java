package arkanoid;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sonido {
    Clip sound;
    String archivo;
    public Sonido(String archivo) {
        this.archivo = archivo;
        sound = getSound(archivo);
    }

    public Clip getSound(String file) {
        try {
            String fic = getClass().getResource(archivo).toString();
            fic = fic.substring(6);
            //AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("sonidos" + System.getProperty("file.separator") + file).getAbsoluteFile());
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fic));
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip sonido = (Clip) AudioSystem.getLine(info);
            sonido.open(audioInputStream);
            return sonido;
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            return null;
        }
    }

    public void playSound(Clip clip) {
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }
}
