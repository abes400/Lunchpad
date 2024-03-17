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

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;
// 0: up
// 1: right
// 2: down
// 3: left

/**
 *
 */
public class MainWindow implements KeyListener {
    public LPWindow mWindow;
    private JPanel[] keyboardPanel;
    private JButton leftSound, rightSound, selectedKeyButton;

    private JTextField soundPackageLocation;
    private final SoundKey[] soundKeys;
    private final HashMap<Character, Integer> keyIndexes;
    private String currentRepository;
    private final SoundPlayer soundPlayer;
    private int selectedKey;
    private Icon[] souncChannelIcon;
    private JLabel selectedChannelLabel;
    private static ResourceBundle bundle = ResourceBundle.getBundle("MainWindowStrings");
    char[] keyString;

    /** Simply call this method with the "new" keyword and you have a MainWindow*/
    public MainWindow(String openRepository) throws java.io.IOException {

        soundPlayer = new SoundPlayer();
        initWindow();

        keyString = "QWERTYUIOPASDFGHJKLZXCVBNM".toCharArray();
        soundKeys = new SoundKey[26];
        keyIndexes = new HashMap<>();

        int j = 0;
        for(int i = 0; i < 26; i++){
            soundKeys[i] = new SoundKey("" + keyString[i]);
            keyIndexes.put(keyString[i], i);
            soundKeys[i].setFocusable(false);
            soundKeys[i].setBackground(WindowActions.BUTTON_COLOR);
            keyboardPanel[j].add(soundKeys[i]);
            final int final_i = i;
            soundKeys[i].addActionListener(e -> this.selectKey(final_i));
            if(keyString[i] == '-' || keyString[i] == 'P'  || keyString[i] == 'L'){
                j++;
            }
        }

        currentRepository = System.getProperty("user.home") + "/LunchPad Repositories/" + openRepository + "/";
        soundPackageLocation.setText(openRepository);

        remapButtons(true, true);
        selectKey(11);

        mWindow.addKeyListener(this);

        WindowActions.centerWindow(mWindow);

        mWindow.setDefaultCloseOperation(LPWindow.EXIT_ON_CLOSE);

        mWindow.closeButton.addActionListener(e -> {
            try{close();}
            catch(Exception ex) {throw new RuntimeException(ex);}
        });

    }

    private void initWindow() throws java.io.IOException {
        mWindow = new LPWindow("");
        mWindow.setMinimumSize(new Dimension(900, 550));

        mWindow.mainPanel.setLayout(new BorderLayout());

        JPanel filePanel = new JPanel();
        filePanel.setBackground(WindowActions.BAR_BACKGROUND);
        filePanel.setPreferredSize(new Dimension(400, 220));
        filePanel.setBorder(getBorder(bundle.getString("MW_SOUNDREPO")));
        filePanel.setLayout(null);

        JPanel keyPanel = new JPanel();
        keyPanel.setBackground(WindowActions.BAR_BACKGROUND);
        keyPanel.setPreferredSize(new Dimension(400, 220));
        keyPanel.setBorder(getBorder(bundle.getString("MW_KEYPROP")));
        keyPanel.setLayout(null);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(WindowActions.PANEL_COLOR);
        topPanel.setOpaque(true);
        topPanel.setPreferredSize(new Dimension(mWindow.getWidth(), 230));
        topPanel.setLayout(new FlowLayout());

        topPanel.add(filePanel);
        topPanel.add(keyPanel);
        mWindow.mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(WindowActions.PANEL_COLOR);
        bottomPanel.setLayout(new GridBagLayout());
        bottomPanel.setAlignmentY(LPWindow.CENTER_ALIGNMENT);
        bottomPanel.setAlignmentX(LPWindow.CENTER_ALIGNMENT);
        mWindow.mainPanel.add(bottomPanel);

        JPanel keyboardLayoutHolder = new JPanel();
        keyboardLayoutHolder.setBackground(WindowActions.SECONDARY_COLOR);
        keyboardLayoutHolder.setLayout(new FlowLayout());
        keyboardLayoutHolder.setAlignmentY(LPWindow.CENTER_ALIGNMENT);
        keyboardLayoutHolder.setAlignmentX(LPWindow.CENTER_ALIGNMENT);
        keyboardLayoutHolder.setPreferredSize(new Dimension(740, 220));
        bottomPanel.add(keyboardLayoutHolder);

        keyboardPanel = new JPanel[3];

        for(int i = 0; i < 3; i ++){
            keyboardPanel[i] = new JPanel();
            keyboardPanel[i].setBackground(WindowActions.SECONDARY_COLOR);
            keyboardPanel[i].setPreferredSize(new Dimension(740, 64));
            keyboardLayoutHolder.add(keyboardPanel[i]);
            keyboardLayoutHolder.add(keyboardPanel[i], new GridBagConstraints());
        }

        Icon stop = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/Artworks/shift.png")))
                .getScaledInstance(15,15, Image.SCALE_SMOOTH));
        JButton stopSounds = new JButton(stop);
        stopSounds.setPreferredSize(new Dimension(120, 60));
        stopSounds.setText(bundle.getString("MW_STOP"));
        stopSounds.setFocusable(false);
        keyboardPanel[2].add(stopSounds);


