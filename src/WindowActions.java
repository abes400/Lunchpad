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
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

public class WindowActions {

    static final Color BUTTON_COLOR = new Color(0x5C5C5C),
                       APPROVE_COLOR = new Color(0x0095FF),
                       HILIGHT_COLOR = new Color(0xCF4A4A),
                       PANEL_COLOR = new Color(0x381003),
                       BLACK_COLOR = new Color(0x000000),
                       SECONDARY_COLOR = new Color(0x313131),
                       WHITE_COLOR = new Color(0xFFFFFF),
                       BOX_FOREGROUND = new Color(0XB8B8B8),
                       BOX_BACKGROUND = new Color(0x404040),
                       BAR_BACKGROUND = new Color(0xAF3C02),
                       PRESSED_KEY_BACKGROUND = new Color(0xAF3C02),
                       BOX_CARET = new Color(0XB8B8B8);

    /**
     This function will put ANY WINDOW INSTANCE, which is passed, to the center of the screen.
     @param candidateWindow the window instance you would like to center
     */
    public static void centerWindow(Window candidateWindow){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension winSize = candidateWindow.getSize();
        Point center = new Point((screenSize.width - winSize.width) / 2, (screenSize.height - winSize.height) / 2);
        candidateWindow.setLocation(center);
    }

}
