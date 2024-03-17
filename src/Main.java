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

import com.formdev.flatlaf.extras.FlatDesktop;
import javax.swing.UIManager;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
//import java.util.Locale;

// Entry point of the application.
public class Main {
    public static void main(String[] args) {

        FlatDesktop.setAboutHandler( () -> {
            if(!About.aboutShowing) new About();
        } );

        try {
             Locale.setDefault(new Locale("en", "US"));
            // Setting the Look And Feel theme of the whole project.
            UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacDarkLaf");

            // Initiates the splash screen (or banner?) to be shown while the main window initiates.
            SplashScreen splashScreen = new SplashScreen();
            splashScreen.baseFrame.setVisible(true);
            Thread.sleep(1000);

            // Creates a directory that holds every audio library that cn be used in the application.
            String startString = fileCAFE();

            // Initiate the main window.
            MainWindow win = new MainWindow(startString);

            // Hide the splash screen
            splashScreen.baseFrame.setVisible(false);

            // Show the main window
            win.mWindow.setVisible(true);

            // At this point, the app should be running without any issue.
            // Otherwise, an exception will be thrown on console.

        } catch (Exception e) {e.printStackTrace();}



    }

    /**
     * C.A.F.E. : A technique that is used to generate a workspace directory system that can be restored if
       there are any missing components. It is very handy when the application uses a simple workspace to function on.

      Create
      Absents,
      Fill
      Empties

     * The following method checks if "Lunchpad Repositories" directory exists in the home directory.

     * If there is no directory like that, it creates an empty one.

     * The method doesn't necessarily put any file or directory inside the freshly created one, assuming that the
       repositories are to be created by user later.

     * The method later initiates the Repository Manager window.

     * If user clicks cancel in the manager, the program closes. Otherwise, the program will return the repository name
       so that in the main method, the output will be used to initiate the main window.
     */
    public static String fileCAFE() throws java.io.IOException{
        Files.createDirectories(Paths.get(System.getProperty("user.home") + "/LunchPad Repositories/"));
        RepoMgr repoMgr = new RepoMgr(System.getProperty("user.home") + "/LunchPad Repositories/", "");
        if(repoMgr.cancelled) System.exit(0);
        System.out.println(repoMgr.getSelectedName());
        return repoMgr.getSelectedName();
    }
    
}