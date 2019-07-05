package components;

import java.awt.Color;
import java.awt.event.ItemEvent;

import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import config.Config;
import laf.MenuButtonUI;

/**
 * Bouton utilisé pour le menu principal.
 */
public class MenuButton extends JToggleButton {

  /**
   * Constructeur.
   *
   * @param text le texte à afficher sur le bouton
   */
  public MenuButton(String text) {
    super(text);
    setUI(new MenuButtonUI());
    setUnselectedStyle();
    addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        setSelectedStyle();
      } else if (e.getStateChange() == ItemEvent.DESELECTED) {
        setUnselectedStyle();
      }
    });
  }

  /**
   * Configure le style "non sélectionné".
   */
  private void setUnselectedStyle() {
    setBorder(new EmptyBorder(0, 4, 0, 0));
    setBackground(Config.MAIN_MENU_BACKGROUND_COLOR);
    setForeground(new Color(140, 140, 140));
  }

  /**
   * Configure le style "sélectionné".
   */
  private void setSelectedStyle() {
    setBorder(new MatteBorder(0, 4, 0, 0, Config.BUTTON_BORDER_COLOR));
    setBackground(Config.SELECTED_BUTTON_BACKGROUND_COLOR);
    setForeground(new Color(115, 65, 255));
  }

}
