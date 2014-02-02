package view;

/**
 * Displays the coin tossing simulation GUI.
 * 
 * @author Fabian Foerg
 */
public final class Main {
    /**
     * Initializes the GUI and displays it.
     * 
     * @param args
     *            unused.
     */
    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.createAndShowGUI();
    }
}
