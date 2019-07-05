package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entities.Fluid;
import entities.Fluid.Type;

/**
 * Gère l'ajout/suppression/modification de fluides dans la base.
 */
public class FluidManager extends Manager<Fluid> {

  /**
   * Constructeur.
   */
  public FluidManager() {
    super("fluid", new String[] { "type" });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Fluid hydrate(ResultSet rs) {
    Fluid fluid = null;

    try {
      int id = rs.getInt(getTable() + ".id");
      Type type = Type.valueOf(rs.getString(getTable() + ".type"));
      fluid = new Fluid(type);
      fluid.setId(id);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return fluid;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void dehydrate(PreparedStatement pst, Fluid entity) {
    try {
      pst.setString(1, entity.getType().name());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
