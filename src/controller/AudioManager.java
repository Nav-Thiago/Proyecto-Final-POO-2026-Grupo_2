package controller;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioManager {

    private static Clip bgmClip;
    private static FloatControl volumeControl;

    private static int currentVolume = 50;

    public static void playMusic(String filePath) {
        try {
            if (bgmClip != null && bgmClip.isRunning()) {
                return;
            }

            java.net.URL audioUrl = AudioManager.class.getResource(filePath);
            if (audioUrl == null) {
                System.err.println("No se encontró el archivo de audio: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioUrl);

            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);

            volumeControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume(currentVolume);

            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error al reproducir audio: " + e.getMessage());
        }
    }

    public static void stopMusic() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    public static void setVolume(int percentage) {
        if (volumeControl != null) {
            if (percentage <= 0) {
                volumeControl.setValue(volumeControl.getMinimum());
            } else {
                float dB = (float) (Math.log10((double) percentage / 100.0) * 20.0);
                volumeControl.setValue(dB);
            }
        }
    }
}
