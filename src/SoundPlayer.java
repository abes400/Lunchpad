/*
 * Lunchpad - A Launchpad/Soundboard application.
 * This file is part of Lunchpad.
 *
 * Lunchpad is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Lunchpad is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Lunchpad. If not, see
 * <https://www.gnu.org/licenses/>.
 * */

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;
import java.io.File;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

public class SoundPlayer {
    private final HashMap buttonSounds;
    private static ResourceBundle bundle = ResourceBundle.getBundle("SoundPlayerStrings");
    public static boolean playOneSound = false;

    public SoundPlayer(){
        buttonSounds = new HashMap<String, Clip[]>();
    }

    public void uploadSound(String name, String path) {
        try {
            int maxIndex = 6;
            Clip[] newClip = new Clip[maxIndex];
            for (int i = 0; i < maxIndex; i++) {
                newClip[i] = AudioSystem.getClip();
                newClip[i].close();
                try {
                    newClip[i].open(AudioSystem.getAudioInputStream(new File(path)));
                } catch (OutOfMemoryError e) {
                    newClip[i].close();
                    JOptionPane.showMessageDialog(null,
                            bundle.getString("SP_ERR"),
                            bundle.getString("SP_ERR_TTL"),
                            JOptionPane.INFORMATION_MESSAGE, null);
                    System.exit(0);
                }
            }
            buttonSounds.put(name, newClip);
        } catch(Exception e) {
            System.out.println("!!!!!!!" + name);
            throw new RuntimeException(e);
        }
    }

    public void clearPlayer(){
        buttonSounds.clear();
    }

    public void playSound(String playedSound) throws NullPointerException{
        Clip[] playedClip = (Clip[]) buttonSounds.get(playedSound);
        try {
            /*
            If you notice the actual for-loop below is problematic by chance, delete it and uncomment this loop.
            for (Clip temp : playedClip) {
                if (!temp.isRunning()) {
                    temp.setMicrosecondPosition(0);
                    temp.start();
                    break;
                }
            }*/
            
            for (int i = 0; i < playedClip.length; i++) {
                                               /////////////////////If malfunctions delete "|| playOneSound"
                if (!playedClip[i].isRunning() || playOneSound) {
                    playedClip[i].setMicrosecondPosition(0);
                    playedClip[i].stop();
                    playedClip[i].start();
                    //System.out.println(i);
                    break;
                                                   /////////////////////If malfunctions delete "&& !playOneSound"
                } else if(i == playedClip.length-1 && !playOneSound) {
                    playedClip[0].setMicrosecondPosition(0);
                    //System.out.println("playing first");
                    break;
                }

            }
        } catch (Exception e){
            throw new NullPointerException();
        }
    }

    public void stopSound() {
        Set<String> sounds = buttonSounds.keySet();
        for(String key : sounds){
            for(Clip clip : (Clip[]) buttonSounds.get(key)){
                if (clip != null && clip.isRunning()){
                    clip.stop();
                }
            }
        }
    }
}
