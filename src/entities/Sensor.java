package entities;

import java.util.List;

import database.Entity;

/**
 * Représente un capteur.
 */
public class Sensor extends Entity {

  /**
   * Nom.
   */
  private String name;

  /**
   * Étage.
   */
  private int floor;

  /**
   * Lieu.
   */
  private String place;

  /**
   * Seuil min.
   */
  private double minThreshold;

  /**
   * Seuil max.
   */
  private double maxThreshold;

  /**
   * Fluide.
   */
  private Fluid fluid;

  /**
   * Bâtiment.
   */
  private Building building;

  /**
   * Liste des valeurs.
   */
  private List<Value> values;

  /**
   * Dernière valeur ajoutée. Cet attribut ne correspond pas à la base et a pour
   * but d'être manipulé manuellement sinon il vaut null (même s'il existe une
   * dernière valeur dans la base).
   */
  private Value lastValue;

  /**
   * Constructeur.
   */
  public Sensor() {
    super();
  }

  /**
   * Constructeur initialisant les seuils par défaut à partir du fluide.
   *
   * @param fluid le fluide
   */
  public Sensor(Fluid fluid) {
    this.fluid = fluid;
    setDefaultThresholds(fluid);
  }

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
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Getter floor.
   *
   * @return floor
   */
  public int getFloor() {
    return floor;
  }

  /**
   * Setter floor.
   *
   * @param floor
   */
  public void setFloor(int floor) {
    this.floor = floor;
  }

  /**
   * Getter place.
   *
   * @return place
   */
  public String getPlace() {
    return place;
  }

  /**
   * Setter place.
   *
   * @param place
   */
  public void setPlace(String place) {
    this.place = place;
  }

  /**
   * Getter minThreshold.
   *
   * @return minThreshold
   */
  public double getMinThreshold() {
    return minThreshold;
  }

  /**
   * Setter minThreshold.
   *
   * @param minThreshold
   */
  public void setMinThreshold(double minThreshold) {
    this.minThreshold = minThreshold;
  }

  /**
   * Getter maxThreshold.
   *
   * @return maxThreshold
   */
  public double getMaxThreshold() {
    return maxThreshold;
  }

  /**
   * Setter maxThreshold.
   *
   * @param maxThreshold
   */
  public void setMaxThreshold(double maxThreshold) {
    this.maxThreshold = maxThreshold;
  }

  /**
   * Getter fluid.
   *
   * @return fluid
   */
  public Fluid getFluid() {
    return fluid;
  }

  /**
   * Setter fluid.
   *
   * @param fluid
   */
  public void setFluid(Fluid fluid) {
    this.fluid = fluid;
  }

  /**
   * Getter building.
   *
   * @return building
   */
  public Building getBuilding() {
    return building;
  }

  /**
   * Setter building.
   *
   * @param building
   */
  public void setBuilding(Building building) {
    this.building = building;
  }

  /**
   * Getter values.
   *
   * @return values
   */
  public List<Value> getValues() {
    return values;
  }

  /**
   * Setter values.
   *
   * @param values
   */
  public void setValues(List<Value> values) {
    this.values = values;
  }

  /**
   * Getter lastValue.
   *
   * @return lastValue
   */
  public Value getLastValue() {
    return lastValue;
  }

  /**
   * Setter lastValue.
   *
   * @param lastValue
   */
  public void setLastValue(Value lastValue) {
    this.lastValue = lastValue;
  }

  /**
   * Initialise les seuils par défaut à partir d'un fluide.
   *
   * @param fluid le fluide
   */
  private void setDefaultThresholds(Fluid fluid) {
    switch (fluid.getType()) {
      case EAU:
        minThreshold = 0;
        maxThreshold = 10;
        break;
      case ELECTRICITE:
        minThreshold = 10;
        maxThreshold = 500;
        break;
      case TEMPERATURE:
        minThreshold = 17;
        maxThreshold = 22;
        break;
      case AIRCOMPRIME:
        minThreshold = 0;
        maxThreshold = 5;
        break;
    }
  }

  @Override
  public String toString() {
    return name;
  }

}
