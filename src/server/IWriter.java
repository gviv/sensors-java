package server;

/**
 * Interface d�finissant l'op�ration appel�e lors de la r�ception d'un message
 * par le serveur.
 */
public interface IWriter {

  /**
   * Appel� lors de la r�ception d'un message.
   *
   * @param message le message re�u
   */
  public void write(String message);

}
