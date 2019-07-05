package container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import database.Entity;
import managers.Manager;

/**
 * Conteneur de managers.
 */
public class ManagerContainer implements IContainer {

  /**
   * Managers stockés dans le conteneur. Cette classe pouvant être appelée de
   * n'importe quel thread, on utilise une ConcurrentHashMap.
   */
  private static Map<Class<Manager<? extends Entity>>, Manager<? extends Entity>> managers = new ConcurrentHashMap<>();

  /**
   * Instance du ManagerContainer.
   */
  private static ManagerContainer instance = new ManagerContainer();

  /**
   * Constructeur privé, cette classe étant un singleton.
   */
  private ManagerContainer() {}

  /**
   * Renvoie l'instance du conteneur.
   *
   * @return l'instance du conteneur
   */
  public static ManagerContainer getInstance() {
    return instance;
  }

  /**
   * {@inheritDoc}
   */
  public <T> boolean has(Class<? extends T> c) {
    return managers.containsKey(c);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public <T> T get(Class<? extends T> c) {
    if (has(c)) {
      return (T) managers.get(c);
    }

    Manager<? extends Entity> manager = null;
    try {
      manager = (Manager<? extends Entity>) c.newInstance();
      managers.put((Class<Manager<? extends Entity>>) c, manager);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return (T) manager;
  }

}
