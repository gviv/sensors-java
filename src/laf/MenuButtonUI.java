package laf;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicToggleButtonUI;

/**
 * UI des boutons de menu.
 */
public class MenuButtonUI extends BasicToggleButtonUI {

  /**
   * Renvoie la taille préférée. Ne fonctionne pas si cette méthode se trouve
   * directement dans MenuButton (d'où l'existence de cette classe).
   */
  @Override
  public Dimension getPreferredSize(JComponent c) {
    return new Dimension(100, 50);
  }

}
