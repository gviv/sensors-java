package tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import entities.Building;
import entities.Sensor;

/**
 * Renderer des cellules du JTree.
 */
public class SensorTreeCellRenderer extends DefaultTreeCellRenderer {

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
      int row, boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

    Building building = extractBuilding(value);
    Integer floor = extractFloor(value);

    if (leaf) {
      // C'est une feuille, on affiche donc le nom du capteur
      Object object = ((DefaultMutableTreeNode) value).getUserObject();
      if (object instanceof Sensor) {
        Sensor sensor = (Sensor) object;
        setText(sensor.getName());
      }
    } else if (building != null) {
      // C'est un bâtiment, on affiche son nom
      setText(building.getName());
    } else if (floor != null) {
      // C'est un étage, on l'affiche
      setText("Étage " + floor);
    }

    return this;
  }

  /**
   * Récupère le bâtiment à partir de la valeur du noeud.
   *
   * @param value la valeur du noeud
   * @return le bâtiment ou null si value n'est pas un bâtiment
   */
  private Building extractBuilding(Object value) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    if (node.getUserObject() instanceof Building) {
      return (Building) node.getUserObject();
    }

    return null;
  }

  /**
   * Récupère l'étage à partir de la valeur du noeud.
   *
   * @param value la valeur du noeud
   * @return l'étage ou null si value n'est pas un Integer
   */
  private Integer extractFloor(Object value) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    if (node.getUserObject() instanceof Integer) {
      return (Integer) node.getUserObject();
    }

    return null;
  }
}
