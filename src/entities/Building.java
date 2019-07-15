package entities;

import database.Entity;

/**
 * Repr�sente un b�timent.
 */
public class Building extends Entity {

  /**
   * Nom du b�timent.
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
   * @param name le nom du b�timent
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

}
