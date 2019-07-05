package server;

/**
 * Interface définissant l'opération appelée lors de la réception d'un message
 * par le serveur.
 */
public interface IWriter {

  /**
   * Appelé lors de la réception d'un message.
   *
   * @param message le message reçu
   */
  public void write(String message);

}
