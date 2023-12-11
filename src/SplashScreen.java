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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class SplashScreen {
    public JFrame baseFrame;
    public SplashScreen() throws  java.io.IOException {
        baseFrame = new JFrame();
        baseFrame.setUndecorated(true);
        baseFrame.setSize(500, 350);
        baseFrame.getContentPane().setBackground(WindowActions.BLACK_COLOR);

        JLabel logo = new JLabel(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/Artworks/logo2.png")))
                .getScaledInstance(500, 333, Image.SCALE_SMOOTH)));

        logo.setBounds(0, 0,500, 313);
        baseFrame.getContentPane().add(logo);

        JLabel text = new JLabel(ResourceBundle.getBundle("SplashScreenStrings").getString("SPLSH_BYLINE"));
        text.setFont(new Font("Sans Serif", Font.PLAIN, 10));
        text.setAlignmentX(SwingConstants.CENTER);
        text.setForeground(WindowActions.BOX_CARET);
        text.setBounds(40, 300, 500, 60);
        baseFrame.add(text);

        WindowActions.centerWindow(baseFrame);
        baseFrame.setLayout(null);
    }
}
