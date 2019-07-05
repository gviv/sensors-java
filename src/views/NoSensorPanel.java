package views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import config.Config;
import utilities.ImageLoader;

/**
 * Panneau indiquant qu'aucun capteur n'a été sélectionné.
 */
public class NoSensorPanel extends JPanel {

  /**
   * Constructeur.
   *
   * @param heading    le texte principal
   * @param subheading le texte secondaire
   */
  public NoSensorPanel(String heading, String subheading) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    ImageLoader im = ImageLoader.getInstance();

    add(Box.createVerticalGlue());
    add(Box.createVerticalGlue());
    // Image
    ImageIcon image = im.loadImageIcon("/assets/cross.png", 70, -1);
    JLabel imageLabel = new JLabel(image);
    imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    add(imageLabel);

    add(Box.createRigidArea(new Dimension(0, 10)));

    // Texte principal
    JLabel headingLabel = new JLabel(heading);
    headingLabel.setFont(Config.getDefaultFont(16));
    headingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    add(headingLabel);

    add(Box.createRigidArea(new Dimension(0, 3)));

    // Sous-texte
    JLabel subheadingLabel = new JLabel(subheading);
    subheadingLabel.setFont(Config.getDefaultFont(14));
    subheadingLabel.setForeground(new Color(120, 120, 120));
    subheadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    add(subheadingLabel);

    add(Box.createVerticalGlue());
    add(Box.createVerticalGlue());
    add(Box.createVerticalGlue());
  }

}
