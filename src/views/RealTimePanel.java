package views;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import components.FluidButtons;
import config.Config;
import entities.Building;
import entities.Fluid.Type;
import entities.Sensor;
import entities.Value;
import laf.LargeToggleButtonUI;
import table.SensorTableModel;
import utilities.ImageLoader;

/**
 * Panneau de visualisation des capteurs en temps réel.
 */
public class RealTimePanel extends JPanel {

  /**
   * Chaîne représentant le panneau contenant le tableau.
   */
  private static final String TABLE_PANEL = "table_panel";

  /**
   * Chaîne représentant le panneau indiquant l'absence de sélection de capteur.
   */
  private static final String NO_SENSOR_PANEL = "no_sensor_panel";

  /**
   * Largeur des icônes de la barre d'infos.
   */
  private static final int INFO_ICON_WIDTH = 15;

  /**
   * Tableau contenant les valeurs en temps réel.
   */
  private JTable table;

  /**
   * JPanel contenant le TABLE_PANEL et le NO_SENSOR_PANEL.
   */
  private JPanel cards;

  /**
   * Label affichant le nombre de capteurs connectés.
   */
  private JLabel labelNbConnected;

  /**
   * Label affichant le nombre de capteurs dont la valeur est hors limites.
   */
  private JLabel labelNbOutOfBounds;

