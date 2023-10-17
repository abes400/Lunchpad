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
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.util.Objects;
import java.util.Scanner;

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
    public About() {
        // Initiating a Lunchpad dialog object with the title "About Lunchpad"
        LPDialog dialog = new LPDialog("About Lunchpad");
        dialog.setSize(500, 350);
        WindowActions.centerWindow(dialog);
        dialog.setModal(true);

        try{
            // Initiating a new Label with the image file containing the application's logo.
            JLabel logo = new JLabel(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/Artworks/logo.png")))
                    .getScaledInstance(270, 180, Image.SCALE_SMOOTH)));
            // Setting the position and dimension of the logo label.
            logo.setBounds(10, 0,250, 70);
            dialog.mainPanel.add(logo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Initiating the JLabel containing the information about the application.
        JLabel[] text = new JLabel[2];
        text[0] = new JLabel("Version 1.0.1");
        text[1] = new JLabel("You can switch between sound channels USING THE ARROW KEYS.");

        // Setting the coordinates and size of each index
        text[0].setForeground(WindowActions.BOX_CARET);
        text[0].setBounds(410, 5, 500, 60);
        dialog.mainPanel.add(text[0]);

        text[1].setForeground(WindowActions.BOX_CARET);
        text[1].setFont(new Font(Font.DIALOG, 0, 10));
        text[1].setBounds(20, 260, 500, 60);
        dialog.mainPanel.add(text[1]);

        StringBuilder GPL = new StringBuilder(); //the string to contain the GPL licence text.

        // Reading the contents of the GPL_Notice.txt and appending it to the GPL.
        try{
            // Creating an InputStream instance "inputStream" for streaming out of "GPL_Notice_and_Credits.txt"
            // then creating an InputStreamReader named "isr" with the "inputStream"
            // then creating a BufferedReader "br" with the "isr"
            InputStream inputStream = getClass().getResourceAsStream("GPL_Notice_and_Credits.txt");
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String nextLine = "";
            // Appending the next line read by "br" to the GPL string
            while((nextLine = br.readLine()) != null)
                GPL.append(nextLine + '\n');

            // I had to use this technique because it wouldn't work when the program is run as .jar file.
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Initiating the text area that contains the GPL string.
        JTextArea copyText = new JTextArea(GPL.toString());
        copyText.setEditable(false);
        //copyText.setLineWrap(true);
        copyText.setFont(new Font(Font.DIALOG, 0, 10));
        copyText.setFocusable(false);
        JScrollPane scrollPane = new JScrollPane(copyText);
        scrollPane.setForeground(WindowActions.BOX_FOREGROUND);
        scrollPane.setBackground(WindowActions.BOX_BACKGROUND);
        scrollPane.setFocusable(false);
        scrollPane.setBounds(10, 70, 480, 200);
        dialog.mainPanel.add(scrollPane);

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
