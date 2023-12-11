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
import javax.sound.sampled.LineUnavailableException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

/**
 * The class derived from Manager that contains a dialog providing a list and
 * a set of tools to help manage the repository that is active in the application.
 * When instantiated in SELECT_AUDIO mode, that is:
 * <pre>
 *     {@code new AudioMgr(my_path_to_repo, my_repo, AudioMgr.SELECT_AUDIO)}
 * </pre>
 * you should use the attribute {@code cancelled} and call the method
 * {@code getSelectedName()} somewhere inside the scope, where the object is at.
 * <pre>Ex:
 *      {@code
 *      my_audio_mgr = new AudioMgr(my_path, my_repo, AudioMgr.SELECT_AUDIO)
 *      if(!my_mgr.cancelled){
 *          doSomethong(my_audio_mgr.getSelectedName())
 *      }}
 * </pre>
 * This way, you will be able to do some specified action(s) according to
 * whether the user has dismissed the manager instead of using a selected audio.
 * @author İ. K. Bilir (Abes400)
 * @since 1.0
 */
public class AudioMgr extends Manager {
    private final String currentRepository; // Holds the repository name to work on.
    public static final boolean MANAGE_AUDIO = false, SELECT_AUDIO = true; // Constants to be passed in the constructor
    private final boolean mode; // The mode that will specify
    private static ResourceBundle bundleAudio = ResourceBundle.getBundle("AudioMgrStrings");

    /**
     * Creates a new AudioMgr dialog.
     * @since 1.0
     * @param path The path to the repository that the manager will work on.
     * @param currentRepository The name of the repository <strong>(MUST MATCH WITH THE FINAL DIRECTORY AT THE path)</strong>
     * @param mode The mode that the manager will be initialized in.
     * <pre><strong>AudioMgr.MANAGE_AUDIO</strong> : Remove and Import buttons will be initialized, Use button will not.</pre>
     *<pre><strong>AudioMgr.SELECT_AUDIO</strong> : The Use button will be initialized, Remove and Import buttons will not.</pre>
     */
    public AudioMgr(String path, String currentRepository, boolean mode){
        super(path);                  // Superclass' constructor should be getting the path only.
        refresh(".wav"); // Refreshing the contents of the JList

        // Assigning the mode and repository so that they will be used later.
        this.mode = mode;
        this.currentRepository = currentRepository;
        System.out.println(currentRepository);

        dialog.setTitle(bundleAudio.getString("AUMGR_TITLE"));

        // Initializing the icon that will be used in Recorder button.
        Icon recordIcon;
        try {
            recordIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass()
                    .getResource("/Artworks/recorder.png")))
                    .getScaledInstance(15, 15, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            throw new RuntimeException();
        }

        // Initializing the Recorder button
        JButton record = new JButton(recordIcon);
        record.setText(bundleAudio.getString("AUMGR_RCDR"));
        record.setBounds(3, 303, 135, 40); // Setting the position and the dimension of the button
        record.setFocusable(false); // If not called, the manager will malfunction.
        record.addActionListener(e -> audioRecorder(path)); // Assigning the button to the function audioRecorder()
                                                            // This is where the path will be used.
        dialog.mainPanel.add(record);

        if(mode) { // Corresponds to AudioMgr.SELECT_AUDIO
            enableUse();    // As mentioned in the Javadoc, it will enable the Use button.
        } else { // Corresponds to AudioMgr.MANAGE_AUDIO
            enableRemove(); // As mentioned in the Javadoc, it will enable the Import and Remove buttons.

            // Re-setting the position and the dimension of the cancel button
            cancel.setBounds(217, 303, 80, 40);
            dialog.mainPanel.add(importF);
        }
        dialog.setVisible(true);
    }

    // This method removes an audio file in the repository being worked on.
    @Override
    protected void remove(){
        remove.setEnabled(false);
        // Assigning the to-be-deleted file to a File object
        // It uses the path and repository name passed to the constructor.
        File deletedItem = new File(System.getProperty("user.home") + "/LunchPad Repositories/"
                + currentRepository + "/" + fileList.getSelectedValue());
        System.out.println(deletedItem);
        try { FileUtils.delete(deletedItem); } // The best way to delete the selected file. Otherwise, the file won't delete.
        catch (IOException ex) { throw new RuntimeException(ex); }
        selectedFileName.setText(fileList.getSelectedValue() + bundleAudio.getString("AUMGR_DELETED"));
        refresh(".wav"); // Refreshes the DLM so the newly selected file is also visible
                                      // as long as it is a .wav file.
    }

    /**
     * Imports the wave file that is selected by user to the path that was passed while constructing the calling object.
     *
     * @since 1.0
     */
    @Override
    public void importF(){
        dialog.setVisible(false);
        try {
            // Load the file selected by user to the current repository, the method is called in file selection mode.
            FileOperations.loadFileTo(System.getProperty("user.home") + "/LunchPad Repositories/"
                    + currentRepository + "/", FileOperations.file);
        } catch (java.io.IOException exception) {throw new RuntimeException(exception);}
        dialog.setVisible(true);
        if(FileOperations.filename != null){ // If user selects something
            refresh(".wav"); // Refreshes the DLM so the newly selected file is also visible
                                          // as long as it is a .wav file.

            if(FileOperations.filename.endsWith(".wav"))
                // Select the newly added file programmatically
                fileList.setSelectedValue(FileOperations.filename, true);

            remove.setEnabled(true);
            selectedFileName.setText(FileOperations.filename + bundleAudio.getString("AUMGR_SELECTED"));
        }
    }

    // Should be called when user clicks an item on the JList.
    @Override
    protected void selectList(){
        if(fileList.getSelectedValue() != null) { // Just to assure that the program doesn't attempt
                                                  // to select a null file and break itself
            selectedFileName.setText(fileList.getSelectedValue() + bundleAudio.getString("AUMGR_SELECTED"));
            if (mode) {
                use.setEnabled(true);
            } else {
                remove.setEnabled(true);
            }
        }
    }

    private void audioRecorder(String path) {
        dialog.setVisible(false);
        try {
            new RecorderDialog(path); // Initializes the Audio Recorder
            refresh(".wav"); // After user exits the Recorder dialog, DLM is refreshed just in case
                                          // the user saves the audio he/she recorded. If he/she did, the new file
                                          // will be included in the list.
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        dialog.setVisible(true);
    }
}
