package controller;

import model.SoundEvent;

import javax.sound.sampled.*;
import java.io.IOException;

public class AudioManager {

    private static Clip bgmClip;
    private static FloatControl volumeControl;

    private static int currentVolume = 50;
    private static int sfxVolume = 50;

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
        currentVolume = Math.max(0, Math.min(100, percentage));
        if (volumeControl == null) {
            return;
        }
        if (currentVolume == 0) {
            volumeControl.setValue(volumeControl.getMinimum());
            return;
        }
        float dB = (float) (Math.log10(currentVolume / 100.0) * 20.0);
        float limitedDb = Math.max(volumeControl.getMinimum(), Math.min(volumeControl.getMaximum(), dB));
        volumeControl.setValue(limitedDb);
    }

    public static void setSfxVolume(int percentage) {
        sfxVolume = Math.max(0, Math.min(100, percentage));
    }

    public static int getSfxVolume() {
        return sfxVolume;
    }

    public static int getVolume() {
        return currentVolume;
    }

    public static void playSfx(SoundEvent event) {
        if (event == null)
            return;

        String filePath = switch (event) {
            case HANDGUN_SHOT -> "/Resources/audio/handgun_shot.wav";
            case RIFLE_SHOT -> "/Resources/audio/rifle_shot.wav";
            case SHOTGUN_SHOT -> "/Resources/audio/shotgun_shot.wav";
            default -> null;
        };

        if (filePath != null) {
            playSound(filePath);
        }
    }

    private static void playSound(String filePath) {
        try {
            java.net.URL audioUrl = AudioManager.class.getResource(filePath);
            if (audioUrl == null) {
                System.err.println("No se encontró el archivo de audio: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioUrl);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (sfxVolume <= 0) {
                    gainControl.setValue(gainControl.getMinimum());
                } else {
                    float dB = (float) (Math.log10((double) sfxVolume / 100.0) * 20.0);
                    gainControl.setValue(Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB)));
                }
            }

            // cerramos el clip apenas termina para no consumir memoria
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error al reproducir SFX: " + e.getMessage());
        }
    }
}
