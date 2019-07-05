package table;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import config.Config;

/**
 * Tableau temps réel des capteurs.
 */
public class SensorTable extends JTable {

  /**
   * Hauteur des lignes.
   */
  public static final int ROW_HEIGHT = 25;

  /**
   * Constructeur.
   */
  public SensorTable() {
    // Création du SensorTableModel
    setModel(new SensorTableModel(this));

    // Création d'un row sorter
    setRowSorter(new TableRowSorter<TableModel>(getModel()));

    // Association des colonnes au SensorTableRenderer
    SensorTableRenderer renderer = new SensorTableRenderer();
    TableColumnModel columnModel = getColumnModel();
    for (int i = 0; i < columnModel.getColumnCount(); ++i) {
      columnModel.getColumn(i).setCellRenderer(renderer);
    }

    // Customisation visuelle
    columnModel.getColumn(1).setPreferredWidth(140);
    getTableHeader().setReorderingAllowed(false);
    setFillsViewportHeight(true);
    setBackground(Config.BACKGROUND_COLOR);
    setShowVerticalLines(false);
    setIntercellSpacing(new Dimension(0, 1));
    setRowHeight(ROW_HEIGHT);
  }

}
