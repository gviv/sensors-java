package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import database.Query;
import entities.Sensor;
import entities.Value;
import proxies.ValueProxy;

/**
 * Gère l'ajout/suppression/modification de valeurs dans la base.
 */
public class ValueManager extends Manager<Value> {

  /**
   * Constructeur.
   */
  public ValueManager() {
    super("value", new String[] { "value", "date_time", "sensor_id" });
  }

  /**
   * Récupère la liste des valeurs d'un certain capteur comprises entre t1 et t2
   * inclus.
   *
   * @param sensorId l'ID du capteur dont on veut les valeurs
   * @param t1       la date de début
   * @param t2       la date de fin
   * @return la liste des valeurs du capteur comprises entre t1 et t2
   */
  public List<Value> findAllBySensorBetween(int sensorId, Timestamp t1, Timestamp t2) {
    Query query = baseQuery()
      .where(getTable() + ".date_time >= ?", getTable() + ".date_time <= ?").params(t1, t2)
      .where(getTable() + ".sensor_id = ?").params(sensorId);

    return fetchAll(query);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Value hydrate(ResultSet rs) {
    ValueProxy value = null;

    try {
      int id = rs.getInt(getTable() + ".id");
      double val = rs.getDouble(getTable() + ".value");
      Timestamp dateTime = rs.getTimestamp(getTable() + ".date_time");
      int sensorId = rs.getInt(getTable() + ".sensor_id");

      // Le capteur sera récupéré lors de l'appel à getSensor (on aurait
      // aussi pu faire une jointure pour l'hydrater directement)
      value = new ValueProxy(sensorId);
      value.setId(id);
      value.setValue(val);
      value.setDateTime(dateTime);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void dehydrate(PreparedStatement pst, Value entity) {
    SensorManager sm = mc.get(SensorManager.class);

    try {
      pst.setDouble(1, entity.getValue());
      pst.setTimestamp(2, entity.getDateTime());
      Sensor sensor = entity.getSensor();
      if (sensor != null) {
        if (sensor.getId() == 0) {
          // Le capteur n'est pas dans la base, on l'ajoute.
          // Peut entraîner un blocage si la valeur n'était pas
          // déjà dans la base, car le capteur va aussi ajouter cette
          // valeur et ainsi de suite. Cela n'arrive pas en pratique
          // car quand on insère une valeur, le capteur est déjà
          // dans la base.
          sm.insert(sensor);
        } else {
          sm.update(sensor);
        }
        pst.setInt(3, sensor.getId());
      } else {
        pst.setNull(3, Types.INTEGER);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
