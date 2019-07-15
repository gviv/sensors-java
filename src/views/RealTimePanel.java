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
 * Panneau de visualisation des capteurs en temps r�el.
 */
public class RealTimePanel extends JPanel {

  /**
   * Cha�ne repr�sentant le panneau contenant le tableau.
   */
  private static final String TABLE_PANEL = "table_panel";

  /**
   * Cha�ne repr�sentant le panneau indiquant l'absence de s�lection de capteur.
   */
  private static final String NO_SENSOR_PANEL = "no_sensor_panel";

  /**
   * Largeur des ic�nes de la barre d'infos.
   */
  private static final int INFO_ICON_WIDTH = 15;

  /**
   * Tableau contenant les valeurs en temps r�el.
   */
  private JTable table;

  /**
   * JPanel contenant le TABLE_PANEL et le NO_SENSOR_PANEL.
   */
  private JPanel cards;

  /**
   * Label affichant le nombre de capteurs connect�s.
   */
  private JLabel labelNbConnected;

  /**
   * Label affichant le nombre de capteurs dont la valeur est hors limites.
   */
  private JLabel labelNbOutOfBounds;

  /**
   * Constructeur.
   *
   * @param table le tableau temps r�el
   * @param port  le port de connexion du serveur
   */
  public RealTimePanel(JTable table, int port) {
    super(new CardLayout());
    this.table = table;
    cards = this;
    ImageLoader im = ImageLoader.getInstance();

    // Panneau indiquant l'absence de capteur connect�
    add(new NoSensorPanel("Aucun capteur connect�", "�coute sur le port " + port), NO_SENSOR_PANEL);

    // Panneau principal
    JPanel root = new JPanel(new BorderLayout());

    // Panneau contenant le tableau et les infos
    JPanel tableAndInfosPanel = new JPanel(new BorderLayout());
    JScrollPane sc = new JScrollPane(table);
    sc.setBorder(new MatteBorder(1, 0, 0, 0, Config.BORDER_COLOR));
    tableAndInfosPanel.add(sc, BorderLayout.CENTER);

    // Panneau contenant des infos sur le tableau
    JPanel infosPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));

    // Label du nombre de capteurs connect�s
    labelNbConnected = new JLabel();
    labelNbConnected.setIcon(im.loadImageIcon("/assets/waves.png", -1, INFO_ICON_WIDTH));

    // Label du nombre de capteurs hors limites
    labelNbOutOfBounds = new JLabel();
    labelNbOutOfBounds.setIcon(im.loadImageIcon("/assets/warning.png", -1, INFO_ICON_WIDTH));

    infosPanel.add(labelNbConnected);
    infosPanel.add(labelNbOutOfBounds);
    tableAndInfosPanel.add(infosPanel, BorderLayout.NORTH);
    root.add(tableAndInfosPanel, BorderLayout.CENTER);

    // Ajout du panneau de s�lection des filtres
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
     * Filtres de fluides actuellement appliqu�s.
     */
    private Set<String> fluidFilters = new HashSet<>();

    /**
     * Filtres de b�timents actuellement appliqu�s.
     */
    private Set<String> buildingFilters = new HashSet<>();

    /**
     * Panneau contenant les b�timents.
     */
    private JPanel buildingsPanel;

    /**
     * Associe un b�timent et un bouton.
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

      // Cr�ation des boutons
      buttons = new FluidButtons(4);
      buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
      fluidButtonsPanel.add(buttons);

      // Ajout du listener
      buttons.getBtnWater().addItemListener(e -> onFluidClick());
      buttons.getBtnElec().addItemListener(e -> onFluidClick());
      buttons.getBtnTemp().addItemListener(e -> onFluidClick());
      buttons.getBtnAir().addItemListener(e -> onFluidClick());

      // Panneau contenant les boutons de b�timents
      buildingsPanel = new JPanel();
      buildingsPanel.setLayout(new BoxLayout(buildingsPanel, BoxLayout.Y_AXIS));
      JScrollPane sc = new JScrollPane(buildingsPanel);
      sc.setBackground(Config.BACKGROUND_COLOR);
      sc.setBorder(null);

      JLabel buildingLabel = new JLabel("B�timents");
      buildingLabel.setFont(Config.getDefaultFont(13));
      buildingLabel.setBorder(new EmptyBorder(5, 5, 5, 0));

      buildingsPanel.add(buildingLabel);
      add(buildingsPanel, BorderLayout.CENTER);
    }

    /**
     * Applique un filtre sur le tableau en fonction des fluides et b�timents
     * s�lectionn�s.
     */
    private void filter() {
      if (fluidFilters.isEmpty() && buildingFilters.isEmpty()) {
        rowSorter.setRowFilter(null);
      } else {
        // Cr�ation des filtres de fluides/b�timents
        List<RowFilter<TableModel, Object>> listFluidFilters = new ArrayList<>();
        List<RowFilter<TableModel, Object>> listBuildingFilters = new ArrayList<>();
        try {
          // Ajout des filtres de fluide
          for (String s : fluidFilters) {
            listFluidFilters.add(RowFilter.regexFilter(s));
          }

          // Ajout des filtres de b�timent
          for (String s : buildingFilters) {
            listBuildingFilters.add(RowFilter.regexFilter(s));
          }
        } catch (PatternSyntaxException e) {
          e.printStackTrace();
          return;
        }

        // Cr�ation d'un filtre ET entre les fluides et les b�timents
        List<RowFilter<TableModel, Object>> filters = new ArrayList<>();
        if (!fluidFilters.isEmpty()) filters.add(RowFilter.orFilter(listFluidFilters));
        if (!buildingFilters.isEmpty()) filters.add(RowFilter.orFilter(listBuildingFilters));
        rowSorter.setRowFilter(RowFilter.andFilter(filters));
      }
    }

    /**
     * Ajoute les fluides s�lectionn�s � la liste des filtres de fluide.
     */
    private void onFluidClick() {
      addOrRemoveFilter(fluidFilters, buttons.getBtnWater(), Type.EAU.toString());
      addOrRemoveFilter(fluidFilters, buttons.getBtnElec(), Type.ELECTRICITE.toString());
      addOrRemoveFilter(fluidFilters, buttons.getBtnTemp(), Type.TEMPERATURE.toString());
      addOrRemoveFilter(fluidFilters, buttons.getBtnAir(), Type.AIRCOMPRIME.toString());

      filter();
    }

    /**
     * Ajoute les b�timents s�lectionn�s � la liste des filtres de b�timent.
     */
    private void onBuildingClick() {
      Set<Map.Entry<Building, AbstractButton>> entrySet = buildingToButton.entrySet();

      // On parcourt tous les boutons et on regarde s'ils sont selectionn�s
      for (Map.Entry<Building, AbstractButton> entry : entrySet) {
        Building building = entry.getKey();
        AbstractButton btn = entry.getValue();
        addOrRemoveFilter(buildingFilters, btn, building.getName());
      }

      filter();
    }

    /**
     * Ajoute ou enl�ve un filtre au set selon que le bouton est s�lectionn� ou non.
     *
     * @param set    le set auquel ajouter le filtre
     * @param button le bouton � tester
     * @param filter le filtre � ajouter
     */
    private void addOrRemoveFilter(Set<String> set, AbstractButton button, String filter) {
      if (button.isSelected()) {
        set.add(filter);
      } else {
        set.remove(filter);
      }
    }

    /**
     * Met � jour le label affichant le nombre de capteurs actuellement connect�s.
     *
     * @param nbConnected le nombre de capteurs connect�s
     */
    private void updateLabelNbConnected(int nbConnected) {
      labelNbConnected.setText(nbConnected + " connect�" + (nbConnected <= 1 ? "" : "s"));
    }

    /**
     * Met � jour le label affichant le nombre de capteurs actuellement hors
     * limites.
     *
     * @param tableModel le mod�le du tableau temps r�el
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
     * Ajoute un bouton de b�timent si un capteur avec un nouveau b�timent est
     * ajout� au tableau. Affiche aussi le tableau quand le premier capteur se
     * connecte et le masque si plus aucun capteur n'est connect�.
     *
     * @param e le TableModelEvent
     */
    @Override
    public void tableChanged(TableModelEvent e) {
      SensorTableModel model = (SensorTableModel) e.getSource();
      int firstRow = e.getFirstRow();
      int lastRow = e.getLastRow();
      // On ignore l'en-t�te
      if (firstRow == TableModelEvent.HEADER_ROW) firstRow++;

      updateLabelNbConnected(model.getRowCount());
      updateLabelNbOutOfBounds(model);

      // Pour chaque ligne modifi�e
      for (int i = firstRow; i <= lastRow; ++i) {
        if (e.getType() == TableModelEvent.INSERT) {
          // Nouveau capteur ins�r�
          if (model.getRowCount() == 1) {
            // Premier capteur connect�, on affiche le tableau
            CardLayout layout = (CardLayout) cards.getLayout();
            layout.show(cards, TABLE_PANEL);
          }

          // On r�cup�re le building de la ligne ins�r�e
          Building building = (Building) model.getValueAt(table.convertRowIndexToModel(i), 2);
          if (!buildingToButton.containsKey(building)) {
            // Ajout du nouveau bouton de b�timent
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
          // Capteur supprim� du tableau
          if (model.getRowCount() == 0) {
            // Plus aucun capteur connect�, on enl�ve le tableau
            CardLayout layout = (CardLayout) cards.getLayout();
            layout.show(cards, NO_SENSOR_PANEL);
          }
          // On aurait pu supprimer le bouton d'un b�timent donn� si plus
          // aucun capteur n'appartient � ce b�timent mais vu que la
          // ligne a d�j� �t� supprim�e, on ne sait pas quel b�timent
          // a potentiellement �t� enlev�. On aurait pu parcourir tout
          // le tableau pour v�rifier quels b�timents sont encore pr�sents
          // mais cette m�thode est appel�e quand une ligne visible
          // (non filtr�e) a �t� modifi�e, ce qui rendrait la fonctionnalit�
          // non fiable (ne fonctionnerait pas lors de suppression de
          // filtr�es). On se passe donc de la suppression des boutons
          // de b�timent qui ne correspondent plus � aucun capteur.
        }
      }
    }

  }

}
