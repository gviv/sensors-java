package utilities;

import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * Permet d'ignorer les spams de clic en annulant l'action a effectuer si un
 * clic se produit avant un certain d�lai (utile pour �viter d'effectuer
 * plusieurs fois la m�me action si l'utilisateur clique plusieurs fois).
 */
public class AntiSpamClick extends Timer {

  /**
   * D�lai avant d'effectuer l'action.
   */
  private static final int DELAY = 400;

  /**
   * Constructeur.
   *
   * @param listener l'action � effectuer apr�s le spam de clic
   */
  public AntiSpamClick(ActionListener listener) {
    super(DELAY, listener);
    setRepeats(false);
  }

  /**
   * Appeler cette m�thode pour indiquer qu'un clic a eu lieu.
   */
  public void click() {
    restart();
  }

}
