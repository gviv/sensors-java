package entities;

import database.Entity;

/**
 * Représente un fluide.
 */
public class Fluid extends Entity {

  /**
   * Stocke les types de fluides. Les types stockés dans la base seront ces même
   * types sous forme de chaîne de caractères.
   */
  public static enum Type {
    EAU, ELECTRICITE, AIRCOMPRIME, TEMPERATURE;

    /**
     * Renvoie l'unité associée au type de fluide.
     *
     * @return l'unité
     */
    public String getUnit() {
      String unit = "";

      switch (this) {
      case EAU:
        unit = "m³";
        break;
      case ELECTRICITE:
        unit = "kWh";
        break;
      case TEMPERATURE:
        unit = "°C";
        break;
      case AIRCOMPRIME:
        unit = "m³/h";
        break;
      }

      return unit;
    }

    /**
     * Formate le type en français correct.
     *
     * @return le type formaté
     */
    @Override
    public String toString() {
      String str = "";

      switch (this) {
      case EAU:
        str = "EAU";
        break;
      case ELECTRICITE:
        str = "ÉLECTRICITÉ";
        break;
      case TEMPERATURE:
        str = "TEMPÉRATURE";
        break;
      case AIRCOMPRIME:
        str = "AIR COMPRIMÉ";
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
