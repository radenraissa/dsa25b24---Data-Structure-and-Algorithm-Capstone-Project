import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

class PacmanSoundManager {

    private Clip themeClip;
    private Clip moveClip;
    private Clip finishClip;

    public PacmanSoundManager() {
        themeClip  = loadClip("resources/sounds/pacman_themesong.wav");
        moveClip   = loadClip("resources/sounds/pacman_move.wav");
        finishClip = loadClip("resources/sounds/pacman_finish.wav");
    }


    private Clip loadClip(String path) {
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

    public void startTheme() {
        if (themeClip == null) return;

        if (themeClip.isRunning())
            themeClip.stop();

        themeClip.setFramePosition(0);
        themeClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // Dipanggil saat exit / close Pacman window
    public void stopTheme() {
        if (themeClip != null && themeClip.isRunning()) {
            themeClip.stop();
        }
    }

    /* ===================== PACMAN MOVE ===================== */

    // Dipanggil saat Pacman MULAI mencari path
    public void startMove() {
        if (moveClip == null) return;

        if (moveClip.isRunning())
            moveClip.stop();

        moveClip.setFramePosition(0);
        moveClip.start();
    }

    // Dipanggil saat Pacman SELESAI sebelum 6 detik
    public void stopMove() {
        if (moveClip != null && moveClip.isRunning()) {
            moveClip.stop();
        }
    }

    /* ===================== FINISH SOUND ===================== */

    // Dipanggil SETELAH stopMove()
    public void playFinish() {
        if (finishClip == null) return;

        if (finishClip.isRunning())
            finishClip.stop();

        finishClip.setFramePosition(0);
        finishClip.start();
    }

    /* ===================== CLEANUP ===================== */

    // Optional: dipanggil saat benar-benar keluar game
    public void releaseAll() {
        stopTheme();
        stopMove();

        if (finishClip != null) finishClip.stop();
    }
}