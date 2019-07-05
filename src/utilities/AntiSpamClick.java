package utilities;

import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * Permet d'ignorer les spams de clic en annulant l'action a effectuer si un
 * clic se produit avant un certain délai (utile pour éviter d'effectuer
 * plusieurs fois la même action si l'utilisateur clique plusieurs fois).
 */
public class AntiSpamClick extends Timer {

  /**
   * Délai avant d'effectuer l'action.
   */
  private static final int DELAY = 400;

  /**
   * Constructeur.
   *
   * @param listener l'action à effectuer après le spam de clic
   */
  public AntiSpamClick(ActionListener listener) {
    super(DELAY, listener);
    setRepeats(false);
  }

  /**
   * Appeler cette méthode pour indiquer qu'un clic a eu lieu.
   */
  public void click() {
    restart();
  }

}
