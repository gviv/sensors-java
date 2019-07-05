package components;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import config.Config;

/**
 * Panneau contenant un titre et des éléments à afficher.
 */
public class SectionPanel extends JPanel {

  /**
   * Panneau contenant les éléments.
   */
  private JPanel contentPanel;

  /**
   * Constructeur.
   *
   * @param title le titre de la section
   */
  public SectionPanel(String title) {
    super(new BorderLayout());
    setAlignmentX(Component.LEFT_ALIGNMENT);
    setBorder(new EmptyBorder(15, 0, 0, 0));

    // Affichage du titre
    JLabel titleLabel = new JLabel(title.toUpperCase());
    titleLabel.setFont(Config.getDefaultFont(10));
    titleLabel.setBorder(new MatteBorder(0, 0, 1, 0, Config.BORDER_COLOR));
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(titleLabel, BorderLayout.NORTH);

    // Création du panneau de contenu
    contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(contentPanel, BorderLayout.CENTER);

  }

  /**
   * Ajoute un composant au panneau de contenu.
   */
  @Override
  public Component add(Component comp) {
    return contentPanel.add(comp);
  }

}
