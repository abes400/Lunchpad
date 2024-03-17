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

import java.awt.*;
import javax.swing.*;

/**
 * The class that extends JButton and encapsulates two String attributes to hold the names of the sound clips
 * assigned to them.
 *
 * @since  1.0
 */
public class SoundKey extends JButton {
    private final String[] clips;
    public String[] names;
    private final String LABEL_PREFIX;
    private static final String LABEL_SUFFIX = "</h6></center></html>";
    private static int currentStreamIndex;
    static Dimension dim = new Dimension(60, 60);
    public static final int RIGHT = 1, LEFT = 0;
    private static final int NAME_END_INDEX = 9;

    public static final Insets keyMargin = new Insets(5, 0, 1, 0);

    public SoundKey(String label) {
        LABEL_PREFIX = "<html> <center>" + label + "<h6><br/>";
        clips = new String[2];
        names = new String[2];
        this.setPreferredSize(dim);
        this.setBackground(WindowActions.BOX_BACKGROUND);

        // Aligning text to the leftside of the button
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setMargin(keyMargin);
    }

    public String[] getClipName(){
        return new String[]{clips[0], clips[1]};
    }

    public void addSound(String name, int clipNumber)
    {
        clips[clipNumber] = name;
        changeNameLabel(name);
    }

    public String getSoundAt(int index) { return clips[index]; }

    public static void switchSound(int clipNumber){
        currentStreamIndex = clipNumber;
    }

    public void changeNameLabel(String name) {
        setText(this.LABEL_PREFIX
                + name.substring(0, name.length() <= NAME_END_INDEX ? name.length() : NAME_END_INDEX)
                + LABEL_SUFFIX);
    }
    public static int getSoundIndex(){return currentStreamIndex;}
}
