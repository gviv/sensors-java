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
 * Mod�le du tableau temps r�el.
 */
public class SensorTableModel extends AbstractTableModel {

  /**
   * Colonnes du tableau.
   */
  private final String[] columns = { "Nom", "Type", "B�timent", "�tage", "Lieu", "Valeur" };

  /**
   * Liste des capteurs actuellements stock�s.
   */
  private List<Sensor> sensors = new ArrayList<>();

  /**
   * La table associ�e au mod�le (on en a besoin pour convertir les indices de la
   * vue au mod�le)
   */
  private JTable table;

  /**
   * Constructeur.
   *
   * @param table la table associ�e
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
   * R�cup�re le nom d'une colonne.
   *
   * @param column l'indice de la colonne
   * @return le nom de la colonne
   */
  @Override
  public String getColumnName(int column) {
    return columns[column];
  }

  /**
   * Renvoie la classe associ�e � une colonne.
   *
   * @param columnIndex l'indice de la colonne
   * @return la classe associ�e � la colonne
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
   * R�cup�re la valeur d'une ligne/colonne. La colonne des valeurs peut contenir
   * des valeurs nulles (quand on n'a pas encore re�u de donn�es).
   *
   * @param modelRowIndex l'indice de la ligne par rapport au mod�le
   * @param columnIndex   l'indice de la colonne
   * @return la valeur situ�e aux indices donn�s
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
   * Ins�re un capteur dans le tableau.
   *
   * @param sensor le capteur � ins�rer
   */
  @SuppressWarnings("unchecked")
  public void insert(Sensor sensor) {
    TableRowSorter<? extends TableModel> sorter = (TableRowSorter<? extends TableModel>) table.getRowSorter();
    RowFilter<TableModel, Object> rf = (RowFilter<TableModel, Object>) sorter.getRowFilter();

    // On d�sactive les filtres le temps de l'insertion
    sorter.setRowFilter(null);
    // On d�sactive le sorter le temps de l'insertion (convertRowIndexToView
    // n'a pas l'air de fonctionner dans le cas de l'insertion quand un tri
    // est appliqu�)
    List<? extends SortKey> sk = sorter.getSortKeys();
    sorter.setSortKeys(null);

    int rowIndex = sensors.size();
    // Ajout du capteur
    sensors.add(sensor);

    // On informe de la nouvelle ligne ins�r�e
    fireTableRowsInserted(rowIndex, rowIndex);

    // On remet le filter/sorter
    sorter.setRowFilter(rf);
    sorter.setSortKeys(sk);
  }

  /**
   * Enl�ve un capteur du tableau.
   *
   * @param sensor le capteur � enlever
   */
  @SuppressWarnings("unchecked")
  public void remove(Sensor sensor) {
    TableRowSorter<? extends TableModel> sorter = (TableRowSorter<? extends TableModel>) table.getRowSorter();
    RowFilter<TableModel, Object> rf = (RowFilter<TableModel, Object>) sorter.getRowFilter();

    // On d�sactive le filter
    sorter.setRowFilter(null);

    // On r�cup�re la ligne modifi�e de la vue
    int viewRowIndex = table.convertRowIndexToView(sensors.indexOf(sensor));
    // Suppression du capteur
    sensors.remove(sensor);

    // Si la ligne est visible, on informe de la modification
    if (viewRowIndex != -1) fireTableRowsDeleted(viewRowIndex, viewRowIndex);

    // On remet le filter
    sorter.setRowFilter(rf);
  }

  /**
   * Met � jour un capteur du tableau.
   *
   * @param sensor le capteur � mettre � jour
   */
  @SuppressWarnings("unchecked")
  public void update(Sensor sensor) {
    TableRowSorter<? extends TableModel> sorter = (TableRowSorter<? extends TableModel>) table.getRowSorter();
    RowFilter<TableModel, Object> rf = (RowFilter<TableModel, Object>) sorter.getRowFilter();

    // On d�sactive temporairement le rowFilter (sinon les valeurs ne sont pas mises
    // � jour
    // quand un filtre est appliqu�)
    sorter.setRowFilter(null);

    int modelRowIndex = sensors.indexOf(sensor);
    int viewRowIndex = table.convertRowIndexToView(modelRowIndex);
    // Mise � jour du capteur
    sensors.set(modelRowIndex, sensor);

    // Si la ligne est visible, on informe de la modification
    if (viewRowIndex != -1) fireTableRowsUpdated(viewRowIndex, viewRowIndex);

    // On remet le filter
    sorter.setRowFilter(rf);
  }

  /**
   * R�cup�re le capteur situ�e � la ligne donn�e.
   *
   * @param modelRowIndex l'indice de la ligne par rapport au mod�le
   * @return le capteur
   */
  public Sensor getSensorAt(int modelRowIndex) {
    return sensors.get(modelRowIndex);
  }

}
