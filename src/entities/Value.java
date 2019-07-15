package entities;

import java.sql.Timestamp;

import database.Entity;

/**
 * Repr�sente une valeur de capteur.
 */
public class Value extends Entity implements Comparable<Value> {

  /**
   * Valeur effective.
   */
  private double value;

  /**
   * Date/heure de la valeur.
   */
  private Timestamp dateTime;

  /**
   * Capteur associ� � cette valeur
   */
  private Sensor sensor;

  /**
   * Getter value.
   *
   * @return value
   */
  public double getValue() {
    return value;
  }

  /**
   * Setter value.
   *
   * @param value
   */
  public void setValue(double value) {
    this.value = value;
  }

  /**
   * Getter dateTime.
   *
   * @return dateTime
   */
  public Timestamp getDateTime() {
    return dateTime;
  }

  /**
   * Setter dateTime.
   *
   * @param dateTime
   */
  public void setDateTime(Timestamp dateTime) {
    this.dateTime = dateTime;
  }

  /**
   * Getter sensor.
   *
   * @return sensor
   */
  public Sensor getSensor() {
    return sensor;
  }

  /**
   * Setter sensor.
   *
   * @param sensor
   */
  public void setSensor(Sensor sensor) {
    this.sensor = sensor;
  }

  /**
   * D�termine si la valeur est hors des seuils du capteur.
   *
   * @param sensor le capteur � tester
   * @return true si hors limites, false sinon
   */
  public boolean isOutOfBounds(Sensor sensor) {
    return value < sensor.getMinThreshold() || value > sensor.getMaxThreshold();
  }

  /**
   * Compare la valeur sur l'attribut value.
   *
   * @param v la Value � comparer
   */
  @Override
  public int compareTo(Value v) {
    double comp = value - v.value;

    if (comp < 0.) return -1;
    else if (comp > 0.) return 1;
    return 0;
  }

  @Override
  public String toString() {
    return value + " (" + dateTime + ")";
  }
}
