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

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;

/**
 * The class that extends JButton and encapsulates two String attributes to hold the names of the sound clips
 * assigned to them.
 *
 * @since  1.0
 */
public class SoundKey extends JButton {
    private final String[] clips;
    public String[] names;
    private static int currentStreamIndex;
    static Dimension dim = new Dimension(60, 60);
    public static final int RIGHT = 1, LEFT = 0;

    public SoundKey(String label) {
        setText(label);
        clips = new String[2];
        names = new String[2];
        this.setPreferredSize(dim);
        this.setBackground(new Color(0x5C5C5C));
    }

    public String[] getClipName(){
        return new String[]{clips[0], clips[1]};
    }

    public void addSound(String name, int clipNumber)
    {
        clips[clipNumber] = name;
    }

    public String getSound(){
        return clips[currentStreamIndex];
    }

    public static void switchSound(int clipNumber){ currentStreamIndex = clipNumber; }
    public static int getSoundIndex(){return currentStreamIndex;}
}
