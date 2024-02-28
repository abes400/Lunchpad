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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JTextField;
import com.formdev.flatlaf.util.StringUtils;


/**
 * The class derived from Manager that contains a dialog providing a list and
 * a set of tools to help manage the directory where repositories are stored.
 * In many occasions, you're going to want to  use the attribute {@code cancelled}
 * and call the method {@code getSelectedName()} somewhere inside the scope,
 * where the object is at.
 * <pre>Ex:
 *      {@code
 *      if(!my_mgr.cancelled){
 *          // Use the file selected
 *      }}
 * </pre>
 * This way, you will be able to do some specified action(s) according to
 * whether the user has dismissed the manager instead of using a selected audio.
 * @author İ. K. Bilir (Abes400)
 * @since 1.0
 */
public class RepoMgr extends Manager {
    final private JButton completeAdd, add;
    final private JTextField name;
    final private String currentRepository;
    private static ResourceBundle bundleRepo = ResourceBundle.getBundle("RepoMgrStrings");

    /**
     * Creates a new RepoMgr dialog.
     * @since 1.0
     * @param path The path to the repository that the manager will work on.
     * @param currentRepository The name of the repository
     **/
    public RepoMgr(String path, String currentRepository){
        super(path);      // Superclass' constructor should be getting the path only.

        dialog.setTitle(bundleRepo.getString("REPOMGR_TITLE"));

        refresh(""); // Refreshing the contents of the JList

        this.currentRepository = currentRepository;

        enableUse(); // Enable the Use button.
        enableRemove(); //enable the Import and Remove buttons.

        // Initializing the "Vreate repository" button which creates an empty repository folder.
        add = new JButton("+");
        add.putClientProperty("JButton.buttonType", "square");
        add.setBounds(170, 273, 25, 25);
        add.setBackground(WindowActions.BUTTON_COLOR);
        add.addActionListener(e -> addFn());
        add.setFocusable(false);
        dialog.mainPanel.add(add);

        // Where user will enter the name of the new repository
        name = new JTextField();
        name.setBounds(3, 303, 235, 40);
        name.setBackground(WindowActions.BOX_BACKGROUND);
        name.setForeground(WindowActions.BOX_FOREGROUND);
        name.setCaretColor(WindowActions.BOX_CARET);
        name.setVisible(false);
        name.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    addRepo();
            }
        });
        dialog.mainPanel.add(name);

        // Initializing the button to confirm the repository creation
        completeAdd = new JButton(bundleRepo.getString("REPOMGR_OK"));
        completeAdd.setBounds(240, 303, 55, 40);
        completeAdd.setBackground(WindowActions.BUTTON_COLOR);
        completeAdd.setFocusable(false);
        completeAdd.addActionListener(e -> addRepo());
        completeAdd.setVisible(false);
        dialog.mainPanel.add(completeAdd);

        dialog.mainPanel.add(importF);

        dialog.setVisible(true);
    }

    // This method removes the selected repository
    @Override
    protected void remove(){
        if (!currentRepository.equals(fileList.getSelectedValue())) {
            remove.setEnabled(false);
            use.setEnabled(false);
            // Since the repository is actually a directory, we need to delete it recursively, otherwise an error will
            // be thrown saying that the directory is not empty.
            deleteDirectory(new File(System.getProperty("user.home") + "/LunchPad Repositories/"
                    + fileList.getSelectedValue()));
            selectedFileName.setText(fileList.getSelectedValue() + bundleRepo.getString("REPOMGR_DELETED"));
            refresh("");
        } else {
            selectedFileName.setText(bundleRepo.getString("REPOMGR_DELERR"));
        }
    }

    /**
     * Imports the repository that is selected by user to the path that was passed while constructing the calling object.
     *
     * @since 1.0
     */
    @Override
    public void importF(){

        try {
            //dialog.setVisible(false);
            // Load the directory selected by user to the current repository, the method is called in directory selection mode.
            FileOperations.loadFileTo(System.getProperty("user.home") + "/LunchPad Repositories/", FileOperations.dir);
            System.out.println("Dialog flag");
        } catch (IOException e) { e.printStackTrace();}


        if (FileOperations.filename != null) { // If user selects something
            refresh(""); // Refreshes the DLM so the newly selected dir is also visible
            // Select the newly added file programmatically
            fileList.setSelectedValue(FileOperations.filename, true);
            //dialog.setVisible(true);
            use.setEnabled(true);
            remove.setEnabled(true);
            selectedFileName.setText(FileOperations.filename + bundleRepo.getString("REPOMGR_SELECT"));
        }
    }


    private void addFn(){
        fileList.clearSelection();
        remove.setEnabled(false);
        use.setEnabled(false);
        cancel.setVisible(false);
        selectedFileName.setText(bundleRepo.getString("REPOMGR_NEWNAME"));
        name.setVisible(true);
        name.grabFocus();
        completeAdd.setVisible(true);
        remove.setVisible(false);
        add.setVisible(false);
        use.setVisible(false);
        importF.setVisible(false);
        name.setText("");
    }

    // Function of repository creation
    private void addRepo(){
        if(completeAdd.isVisible()) {
            name.setVisible(false);
            completeAdd.setVisible(false);
            remove.setVisible(true);
            add.setVisible(true);
            use.setVisible(true);
            cancel.setVisible(true);
            importF.setVisible(true);
            final String newRepoName = name.getText();
            if(StringUtils.isEmpty(newRepoName)) {
                selectedFileName.setText(bundleRepo.getString("REPOMGR_CANCELLED"));
            } else if (DLM.contains(newRepoName)){
                selectedFileName.setText(bundleRepo.getString("REPOMGR_EXISTS"));
            } else {
                Path path = Paths.get(System.getProperty("user.home") + "/LunchPad Repositories/" + newRepoName + "/");
                try {
                    Files.createDirectory(path);
                } catch (Exception exception) {
                    throw new RuntimeException(exception); // Gives the exception error in detail
                }
                refresh("");
                fileList.setSelectedValue(newRepoName, true);
                use.setEnabled(true);
                remove.setEnabled(true);
                selectedFileName.setText(fileList.getSelectedValue() + bundleRepo.getString("REPOMGR_SELECT"));
            }
        }
    }

    // Should be called when user clicks an item on the JList.
    @Override
    protected void selectList(){
        if(fileList.getSelectedValue() != null) { // Just to assure that the program doesn't attempt
                                                  // to select a null file and break itself
            selectedFileName.setText(fileList.getSelectedValue() + bundleRepo.getString("REPOMGR_SELECT"));
            remove.setEnabled(true);
            use.setEnabled(true);
        }
    }

    // The recursive algorithm that deletes a non-empty directory
    private void deleteDirectory(File dir){
        for(File subElement : Objects.requireNonNull(dir.listFiles())){
            if(subElement.isDirectory()){
                deleteDirectory(subElement);
            }
            System.out.println(subElement.delete());
        }
        System.out.println(dir.delete());
    }
}
