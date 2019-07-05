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
 * Panneau de réglage des capteurs.
 */
public class SettingsPanel extends JPanel {

  /**
   * Panneau du capteur actuellement affiché.
   */
  private SensorPanel sensorPanel;

  /**
   * Constructeur.
   *
   * @param treeModel le modèle de l'arbre des capteurs
   */
  public SettingsPanel(TreeModel treeModel) {
    super(new BorderLayout());
    LoadingBar loadingBar = new LoadingBar();

    add(new NoSensorPanel("Aucun capteur sélectionné", null), BorderLayout.CENTER);

    // Création de l'arbre
    JTree tree = new JTree(treeModel);
    tree.setBorder(new EmptyBorder(5, 10, 5, 10));
    tree.setCellRenderer(new SensorTreeCellRenderer());

    // Création du JScrollPane contenant l'arbre
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

        // Suppression du panneau actuellement affiché
        Component existing = ((BorderLayout) getLayout()).getLayoutComponent(this, BorderLayout.CENTER);
        if (existing != null) {
          remove(existing);
        }

        // Création du panneau contenant les infos & la barre de chargement
        JPanel sensorAndLoadingBarPanel = new JPanel(new BorderLayout());
        sensorAndLoadingBarPanel.add(loadingBar, BorderLayout.NORTH);

        // Création du panneau des infos
        sensorPanel = new SensorPanel(sensor, loadingBar);
        sensorAndLoadingBarPanel.add(sensorPanel, BorderLayout.CENTER);

        add(sensorAndLoadingBarPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
      }

    });
  }

}
