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

import javax.swing.SwingConstants;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.MouseInfo;
import java.awt.BorderLayout;

/**
 * The class that extends from JDialog, which features a title bar designed for Lunchpad.
 * <pre></pre>
 * <strong>IMPORTANT: The contents that you want to add to LPDialog don't directly go to
 * the LPDialog itself, they go to mainPanel, which is there to hold the added components.</strong>
 * <pre></pre>
 * With that being said, when adding a component, don't just directly call {@code add(my_component)},
 * instead, call {@code mainPanel.add(my_component)}.
 * <pre></pre>
 * The layout of the mainPanel is set to null as default. You may change it later.
 * <pre></pre>
 * @author İ. K. Bilir
 * @since 1.0
 */
public class LPDialog extends JDialog {
    // dragX holds the difference between the abscissa of the cursor's position and the abscissa of the topBar's position on screen
    // dragY holds the difference between the ordinate of the cursor's position and the ordinate of the topBar's position on screen
    private int dragX, dragY;
    protected JPanel topBar, mainPanel;
    protected JLabel windowTitle;

    /**
     * Creates a new LPDialog object.
     * @param title Title of the dialog. Leave empty if you don't want any title.
     */
    public LPDialog(String title){
        setLayout(new BorderLayout()); // Setting the layout for the dialog itself.
        setUndecorated(true); // Do not include the native window title.

        topBar = new JPanel(); // Contains a bar that contains the window title can be used to drag the LPDialog.
        topBar.setLayout(new BorderLayout()); // Layout of the topBar. BorderLayout helps align the title.
        topBar.setBackground(WindowActions.BOX_BACKGROUND);
        topBar.setPreferredSize(new Dimension(50, 35));
           /*
            * dragX and dragY are used as offset coordinates for the  mouse's coordinates.
            * Whenever the mouse is dragged, the coordinate of the window is changed to the mouse's coordinates added by the offset.
            * This is how the illusion of dragging is made.
            */
        topBar.addMouseListener(new MouseAdapter() {
           /*
            * When mouse is down on the topBar dragX and dragY are calculated.
            * dragX holds the difference between the abscissa of the cursor's position and the abscissa of the topBar's position on screen
            * dragY holds the difference between the ordinate of the cursor's position and the ordinate of the topBar's position on screen
            */
            @Override
            public void mousePressed(MouseEvent e) {
                dragX = MouseInfo.getPointerInfo().getLocation().x - topBar.getLocationOnScreen().x;
                dragY = MouseInfo.getPointerInfo().getLocation().y - topBar.getLocationOnScreen().y;
            }
        });
        topBar.addMouseMotionListener(new MouseAdapter(){
            /*
             * When the topBar is dragged, the x coordinate of the whole dialog is set to difference between the mouse's x and dragX
             * and the y coordinate of the whole dialog is set to difference between the mouse's y and dragY
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(new Point(MouseInfo.getPointerInfo().getLocation().x - dragX,
                        MouseInfo.getPointerInfo().getLocation().y - dragY));
            }
        });
        add(topBar, BorderLayout.NORTH);

        windowTitle = new JLabel(title, SwingConstants.CENTER);
        topBar.add(windowTitle, BorderLayout.CENTER);

        mainPanel = new JPanel();
        mainPanel.setLayout(null); // Default layout is set to null
        mainPanel.setBackground(WindowActions.PANEL_COLOR);
        add(mainPanel, BorderLayout.CENTER);
    }
    @Override
    public void setTitle(String title){
        windowTitle.setText(title);
    }
}
