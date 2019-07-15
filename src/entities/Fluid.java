package entities;

import database.Entity;

/**
 * Repr�sente un fluide.
 */
public class Fluid extends Entity {

  /**
   * Stocke les types de fluides. Les types stock�s dans la base seront ces m�me
   * types sous forme de cha�ne de caract�res.
   */
  public static enum Type {
    EAU, ELECTRICITE, AIRCOMPRIME, TEMPERATURE;

    /**
     * Renvoie l'unit� associ�e au type de fluide.
     *
     * @return l'unit�
     */
    public String getUnit() {
      String unit = "";

      switch (this) {
      case EAU:
        unit = "m�";
        break;
      case ELECTRICITE:
        unit = "kWh";
        break;
      case TEMPERATURE:
        unit = "�C";
        break;
      case AIRCOMPRIME:
        unit = "m�/h";
        break;
      }

      return unit;
    }

    /**
     * Formate le type en fran�ais correct.
     *
     * @return le type format�
     */
    @Override
    public String toString() {
      String str = "";

      switch (this) {
      case EAU:
        str = "EAU";
        break;
      case ELECTRICITE:
        str = "�LECTRICIT�";
        break;
      case TEMPERATURE:
        str = "TEMP�RATURE";
        break;
      case AIRCOMPRIME:
        str = "AIR COMPRIM�";
        break;
      }

      return str;
    }
  }

  /**
   * Constructeur.
   *
   * @param type le type du fluide
   */
  public Fluid(Type type) {
    this.type = type;
  }

  /**
   * Type du fluide.
   */
  private Type type;

  /**
   * Getter type.
   *
   * @return type
   */
  public Type getType() {
    return type;
  }

  /**
   * Setter type.
   *
   * @param type le type du fluide
   */
  public void setType(Type type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type.toString();
  }

}
