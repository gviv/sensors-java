package proxies;

import java.util.List;

import container.ManagerContainer;
import entities.Fluid;
import entities.Sensor;
import entities.Value;
import managers.ValueManager;

/**
 * Représente un objet intermédiaire entre un Sensor et la base. Permet le lazy
 * loading.
 */
public class SensorProxy extends Sensor {

  /**
   * Le ValueManager
   */
  private ValueManager vm;

  /**
   * Constructeur.
   *
   * @param id    l'ID du capteur
   * @param fluid le fluide du capteur
   */
  public SensorProxy(int id, Fluid fluid) {
    super(fluid);
    setId(id);
    ManagerContainer mc = ManagerContainer.getInstance();
    vm = mc.get(ValueManager.class);
  }

  /**
   * Renvoie les valeurs du capteur et va les chercher depuis la base s'il ne les
   * a pas déjà.
   *
   * @return la liste des valeurs du capteur
   */
  @Override
  public List<Value> getValues() {
    if (super.getValues() == null) {
      setValues(vm.findAllBy("sensor_id", getId()));
    }

    return super.getValues();
  }

}
