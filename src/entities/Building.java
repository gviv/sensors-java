package entities;

import database.Entity;

/**
 * Représente un bâtiment.
 */
public class Building extends Entity {

  /**
   * Nom du bâtiment.
   */
  private String name;

  /**
   * Getter name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Setter name.
   *
   * @param name le nom du bâtiment
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

}
