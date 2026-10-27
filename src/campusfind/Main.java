package campusfind;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application entry point for CampusFind.
 * 
 * Software Engineering Note:
 * This class initializes dependencies (FileStorage and RecoveryService)
 * and starts the graphical user interface on the Swing Event Dispatch Thread (EDT).
 */
public class Main {

    public static void main(String[] args) {
        // Run GUI initialization on Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system native look and feel for a modern desktop appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Fallback to default Swing Look & Feel if system look cannot be loaded
                System.out.println("Notice: Using default Swing Look and Feel (" + e.getMessage() + ")");
            }

            // Initialize storage and business logic service
            FileStorage storage = new FileStorage("data");
            RecoveryService recoveryService = new RecoveryService(storage);

            // Create and display main application window
            MainFrame mainFrame = new MainFrame(recoveryService);
            mainFrame.setVisible(true);
        });
    }
}
