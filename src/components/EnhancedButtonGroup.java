package components;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;

/**
 * Permet de grouper des boutons, c'est-à-dire de gérer le nombre maximal de
 * boutons simultanément sélectionnables.
 */
public class EnhancedButtonGroup implements ItemListener {

  /**
   * Nombre de boutons actuellement sélectionnés.
   */
  private int nbSelected = 0;

  /**
   * Nombre maximal de boutons sélectionnables simultanément.
   */
  private int nbSimultMax;

  /**
   * Dernier bouton sélectionné.
   */
  private AbstractButton lastSelected;

  /**
   * Constructeur.
   *
   * @param nbSimultMax le nombre maximal de boutons simultanément sélectionnables
   *                    souhaité
   */
  public EnhancedButtonGroup(int nbSimultMax) {
    if (nbSimultMax <= 0) {
      nbSimultMax = 1;
      System.err.println("Invalid nbSimultMax, set to 1");
    }
    this.nbSimultMax = nbSimultMax;
  }

  /**
   * Ajoute un bouton au groupe.
   *
   * @param button le bouton à ajouter
   */
  public void add(AbstractButton button) {
    button.addItemListener(this);
  }

  /**
   * Enlève un bouton du groupe.
   *
   * @param button le bouton à enlever
   */
  public void remove(AbstractButton button) {
    button.removeItemListener(this);
  }

  /**
   * Met à jour les boutons sélectionnés lors du changement d'état d'un des
   * boutons du groupe.
   *
   * @param e l'ItemEvent
   */
  @Override
  public void itemStateChanged(ItemEvent e) {
    AbstractButton button = (AbstractButton) e.getSource();

    if (e.getStateChange() == ItemEvent.SELECTED) {
      if (nbSelected == nbSimultMax) {
        // Nombre max de boutons sélectionnés atteint, on déselectionne
        // le dernier sélectionné
        lastSelected.setSelected(false);
      }
      lastSelected = button;
      nbSelected++;
    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
      nbSelected--;
    }
  }

}
