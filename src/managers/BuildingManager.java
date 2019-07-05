package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entities.Building;

/**
 * Gère l'ajout/suppression/modification de bâtiments dans la base.
 */
public class BuildingManager extends Manager<Building> {

  /**
   * Constructeur.
   */
  public BuildingManager() {
    super("building", new String[] { "name" });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Building hydrate(ResultSet rs) {
    Building building = null;

    try {
      int id = rs.getInt(getTable() + ".id");
      String name = rs.getString(getTable() + ".name");
      building = new Building();
      building.setId(id);
      building.setName(name);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return building;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void dehydrate(PreparedStatement pst, Building entity) {
    try {
      pst.setString(1, entity.getName());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
