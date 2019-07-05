package components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.Timer;

import config.Config;

/**
 * Barre de chargement indiquant qu'une action est en cours.
 */
public class LoadingBar extends JComponent implements ActionListener {

  /**
   * Hauteur de la barre.
   */
  private static final int BAR_HEIGHT = 2;

  /**
   * Longueur initiale de la barre.
   */
  private static final int INITIAL_BAR_WIDTH = 10;

  /**
   * Durée de l'animation (en ms).
   */
  private static final int DURATION = 800;

  /**
   * Temps entre deux images (en ms).
   */
  private static final int FRAME_TIME = 16;

  /**
   * Couleurs prises successivement par la barre.
   */
  private static final Color[] COLORS = { new Color(123, 0, 232), new Color(232, 0, 0), new Color(239, 151, 0) };

  /**
   * Longueur courante de la barre.
   */
  private int curWidth = 0;

  /**
   * Position courante de la barre.
   */
  private int curPos = -INITIAL_BAR_WIDTH;

  /**
   * Timer utilisé pour l'animation.
   */
  private Timer timer;

  /**
   * Couleur courante de la barre.
   */
  private Color curColor = COLORS[0];

  /**
   * Indice de la couleur courante.
   */
  private int colorIndex = 0;

  /**
   * Indique si l'animation doit s'arrêter.
   */
  private boolean mustStop;

  /**
   * Image courante.
   */
  private int curFrame = 0;

  /**
   * Constructeur.
   */
  public LoadingBar() {
    setPreferredSize(new Dimension(0, BAR_HEIGHT));
    timer = new Timer(16, this);
  }

  /**
   * Démarre l'animation.
   */
  public void start() {
    mustStop = false;
    timer.restart();
  }

  /**
   * Ordonne l'arrêt de l'animation.
   */
  public void stop() {
    mustStop = true;
  }

  /**
   * Dessine la barre.
   *
   * @param g l'objet graphique
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Remplissage du fond
    g.setColor(Config.BACKGROUND_COLOR);
    g.fillRect(0, 0, getWidth(), getHeight());

    // Remplissage de la barre
    g.setColor(curColor);
    g.fillRect((int) curPos, 0, (int) curWidth, BAR_HEIGHT);
  }

  /**
   * Anime la barre.
   *
   * @param e l'ActionEvent
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    curFrame++;

    curPos = (int) easeInOutCubic(curFrame, -INITIAL_BAR_WIDTH, getWidth() + INITIAL_BAR_WIDTH);
    curWidth += Math.cos(curPos / (getWidth() / Math.PI)) * getWidth() * 0.03;

    if (curPos >= getWidth() - 1) {
      // La barre a atteint la fin, on la replace au début
      curWidth = INITIAL_BAR_WIDTH;
      curPos = -curWidth;
      curFrame = 0;

      // Changement de couleur
      colorIndex++;
      curColor = COLORS[colorIndex % COLORS.length];

      if (mustStop) {
        timer.stop();
      }
    }
    repaint();
  }

  /**
   * Fonction d'accélération cubique du mouvement de la barre.
   *
   * Copyright (c) 2001 Robert Penner
   * http://robertpenner.com/easing_terms_of_use.html
   *
   * @param t l'avancement courant
   * @param b la valeur de départ
   * @param c avancement total - valeur de départ
   * @return la nouvelle position
   */
  private double easeInOutCubic(double t, double b, double c) {
    if ((t /= DURATION / (2 * FRAME_TIME)) < 1) {
      return c / 2 * t * t * t + b;
    }
    return c / 2 * ((t -= 2) * t * t + 2) + b;
  }

}
