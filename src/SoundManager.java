import javax.sound.sampled.*;
import java.io.File;


public class SoundManager {

    private Clip diceClip;
    private Clip moveClip;
    private Clip ladderClip;
    private Clip victoryClip;
    private Clip themeClip;

    private FloatControl themeVolume;

    public SoundManager() {
        diceClip    = loadSound("resources/sounds/dice_roll.wav");
        moveClip    = loadSound("resources/sounds/move.wav");
        ladderClip  = loadSound("resources/sounds/ladder.wav");
        victoryClip = loadSound("resources/sounds/victory.wav");
        themeClip   = loadSound("resources/sounds/theme_song.wav");

        setupThemeVolume();
        playThemeLoop();
    }

    private Clip loadSound(String path) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            System.out.println("Failed to load sound: " + path);
            e.printStackTrace();
            return null;
        }
    }

    private void playOnce(Clip clip) {
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void playDice() {
        playOnce(diceClip);
    }

    public void playMove() {
        playOnce(moveClip);
    }

    public void playVictory() {
        playOnce(victoryClip);
    }

    // Mendapatkan durasi sound dadu dalam milliseconds
    public long getDiceDuration() {
        if (diceClip == null) return 0;
        return diceClip.getMicrosecondLength() / 1000;
    }


    private void setupThemeVolume() {
        if (themeClip == null) return;

        if (themeClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            themeVolume = (FloatControl) themeClip.getControl(FloatControl.Type.MASTER_GAIN);
            float currentVolume = themeVolume.getValue();
            themeVolume.setValue(currentVolume - 3.0f); // Turunkan 3 dB
        }
    }

    private void playThemeLoop() {
        if (themeClip == null) return;

        new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    themeClip.setFramePosition(0);
                    themeClip.start();

                    // Tunggu sampai audio selesai diputar
                    Thread.sleep(themeClip.getMicrosecondLength() / 1000);

                    // Jeda 1 detik sebelum loop berikutnya
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    // Method untuk stop theme (jika diperlukan)
    public void stopTheme() {
        if (themeClip != null && themeClip.isRunning()) {
            themeClip.stop();
        }
    }
}