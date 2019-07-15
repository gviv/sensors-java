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
 * R�ceptionne les message du serveur et ex�cute l'action voulue (ajout dans la
 * base, notification du tableau temps r�el, etc.).
 */
public class DBWriter implements IWriter {

  /**
   * Le mod�le du tableau temps r�el.
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
   * @param tableModel le mod�le du tableau temps r�el
   */
  public DBWriter(ManagerContainer mc, SensorTableModel tableModel) {
    this.tableModel = tableModel;
    sm = mc.get(SensorManager.class);
    fm = mc.get(FluidManager.class);
    bm = mc.get(BuildingManager.class);
    vm = mc.get(ValueManager.class);
  }

  /**
   * Re�oit le message du serveur et effectue l'action correspondante.
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
   * G�re la connexion d'un capteur. Tous les threads ayant la m�me connexion � la
   * base, il est n�cessaire de synchroniser cette m�thode sinon un thread peut
   * ajouter un nouveau capteur (avec par ex. un nouveau b�timent) pendant qu'un
   * autre ne va pas trouver le b�timent (il est en train d'�tre ajout�) et va
   * donc consid�rer qu'il faut l'ajouter.
   *
   * @param sensorName  le nom du capteur qui vient de se connecter
   * @param description la cha�ne d�crivant le capteur
   */
  private synchronized void connect(String sensorName, String description) {
    String[] parts = description.split(":");
    String fluidType = parts[0];
    String buildingName = parts[1];
    String floor = parts[2];
    String place = parts[3];

    Sensor sensor = sm.findBy("name", sensorName);
    boolean exists = sensor != null;

    // R�cup�ration du fluide depuis la base et cr�ation si inexistant
    Fluid fluid = fm.findBy("type", fluidType);
    if (fluid == null) {
      fluid = new Fluid(Type.valueOf(fluidType));
    }

    // R�cup�ration du b�timent depuis la base et cr�ation si inexistant
    Building building = bm.findBy("name", buildingName);
    if (building == null) {
      building = new Building();
      building.setName(buildingName);
    }

    if (exists) {
      // On met � jour le capteur existant
      sensor.setFloor(Integer.valueOf(floor));
      sensor.setPlace(place);
      sensor.setFluid(fluid);
      sensor.setBuilding(building);
      sm.update(sensor);
    } else {
      // On cr�e un nouveau capteur et on l'ins�re
      sensor = new Sensor(fluid);
      sensor.setName(sensorName);
      sensor.setFloor(Integer.valueOf(floor));
      sensor.setPlace(place);
      sensor.setBuilding(building);
      sm.insert(sensor);
    }

    // On notifie le tableau temps r�el de l'ajout d'un capteur (sensor
    // n'�tant pas final, on passe par une autre variable)
    Sensor s = sensor;
    SwingUtilities.invokeLater(() -> tableModel.insert(s));
  }

  /**
   * G�re la r�ception d'une donn�e de capteur.
   *
   * @param sensorName le nom du capteur qui a �mis la donn�e
   * @param value      la valeur �mise
   */
  private void data(String sensorName, double value) {
    // R�cup�ration du capteur
    Sensor sensor = sm.findBy("name", sensorName);
    if (sensor == null) {
      System.err.println("Capteur \"" + sensorName + "\" inexistant. Donn�e ignor�e.");
      return;
    }

    // Cr�ation d'une nouvelle valeur
    Value v = new Value();
    v.setValue(value);
    v.setDateTime(new Timestamp(System.currentTimeMillis()));
    v.setSensor(sensor);
    vm.insert(v);
    sensor.setLastValue(v);

    // Mise � jour du tableau
    SwingUtilities.invokeLater(() -> tableModel.update(sensor));
  }

  /**
   * G�re la d�connexion d'un capteur.
   *
   * @param sensorName le capteur qui s'est d�connect�
   */
  private void disconnect(String sensorName) {
    // On r�cup�re le capteur
    Sensor sensor = sm.findBy("name", sensorName);
    if (sensor == null) {
      System.err.println("Capteur \"" + sensorName + "\" inexistant.");
      return;
    }

    // Suppression du capteur dans le tableau
    SwingUtilities.invokeLater(() -> tableModel.remove(sensor));
  }

}
