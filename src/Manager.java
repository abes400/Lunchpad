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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

//JList <Type> name = new JList<>(parameters); This is how you declare

/**
 * The abstract class that contains a dialog providing a list and a set of tools to help
 * manipulate the items in a specified directory.
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
 * whether the user has dismissed the manager instead of "Using" the item.
 * @author İ. K. Bilir (Abes400)
 * @since 1.0
 */
public abstract class Manager {
    protected DefaultListModel<String> DLM; // Holds the contents of the JList.
    protected JList<String> fileList; // Shows the DLM object in a GUI environment
    protected JScrollPane scroller; // Makes JList scrollable
    protected JButton remove, use, cancel, importF;
    protected JLabel selectedFileName; // Shows the last operation performed and selected file name
    protected LPDialog dialog; // The dialog that will be shown in the GUI
    protected boolean cancelled; // Holds whether the user clicked Dismiss button => helps
    protected File fileDir; // The directoey worked on by the manager
    protected static ResourceBundle bundle = ResourceBundle.getBundle("ManagerStrings");

    public Manager(String path){
        fileDir = new File(path); // Assigns the working directory to fileDir.
        System.out.println(path);

        dialog = new LPDialog(""); // Initiate the dialog.
        dialog.setSize(300, 380);
        dialog.setModal(true); // Dialog won't let the user control the other window until it's closed.
        WindowActions.centerWindow(dialog);
        dialog.getContentPane().setBackground(WindowActions.PANEL_COLOR);
        dialog.setResizable(false);

        DLM = new DefaultListModel<>(); // Initializing the DLM DefaultListModel that will contain the Items.
        fileList = new JList<>(DLM); // Initializing fileList with DLM as the DefaultListModel
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single-item-selection only
        fileList.setBackground(new Color(0x404040));
        fileList.setForeground(new Color(0XB8B8B8));
        fileList.setFocusable(false);
        fileList.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                selectList();
            }
        }); // The JList will already highlight the selected item. But after we assign the selectList() method,
            // it will call selectList() after highlighting the selected item. This way it is possible to do
            // more advanced operations when an item is selected. See the implementation of selectList() below.

        scroller = new JScrollPane(fileList); // scroller will now make the fileList scrollable.
        scroller.setBounds(3, 3, 295, 270);
        scroller.setForeground(WindowActions.BOX_FOREGROUND);
        dialog.mainPanel.add(scroller);

        selectedFileName = new JLabel(bundle.getString("MGR_NO_ITEM"), SwingConstants.LEFT);
        selectedFileName.setForeground(WindowActions.BOX_FOREGROUND);
        selectedFileName.setBounds(3, 273, 295, 20);
        dialog.mainPanel.add(selectedFileName);

        cancel = new JButton(bundle.getString("MGR_CLOSE"));
        cancel.setBounds(137, 303, 80, 40);
        cancel.setBackground(WindowActions.BUTTON_COLOR);
        cancel.setFocusable(false);
        cancel.addActionListener(e -> cancel()); // assigning cancel() method to the button
        dialog.mainPanel.add(cancel);

        importF = new JButton(bundle.getString("MGR_IMPORT"));
        importF.setBounds(217, 273, 80, 25);
        importF.setBackground(WindowActions.BUTTON_COLOR);
        importF.setFocusable(false);
        importF.addActionListener(e -> importF()); // assigning importF() method to the button

    }

    // These three methods are abstract to ensure that each derivation of this class implement their own version of
    // these methods.

    /**
     * Implement the operations whenever the Import button is clicked.
     * @since 1.0
     */
    public abstract void importF();

    /**
     * Implement the operations whenever the Remove (-) button is clicked
     * You can implement different ways of deleting different kind of files or folders in the subclass you derived
     * @since 1.0
     */
    protected abstract void remove();
    protected abstract void selectList();

    /**
     * Refreshing the content of DLM with the suffix passed as parameter.
     * @param exclusiveSuffix only include the items with the given suffix. Pass empty string for no suffix.
     * @since 1.0
     */
    protected void refresh(String exclusiveSuffix){
        String[] files = fileDir.list(); // List the contents of the directory in an array of String
        DLM.clear(); // Clear the DLM
        assert files != null; // Terminate if file doesn't exist somehow.
        for(String item : files)
            if(item.toCharArray()[0] != '.' && item.endsWith(exclusiveSuffix)) { //Just to guarantee that .DS_Store isn't
                                                                                 //included and the filename ends with the suffix.
                DLM.addElement(item);
            }
    }


    /**
     * Initializing the Use button whenever needed.
     * @since 1.0
     */
    protected void enableUse(){
        use = new JButton(bundle.getString("MGR_USE")); // Initialize the button
        use.setBounds(217, 303, 80, 40); // Set position and dimension
        use.setBackground(WindowActions.APPROVE_COLOR); // Change the color of the button
        use.setFocusable(false);
        use.addActionListener(e -> use()); // Assign use() method to run when clicked
        use.setEnabled(false); // Gray-out
        dialog.mainPanel.add(use);
    }

    /**
     * Initializing the Remove (-) button whenever needed.
     * @since 1.0
     */
    protected void enableRemove(){
        remove = new JButton("-"); // Initialize the button
        remove.setBounds(193, 273, 25, 25); // Set position and dimension
        remove.setBackground(WindowActions.BUTTON_COLOR); // Change the color of the button
        remove.setFocusable(false);
        remove.addActionListener(e -> remove()); // Assign remove() method to run when clicked
        remove.setEnabled(false); // Gray-out
        dialog.mainPanel.add(remove);
    }

    /*
     * Dismisses the dialog with assigning false to {@code cancelled} attribute.
     */
    protected void use(){
        dialog.setVisible(false);
        cancelled = false;
    }

    /*
     * Dismisses the dialog with assigning false to {@code cancelled} attribute.
     */
    protected void cancel(){
        DLM.clear();
        dialog.setVisible(false);
        cancelled = true;
    }

    /**
     * Returns the value of the selected item.
     * @return The value of the selected item.
     * @since 1.0
     */
    public String getSelectedName(){
        return fileList.getSelectedValue();
    }

}
