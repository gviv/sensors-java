package database;

/**
 * Exception lev�e lors de la construction d'une Query avec des param�tres qui
 * ne sont pas pris en charge.
 */
public class UnsupportedParameterException extends RuntimeException {

  public UnsupportedParameterException() {
    super();
  }

  public UnsupportedParameterException(String message) {
    super(message);
  }

  public UnsupportedParameterException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnsupportedParameterException(Throwable cause) {
    super(cause);
  }
}
