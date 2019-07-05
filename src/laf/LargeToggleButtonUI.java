package laf;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicToggleButtonUI;

import config.Config;

/**
 * UI des toggle buttons customisés.
 */
public class LargeToggleButtonUI extends BasicToggleButtonUI {

  /**
   * Hauteur des boutons.
   */
  public static final int HEIGHT = 30;

  @Override
  public void paint(Graphics g, JComponent c) {
    AbstractButton b = (AbstractButton) c;
    if (!b.isSelected()) {
      // Couleur par défaut
      g.setColor(Config.BACKGROUND_COLOR);
      g.fillRect(0, 0, b.getWidth(), b.getHeight());
    }
    super.paint(g, c);
  }

  @Override
  protected void paintButtonPressed(Graphics g, AbstractButton b) {
    // Couleur quand bouton pressé/sélectionné
    g.setColor(Config.SELECTED_BUTTON_BACKGROUND_COLOR);
    g.fillRect(0, 0, b.getWidth(), b.getHeight());
  }

  @Override
  protected void installDefaults(AbstractButton b) {
    super.installDefaults(b);
    b.setBorder(
        new CompoundBorder(new MatteBorder(1, 0, 1, 0, Config.BUTTON_BORDER_COLOR), new EmptyBorder(5, 5, 5, 5)));
  }

  @Override
  public Dimension getPreferredSize(JComponent c) {
    return c.getPreferredSize();
  }

}
