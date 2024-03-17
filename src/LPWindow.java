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

import com.sun.awt.AWTUtilities;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;
import com.formdev.flatlaf.icons.*;

/**
 * The class that extends from JFrame, which features a title bar and window control buttons designed for Lunchpad.
 * <pre></pre>
 * <strong>IMPORTANT: The contents that you want to add to LPWindow don't directly go to
 * the LPWindow itself, they go to mainPanel, which is there to hold the added components.</strong>
 * <pre></pre>
 * With that being said, when adding a component, don't just directly call {@code add(my_component)},
 * instead, call {@code mainPanel.add(my_component)}.
 * <pre></pre>
 * The layout of the mainPanel is set to null as default. You may change it later.
 * <pre></pre>
 * @author İ. K. Bilir
 * @since 1.0
 */

public class LPWindow extends JFrame {
    // dragX holds the difference between the abscissa of the cursor's position and the abscissa of the topBar's position on screen
    // dragY holds the difference between the ordinate of the cursor's position and the ordinate of the topBar's position on screen
    //prevWidth holds the width of the window before resizing
    //prevHeight holds the height of the window before resizing
    protected int dragX, dragY, pinX, pinY, prevWidth, prevHeight;
    protected JButton minimizeButton, maximizeButton;
    public JButton closeButton;
    protected JPanel topBar, mainPanel, windowControlsPanel;
    protected JPanel[] borders; // Borders are used for resizing the window. They respond to mouse draggings.
    protected Dimension dimensionBeforeMaximize; // Keeps the last dimension of the window before maximizing
                                                 // When window is minimized, it goes back to the dimension stored here
    protected Point locationBeforeMaximize; // Keeps the last position of the window before maximizing
                                            // When window is minimized, it goes back to the position stored here
    private final JLabel windowTitle;

    protected static // Initializing icons for the window control buttons.
    Icon x = new FlatWindowCloseIcon(),
         y = new FlatWindowIconifyIcon(),
         z = new FlatWindowMaximizeIcon(),
         t = new FlatWindowRestoreIcon();

