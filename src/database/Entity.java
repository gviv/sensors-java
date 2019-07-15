package database;

/**
 * Repr�sente une entit� (table) de la base de donn�es.
 */
public abstract class Entity {

  /**
   * ID de l'entite (vaut 0 si elle n'est pas dans la base).
   */
  private int id = 0;

  /**
   * Getter id.
   *
   * @return id
   */
  public int getId() {
    return id;
  }

  /**
   * Setter id.
   *
   * @param id l'id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * V�rifie l'�galit� sur l'ID.
   *
   * @return true si �gaux, false sinon
   */
  @Override
  public boolean equals(Object obj) {
    // On v�rifie si l'objet est de la m�me classe que this ou si
    // c'est une sous-classe ou superclasse (car un xxx et un xxxProxy
    // doivent �tre �gaux)
    if (obj != null && (obj.getClass() == getClass() || obj.getClass().getSuperclass() == getClass()
        || obj.getClass() == getClass().getSuperclass())) {
      Entity e = (Entity) obj;
      return id == e.id;
    }

    return false;
  }

  /**
   * Renvoie le hashCode calcul� � partir de l'ID.
   *
   * @return le hashCode
   */
  @Override
  public int hashCode() {
    return (getClass().hashCode() + id) * 31;
  }

}
