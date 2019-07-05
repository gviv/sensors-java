package laf;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicToggleButtonUI;

import config.Config;

/**
 * UI des boutons de fluide.
 */
public class RoundToggleButtonUI extends BasicToggleButtonUI {

  /**
   * Largeur.
   */
  public static final int WIDTH = 40;

  /**
   * Hauteur.
   */
  public static final int HEIGHT = 40;

  @Override
  public Dimension getPreferredSize(JComponent c) {
    return new Dimension(WIDTH, HEIGHT);
  }

  @Override
  protected void paintButtonPressed(Graphics g, AbstractButton b) {
    ButtonModel model = b.getModel();
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (model.isSelected()) {
      // On dessine le rond
      g2.setColor(Config.SELECTED_BUTTON_BACKGROUND_COLOR);
      g2.fillOval(0, 0, b.getWidth(), b.getHeight());
    }
  }

  @Override
  protected void installDefaults(AbstractButton b) {
    super.installDefaults(b);
    b.setBorderPainted(false);
    b.setBackground(Config.BACKGROUND_COLOR);
  }

}
