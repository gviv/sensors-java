package tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import container.ManagerContainer;
import entities.Building;
import entities.Sensor;
import managers.BuildingManager;
import managers.SensorManager;

/**
 * Modèle de l'arbre des capteurs.
 */
public class SensorTreeModel extends DefaultTreeModel implements TableModelListener {

  /**
   * Capteurs actuellement ajoutés.
   */
  private Set<Sensor> addedSensors = new HashSet<>();

  /**
   * Bâtiments actuellement ajoutés.
   */
  private Set<Building> addedBuildings = new HashSet<>();

  /**
   * Noeud racine.
   */
  private DefaultMutableTreeNode root;

  /**
   * Constructeur.
   */
  public SensorTreeModel() {
    super(new DefaultMutableTreeNode("Capteurs"));
    root = (DefaultMutableTreeNode) getRoot();
    construct();
  }

  /**
   * Construit l'arborescence du modèle.
   */
  private void construct() {
    ManagerContainer mc = ManagerContainer.getInstance();

    // Récupération de tous les bâtiments
    List<Building> buildings = mc.get(BuildingManager.class).findAll();

    for (Building b : buildings) {
      // Récupération de tous les capteurs du bâtiment
      List<Sensor> sensors = mc.get(SensorManager.class).findAllBy("building_id", b.getId());

      // Tri des capteurs selon l'étage
      NavigableMap<Integer, List<Sensor>> sensorsOnFloor = getSensorsByFloor(sensors);

      if (!sensorsOnFloor.isEmpty() && !addedBuildings.contains(b)) {
        // Ajout du bâtiment
        addedBuildings.add(b);
        DefaultMutableTreeNode buildingNode = new DefaultMutableTreeNode(b);
        root.add(buildingNode);

        Set<Integer> floors = sensorsOnFloor.keySet();

        // Ajout des étages du bâtiment
        for (Integer floor : floors) {
          DefaultMutableTreeNode floorNode = new DefaultMutableTreeNode(floor);
          buildingNode.add(floorNode);

          // Ajout des capteurs de l'étage
          for (Sensor s : sensorsOnFloor.get(floor)) {
            if (!addedSensors.contains(s)) {
              DefaultMutableTreeNode nodeSensor = new DefaultMutableTreeNode(s);
              floorNode.add(nodeSensor);
              addedSensors.add(s);
            }
          }
        }
      }
    }
  }

  /**
   * Récupère la liste des capteurs par étage.
   *
   * @param sensors la liste des capteurs à trier
   * @return la map associant un étage à la liste de ses capteurs
   */
  private NavigableMap<Integer, List<Sensor>> getSensorsByFloor(List<Sensor> sensors) {
    NavigableMap<Integer, List<Sensor>> floorToSensorsList = new TreeMap<>();

    for (Sensor s : sensors) {
      Integer floor = s.getFloor();
      List<Sensor> sensorsOnFloor = floorToSensorsList.get(floor);

      if (sensorsOnFloor == null) {
        sensorsOnFloor = new ArrayList<>();
        sensorsOnFloor.add(s);
        floorToSensorsList.put(floor, sensorsOnFloor);
      } else {
        sensorsOnFloor.add(s);
      }
    }

    return floorToSensorsList;
  }

  /**
   * Reconstruit le modèle en cas d'ajout de capteur dans la table temps réel.
   */
  @Override
  public void tableChanged(TableModelEvent e) {
    if (e.getType() == TableModelEvent.INSERT) {
      construct();
      reload();
    }
  }

}
