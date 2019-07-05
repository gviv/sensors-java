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
 * Classe abstraite définissant les opérations permettant d'ajouter/supprimer ou
 * modifier des informations dans la base de données.
 */
public abstract class Manager<T extends Entity> {

  /**
   * Nom de la table gérée.
   */
  private String table;

  /**
   * Conteneur de managers.
   */
  protected ManagerContainer mc;

  /**
   * Connexion à la base.
   */
  protected Connection conn;

  /**
   * Colonnes de la table.
   */
  String[] columns;

  /**
   * Constructeur.
   *
   * @param table   le nom de la table à gérer
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
   * Crée une requête de base récupérant tous les enregistrements de la table.
   *
   * @return une requête récupérant tous les enregistrement de la table
   */
  protected Query baseQuery() {
    return new Query().from(table);
  }

  /**
   * Récupère un enregistrement à partir de son ID.
   *
   * @param id l'ID de l'entité à trouver
   * @return l'entitée trouvée ou null si inexistante
   */
  public T find(int id) {
    return findBy("id", id);
  }

  /**
   * Récupère un enregistrement à partir d'une association colonne/valeur
   *
   * @param column la colonne à prendre en compte
   * @param value  la valeur de cette colonne
   * @return l'objet correspondant à la recherche ou null si inexistant
   */
  public T findBy(String column, Object value) {
    Query query = baseQuery().where(table + "." + column + " = ?").params(value);
    return fetch(query);
  }

  /**
   * Récupère la liste de tous les enregistrements de la table.
   *
   * @return la liste de toutes les entités de la table.
   */
  public List<T> findAll() {
    return fetchAll(baseQuery());
  }

  /**
   * Récupère la liste des enregistrements satisfaisant un certain critère.
   *
   * @param column la colonne à tester
   * @param value  la valeur de cette colonne
   * @return les entités satisfaisant le critère
   */
  public List<T> findAllBy(String column, Object value) {
    Query query = baseQuery().where(table + "." + column + " = ?").params(value);
    return fetchAll(query);
  }

  /**
   * Exécute la requête et renvoie la première entité sélectionnée par la requête.
   *
   * @param query la requête à effectuer
   * @return l'entitée hydratée
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
   * Exécute la requête et renvoie l'ensemble des entités sélectionnées par la
   * requête.
   *
   * @param query la requête à effectuer
   * @return la liste des entités récupérées par la requête
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
   * Insère l'objet dans la base de données.
   *
   * @param entity l'entité à insérer
   */
  public void insert(T entity) {
    if (entity == null) return;

    String q = generateInsertQuery();

    try (PreparedStatement pst = conn.prepareStatement(q, Statement.RETURN_GENERATED_KEYS)) {
      dehydrate(pst, entity);
      pst.executeUpdate();
      try (ResultSet keys = pst.getGeneratedKeys()) {
        if (keys.first()) {
          // L'entité a maintenant un ID : on lui donne
          entity.setId(keys.getInt(1));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Supprime l'entité de la base de données.
   *
   * @param entity l'entité à supprimer
   */
  public void delete(T entity) {
    if (entity == null) return;

    String query = "DELETE FROM " + table + " WHERE id=" + entity.getId();

    try (Statement st = conn.createStatement()) {
      st.executeUpdate(query);
      // L'entité n'est plus dans la base, elle n'a donc plus d'ID
      entity.setId(0);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Met à jour la base de données pour correspondre à l'entité.
   *
   * @param entity l'entité à mettre à jour
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
   * Génère la requête d'insertion dans la base.
   *
   * @return la chaîne représentant la requête SQL préparée d'insertion
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
   * Génère la requête de mise à jour de la table.
   *
   * @param id l'ID de l'enregistrement à mettre à jour
   * @return la chaîne représentant la requête SQL préparée de mise à jour
   */
  private String generateUpdateQuery(int id) {
    return "UPDATE " + table + " SET " + String.join("=?, ", columns) + "=? WHERE id=" + id;
  }

  /**
   * Crée une nouvelle instance de T en l'hydratant avec les valeurs du ResultSet.
   *
   * @param rs Les résultats de la requête.
   * @return Une nouvelle entité hydratée.
   */
  protected abstract T hydrate(ResultSet rs);

  /**
   * Injecte les attributs de l'entité dans le PreparedStatement (c'est
   * l'opération de "remplissage de la table").
   *
   * @param pst    le PreparedStatement de la requête
   * @param entity l'entité à injecter
   */
  protected abstract void dehydrate(PreparedStatement pst, T entity);

}