    /**
     * Creates a new LPWindow object.
     * @param title Title of the window. Leave empty if you don't want any title.
     */
    public LPWindow(String title) throws java.io.IOException {
        setLayout(new BorderLayout()); // Setting the layout for the frame itself.
        setUndecorated(true); // Do not include the native window title.
        
        topBar = new JPanel(); // Contains a bar that contains the window title can be used to drag the LPWindow.
        dimensionBeforeMaximize = new Dimension();
        locationBeforeMaximize = new Point();

        topBar.setLayout(new BorderLayout()); // Layout of the topBar. BorderLayout helps align the title.
        topBar.setBackground(WindowActions.BAR_BACKGROUND);
        topBar.setPreferredSize(new Dimension(50, 38));
        /*
         * dragX and dragY are used as offset coordinates for the  mouse's coordinates.
         * Whenever the mouse is dragged, the coordinate of the window is changed to the mouse's coordinates added by the offset.
         * This is how the illusion of dragging is made.
         */
        topBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                /*
                 * When mouse is down on the topBar dragX and dragY are calculated.
                 * dragX holds the difference between the abscissa of the cursor's position and the abscissa of the topBar's position on screen
                 * dragY holds the difference between the ordinate of the cursor's position and the ordinate of the topBar's position on screen
                 */
                dragX = MouseInfo.getPointerInfo().getLocation().x - topBar.getLocationOnScreen().x;
                dragY = MouseInfo.getPointerInfo().getLocation().y - topBar.getLocationOnScreen().y;
            }
        });
        topBar.addMouseMotionListener(new MouseAdapter(){
            @Override
            public void mouseDragged(MouseEvent e) {
                /*
                 * When the topBar is dragged, the x coordinate of the whole window is set to difference between the mouse's x and dragX
                 * and the y coordinate of the whole window is set to difference between the mouse's y and dragY
                 */
                setLocation(new Point(MouseInfo.getPointerInfo().getLocation().x - dragX,
                        MouseInfo.getPointerInfo().getLocation().y - dragY));

            }
        });
        add(topBar, BorderLayout.NORTH);

        windowTitle = new JLabel(title, SwingConstants.CENTER);
        topBar.add(windowTitle, BorderLayout.CENTER);

        // Initiating borders
        borders = new JPanel[8];
        for(int i = 0; i < 8; i++){
            final int borderIndex = i;
            borders[i] = new JPanel();
            if(i == 1 || i == 3) borders[i].setLayout(new BorderLayout());
            else borders[i].setLayout(null);
            if(i == 3 || i == 4 ||i == 5 || i == 6 || i == 7)
                borders[i].setBackground(WindowActions.BAR_BACKGROUND);
            else borders[i].setBackground(WindowActions.PANEL_COLOR);

            borders[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                        resizeWindow(borderIndex);
                    }
                @Override
                public void mouseEntered(MouseEvent e) {
                        changeCursor(borderIndex);
                    }
                @Override
                public void mouseExited(MouseEvent e){
                        changeCursor(-1);
                    }
            });
            borders[i].addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {resize(borderIndex);
                    }
                });
        }

        borders[0].setPreferredSize(new Dimension(5, 25));
        borders[1].setPreferredSize(new Dimension(5, 5));
        borders[2].setPreferredSize(new Dimension(5, 25));
        borders[3].setPreferredSize(new Dimension(5, 4));
        add(borders[0], BorderLayout.WEST);
        add(borders[1], BorderLayout.SOUTH);
        add(borders[2], BorderLayout.EAST);
        topBar.add(borders[3], BorderLayout.NORTH);

        borders[1].add(borders[4], BorderLayout.WEST);
        borders[1].add(borders[5], BorderLayout.EAST);
        borders[3].add(borders[6], BorderLayout.EAST);
        borders[3].add(borders[7], BorderLayout.WEST);

        windowControlsPanel = new JPanel(null);
        windowControlsPanel.setBackground(WindowActions.BAR_BACKGROUND);
        windowControlsPanel.setPreferredSize(new Dimension(180, 30));
        //windowControlsPanel.setBounds(0, 0, 180, 10);

        // Initializing the window control buttons
        closeButton = new JButton(x);
        closeButton.setBounds(0, 0, 50, 30);
        closeButton.setBackground(WindowActions.BAR_BACKGROUND);
        closeButton.setFocusable(false);

        minimizeButton = new JButton(y);
        minimizeButton.setBounds(50, 0, 50, 30);
        minimizeButton.setBackground(WindowActions.BAR_BACKGROUND);
        minimizeButton.setFocusable(false);
        minimizeButton.addActionListener(e -> this.setState(Frame.ICONIFIED));

        maximizeButton = new JButton(z);
        maximizeButton.setBounds(100, 0, 50, 30);
        maximizeButton.setBackground(WindowActions.BAR_BACKGROUND);
        maximizeButton.setFocusable(false);

        // Detect whether the program runs on a Macintosh machine, and arrange window control buttons accordingly.
        if(System.getProperty("os.name").toLowerCase().startsWith("mac")){
            topBar.add(windowControlsPanel, BorderLayout.WEST);
            windowControlsPanel.add(closeButton);
            windowControlsPanel.add(minimizeButton);
            windowControlsPanel.add(maximizeButton);
        } else {
            topBar.add(windowControlsPanel, BorderLayout.EAST);
            windowControlsPanel.add(minimizeButton);
            windowControlsPanel.add(maximizeButton);
            windowControlsPanel.add(closeButton);
        }

        // Maximize button has two different actions, therefore the action listener gets the condition statement.
        maximizeButton.addActionListener(e ->
            {
               if(getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                   setExtendedState(JFrame.MAXIMIZED_BOTH);
                   maximizeButton.setIcon(t);
               } else {
                   setExtendedState(JFrame.NORMAL);
                   maximizeButton.setIcon(z);
               }
           }
        );

        // mainPanel is where the new components should be added.
        mainPanel = new JPanel();
        mainPanel.setBackground(WindowActions.PANEL_COLOR);
        add(mainPanel, BorderLayout.CENTER);

    }

    // Assigns pinning variables (int) before resizing (dragging)
    // The method is called by the border dragged in order to resize.
    protected void resizeWindow(int borderIndex){
        if(borderIndex == 0 || borderIndex == 4 || borderIndex == 7){
            pinX = borders[0].getLocationOnScreen().x;
            dragX = MouseInfo.getPointerInfo().getLocation().x - borders[0].getLocationOnScreen().x;
            prevWidth = getWidth();
        }

        if(borderIndex == 1 || borderIndex == 4 || borderIndex == 5){
            pinY = borders[1].getLocationOnScreen().y;
            prevHeight = getHeight();
        }

        if(borderIndex == 2 || borderIndex == 5 || borderIndex == 6){
            pinX = borders[2].getLocationOnScreen().x;
            prevWidth = getWidth();
        }

        if(borderIndex == 3 || borderIndex == 6 || borderIndex == 7){
            pinY = borders[3].getLocationOnScreen().y;
            dragY = MouseInfo.getPointerInfo().getLocation().y - borders[3].getLocationOnScreen().y;
            prevHeight = getHeight();
        }
    }

    // Actually resizing the window. Method called after the pinning values assigned.
    protected void resize(int borderIndex){

        if(borderIndex == 0 || borderIndex == 4 || borderIndex == 7){
            setSize(prevWidth + pinX - MouseInfo.getPointerInfo().getLocation().x, getHeight());
            if(getSize().getWidth() > getMinimumSize().getWidth()) {
                setLocation(new Point(MouseInfo.getPointerInfo().getLocation().x, getY()));
            }
        }

        if(borderIndex == 1 || borderIndex == 4 || borderIndex == 5){
            setSize(getWidth(), prevHeight  + MouseInfo.getPointerInfo().getLocation().y - pinY);
        }

        if(borderIndex == 2 || borderIndex == 5 || borderIndex == 6){
            setSize(prevWidth  + MouseInfo.getPointerInfo().getLocation().x - pinX, getHeight());
        }

        if(borderIndex == 3 || borderIndex == 6 || borderIndex == 7){
            setSize(getWidth(), prevHeight + pinY - MouseInfo.getPointerInfo().getLocation().y);
            if(getSize().getHeight() > getMinimumSize().getHeight()) {
                setLocation(new Point(getX(), MouseInfo.getPointerInfo().getLocation().y));
            }
        }
    }

    // Changes the cursor look
    protected  void changeCursor(int borderIndex){
        switch (borderIndex){
            case 0:
                setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
                break;
            case 1:
                setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
                break;
            case 2:
                setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
                break;
            case 3:
                setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
                break;
            case 4:
                setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
                break;
            case 5:
                setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
                break;
            case 6:
                setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
                break;
            case 7:
                setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
                break;
            case -1:
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                break;
        }

    }

    @Override
    public void setTitle(String title){
        windowTitle.setText(title);
    }
}
