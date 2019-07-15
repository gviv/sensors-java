package proxies;

import container.ManagerContainer;
import entities.Sensor;
import entities.Value;
import managers.SensorManager;

/**
 * Repr�sente un objet interm�diaire entre une Value et la base. Permet le lazy
 * loading.
 */
public class ValueProxy extends Value {

  /**
   * Le SensorManager.
   */
  private SensorManager sm;

  /**
   * L'ID du capteur associ�.
   */
  private int sensorId;

  /**
   * Constructeur.
   *
   * @param sensorId l'ID du capteur associ�
   */
  public ValueProxy(int sensorId) {
    ManagerContainer mc = ManagerContainer.getInstance();
    sm = mc.get(SensorManager.class);
    this.sensorId = sensorId;
  }

  /**
   * Renvoie le capteur et va le chercher dans la base s'il ne l'a pas d�j�.
   *
   * @return le capteur associ� � la valeur
   */
  @Override
  public Sensor getSensor() {
    if (super.getSensor() == null) {
      setSensor(sm.find(sensorId));
    }

    return super.getSensor();
  }

}
