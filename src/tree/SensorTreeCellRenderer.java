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
      // C'est un b�timent, on affiche son nom
      setText(building.getName());
    } else if (floor != null) {
      // C'est un �tage, on l'affiche
      setText("�tage " + floor);
    }

    return this;
  }

  /**
   * R�cup�re le b�timent � partir de la valeur du noeud.
   *
   * @param value la valeur du noeud
   * @return le b�timent ou null si value n'est pas un b�timent
   */
  private Building extractBuilding(Object value) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    if (node.getUserObject() instanceof Building) {
      return (Building) node.getUserObject();
    }

    return null;
  }

  /**
   * R�cup�re l'�tage � partir de la valeur du noeud.
   *
   * @param value la valeur du noeud
   * @return l'�tage ou null si value n'est pas un Integer
   */
  private Integer extractFloor(Object value) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    if (node.getUserObject() instanceof Integer) {
      return (Integer) node.getUserObject();
    }

    return null;
  }
}
