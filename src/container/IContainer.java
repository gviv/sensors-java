package container;

/**
 * D�finit un conteneur g�n�rique.
 */
public interface IContainer {

  /**
   * V�rifie si le conteneur contient le T.
   *
   * @param c la classe du T
   * @return true si T est pr�sent, false sinon
   */
  public <T> boolean has(Class<? extends T> c);

  /**
   * R�cup�re un T � partir de sa classe. C'est la m�me instance qui est renvoy�e.
   * Si T n'est pas pr�sent, une nouvelle instance est cr��e.
   *
   * @param c la classe du T
   * @return le T recherch�
   */
  public <T> T get(Class<? extends T> c);

}
