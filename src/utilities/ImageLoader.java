package utilities;

import java.awt.Image;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * Classe utilitaire pour g�n�rer des ImageIcon � partir de chemins vers une
 * image.
 */
public class ImageLoader {

  /**
   * Instance de l'ImageLoader.
   */
  private static ImageLoader instance = new ImageLoader();

  /**
   * Stocke les images d�j� charg�es.
   */
  private Map<URL, ImageIcon> urlToImageIcon = new HashMap<>();

  /**
   * Constructeur priv�.
   */
  private ImageLoader() {}

  /**
   * G�n�re une ImageIcon � partir d'un chemin vers une image.
   *
   * @param path le chemin de l'image � charger
   * @return l'ic�ne g�n�r�e
   * @throws NoSuchFileException si image non trouv�e
   */
  public ImageIcon loadImageIcon(String path) throws NoSuchFileException {
    URL imgURL = getUrl(path);

    if (urlToImageIcon.containsKey(imgURL)) {
      return urlToImageIcon.get(imgURL);
    } else {
      ImageIcon imageIcon = new ImageIcon(imgURL);
      urlToImageIcon.put(imgURL, imageIcon);
      return imageIcon;
    }
  }

  /**
   * G�n�re une ImageIcon de la taille voulue � partir d'un chemin vers une image.
   * Si la hauteur/largeur vaut -1, la taille sera calcul�e de fa�on � conserver
   * les proportions de l'image en utilisant respectivement la largeur et la
   * hauteur.
   *
   * @param path   le chemin vers l'image
   * @param width  la largeur voulue
   * @param height la hauteur voulue
   * @return l'ic�ne g�n�r�e
   */
  public ImageIcon loadImageIcon(String path, int width, int height) {
    try {
      return new ImageIcon(loadImageIcon(path).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    } catch (NoSuchFileException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Transforme la cha�ne repr�sentant le chemin en une URL.
   *
   * @param path le chemin dont on veut l'URL
   * @return l'URL correspondant au chemin
   * @throws NoSuchFileException si le chemin ne correspond � rien
   */
  private URL getUrl(String path) throws NoSuchFileException {
    URL imgURL = getClass().getResource(path);
    if (imgURL != null) {
      return imgURL;
    }
    throw new NoSuchFileException(path);
  }

  /**
   * @return l'instance de l'ImageLoader
   */
  public static ImageLoader getInstance() {
    return instance;
  }

}
