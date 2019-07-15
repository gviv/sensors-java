package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Repr�sente une requ�te SQL (simplifi�e) d'interrogation des donn�es.
 */
public class Query {

  /**
   * Champs de la clause SELECT.
   */
  private List<String> select = new ArrayList<>();

  /**
   * Champs de la clause FROM.
   */
  private List<String> from = new ArrayList<>();

  /**
   * Champs de la clause JOIN.
   */
  private Map<String, List<String>> joins = new LinkedHashMap<>();

  /**
   * Champs de la clause WHERE.
   */
  private List<String> where = new ArrayList<>();

  /**
   * Champs de la clause ORDER BY
   */
  private Map<String, Order> orders = new LinkedHashMap<>();

  /**
   * Param�tres effectifs pass�s aux requ�tes param�tr�es.
   */
  private List<Object> params = new ArrayList<>();

  /**
   * Ordres possibles.
   */
  public static enum Order {
    ASC, DESC;
  }

  /**
   * Ajoute des champs au SELECT.
   *
   * @param fields les champs � ajouter
   * @return this
   */
  public Query select(String... fields) {
    for (String field : fields) {
      select.add(field);
    }

    return this;
  }

  /**
   * Ajoute des tables au FROM.
   *
   * @param tables les tables � ajouter
   * @return this
   */
  public Query from(String... tables) {
    for (String table : tables) {
      from.add(table);
    }

    return this;
  }

  /**
   * Ajoute une table au JOIN. La requ�te g�n�r�e sera de la forme "LEFT JOIN
   * table ON condition".
   *
   * @param table     la table � ajouter
   * @param condition la condition du join
   * @return this
   */
  public Query join(String table, String condition) {
    List<String> curConditions = joins.get(table);

    if (curConditions == null) {
      curConditions = new ArrayList<>();
      joins.put(table, curConditions);
    }
    curConditions.add(condition);

    return this;
  }

  /**
   * Ajoute des conditions au WHERE.
   *
   * @param conditions les conditions � ajouter
   * @return this
   */
  public Query where(String... conditions) {
    for (String condition : conditions) {
      where.add(condition);
    }

    return this;
  }

  /**
   * Ajoute des colonnes � l'ORDER BY.
   *
   * @param column la colonne sur laquelle ordonner
   * @param order  l'ordre voulu
   * @return this
   */
  public Query orderBy(String column, Order order) {
    orders.put(column, order);

    return this;
  }

  /**
   * Ajoute des param�tres qui seront pass�s � la requ�te param�tr�e.
   *
   * @param parameters les param�tres � ajouter
   * @return this
   */
  public Query params(Object... parameters) {
    for (Object param : parameters) {
      params.add(param);
    }

    return this;
  }

  /**
   * Injecte les param�tres effectifs dans la requ�te pr�par�e.
   *
   * @param pst la requ�te pr�par�e dont on veut injecter des param�tres
   * @return le r�sultat de l'ex�cution du PreparedStatement
   * @throws SQLException
   * @throws UnsupportedParameterException
   */
  public ResultSet execute(PreparedStatement pst) throws SQLException {
    int i = 1;

    for (Object o : params) {
      try {
        // On appelle la bonne m�thode selon le type du param�tre
        if (o instanceof Boolean) pst.setBoolean(i, (Boolean) o);
        else if (o instanceof Double) pst.setDouble(i, (Double) o);
        else if (o instanceof Float) pst.setFloat(i, (Float) o);
        else if (o instanceof Integer) pst.setInt(i, (Integer) o);
        else if (o instanceof String) pst.setString(i, (String) o);
        else if (o instanceof Timestamp) pst.setTimestamp(i, (Timestamp) o);
        else if (o instanceof Enum) pst.setString(i, ((Enum<?>) o).name());
        // Il faudrait normalement supporter les autres types mais ce
        // n'est pas utile pour ce projet donc on se contente de lancer
        // une exception
        else throw new UnsupportedParameterException();
      } catch (SQLException e) {
        e.printStackTrace();
      }

      ++i;
    }

    return pst.executeQuery();
  }

  /**
   * Transforme la requ�te en cha�ne de caract�re repr�sentant la requ�te SQL.
   *
   * @return la requ�te sous forme de cha�ne
   */
  @Override
  public String toString() {
    List<String> parts = new ArrayList<>();

    // Construction de la requ�te
    constructSelect(parts);
    constructFrom(parts);
    constructJoin(parts);
    constructWhere(parts);
    constructOrderBy(parts);

    return String.join(" ", parts);
  }

  /**
   * Construit le champ SELECT de la requ�te.
   *
   * @param parts les parties auxquelles ajouter le SELECT
   */
  private void constructSelect(List<String> parts) {
    parts.add("SELECT");
    if (select.isEmpty()) {
      parts.add("*");
    } else {
      parts.add(String.join(", ", select));
    }
  }

  /**
   * Construit le champ FROM de la requ�te.
   *
   * @param parts les parties auxquelles ajouter le FROM
   */
  private void constructFrom(List<String> parts) {
    parts.add("FROM");
    if (from.isEmpty()) {
      throw new IllegalStateException("from must be called before toString");
    }
    parts.add(String.join(", ", from));
  }

  /**
   * Construit le champ JOIN de la requ�te.
   *
   * @param parts les parties auxquelles ajouter le JOIN
   */
  private void constructJoin(List<String> parts) {
    if (!joins.isEmpty()) {
      Set<Map.Entry<String, List<String>>> entrySet = joins.entrySet();
      for (Map.Entry<String, List<String>> entry : entrySet) {
        parts.add("LEFT JOIN " + entry.getKey() + " ON " + String.join(" AND ", entry.getValue()));
      }
    }
  }

  /**
   * Construit le champ WHERE de la requ�te.
   *
   * @param parts les parties auxquelles ajouter le WHERE
   */
  private void constructWhere(List<String> parts) {
    if (!where.isEmpty()) {
      parts.add("WHERE");
      parts.add("(" + String.join(") AND (", where) + ")");
    }
  }

  /**
   * Construit le champ ORDER BY de la requ�te.
   *
   * @param parts les parties auxquelles ajouter le ORDER BY
   */
  private void constructOrderBy(List<String> parts) {
    if (!orders.isEmpty()) {
      parts.add("ORDER BY");
      StringBuilder strBuilder = new StringBuilder();
      Set<Map.Entry<String, Order>> entrySet = orders.entrySet();
      for (Iterator<Map.Entry<String, Order>> iter = entrySet.iterator(); iter.hasNext();) {
        Map.Entry<String, Order> entry = iter.next();
        strBuilder.append(entry.getKey() + " " + entry.getValue());
        if (iter.hasNext()) {
          strBuilder.append(", ");
        }
      }
      parts.add(strBuilder.toString());
    }
  }

}
