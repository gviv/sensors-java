package laf;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicToggleButtonUI;

/**
 * UI des boutons de menu.
 */
public class MenuButtonUI extends BasicToggleButtonUI {

  /**
   * Renvoie la taille pr�f�r�e. Ne fonctionne pas si cette m�thode se trouve
   * directement dans MenuButton (d'o� l'existence de cette classe).
   */
  @Override
  public Dimension getPreferredSize(JComponent c) {
    return new Dimension(100, 50);
  }

}
