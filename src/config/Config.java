package config;

import java.awt.Color;
import java.awt.Font;

/**
 * Stocke les diff�rents param�tres configurables de l'application.
 */
public class Config {

  /**
   * Couleur d'arri�re-plan par d�faut
   */
  public static final Color BACKGROUND_COLOR = new Color(252, 251, 255);

  /**
   * Couleur du texte par d�faut.
   */
  public static final Color DEFAULT_TEXT_COLOR = new Color(0, 19, 48);

  /**
   * Couleur de fond du menu principal.
   */
  public static final Color MAIN_MENU_BACKGROUND_COLOR = BACKGROUND_COLOR;

  /**
   * Couleur des bordures.
   */
  public static final Color BORDER_COLOR = new Color(206, 206, 206);

  /**
   * Couleur des toggle buttons s�lectionn�s.
   */
  public static final Color SELECTED_BUTTON_BACKGROUND_COLOR = new Color(211, 211, 255);

  /**
   * Couleur de la bordure des toggle buttons.
   */
  public static final Color BUTTON_BORDER_COLOR = new Color(38, 31, 242);

  /**
   * Renvoie la fonte par d�faut avec la taille d�sir�e.
   *
   * @param pt la taille voulue
   * @return la fonte par d�faut � la taille voulue
   */
  public static Font getDefaultFont(int pt) {
    return new Font("Montserrat", Font.PLAIN, pt);
  }

}
