package container;

/**
 * Définit un conteneur générique.
 */
public interface IContainer {

  /**
   * Vérifie si le conteneur contient le T.
   *
   * @param c la classe du T
   * @return true si T est présent, false sinon
   */
  public <T> boolean has(Class<? extends T> c);

  /**
   * Récupère un T à partir de sa classe. C'est la même instance qui est renvoyée.
   * Si T n'est pas présent, une nouvelle instance est créée.
   *
   * @param c la classe du T
   * @return le T recherché
   */
  public <T> T get(Class<? extends T> c);

}