  /**
   * Constructeur.
   *
   * @param table le tableau temps réel
   * @param port  le port de connexion du serveur
   */
  public RealTimePanel(JTable table, int port) {
    super(new CardLayout());
    this.table = table;
    cards = this;
    ImageLoader im = ImageLoader.getInstance();

    // Panneau indiquant l'absence de capteur connecté
    add(new NoSensorPanel("Aucun capteur connecté", "Écoute sur le port " + port), NO_SENSOR_PANEL);

    // Panneau principal
    JPanel root = new JPanel(new BorderLayout());

    // Panneau contenant le tableau et les infos
    JPanel tableAndInfosPanel = new JPanel(new BorderLayout());
    JScrollPane sc = new JScrollPane(table);
    sc.setBorder(new MatteBorder(1, 0, 0, 0, Config.BORDER_COLOR));
    tableAndInfosPanel.add(sc, BorderLayout.CENTER);

    // Panneau contenant des infos sur le tableau
    JPanel infosPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));

    // Label du nombre de capteurs connectés
    labelNbConnected = new JLabel();
    labelNbConnected.setIcon(im.loadImageIcon("/assets/waves.png", -1, INFO_ICON_WIDTH));

    // Label du nombre de capteurs hors limites
    labelNbOutOfBounds = new JLabel();
    labelNbOutOfBounds.setIcon(im.loadImageIcon("/assets/warning.png", -1, INFO_ICON_WIDTH));

    infosPanel.add(labelNbConnected);
    infosPanel.add(labelNbOutOfBounds);
    tableAndInfosPanel.add(infosPanel, BorderLayout.NORTH);
    root.add(tableAndInfosPanel, BorderLayout.CENTER);

    // Ajout du panneau de sélection des filtres
    FilterPanel filterPanel = new FilterPanel();
    table.getModel().addTableModelListener(filterPanel);
    root.add(filterPanel, BorderLayout.WEST);
    add(root, TABLE_PANEL);
  }

  /**
   * Panneau contenant les options de filtrage.
   */
  private class FilterPanel extends JPanel implements TableModelListener {

    /**
     * Filtres de fluides actuellement appliqués.
     */
    private Set<String> fluidFilters = new HashSet<>();

    /**
     * Filtres de bâtiments actuellement appliqués.
     */
    private Set<String> buildingFilters = new HashSet<>();

    /**
     * Panneau contenant les bâtiments.
     */
    private JPanel buildingsPanel;

    /**
     * Associe un bâtiment et un bouton.
     */
    private Map<Building, AbstractButton> buildingToButton = new HashMap<>();

    /**
     * Sorter de la table.
     */
    private TableRowSorter<? extends TableModel> rowSorter;

    /**
     * Boutons des fluides.
     */
    FluidButtons buttons;

    /**
     * Constructeur.
     */
    public FilterPanel() {
      super(new BorderLayout());
      rowSorter = (TableRowSorter<? extends TableModel>) table.getRowSorter();

      setBorder(new MatteBorder(0, 0, 0, 2, Config.BORDER_COLOR));

      // Panneau contenant les boutons
      JPanel fluidButtonsPanel = new JPanel();
      add(fluidButtonsPanel, BorderLayout.NORTH);
      fluidButtonsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      fluidButtonsPanel.setLayout(new BoxLayout(fluidButtonsPanel, BoxLayout.Y_AXIS));
      JLabel titleLabel = new JLabel("FILTRER");
      titleLabel.setFont(Config.getDefaultFont(14));
      fluidButtonsPanel.add(titleLabel);

      // Création des boutons
      buttons = new FluidButtons(4);
      buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
      fluidButtonsPanel.add(buttons);

      // Ajout du listener
      buttons.getBtnWater().addItemListener(e -> onFluidClick());
      buttons.getBtnElec().addItemListener(e -> onFluidClick());
      buttons.getBtnTemp().addItemListener(e -> onFluidClick());
      buttons.getBtnAir().addItemListener(e -> onFluidClick());

      // Panneau contenant les boutons de bâtiments
      buildingsPanel = new JPanel();
      buildingsPanel.setLayout(new BoxLayout(buildingsPanel, BoxLayout.Y_AXIS));
      JScrollPane sc = new JScrollPane(buildingsPanel);
      sc.setBackground(Config.BACKGROUND_COLOR);
      sc.setBorder(null);

      JLabel buildingLabel = new JLabel("Bâtiments");
      buildingLabel.setFont(Config.getDefaultFont(13));
      buildingLabel.setBorder(new EmptyBorder(5, 5, 5, 0));

      buildingsPanel.add(buildingLabel);
      add(buildingsPanel, BorderLayout.CENTER);
    }

    /**
     * Applique un filtre sur le tableau en fonction des fluides et bâtiments
     * sélectionnés.
     */
    private void filter() {
      if (fluidFilters.isEmpty() && buildingFilters.isEmpty()) {
        rowSorter.setRowFilter(null);
      } else {
        // Création des filtres de fluides/bâtiments
        List<RowFilter<TableModel, Object>> listFluidFilters = new ArrayList<>();
        List<RowFilter<TableModel, Object>> listBuildingFilters = new ArrayList<>();
        try {
          // Ajout des filtres de fluide
          for (String s : fluidFilters) {
            listFluidFilters.add(RowFilter.regexFilter(s));
          }

          // Ajout des filtres de bâtiment
          for (String s : buildingFilters) {
            listBuildingFilters.add(RowFilter.regexFilter(s));
          }
        } catch (PatternSyntaxException e) {
          e.printStackTrace();
          return;
        }

        // Création d'un filtre ET entre les fluides et les bâtiments
        List<RowFilter<TableModel, Object>> filters = new ArrayList<>();
        if (!fluidFilters.isEmpty()) filters.add(RowFilter.orFilter(listFluidFilters));
        if (!buildingFilters.isEmpty()) filters.add(RowFilter.orFilter(listBuildingFilters));
        rowSorter.setRowFilter(RowFilter.andFilter(filters));
      }
    }

    /**
     * Ajoute les fluides sélectionnés à la liste des filtres de fluide.
     */
    private void onFluidClick() {
      addOrRemoveFilter(fluidFilters, buttons.getBtnWater(), Type.EAU.toString());
      addOrRemoveFilter(fluidFilters, buttons.getBtnElec(), Type.ELECTRICITE.toString());
      addOrRemoveFilter(fluidFilters, buttons.getBtnTemp(), Type.TEMPERATURE.toString());
      addOrRemoveFilter(fluidFilters, buttons.getBtnAir(), Type.AIRCOMPRIME.toString());

      filter();
    }

    /**
     * Ajoute les bâtiments sélectionnés à la liste des filtres de bâtiment.
     */
    private void onBuildingClick() {
      Set<Map.Entry<Building, AbstractButton>> entrySet = buildingToButton.entrySet();

      // On parcourt tous les boutons et on regarde s'ils sont selectionnés
      for (Map.Entry<Building, AbstractButton> entry : entrySet) {
        Building building = entry.getKey();
        AbstractButton btn = entry.getValue();
        addOrRemoveFilter(buildingFilters, btn, building.getName());
      }

      filter();
    }

    /**
     * Ajoute ou enlève un filtre au set selon que le bouton est sélectionné ou non.
     *
     * @param set    le set auquel ajouter le filtre
     * @param button le bouton à tester
     * @param filter le filtre à ajouter
     */
    private void addOrRemoveFilter(Set<String> set, AbstractButton button, String filter) {
      if (button.isSelected()) {
        set.add(filter);
      } else {
        set.remove(filter);
      }
    }

    /**
     * Met à jour le label affichant le nombre de capteurs actuellement connectés.
     *
     * @param nbConnected le nombre de capteurs connectés
     */
    private void updateLabelNbConnected(int nbConnected) {
      labelNbConnected.setText(nbConnected + " connecté" + (nbConnected <= 1 ? "" : "s"));
    }

    /**
     * Met à jour le label affichant le nombre de capteurs actuellement hors
     * limites.
     *
     * @param tableModel le modèle du tableau temps réel
     */
    private void updateLabelNbOutOfBounds(SensorTableModel tableModel) {
      int nbOutOfBounds = 0;

      for (int i = 0; i < tableModel.getRowCount(); ++i) {
        Value v = (Value) tableModel.getValueAt(i, 5);
        Sensor s = (Sensor) tableModel.getSensorAt(i);
        if (v != null && v.isOutOfBounds(s)) {
          nbOutOfBounds++;
        }
      }

      labelNbOutOfBounds.setText(nbOutOfBounds + " hors limites");
    }

    /**
     * Ajoute un bouton de bâtiment si un capteur avec un nouveau bâtiment est
     * ajouté au tableau. Affiche aussi le tableau quand le premier capteur se
     * connecte et le masque si plus aucun capteur n'est connecté.
     *
     * @param e le TableModelEvent
     */
    @Override
    public void tableChanged(TableModelEvent e) {
      SensorTableModel model = (SensorTableModel) e.getSource();
      int firstRow = e.getFirstRow();
      int lastRow = e.getLastRow();
      // On ignore l'en-tête
      if (firstRow == TableModelEvent.HEADER_ROW) firstRow++;

      updateLabelNbConnected(model.getRowCount());
      updateLabelNbOutOfBounds(model);

      // Pour chaque ligne modifiée
      for (int i = firstRow; i <= lastRow; ++i) {
        if (e.getType() == TableModelEvent.INSERT) {
          // Nouveau capteur inséré
          if (model.getRowCount() == 1) {
            // Premier capteur connecté, on affiche le tableau
            CardLayout layout = (CardLayout) cards.getLayout();
            layout.show(cards, TABLE_PANEL);
          }

          // On récupère le building de la ligne insérée
          Building building = (Building) model.getValueAt(table.convertRowIndexToModel(i), 2);
          if (!buildingToButton.containsKey(building)) {
            // Ajout du nouveau bouton de bâtiment
            JToggleButton btn = new JToggleButton(building.getName());
            btn.setUI(new LargeToggleButtonUI());
            btn.setPreferredSize(new Dimension(buildingsPanel.getWidth(), LargeToggleButtonUI.HEIGHT));
            btn.setToolTipText(building.getName());
            buildingsPanel.add(btn);
            buildingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            buildingToButton.put(building, btn);
            btn.addItemListener(ev -> onBuildingClick());
            buildingsPanel.revalidate();
            buildingsPanel.repaint();
          }
        } else if (e.getType() == TableModelEvent.DELETE) {
          // Capteur supprimé du tableau
          if (model.getRowCount() == 0) {
            // Plus aucun capteur connecté, on enlève le tableau
            CardLayout layout = (CardLayout) cards.getLayout();
            layout.show(cards, NO_SENSOR_PANEL);
          }
          // On aurait pu supprimer le bouton d'un bâtiment donné si plus
          // aucun capteur n'appartient à ce bâtiment mais vu que la
          // ligne a déjà été supprimée, on ne sait pas quel bâtiment
          // a potentiellement été enlevé. On aurait pu parcourir tout
          // le tableau pour vérifier quels bâtiments sont encore présents
          // mais cette méthode est appelée quand une ligne visible
          // (non filtrée) a été modifiée, ce qui rendrait la fonctionnalité
          // non fiable (ne fonctionnerait pas lors de suppression de
          // filtrées). On se passe donc de la suppression des boutons
          // de bâtiment qui ne correspondent plus à aucun capteur.
        }
      }
    }

  }

}
