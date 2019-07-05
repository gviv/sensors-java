package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import database.Query;
import entities.Building;
import entities.Fluid;
import entities.Sensor;
import proxies.SensorProxy;

/**
 * Gère l'ajout/suppression/modification de capteurs dans la base.
 */
public class SensorManager extends Manager<Sensor> {

  /**
   * Constructeur.
   */
  public SensorManager() {
    super("sensor",
        new String[] { "name", "floor", "place", "fluid_id", "building_id", "min_threshold", "max_threshold" });
  }

  /**
   * Redéfinit la requête de base pour récupérer le fluide et le bâtiment.
   * {@inheritDoc}
   */
  @Override
  protected Query baseQuery() {
    String fTable = mc.get(FluidManager.class).getTable();
    String bTable = mc.get(BuildingManager.class).getTable();

    return super.baseQuery()
      .join(fTable, getTable() + ".fluid_id = " + fTable + ".id")
      .join(bTable, getTable() + ".building_id = " + bTable + ".id");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Sensor hydrate(ResultSet rs) {
    Sensor sensor = null;
    FluidManager fm = mc.get(FluidManager.class);
    BuildingManager bm = mc.get(BuildingManager.class);

    try {
      int id = rs.getInt(getTable() + ".id");
      String name = rs.getString(getTable() + ".name");
      int floor = rs.getInt(getTable() + ".floor");
      String place = rs.getString(getTable() + ".place");
      double minThreshold = rs.getDouble(getTable() + ".min_threshold");
      double maxThreshold = rs.getDouble(getTable() + ".max_threshold");

      // Création du fluide et bâtiment à partir des résultats de requête
      Fluid fluid = fm.hydrate(rs);
      Building building = bm.hydrate(rs);

      // On ne récupère pas les valeurs maintenant, elles le seront
      // automatiquement lors de l'appel à getValues du SensorProxy
      sensor = new SensorProxy(id, fluid);
      sensor.setName(name);
      sensor.setFloor(floor);
      sensor.setPlace(place);
      sensor.setBuilding(building);
      sensor.setMinThreshold(minThreshold);
      sensor.setMaxThreshold(maxThreshold);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return sensor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void dehydrate(PreparedStatement pst, Sensor entity) {
    ValueManager vm = mc.get(ValueManager.class);
    FluidManager fm = mc.get(FluidManager.class);
    BuildingManager bm = mc.get(BuildingManager.class);

    try {
      pst.setString(1, entity.getName());
      pst.setInt(2, entity.getFloor());
      pst.setString(3, entity.getPlace());
      Fluid fluid = entity.getFluid();
      if (fluid != null) {
        // Si pas dans la base, on l'insère sinon on le met à jour
        if (fluid.getId() == 0)
          fm.insert(fluid);
        else
          fm.update(fluid);
        pst.setInt(4, fluid.getId());
      } else {
        pst.setNull(4, Types.INTEGER);
      }

      Building building = entity.getBuilding();
      if (building != null) {
        // Si pas dans la base, on l'insère sinon on le met à jour
        if (building.getId() == 0)
          bm.insert(building);
        else
          bm.update(building);
        pst.setInt(5, building.getId());
      } else {
        pst.setNull(5, Types.INTEGER);
      }
      pst.setDouble(6, entity.getMinThreshold());
      pst.setDouble(7, entity.getMaxThreshold());

      if (entity.getValues() != null) {
        entity.getValues().forEach(value -> {
          if (value.getId() == 0) {
            // La valeur n'est pas dans la base, on l'insère
            vm.insert(value);
          }
          // Il faudrait normalement mettre à jour la valeur de la base si
          // elle a été modifiée pour garantir la cohérence entre
          // les objets Java et la base mais c'est beaucoup trop lourd de
          // mettre à jour chaque valeur à chaque fois (surtout qu'elles
          // ne changent pas).
        });
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
