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
 * Mod�le de l'arbre des capteurs.
 */
public class SensorTreeModel extends DefaultTreeModel implements TableModelListener {

  /**
   * Capteurs actuellement ajout�s.
   */
  private Set<Sensor> addedSensors = new HashSet<>();

  /**
   * B�timents actuellement ajout�s.
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
   * Construit l'arborescence du mod�le.
   */
  private void construct() {
    ManagerContainer mc = ManagerContainer.getInstance();

    // R�cup�ration de tous les b�timents
    List<Building> buildings = mc.get(BuildingManager.class).findAll();

    for (Building b : buildings) {
      // R�cup�ration de tous les capteurs du b�timent
      List<Sensor> sensors = mc.get(SensorManager.class).findAllBy("building_id", b.getId());

      // Tri des capteurs selon l'�tage
      NavigableMap<Integer, List<Sensor>> sensorsOnFloor = getSensorsByFloor(sensors);

      if (!sensorsOnFloor.isEmpty() && !addedBuildings.contains(b)) {
        // Ajout du b�timent
        addedBuildings.add(b);
        DefaultMutableTreeNode buildingNode = new DefaultMutableTreeNode(b);
        root.add(buildingNode);

        Set<Integer> floors = sensorsOnFloor.keySet();

        // Ajout des �tages du b�timent
        for (Integer floor : floors) {
          DefaultMutableTreeNode floorNode = new DefaultMutableTreeNode(floor);
          buildingNode.add(floorNode);

          // Ajout des capteurs de l'�tage
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
   * R�cup�re la liste des capteurs par �tage.
   *
   * @param sensors la liste des capteurs � trier
   * @return la map associant un �tage � la liste de ses capteurs
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
   * Reconstruit le mod�le en cas d'ajout de capteur dans la table temps r�el.
   */
  @Override
  public void tableChanged(TableModelEvent e) {
    if (e.getType() == TableModelEvent.INSERT) {
      construct();
      reload();
    }
  }

}
