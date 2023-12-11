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

import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.Clip;
import javax.imageio.ImageIO;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

/**
 * The class that contains a simple voice recording interface.
 * It only records in .wav format
 */
public class RecorderDialog{
    private final JTextField filenameT;
    private final JButton recordStop, play, discard, save;
    private String newName;
    private final LPDialog dialog;
    private TargetDataLine targetLine;
    private final Clip clip;
    private final Icon recordIcon, stopIcon, playIcon;

    // Since the buttons recordStop and play have two different functionalities these booleans are needed.
    private boolean recording, playing, rerecording, saved;
    private static ResourceBundle bundle = ResourceBundle.getBundle("RecorderStrings");

    /**
     * Creates a RecorderDialog object that records the voice, play it and save it
     * in the path given as a parameter in the constructor.
     * @param path Where the recorder is saving the new recording
     */
    public RecorderDialog(String path) throws LineUnavailableException{
        File fileDir = new File(path); // Holds the directory in which the recording will be saved.
        String[] files = fileDir.list(); // List of the contents of the target directory fileDir
        assert files != null;
        for(String file : files) System.out.println(file);

        // Initializing the dialog box for the GUI
        dialog = new LPDialog(bundle.getString("REC_TITLE"));
        //Fixed size
        dialog.setResizable(false);
        dialog.setSize(new Dimension(470, 170));
        dialog.setModal(true); // Force user to close the dialog to use other windows or dialogs
        //dialog.mainPanel.setLayout(null);

        JLabel saveAsLabel = new JLabel(bundle.getString("REC_SAVEAS"));
        saveAsLabel.setBounds(30, 20, 100, 30); // Position and dimension
        saveAsLabel.setForeground(WindowActions.BOX_FOREGROUND);
        dialog.mainPanel.add(saveAsLabel);

        filenameT = new JTextField(); // Where the user gives the name to the new recording file
        filenameT.setBounds(120, 20, 270, 30); // Position and dimension
        filenameT.setBackground(WindowActions.BOX_BACKGROUND);
        filenameT.setForeground(WindowActions.BOX_FOREGROUND);
        filenameT.setCaretColor(WindowActions.BOX_CARET); // Outline color

        JLabel wavLabel = new JLabel(".wav"); // Just to show that the file format is .wav
        wavLabel.setBounds(400, 20, 100, 30); // Position and dimension
        wavLabel.setForeground(WindowActions.BOX_FOREGROUND);
        dialog.mainPanel.add(wavLabel);

        // When the object is constructed, as a default it should create a unique file name for the new recording file.
        // Here, we assure that the new file has a unique name, so it won't be overwritten to an existing file.
        newName = FileOperations.createUniquePathName(path, bundle.getString("REC_NEWREC"), ".wav");

        filenameT.setText(newName);
        dialog.mainPanel.add(filenameT);

        // Initializing the icons that will be used in buttons.
        try {
            recordIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/Artworks/record.png")))
                    .getScaledInstance(15, 15, Image.SCALE_SMOOTH));

            stopIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/Artworks/stop.png")))
                    .getScaledInstance(15, 15, Image.SCALE_SMOOTH));

            playIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/Artworks/play.png")))
                    .getScaledInstance(15, 15, Image.SCALE_SMOOTH));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Initializing the record button
        recordStop = new JButton(recordIcon);
        recordStop.setText(bundle.getString("REC_REC"));
        recordStop.setBackground(WindowActions.HILIGHT_COLOR); // Make it RED
        recordStop.setBounds(30, 70, 90, 30); // Position and dimension
        recordStop.setFocusable(false);
        recordStop.addActionListener(e -> recordOrStop(path)); // Assigning the function that will be called when clicked
        dialog.mainPanel.add(recordStop);

        // The process for the buttons are basically the same.

        play = new JButton(playIcon);
        play.setText(bundle.getString("REC_PLY"));
        play.setForeground(WindowActions.WHITE_COLOR);
        play.setBounds(120, 70, 90, 30);
        play.setFocusable(false);
        play.addActionListener(e -> play());
        play.setEnabled(false);
        dialog.mainPanel.add(play);

        discard = new JButton(bundle.getString("REC_DSC"));
        discard.setForeground(WindowActions.WHITE_COLOR);
        discard.setBounds(340, 70, 90, 30);
        discard.setFocusable(false);
        discard.addActionListener(e -> discard(path));
        dialog.mainPanel.add(discard);

        save = new JButton(bundle.getString("REC_SV"));
        save.setForeground(WindowActions.WHITE_COLOR);
        save.setBounds(250, 70, 90, 30);
        save.setFocusable(false);
        save.addActionListener(e -> save());
        save.setEnabled(false);
        dialog.mainPanel.add(save);

        WindowActions.centerWindow(dialog);

        // Initializing the clip that can be played when a newly recorded sample is wanted to be previewed.
        clip = AudioSystem.getClip();
        clip.addLineListener(l -> {
            if(l.getType() == LineEvent.Type.STOP && playing){
                play();
            }
        });

        dialog.setVisible(true);
    }

    //Function of record button
    private void recordOrStop(String path){
        try{
            if (recording){
                discard.setEnabled(true);
                save.setEnabled(true);
                play.setEnabled(true);
                recordStop.setIcon(recordIcon);
                recordStop.setText(bundle.getString("REC_REC"));
                recordStop.setBackground(WindowActions.HILIGHT_COLOR);
                recording = false;
                rerecording = true;
                targetLine.stop();
                targetLine.close();
                clip.open(AudioSystem.getAudioInputStream(new File(path + "/" + newName + ".wav")));

            } else {
                discard.setEnabled(false);
                save.setEnabled(false);
                play.setEnabled(false);
                filenameT.setEnabled(false);
                recordStop.setIcon(stopIcon);
                recordStop.setText(bundle.getString("REC_STP"));
                recordStop.setBackground(WindowActions.BUTTON_COLOR);
                recording = true;

                // The following snippet is explained briefly. For more detailed info,
                // visit: https://www.youtube.com/watch?v=WSyTrdjKeqQ

                //Declaring the Audio Format for our recording
                AudioFormat audioFormat = new AudioFormat(44100.0f, 16, 1, true, true);
                DataLine.Info dataInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

                //System.out.println(dataInfo.getFormats());
                System.out.println(Arrays.toString(dataInfo.getFormats()));

                clip.close();

                if(!AudioSystem.isLineSupported(dataInfo)){
                    System.out.println("Info not supported");
                }

                targetLine = (TargetDataLine) AudioSystem.getLine(dataInfo);
                targetLine.open(); //Getting the microphone ready to capture audio input
                targetLine.start(); // Start capturing data from microphone

                Thread audioThread = new Thread(() -> {
                    AudioInputStream recordingStream = new AudioInputStream(targetLine); // Using targetLine as a source
                    newName = filenameT.getText();
                    if(!rerecording)
                        newName = FileOperations.createUniquePathName(path, newName, ".wav");
                    filenameT.setText(newName);
                    File outputFile = new File(path + "/" + newName + ".wav"); //Output file
                    try{
                        AudioSystem.write(recordingStream, AudioFileFormat.Type.WAVE, outputFile); //Writing input to file
                    } catch (IOException ex) { throw new RuntimeException(); }
                });

                audioThread.start();
            }
        } catch (Exception e) { throw new RuntimeException(); }

    }

    // Function of play button
    private void play(){
        if (playing){
            discard.setEnabled(true);
            save.setEnabled(true);
            recordStop.setEnabled(true);
            play.setIcon(playIcon);
            play.setText(bundle.getString("REC_PLY"));
            playing = false;
            clip.stop();
            System.out.println("Stopping");

            clip.setMicrosecondPosition(0);

        } else {
            discard.setEnabled(false);
            save.setEnabled(false);
            recordStop.setEnabled(false);
            play.setIcon(stopIcon);
            play.setText(bundle.getString("REC_STP"));
            playing = true;
            clip.start();
        }
    }
    // Function of discard button
    private void discard(String path){
        //Remove file
        saved = false;
        dialog.setVisible(false);
        newName = filenameT.getText();
        if(Files.exists(Paths.get(path + "/" + newName + ".wav")))
            try {
                FileUtils.delete(new File(path + "/" + newName + ".wav"));
            }catch (Exception e){throw new RuntimeException();}
    }
    // Function of save button
    private void save(){
        saved = true;
        dialog.setVisible(false);
    }
}
