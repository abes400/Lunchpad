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

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Font;
import java.util.Objects;

/**
 * The class that contains a dialog with the information about the Lunchpad application itself.
 * It contains the application version, author and the license.
 *
 * @author İ. K. Bilir (Abes400)
 * @since 1.0
 */
public class About {
    /**
     * Creates a new Lunchpad dialog that contains the information about the Lunchpad application.
     * @since 1.0
     */
    public About(){
        // Initiating a Lunchpad dialog object with the title "About Lunchpad"
        LPDialog dialog = new LPDialog("About Lunchpad");
        dialog.setSize(500, 350);
        WindowActions.centerWindow(dialog);
        dialog.setModal(true);

        try{
            // Initiating a new Label with the image file containing the application's logo.
            JLabel logo = new JLabel(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/Artworks/logo.png")))
                    .getScaledInstance(360, 240, Image.SCALE_SMOOTH)));
            // Setting the position and dimension of the logo label.
            logo.setBounds(60, 25,360, 200);
            dialog.mainPanel.add(logo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        // Initiating the JLabel containing the information about the application.
        JLabel text = new JLabel("Version 1.0 - Designed by İbrahim Kaan Bilir (Abes400) - Licenced under GPL v3 License");
        text.setFont(new Font("Sans Serif", Font.PLAIN, 10));
        text.setAlignmentX(SwingConstants.CENTER);
        // Setting the color of the text to caret color. (See WindowAction's implementation)
        text.setForeground(WindowActions.BOX_CARET);
        // Setting the position and dimension of the text label.
        text.setBounds(40, 200, 500, 60);
        dialog.mainPanel.add(text);

        // Initializing the button to close the dialog.
        JButton button = new JButton("Close");
        // Setting the position and dimension of the text label.
        button.setBounds(417, 273, 80, 40);
        // Setting the button color of the button to button color. (See WindowAction's implementation)
        button.setBackground(WindowActions.BUTTON_COLOR);
        button.setFocusable(false);
        // Adding the function to the button.
        button.addActionListener(e -> dialog.setVisible(false));
        dialog.mainPanel.add(button);

        dialog.setVisible(true);
    }
}
