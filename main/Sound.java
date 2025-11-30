package main;

import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.AudioInputStream;

public class Sound {
    Clip clip; // used to open audio files
    URL soundURL[] = new URL[30]; // array to store sound file paths
    FloatControl fc;
    int volumeScale = 3; // volume scale from 0 to 5
    float volume;

    public Sound() {
        soundURL[0] = getClass().getResource("/res/sound/IDV Voice of Spring.wav");
        soundURL[1] = getClass().getResource("/res/sound/Big Egg collect 1.wav");
        soundURL[2] = getClass().getResource("/res/sound/Select 1.wav");
        soundURL[3] = getClass().getResource("/res/sound/Confirm 1.wav");
        soundURL[4] = getClass().getResource("/res/sound/Boss hit 1.wav");
        soundURL[5] = getClass().getResource("/res/sound/IDV original soundtrack.wav");
    }

    public void setFile(int i) {

        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkVolume();
    }

    public void play() {
        clip.start();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        clip.stop();
    }

    public void checkVolume() {
        switch (volumeScale) {
            case 0: volume = -80f; break;
            case 1: volume = -20f; break;
            case 2: volume = -12f; break;
            case 3: volume = -5f; break;
            case 4: volume = 1f; break;
            case 5: volume = 6f; break;
        }
        fc.setValue(volume);
    }
}
