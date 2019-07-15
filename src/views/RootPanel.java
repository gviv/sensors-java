package views;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.border.MatteBorder;

import components.MenuButton;
import config.Config;
import tree.SensorTreeModel;

/**
 * Panneau principal contenant les diff�rents sous-panneaux.
 */
public class RootPanel extends JPanel {

  /**
   * Cha�ne repr�sentant le panneau contenant le panneau temps r�el.
   */
  private static final String REAL_TIME_PANEL = "real_time_panel";

  /**
   * Cha�ne repr�sentant le panneau de visualisation a posteriori.
   */
  private static final String HISTORY_PANEL = "history_panel";

  /**
   * Cha�ne repr�sentant le panneau de r�glage des capteurs.
   */
  private static final String SETTINGS_PANEL = "settings_panel";

  /**
   * JPanel contenant le REAL_TIME_PANEL, le HISTORY_PANEL et le SETTINGS_PANEL.
   */
  private JPanel cards;

  /**
   * Constructeur.
   *
   * @param table le tableau temps r�el
   * @param port  le port de connexion du serveur
   */
  public RootPanel(JTable table, int port) {
    super(new BorderLayout());

    // Partie de droite contenant les panneaux
    cards = new JPanel();
    cards.setLayout(new CardLayout());
    add(cards, BorderLayout.CENTER);

    // Le SensorTreeModel veut �tre notifi� de la modification de la table
    SensorTreeModel treeModel = new SensorTreeModel();
    table.getModel().addTableModelListener(treeModel);

    // Cr�ation des diff�rents panneaux
    cards.add(new RealTimePanel(table, port), REAL_TIME_PANEL);
    cards.add(new HistoryPanel(treeModel), HISTORY_PANEL);
    cards.add(new SettingsPanel(treeModel), SETTINGS_PANEL);

    // Menu de gauche
    JPanel sidePanel = new JPanel();
    sidePanel.setBorder(new MatteBorder(0, 0, 0, 2, Config.BORDER_COLOR));
    sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
    sidePanel.setBackground(Config.MAIN_MENU_BACKGROUND_COLOR);
    add(sidePanel, BorderLayout.WEST);

    // Bouton du panneau temps r�el
    JToggleButton realTimeButton = new MenuButton("TEMPS R�EL");
    realTimeButton.addActionListener(e -> showPanel(REAL_TIME_PANEL));
    realTimeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    sidePanel.add(realTimeButton);

    // Bouton du panneau d'historique
    JToggleButton historyButton = new MenuButton("HISTORIQUE");
    historyButton.addActionListener(e -> showPanel(HISTORY_PANEL));
    historyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    sidePanel.add(historyButton);

    // Bouton du panneau de r�glages
    JToggleButton settingsButton = new MenuButton("R�GLAGES");
    settingsButton.addActionListener(e -> showPanel(SETTINGS_PANEL));
    settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    sidePanel.add(settingsButton);

    // On ne veut qu'un seul bouton s�lectionnable simultan�ment
    ButtonGroup group = new ButtonGroup();
    group.add(realTimeButton);
    group.add(historyButton);
    group.add(settingsButton);

    realTimeButton.setSelected(true);
  }

  /**
   * Affiche le panneau demand� depuis le JPanel stockant les diff�rents panneaux.
   *
   * @param panel le panneau demand�
   */
  private void showPanel(String panel) {
    CardLayout layout = (CardLayout) cards.getLayout();
    layout.show(cards, panel);
  }

}
