package table;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import config.Config;
import entities.Building;
import entities.Fluid;
import entities.Fluid.Type;
import entities.Sensor;
import entities.Value;
import utilities.ImageLoader;

/**
 * Renderer du tableau temps réel.
 */
public class SensorTableRenderer extends DefaultTableCellRenderer {

  /**
   * Couleur des lignes dont la valeur est hors des seuils.
   */
  public static final Color COLOR_ROWS_OUTSIDE_RANGE = new Color(255, 40, 40);

  /**
   * Icône du fluide EAU.
   */
  private ImageIcon waterIcon;

  /**
   * Icône du fluide ELECTRICITE.
   */
  private ImageIcon elecIcon;

  /**
   * Icône du fluide TEMPERATURE.
   */
  private ImageIcon tempIcon;

  /**
   * Icône du fluide AIRCOMPRIME.
   */
  private ImageIcon airIcon;

  /**
   * Constructeur.
   */
  public SensorTableRenderer() {
    ImageLoader im = ImageLoader.getInstance();
    int height = SensorTable.ROW_HEIGHT - 5;
    waterIcon = im.loadImageIcon("/assets/icons/" + Type.EAU.name() + ".png", -1, height);
    elecIcon = im.loadImageIcon("/assets/icons/" + Type.ELECTRICITE.name() + ".png", -1, height);
    tempIcon = im.loadImageIcon("/assets/icons/" + Type.TEMPERATURE.name() + ".png", -1, height);
    airIcon = im.loadImageIcon("/assets/icons/" + Type.AIRCOMPRIME.name() + ".png", -1, height);
  }

  /**
   * Fait le rendu des cellules. {@inheritDoc}
   */
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
      int row, int column) {
    setIcon(null);
    setHorizontalAlignment(SwingConstants.LEFT);

    SensorTableModel model = (SensorTableModel) table.getModel();
    Sensor sensor = model.getSensorAt(table.convertRowIndexToModel(row));
    Value v = (Value) model.getValueAt(table.convertRowIndexToModel(row), 5);

    switch (column) {
      case 0:
        // Nom du capteur
        setText(value.toString());
        break;
      case 1:
        // Type du fluide
        Fluid fluid = (Fluid) value;
        switch (fluid.getType()) {
          case EAU:
            setIcon(waterIcon);
            break;
          case ELECTRICITE:
            setIcon(elecIcon);
            break;
          case TEMPERATURE:
            setIcon(tempIcon);
            break;
          case AIRCOMPRIME:
            setIcon(airIcon);
            break;
        }
        setText(fluid.getType().toString());
        break;
      case 2:
        // Bâtiment
        Building building = (Building) value;
        setText(building.getName());
        setHorizontalAlignment(SwingConstants.CENTER);
        break;
      case 3:
        // Étage
        setText(value.toString());
        setHorizontalAlignment(SwingConstants.CENTER);
        break;
      case 4:
        // Lieu
        setText(value.toString());
        break;
      case 5:
        // Valeur
        if (v != null) {
          String str = NumberFormat.getInstance(Locale.FRENCH).format(v.getValue()) + " ";
          str += sensor.getFluid().getType().getUnit();
          setText(str);
        } else {
          setText("N/A");
        }
        break;
    }

    // Coloration en fonction de la valeur
    if (v != null) {
      if (v.isOutOfBounds(sensor)) {
        setBackground(COLOR_ROWS_OUTSIDE_RANGE);
      } else {
        setBackground(Config.BACKGROUND_COLOR);
      }
    }

    return this;
  }

}
