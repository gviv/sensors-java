package managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import container.ManagerContainer;
import database.Entity;
import database.MySQLConnection;
import database.Query;

/**
 * Classe abstraite d�finissant les op�rations permettant d'ajouter/supprimer ou
 * modifier des informations dans la base de donn�es.
 */
public abstract class Manager<T extends Entity> {

  /**
   * Nom de la table g�r�e.
   */
  private String table;

  /**
   * Conteneur de managers.
   */
  protected ManagerContainer mc;

  /**
   * Connexion � la base.
   */
  protected Connection conn;

  /**
   * Colonnes de la table.
   */
  String[] columns;

  /**
   * Constructeur.
   *
   * @param table   le nom de la table � g�rer
   * @param columns les colonnes de la table
   */
  public Manager(String table, String[] columns) {
    this.table = table;
    this.columns = columns;
    this.mc = ManagerContainer.getInstance();
    this.conn = MySQLConnection.getConnection();
  }

  /**
   * Getter table.
   *
   * @return table
   */
  public String getTable() {
    return table;
  }

  /**
   * Cr�e une requ�te de base r�cup�rant tous les enregistrements de la table.
   *
   * @return une requ�te r�cup�rant tous les enregistrement de la table
   */
  protected Query baseQuery() {
    return new Query().from(table);
  }

  /**
   * R�cup�re un enregistrement � partir de son ID.
   *
   * @param id l'ID de l'entit� � trouver
   * @return l'entit�e trouv�e ou null si inexistante
   */
  public T find(int id) {
    return findBy("id", id);
  }

  /**
   * R�cup�re un enregistrement � partir d'une association colonne/valeur
   *
   * @param column la colonne � prendre en compte
   * @param value  la valeur de cette colonne
   * @return l'objet correspondant � la recherche ou null si inexistant
   */
  public T findBy(String column, Object value) {
    Query query = baseQuery().where(table + "." + column + " = ?").params(value);
    return fetch(query);
  }

  /**
   * R�cup�re la liste de tous les enregistrements de la table.
   *
   * @return la liste de toutes les entit�s de la table.
   */
  public List<T> findAll() {
    return fetchAll(baseQuery());
  }

  /**
   * R�cup�re la liste des enregistrements satisfaisant un certain crit�re.
   *
   * @param column la colonne � tester
   * @param value  la valeur de cette colonne
   * @return les entit�s satisfaisant le crit�re
   */
  public List<T> findAllBy(String column, Object value) {
    Query query = baseQuery().where(table + "." + column + " = ?").params(value);
    return fetchAll(query);
  }

  /**
   * Ex�cute la requ�te et renvoie la premi�re entit� s�lectionn�e par la requ�te.
   *
   * @param query la requ�te � effectuer
   * @return l'entit�e hydrat�e
   */
  protected T fetch(Query query) {
    T hydratedEntity = null;

    try (PreparedStatement pst = conn.prepareStatement(query.toString()); ResultSet rs = query.execute(pst)) {
      if (rs.first()) {
        hydratedEntity = hydrate(rs);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return hydratedEntity;
  }

  /**
   * Ex�cute la requ�te et renvoie l'ensemble des entit�s s�lectionn�es par la
   * requ�te.
   *
   * @param query la requ�te � effectuer
   * @return la liste des entit�s r�cup�r�es par la requ�te
   */
  protected List<T> fetchAll(Query query) {
    List<T> entities = new ArrayList<>();

    try (PreparedStatement pst = conn.prepareStatement(query.toString()); ResultSet rs = query.execute(pst)) {
      while (rs.next()) {
        entities.add(hydrate(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return entities;
  }

  /**
   * Ins�re l'objet dans la base de donn�es.
   *
   * @param entity l'entit� � ins�rer
   */
  public void insert(T entity) {
    if (entity == null) return;

    String q = generateInsertQuery();

    try (PreparedStatement pst = conn.prepareStatement(q, Statement.RETURN_GENERATED_KEYS)) {
      dehydrate(pst, entity);
      pst.executeUpdate();
      try (ResultSet keys = pst.getGeneratedKeys()) {
        if (keys.first()) {
          // L'entit� a maintenant un ID : on lui donne
          entity.setId(keys.getInt(1));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Supprime l'entit� de la base de donn�es.
   *
   * @param entity l'entit� � supprimer
   */
  public void delete(T entity) {
    if (entity == null) return;

    String query = "DELETE FROM " + table + " WHERE id=" + entity.getId();

    try (Statement st = conn.createStatement()) {
      st.executeUpdate(query);
      // L'entit� n'est plus dans la base, elle n'a donc plus d'ID
      entity.setId(0);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Met � jour la base de donn�es pour correspondre � l'entit�.
   *
   * @param entity l'entit� � mettre � jour
   */
  public void update(T entity) {
    if (entity == null) return;

    String query = generateUpdateQuery(entity.getId());

    try (PreparedStatement pst = conn.prepareStatement(query)) {
      dehydrate(pst, entity);
      pst.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * G�n�re la requ�te d'insertion dans la base.
   *
   * @return la cha�ne repr�sentant la requ�te SQL pr�par�e d'insertion
   */
  private String generateInsertQuery() {
    String[] interrogationMarks = new String[columns.length];
    for (int i = 0; i < columns.length; ++i) {
      interrogationMarks[i] = "?";
    }

    return "INSERT INTO " + table + "(" + String.join(", ", columns) + ") VALUES("
        + String.join(", ", interrogationMarks) + ")";
  }

  /**
   * G�n�re la requ�te de mise � jour de la table.
   *
   * @param id l'ID de l'enregistrement � mettre � jour
   * @return la cha�ne repr�sentant la requ�te SQL pr�par�e de mise � jour
   */
  private String generateUpdateQuery(int id) {
    return "UPDATE " + table + " SET " + String.join("=?, ", columns) + "=? WHERE id=" + id;
  }

  /**
   * Cr�e une nouvelle instance de T en l'hydratant avec les valeurs du ResultSet.
   *
   * @param rs Les r�sultats de la requ�te.
   * @return Une nouvelle entit� hydrat�e.
   */
  protected abstract T hydrate(ResultSet rs);

  /**
   * Injecte les attributs de l'entit� dans le PreparedStatement (c'est
   * l'op�ration de "remplissage de la table").
   *
   * @param pst    le PreparedStatement de la requ�te
   * @param entity l'entit� � injecter
   */
  protected abstract void dehydrate(PreparedStatement pst, T entity);

}
