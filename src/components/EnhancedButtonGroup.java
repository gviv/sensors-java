package components;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;

/**
 * Permet de grouper des boutons, c'est-�-dire de g�rer le nombre maximal de
 * boutons simultan�ment s�lectionnables.
 */
public class EnhancedButtonGroup implements ItemListener {

  /**
   * Nombre de boutons actuellement s�lectionn�s.
   */
  private int nbSelected = 0;

  /**
   * Nombre maximal de boutons s�lectionnables simultan�ment.
   */
  private int nbSimultMax;

  /**
   * Dernier bouton s�lectionn�.
   */
  private AbstractButton lastSelected;

  /**
   * Constructeur.
   *
   * @param nbSimultMax le nombre maximal de boutons simultan�ment s�lectionnables
   *                    souhait�
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
   * @param button le bouton � ajouter
   */
  public void add(AbstractButton button) {
    button.addItemListener(this);
  }

  /**
   * Enl�ve un bouton du groupe.
   *
   * @param button le bouton � enlever
   */
  public void remove(AbstractButton button) {
    button.removeItemListener(this);
  }

  /**
   * Met � jour les boutons s�lectionn�s lors du changement d'�tat d'un des
   * boutons du groupe.
   *
   * @param e l'ItemEvent
   */
  @Override
  public void itemStateChanged(ItemEvent e) {
    AbstractButton button = (AbstractButton) e.getSource();

    if (e.getStateChange() == ItemEvent.SELECTED) {
      if (nbSelected == nbSimultMax) {
        // Nombre max de boutons s�lectionn�s atteint, on d�selectionne
        // le dernier s�lectionn�
        lastSelected.setSelected(false);
      }
      lastSelected = button;
      nbSelected++;
    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
      nbSelected--;
    }
  }

}
