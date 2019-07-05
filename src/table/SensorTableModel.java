package table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import entities.Sensor;

/**
 * Modèle du tableau temps réel.
 */
public class SensorTableModel extends AbstractTableModel {

  /**
   * Colonnes du tableau.
   */
  private final String[] columns = { "Nom", "Type", "Bâtiment", "Étage", "Lieu", "Valeur" };

  /**
   * Liste des capteurs actuellements stockés.
   */
  private List<Sensor> sensors = new ArrayList<>();

  /**
   * La table associée au modèle (on en a besoin pour convertir les indices de la
   * vue au modèle)
   */
  private JTable table;

  /**
   * Constructeur.
   *
   * @param table la table associée
   */
  public SensorTableModel(JTable table) {
    this.table = table;
  }

  /**
   * @return le nombre de lignes
   */
  @Override
  public int getRowCount() {
    return sensors.size();
  }

  /**
   * @return le nombre de colonnes
   */
  @Override
  public int getColumnCount() {
    return columns.length;
  }

  /**
   * Récupère le nom d'une colonne.
   *
   * @param column l'indice de la colonne
   * @return le nom de la colonne
   */
  @Override
  public String getColumnName(int column) {
    return columns[column];
  }

  /**
   * Renvoie la classe associée à une colonne.
   *
   * @param columnIndex l'indice de la colonne
   * @return la classe associée à la colonne
   */
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    if (getRowCount() == 0) {
      return Object.class;
    }

    Object value = getValueAt(0, columnIndex);
    // On teste si c'est nul car on s'est permis d'avoir des valeurs nulles
    // dans le tableau
    if (value != null) {
      return value.getClass();
    }
    return Object.class;
  }

  /**
   * Récupère la valeur d'une ligne/colonne. La colonne des valeurs peut contenir
   * des valeurs nulles (quand on n'a pas encore reçu de données).
   *
   * @param modelRowIndex l'indice de la ligne par rapport au modèle
   * @param columnIndex   l'indice de la colonne
   * @return la valeur située aux indices donnés
   */
  @Override
  public Object getValueAt(int modelRowIndex, int columnIndex) {
    Sensor sensor = sensors.get(modelRowIndex);

    switch (columnIndex) {
      case 0: return sensor.getName();
      case 1: return sensor.getFluid();
      case 2: return sensor.getBuilding();
      case 3: return sensor.getFloor();
      case 4: return sensor.getPlace();
      case 5: return sensor.getLastValue();
      default: throw new IllegalArgumentException("Invalid column index");
    }
  }

  /**
   * Insère un capteur dans le tableau.
   *
   * @param sensor le capteur à insérer
   */
  @SuppressWarnings("unchecked")
  public void insert(Sensor sensor) {
    TableRowSorter<? extends TableModel> sorter = (TableRowSorter<? extends TableModel>) table.getRowSorter();
    RowFilter<TableModel, Object> rf = (RowFilter<TableModel, Object>) sorter.getRowFilter();

    // On désactive les filtres le temps de l'insertion
    sorter.setRowFilter(null);
    // On désactive le sorter le temps de l'insertion (convertRowIndexToView
    // n'a pas l'air de fonctionner dans le cas de l'insertion quand un tri
    // est appliqué)
    List<? extends SortKey> sk = sorter.getSortKeys();
    sorter.setSortKeys(null);

    int rowIndex = sensors.size();
    // Ajout du capteur
    sensors.add(sensor);

    // On informe de la nouvelle ligne insérée
    fireTableRowsInserted(rowIndex, rowIndex);

    // On remet le filter/sorter
    sorter.setRowFilter(rf);
    sorter.setSortKeys(sk);
  }

  /**
   * Enlève un capteur du tableau.
   *
   * @param sensor le capteur à enlever
   */
  @SuppressWarnings("unchecked")
  public void remove(Sensor sensor) {
    TableRowSorter<? extends TableModel> sorter = (TableRowSorter<? extends TableModel>) table.getRowSorter();
    RowFilter<TableModel, Object> rf = (RowFilter<TableModel, Object>) sorter.getRowFilter();

    // On désactive le filter
    sorter.setRowFilter(null);

    // On récupère la ligne modifiée de la vue
    int viewRowIndex = table.convertRowIndexToView(sensors.indexOf(sensor));
    // Suppression du capteur
    sensors.remove(sensor);

    // Si la ligne est visible, on informe de la modification
    if (viewRowIndex != -1) fireTableRowsDeleted(viewRowIndex, viewRowIndex);

    // On remet le filter
    sorter.setRowFilter(rf);
  }

  /**
   * Met à jour un capteur du tableau.
   *
   * @param sensor le capteur à mettre à jour
   */
  @SuppressWarnings("unchecked")
  public void update(Sensor sensor) {
    TableRowSorter<? extends TableModel> sorter = (TableRowSorter<? extends TableModel>) table.getRowSorter();
    RowFilter<TableModel, Object> rf = (RowFilter<TableModel, Object>) sorter.getRowFilter();

    // On désactive temporairement le rowFilter (sinon les valeurs ne sont pas mises
    // à jour
    // quand un filtre est appliqué)
    sorter.setRowFilter(null);

    int modelRowIndex = sensors.indexOf(sensor);
    int viewRowIndex = table.convertRowIndexToView(modelRowIndex);
    // Mise à jour du capteur
    sensors.set(modelRowIndex, sensor);

    // Si la ligne est visible, on informe de la modification
    if (viewRowIndex != -1) fireTableRowsUpdated(viewRowIndex, viewRowIndex);

    // On remet le filter
    sorter.setRowFilter(rf);
  }

  /**
   * Récupère le capteur située à la ligne donnée.
   *
   * @param modelRowIndex l'indice de la ligne par rapport au modèle
   * @return le capteur
   */
  public Sensor getSensorAt(int modelRowIndex) {
    return sensors.get(modelRowIndex);
  }

}
