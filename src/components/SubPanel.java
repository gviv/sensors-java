package components;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import config.Config;

/**
 * Panneau capable de s'indenter en fonction de la profondeur désirée.
 */
public class SubPanel extends JPanel {

  /**
   * Constructeur.
   *
   * @param title le titre du panneau
   * @param depth la profondeur souhaitée
   */
  public SubPanel(String title, int depth) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(2, 10 * depth, 0, 0));
    JLabel label = new JLabel(title);
    label.setFont(Config.getDefaultFont(15 - depth * 2));
    add(label);
  }

}
