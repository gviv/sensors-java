package server;

import java.sql.Timestamp;

import javax.swing.SwingUtilities;

import container.ManagerContainer;
import entities.Building;
import entities.Fluid;
import entities.Fluid.Type;
import entities.Sensor;
import entities.Value;
import managers.BuildingManager;
import managers.FluidManager;
import managers.SensorManager;
import managers.ValueManager;
import table.SensorTableModel;

/**
 * Réceptionne les message du serveur et exécute l'action voulue (ajout dans la
 * base, notification du tableau temps réel, etc.).
 */
public class DBWriter implements IWriter {

  /**
   * Le modèle du tableau temps réel.
   */
  private SensorTableModel tableModel;

  /**
   * Le SensorManager.
   */
  private SensorManager sm;

  /**
   * Le FluidManager.
   */
  private FluidManager fm;

  /**
   * Le BuildingManager.
   */
  private BuildingManager bm;

  /**
   * Le ValueManager.
   */
  private ValueManager vm;

  /**
   * Constructeur.
   *
   * @param mc         le conteneur de managers
   * @param tableModel le modèle du tableau temps réel
   */
  public DBWriter(ManagerContainer mc, SensorTableModel tableModel) {
    this.tableModel = tableModel;
    sm = mc.get(SensorManager.class);
    fm = mc.get(FluidManager.class);
    bm = mc.get(BuildingManager.class);
    vm = mc.get(ValueManager.class);
  }

  /**
   * Reçoit le message du serveur et effectue l'action correspondante.
   *
   * @param message le message du serveur
   */
  public void write(String message) {
    String[] parts = message.split(" ");

    switch (parts[0].toLowerCase()) {
    case "connexion":
      connect(parts[1], parts[2]);
      break;
    case "donnee":
      data(parts[1], Double.valueOf(parts[2]));
      break;
    case "deconnexion":
      if (parts.length >= 2) {
        disconnect(parts[1]);
      }
      break;
    }
  }

  /**
   * Gère la connexion d'un capteur. Tous les threads ayant la même connexion à la
   * base, il est nécessaire de synchroniser cette méthode sinon un thread peut
   * ajouter un nouveau capteur (avec par ex. un nouveau bâtiment) pendant qu'un
   * autre ne va pas trouver le bâtiment (il est en train d'être ajouté) et va
   * donc considérer qu'il faut l'ajouter.
   *
   * @param sensorName  le nom du capteur qui vient de se connecter
   * @param description la chaîne décrivant le capteur
   */
  private synchronized void connect(String sensorName, String description) {
    String[] parts = description.split(":");
    String fluidType = parts[0];
    String buildingName = parts[1];
    String floor = parts[2];
    String place = parts[3];

    Sensor sensor = sm.findBy("name", sensorName);
    boolean exists = sensor != null;

    // Récupération du fluide depuis la base et création si inexistant
    Fluid fluid = fm.findBy("type", fluidType);
    if (fluid == null) {
      fluid = new Fluid(Type.valueOf(fluidType));
    }

    // Récupération du bâtiment depuis la base et création si inexistant
    Building building = bm.findBy("name", buildingName);
    if (building == null) {
      building = new Building();
      building.setName(buildingName);
    }

    if (exists) {
      // On met à jour le capteur existant
      sensor.setFloor(Integer.valueOf(floor));
      sensor.setPlace(place);
      sensor.setFluid(fluid);
      sensor.setBuilding(building);
      sm.update(sensor);
    } else {
      // On crée un nouveau capteur et on l'insère
      sensor = new Sensor(fluid);
      sensor.setName(sensorName);
      sensor.setFloor(Integer.valueOf(floor));
      sensor.setPlace(place);
      sensor.setBuilding(building);
      sm.insert(sensor);
    }

    // On notifie le tableau temps réel de l'ajout d'un capteur (sensor
    // n'étant pas final, on passe par une autre variable)
    Sensor s = sensor;
    SwingUtilities.invokeLater(() -> tableModel.insert(s));
  }

  /**
   * Gère la réception d'une donnée de capteur.
   *
   * @param sensorName le nom du capteur qui a émis la donnée
   * @param value      la valeur émise
   */
  private void data(String sensorName, double value) {
    // Récupération du capteur
    Sensor sensor = sm.findBy("name", sensorName);
    if (sensor == null) {
      System.err.println("Capteur \"" + sensorName + "\" inexistant. Donnée ignorée.");
      return;
    }

    // Création d'une nouvelle valeur
    Value v = new Value();
    v.setValue(value);
    v.setDateTime(new Timestamp(System.currentTimeMillis()));
    v.setSensor(sensor);
    vm.insert(v);
    sensor.setLastValue(v);

    // Mise à jour du tableau
    SwingUtilities.invokeLater(() -> tableModel.update(sensor));
  }

  /**
   * Gère la déconnexion d'un capteur.
   *
   * @param sensorName le capteur qui s'est déconnecté
   */
  private void disconnect(String sensorName) {
    // On récupère le capteur
    Sensor sensor = sm.findBy("name", sensorName);
    if (sensor == null) {
      System.err.println("Capteur \"" + sensorName + "\" inexistant.");
      return;
    }

    // Suppression du capteur dans le tableau
    SwingUtilities.invokeLater(() -> tableModel.remove(sensor));
  }

}
