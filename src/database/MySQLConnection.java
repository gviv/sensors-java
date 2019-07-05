package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Représente une connexion à une base de données MySQL.
 */
public class MySQLConnection {

  /**
   * Nom d'hôte.
   */
  private static final String HOST = "";

  /**
   * Nom de la base de données.
   */
  private static final String DB_NAME = "";

  /**
   * Nom d'utilisateur.
   */
  private static final String USERNAME = "";

  /**
   * Mot de passe.
   */
  private static final String PASSWORD = "";

  /**
   * Instance de la connexion.
   */
  private static Connection conn;

  /**
   * Renvoie l'instance de la connexion à la base.
   *
   * @return l'instance de la connexion
   */
  public static Connection getConnection() {
    if (conn == null) {
      try {
        conn = DriverManager.getConnection("jdbc:mysql://" + HOST + "/" + DB_NAME, USERNAME, PASSWORD);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return conn;
  }

}
