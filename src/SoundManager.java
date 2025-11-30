class SoundManager {
    private static final String SOUND_PATH = "resources/sounds/";

    // Sound clips
    private Clip moveClip;
    private Clip diceRollClip;
    private Clip ladderClip;
    private Clip victoryClip;

    public SoundManager() {
        try {
            // Load all sound files
            moveClip = loadSound("move.wav");
            diceRollClip = loadSound("dice_roll.wav");
            ladderClip = loadSound("ladder.wav");
            victoryClip = loadSound("victory.wav");
        } catch (Exception e) {
            System.err.println("Warning: Could not load sound files. Make sure sound files exist in " + SOUND_PATH);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private Clip loadSound(String filename) {
        try {
            // Try to load from resources folder
            File soundFile = new File(SOUND_PATH + filename);

            if (!soundFile.exists()) {
                System.err.println("Sound file not found: " + soundFile.getAbsolutePath());
                return null;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (Exception e) {
            System.err.println("Error loading sound: " + filename + " - " + e.getMessage());
            return null;
        }
    }

    public void playMove() {
        playSound(moveClip);
    }

    public void playDiceRoll() {
        playSound(diceRollClip);
    }

    public void playLadder() {
        playSound(ladderClip);
    }

    public void playVictory() {
        playSound(victoryClip);
    }

    private void playSound(Clip clip) {
        if (clip != null) {
            // Stop if already playing
            if (clip.isRunning()) {
                clip.stop();
            }
            // Reset to beginning
            clip.setFramePosition(0);
            // Play
            clip.start();
        }
    }

    // Cleanup method
    public void cleanup() {
        if (moveClip != null) moveClip.close();
        if (diceRollClip != null) diceRollClip.close();
        if (ladderClip != null) ladderClip.close();
        if (victoryClip != null) victoryClip.close();
    }
}