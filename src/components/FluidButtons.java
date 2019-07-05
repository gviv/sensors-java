package components;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import config.Config;
import entities.Fluid.Type;
import laf.RoundToggleButtonUI;
import utilities.ImageLoader;

/**
 * JPanel contenant les boutons de sélection du type de fluide.
 */
public class FluidButtons extends JPanel {

  /**
   * Bouton de type EAU.
   */
  private AbstractButton btnWater;

  /**
   * Bouton de type ELECTRICITE.
   */
  private AbstractButton btnElec;

  /**
   * Bouton de type TEMPERATURE.
   */
  private AbstractButton btnTemp;

  /**
   * Bouton de type AIR.
   */
  private AbstractButton btnAir;

  /**
   * Constructeur.
   *
   * @param nbSimultMax le nombre maximal de boutons sélectionnables simultanément
   */
  public FluidButtons(int nbSimultMax) {
    ImageLoader im = ImageLoader.getInstance();

    // Création des boutons
    int height = RoundToggleButtonUI.HEIGHT - 8;
    btnWater = new JToggleButton(im.loadImageIcon("/assets/icons/" + Type.EAU.name() + ".png", -1, height));
    btnWater.setUI(new RoundToggleButtonUI());
    btnWater.setToolTipText(Type.EAU.toString());

    btnElec = new JToggleButton(im.loadImageIcon("/assets/icons/" + Type.ELECTRICITE.name() + ".png", -1, height));
    btnElec.setUI(new RoundToggleButtonUI());
    btnElec.setToolTipText(Type.ELECTRICITE.toString());

    btnTemp = new JToggleButton(im.loadImageIcon("/assets/icons/" + Type.TEMPERATURE.name() + ".png", -1, height));
    btnTemp.setUI(new RoundToggleButtonUI());
    btnTemp.setToolTipText(Type.TEMPERATURE.toString());

    btnAir = new JToggleButton(im.loadImageIcon("/assets/icons/" + Type.AIRCOMPRIME.name() + ".png", -1, height));
    btnAir.setUI(new RoundToggleButtonUI());
    btnAir.setToolTipText(Type.AIRCOMPRIME.toString());
    add(btnWater);
    add(btnElec);
    add(btnTemp);
    add(btnAir);
    setBackground(Config.BACKGROUND_COLOR);

    // On met les boutons dans un groupe
    if (nbSimultMax == 1) {
      // On utilise un simple ButtonGroup si on veut sélectionner au plus
      // un seul bouton car dans ce cas, on ne veut pas pouvoir
      // désélectionner ce bouton lors d'un clic alors qu'il est sélectionné
      // (ce comportement n'est pas implémenté dans le EnhancedButtonGroup)
      ButtonGroup bg = new ButtonGroup();
      bg.add(btnWater);
      bg.add(btnElec);
      bg.add(btnTemp);
      bg.add(btnAir);
    } else {
      EnhancedButtonGroup bg = new EnhancedButtonGroup(nbSimultMax);
      bg.add(btnWater);
      bg.add(btnElec);
      bg.add(btnTemp);
      bg.add(btnAir);
    }
  }

  /**
   * Getter btnWater.
   *
   * @return btnWater
   */
  public AbstractButton getBtnWater() {
    return btnWater;
  }

  /**
   * Getter btnElec.
   *
   * @return btnElec
   */
  public AbstractButton getBtnElec() {
    return btnElec;
  }

  /**
   * Getter btnTemp.
   *
   * @return btnTemp
   */
  public AbstractButton getBtnTemp() {
    return btnTemp;
  }

  /**
   * Getter btnAir.
   *
   * @return btnAir
   */
  public AbstractButton getBtnAir() {
    return btnAir;
  }

}