        JLabel logo = new JLabel(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/Artworks/logo.png")))
                    .getScaledInstance(120, 80, Image.SCALE_SMOOTH)));
        logo.setSize(240, 160);

        // Detect whether the program runs on a Macintosh machine, and align the logo accordingly.
        if(System.getProperty("os.name").toLowerCase().startsWith("mac"))
            mWindow.topBar.add(logo, BorderLayout.EAST);
        else
            mWindow.topBar.add(logo, BorderLayout.WEST);

        Icon audioIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/Artworks/add_sound.png")))
                                      .getScaledInstance(15,15, Image.SCALE_SMOOTH));


        leftSound = new JButton("None");
        leftSound.setBounds(10, 85, 150, 30);
        leftSound.setBackground(WindowActions.BUTTON_COLOR);
        leftSound.addActionListener(e -> changeSound(SoundKey.LEFT));
        leftSound.setFocusable(false);
        keyPanel.add(leftSound);

        rightSound = new JButton("None");
        rightSound.setBounds(240, 85, 150, 30);
        rightSound.setBackground(WindowActions.BUTTON_COLOR);
        rightSound.addActionListener(e -> changeSound(SoundKey.RIGHT));
        rightSound.setFocusable(false);
        keyPanel.add(rightSound);

        selectedKeyButton = new JButton();
        selectedKeyButton.setBounds(170, 70, 60, 60);
        selectedKeyButton.setBackground(WindowActions.BUTTON_COLOR);
        selectedKeyButton.setFocusable(false);
        keyPanel.add(selectedKeyButton);

        Icon repoIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/Artworks/manage_rep.png")))
                                    .getScaledInstance(15,15, Image.SCALE_SMOOTH));

        JButton addSound = new JButton(audioIcon);
        addSound.setText(bundle.getString("MW_MNGAUDIO"));
        addSound.setBounds(50, 110, 150, 50);
        addSound.setBackground(WindowActions.BUTTON_COLOR);
        addSound.addActionListener(e -> addSound());
        addSound.setFocusable(false);
        filePanel.add(addSound);

        JButton manageRep = new JButton(repoIcon);
        manageRep.setText(bundle.getString("MW_REPOS"));
        manageRep.setBounds(200, 110, 150, 50);
        manageRep.setBackground(WindowActions.BUTTON_COLOR);
            manageRep.addActionListener(e -> {
                try { manageRep(); }
                catch (IOException ex) { throw new RuntimeException(ex); }
            });
        manageRep.setFocusable(false);
        filePanel.add(manageRep);

        soundPackageLocation = new JTextField();
        soundPackageLocation.setBounds(50, 65, 300, 30);
        soundPackageLocation.setBackground(WindowActions.BOX_BACKGROUND);
        soundPackageLocation.setForeground(WindowActions.BOX_FOREGROUND);
        soundPackageLocation.setEditable(false);
        soundPackageLocation.setFocusable(false);
        filePanel.add(soundPackageLocation);

        souncChannelIcon = new Icon[2];
        souncChannelIcon[0] = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass()
                        .getResource("/Artworks/channel_left.png")))
                        .getScaledInstance(25,25, Image.SCALE_SMOOTH));
        souncChannelIcon[1] = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass()
                        .getResource("/Artworks/channel_right.png")))
                        .getScaledInstance(25,25, Image.SCALE_SMOOTH));

        selectedChannelLabel = new JLabel(bundle.getString("MW_CRNT_CHNL"));
        selectedChannelLabel.setBounds(140, 185, 150, 25);
        selectedChannelLabel.setIcon(souncChannelIcon[0]);
        selectedChannelLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        keyPanel.add(selectedChannelLabel);

        JButton about = new JButton("i");
        about.setBounds(3, 192, 25, 25);
        about.setBackground(WindowActions.BUTTON_COLOR);
        about.addActionListener(e -> about());
        about.setFocusable(false);
        keyPanel.add(about);

    }

    private void remapButtons(boolean remapButtons, boolean uploadSound) throws java.io.IOException{
            if(remapButtons) {
                File mapping = new File(currentRepository + "keymappings.lpr");
                if (mapping.createNewFile()) {
                    Writer infoWrite = new FileWriter(mapping);
                    for (int i = 0; i < 52; i++) {
                        infoWrite.write("-\n");
                    }
                    infoWrite.close();
                }
                Scanner infoRead = new Scanner(mapping);
                String current;
                for (int i = 0; i < 26; i++) {
                    for (int j = 0; j < 2; j++) {
                        current = infoRead.nextLine();
                        if (!current.equals("-"))
                            soundKeys[i].addSound(current, j);
                        else
                            soundKeys[i].addSound("None", j);
                    }
                }
                infoRead.close();
            }

            if(uploadSound) {
                soundPlayer.clearPlayer();
                System.out.println(currentRepository);
                String[] repList = new File(currentRepository).list();
                assert repList != null;
                for (String tempsound : repList) {
                    if (tempsound.endsWith(".wav")) {
                        soundPlayer.uploadSound(tempsound, currentRepository + tempsound);
                    }
                }
            }
    }

    void renameButtons() {
        SoundKey.switchSound(SoundKey.LEFT);
        selectedChannelLabel.setIcon(souncChannelIcon[SoundKey.LEFT]);
        for(SoundKey k : soundKeys) k.changeNameLabel(k.getSoundAt(SoundKey.LEFT));
    }

    private void saveButtonMap() throws  java.io.IOException{
        File mapping = new File(currentRepository + "keymappings.lpr");
        Writer infoWrite = new FileWriter(mapping);
        String[] names;
        for(int i = 0 ; i < 26; i++){
            names = soundKeys[i].getClipName();
            for(int j = 0; j < 2; j++){
                if(names[j].equals("None") || names[j] == null || names[j].isEmpty())
                    infoWrite.write("-\n");
                else
                    infoWrite.write(names[j] + "\n");
            }
        }
        infoWrite.close();
    }

    private Border getBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WindowActions.APPROVE_COLOR),
                title,
                TitledBorder.CENTER,
                TitledBorder.BELOW_TOP,
                new Font(Font.SANS_SERIF, Font.PLAIN, 12),
                new Color(0x9F9F9F)
        );
    }

    public void selectKey(int i){
        String[] names = soundKeys[i].getClipName();
        selectedKey = i;
        rightSound.setText(names[SoundKey.RIGHT]);
        leftSound.setText(names[SoundKey.LEFT]);
        selectedKeyButton.setText(""+keyString[i]);
    }

    public void manageRep() throws java.io.IOException {
        RepoMgr repoMgr = new RepoMgr(System.getProperty("user.home") + "/LunchPad Repositories/",
                soundPackageLocation.getText());

        if(!repoMgr.cancelled){
            String selectedRepo = repoMgr.getSelectedName();
            saveButtonMap();
            currentRepository = System.getProperty("user.home") + "/LunchPad Repositories/" + selectedRepo + "/";
            System.out.println(currentRepository);
            soundPackageLocation.setText(selectedRepo);
            remapButtons(true, true);
            selectKey(selectedKey);
            renameButtons();
        }
    }

    public void changeSound(int mode) {
        try {
            saveButtonMap();
            AudioMgr audioMgr = new AudioMgr(currentRepository, soundPackageLocation.getText(), AudioMgr.SELECT_AUDIO);
            if(!audioMgr.cancelled){
                remapButtons(false, true);
                soundKeys[selectedKey].addSound(audioMgr.getSelectedName(), mode);
                selectKey(selectedKey);
            }
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    public void close() throws java.io.IOException{
        mWindow.setVisible(false);
        saveButtonMap();
        System.out.println("Program ended.");
        System.exit(0);
    }

    public void addSound() {
        try {
            saveButtonMap();
            new AudioMgr(currentRepository, soundPackageLocation.getText(), AudioMgr.MANAGE_AUDIO);
            remapButtons(false, true);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    public void about() {
        new About();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
            if (Character.isLetter(e.getKeyChar()) || e.getKeyChar() == '-'){
                try {
                    soundPlayer.playSound(soundKeys[keyIndexes.get(Character.toUpperCase(e.getKeyChar()))].getSoundAt(SoundKey.getSoundIndex()));
                    soundKeys[keyIndexes.get(Character.toUpperCase(e.getKeyChar()))].setBackground(WindowActions.PRESSED_KEY_BACKGROUND);
                } catch (NullPointerException nofile) {
                    soundKeys[keyIndexes.get(Character.toUpperCase(e.getKeyChar()))].addSound("None", SoundKey.getSoundIndex());
                    selectKey(selectedKey);

                }
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                SoundKey.switchSound(SoundKey.RIGHT);
                selectedChannelLabel.setIcon(souncChannelIcon[SoundKey.RIGHT]);
                for(SoundKey k : soundKeys) k.changeNameLabel(k.getSoundAt(SoundKey.RIGHT));
            } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                SoundKey.switchSound(SoundKey.LEFT);
                selectedChannelLabel.setIcon(souncChannelIcon[SoundKey.LEFT]);
                for(SoundKey k : soundKeys) k.changeNameLabel(k.getSoundAt(SoundKey.LEFT));
            } else if(e.getKeyCode() == KeyEvent.VK_SHIFT){

                SoundPlayer.playOneSound = true;
                 soundPlayer.stopSound();
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {

            if (Character.isLetter(e.getKeyChar()) || e.getKeyChar() == '-')
                soundKeys[keyIndexes.get(Character.toUpperCase(e.getKeyChar()))].setBackground(WindowActions.BUTTON_COLOR);
            if(e.getKeyCode() == KeyEvent.VK_SHIFT)
                SoundPlayer.playOneSound = false;


    }
}
