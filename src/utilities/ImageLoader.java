package utilities;

import java.awt.Image;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * Classe utilitaire pour générer des ImageIcon à partir de chemins vers une
 * image.
 */
public class ImageLoader {

  /**
   * Instance de l'ImageLoader.
   */
  private static ImageLoader instance = new ImageLoader();

  /**
   * Stocke les images déjà chargées.
   */
  private Map<URL, ImageIcon> urlToImageIcon = new HashMap<>();

  /**
   * Constructeur privé.
   */
  private ImageLoader() {}

  /**
   * Génère une ImageIcon à partir d'un chemin vers une image.
   *
   * @param path le chemin de l'image à charger
   * @return l'icône générée
   * @throws NoSuchFileException si image non trouvée
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
   * Génère une ImageIcon de la taille voulue à partir d'un chemin vers une image.
   * Si la hauteur/largeur vaut -1, la taille sera calculée de façon à conserver
   * les proportions de l'image en utilisant respectivement la largeur et la
   * hauteur.
   *
   * @param path   le chemin vers l'image
   * @param width  la largeur voulue
   * @param height la hauteur voulue
   * @return l'icône générée
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
   * Transforme la chaîne représentant le chemin en une URL.
   *
   * @param path le chemin dont on veut l'URL
   * @return l'URL correspondant au chemin
   * @throws NoSuchFileException si le chemin ne correspond à rien
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
