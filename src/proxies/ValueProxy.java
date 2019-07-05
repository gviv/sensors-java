package proxies;

import container.ManagerContainer;
import entities.Sensor;
import entities.Value;
import managers.SensorManager;

/**
 * Représente un objet intermédiaire entre une Value et la base. Permet le lazy
 * loading.
 */
public class ValueProxy extends Value {

  /**
   * Le SensorManager.
   */
  private SensorManager sm;

  /**
   * L'ID du capteur associé.
   */
  private int sensorId;

  /**
   * Constructeur.
   *
   * @param sensorId l'ID du capteur associé
   */
  public ValueProxy(int sensorId) {
    ManagerContainer mc = ManagerContainer.getInstance();
    sm = mc.get(SensorManager.class);
    this.sensorId = sensorId;
  }

  /**
   * Renvoie le capteur et va le chercher dans la base s'il ne l'a pas déjà.
   *
   * @return le capteur associé à la valeur
   */
  @Override
  public Sensor getSensor() {
    if (super.getSensor() == null) {
      setSensor(sm.find(sensorId));
    }

    return super.getSensor();
  }

}
