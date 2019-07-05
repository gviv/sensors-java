package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import config.Config;
import container.ManagerContainer;
import server.DBWriter;
import server.SensorServer;
import table.SensorTable;
import table.SensorTableModel;
import views.RootPanel;

/**
 * Classe principale de l'application.
 */
public class Main {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> createGUI());
  }

  /**
   * Crée l'interface.
   */
  public static void createGUI() {
    setDefaultFont();
    setDefaultColors();

    // Création de la JFrame
    JFrame frame = new JFrame("Projet Capteurs");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1280, 720);
    frame.setLocationRelativeTo(null);

    int port = getPort(frame);

    // Création de la table utilisée dans l'application
    JTable table = new SensorTable();

    // Lancement du serveur
    SensorServer ss = new SensorServer(port,
        new DBWriter(ManagerContainer.getInstance(), (SensorTableModel) table.getModel()));
    Thread t = new Thread(ss);
    t.start();

    // Création des panneaux de l'application
    frame.add(new RootPanel(table, port));

    frame.setVisible(true);
  }

  /**
   * Configure les fontes par défaut.
   */
  private static void setDefaultFont() {
    // Ajout d'une nouvelle police
    try {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
          Main.class.getClassLoader().getResourceAsStream("assets/fonts/Montserrat.ttf")));
    } catch (IOException | FontFormatException e) {
      e.printStackTrace();
    }

    Font f = new FontUIResource("Montserrat", Font.PLAIN, 12);

    // Modification de la police par défaut pour les principaux composants
    UIManager.put("Button.font", f);
    UIManager.put("ToggleButton.font", f);
    UIManager.put("ComboBox.font", f);
    UIManager.put("Label.font", f);
    UIManager.put("OptionPane.font", f);
    UIManager.put("Panel.font", f);
    UIManager.put("ScrollPane.font", f);
    UIManager.put("TextField.font", f);
    UIManager.put("Spinner.font", f);
    UIManager.put("Viewport.font", f);
    UIManager.put("TabbedPane.font", f);
    UIManager.put("Table.font", f);
    UIManager.put("TableHeader.font", f);
    UIManager.put("TitledBorder.font", f);
    UIManager.put("ToolTip.font", f);
    UIManager.put("Tree.font", f);
  }

  /**
   * Configure les couleurs par défaut.
   */
  private static void setDefaultColors() {
    UIManager.put("Panel.background", Config.BACKGROUND_COLOR);
    UIManager.put("OptionPane.background", Config.BACKGROUND_COLOR);
    UIManager.put("RootPane.background", Color.BLACK);
    UIManager.put("Label.foreground", Config.DEFAULT_TEXT_COLOR);
  }

  /**
   * Demande le port de connexion à l'utilisateur.
   *
   * @return le port demandé
   */
  private static int getPort(JFrame frame) {
    int port;

    do {
      String input = (String) JOptionPane.showInputDialog(frame, "Port :", "Connexion au serveur",
          JOptionPane.PLAIN_MESSAGE, null, null, 8952);

      if (input == null) {
        frame.dispose();
        System.exit(0);
      }

      try {
        port = Integer.valueOf(input);
      } catch (NumberFormatException e) {
        port = -1;
      }
    } while (port < 0 || port > 65535);

    return port;
  }

}
