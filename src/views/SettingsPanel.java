package views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import components.LoadingBar;
import config.Config;
import entities.Sensor;
import tree.SensorTreeCellRenderer;

/**
 * Panneau de r�glage des capteurs.
 */
public class SettingsPanel extends JPanel {

  /**
   * Panneau du capteur actuellement affich�.
   */
  private SensorPanel sensorPanel;

  /**
   * Constructeur.
   *
   * @param treeModel le mod�le de l'arbre des capteurs
   */
  public SettingsPanel(TreeModel treeModel) {
    super(new BorderLayout());
    LoadingBar loadingBar = new LoadingBar();

    add(new NoSensorPanel("Aucun capteur s�lectionn�", null), BorderLayout.CENTER);

    // Cr�ation de l'arbre
    JTree tree = new JTree(treeModel);
    tree.setBorder(new EmptyBorder(5, 10, 5, 10));
    tree.setCellRenderer(new SensorTreeCellRenderer());

    // Cr�ation du JScrollPane contenant l'arbre
    JScrollPane sc = new JScrollPane(tree);
    sc.setBorder(new MatteBorder(0, 0, 0, 2, Config.BORDER_COLOR));
    sc.setPreferredSize(new Dimension(180, 0));
    add(sc, BorderLayout.WEST);

    // Listener de l'arbre
    tree.addTreeSelectionListener(e -> {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

      if (node == null) return;
      Object object = node.getUserObject();

      if (node.isLeaf() && object instanceof Sensor) {
        Sensor sensor = (Sensor) object;

        // Suppression du panneau actuellement affich�
        Component existing = ((BorderLayout) getLayout()).getLayoutComponent(this, BorderLayout.CENTER);
        if (existing != null) {
          remove(existing);
        }

        // Cr�ation du panneau contenant les infos & la barre de chargement
        JPanel sensorAndLoadingBarPanel = new JPanel(new BorderLayout());
        sensorAndLoadingBarPanel.add(loadingBar, BorderLayout.NORTH);

        // Cr�ation du panneau des infos
        sensorPanel = new SensorPanel(sensor, loadingBar);
        sensorAndLoadingBarPanel.add(sensorPanel, BorderLayout.CENTER);

        add(sensorAndLoadingBarPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
      }

    });
  }

}
