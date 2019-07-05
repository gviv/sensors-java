package views;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import components.LoadingBar;
import components.SectionPanel;
import config.Config;
import container.ManagerContainer;
import entities.Sensor;
import managers.SensorManager;
import utilities.AntiSpamClick;
import utilities.ImageLoader;

/**
 * Panneau contenant les informations d'un capteur.
 */
public class SensorPanel extends JPanel {

  /**
   * Taille d'un spinner.
   */
  private static final Dimension SPINNER_SIZE = new Dimension(70, 20);

  /**
   * Spinner de sélection du seuil minimal.
   */
  private JSpinner spinnerMin;

  /**
   * Spinner de sélection du seuil maximal.
   */
  private JSpinner spinnerMax;

  /**
   * Constructeur.
   *
   * @param sensor     le capteur à afficher
   * @param loadingBar la barre de chargement
   */
  public SensorPanel(Sensor sensor, LoadingBar loadingBar) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(5, 10, 5, 10));
    ImageLoader im = ImageLoader.getInstance();

    // Affichage du nom du capteur
    JLabel sensorNameLabel = new JLabel(sensor.getName());
    sensorNameLabel.setFont(Config.getDefaultFont(24));

    // Affichage du fluide
    JLabel fluidLabel = new JLabel(sensor.getFluid().getType().toString());
    fluidLabel.setFont(Config.getDefaultFont(16));
    fluidLabel.setIcon(im.loadImageIcon("/assets/icons/" + sensor.getFluid().getType().name() + ".png", -1, 20));

    // Affichage de la localisation
    JPanel locationPanel = new SectionPanel("Localisation");
    locationPanel.add(new JLabel("Bâtiment " + sensor.getBuilding().getName()));
    locationPanel.add(new JLabel("Étage " + sensor.getFloor()));
    locationPanel.add(new JLabel(sensor.getPlace()));

    // Affichage des spinners de seuil
    JPanel thresholdPanel = new SectionPanel("Seuils");
    SensorManager sm = ManagerContainer.getInstance().get(SensorManager.class);
    JPanel spinnersPanel = new JPanel();
    spinnersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    thresholdPanel.add(spinnersPanel);

    // Action à effectuer lors de la modification d'un spinner
    AntiSpamClick asc = new AntiSpamClick(e -> {
      loadingBar.start();
      sm.update(sensor);
      loadingBar.stop();
    });

    // Spinner du seuil minimal
    SpinnerNumberModel modelMin = new SpinnerNumberModel(sensor.getMinThreshold(), null, null, .1);
    spinnerMin = new JSpinner(modelMin);
    spinnerMin.setPreferredSize(SPINNER_SIZE);

    // Spinner du seuil maximal
    SpinnerNumberModel modelMax = new SpinnerNumberModel(sensor.getMaxThreshold(), null, null, .1);
    spinnerMax = new JSpinner(modelMax);
    spinnerMax.setPreferredSize(SPINNER_SIZE);

    // Configuration initiale des spinners
    setMaxSpinnerMin();
    setMinSpinnerMax();

    // Ajout des listeners
    spinnerMin.addChangeListener(e -> {
      setMinSpinnerMax();
      sensor.setMinThreshold((double) modelMin.getValue());
      asc.click();
    });
    spinnerMax.addChangeListener(e -> {
      setMaxSpinnerMin();
      sensor.setMaxThreshold((double) modelMax.getValue());
      asc.click();
    });

    // Ajout des spinners au spinnersPanel
    spinnersPanel.add(spinnerMin);
    spinnersPanel.add(new JLabel("-"));
    spinnersPanel.add(spinnerMax);
    spinnersPanel.add(new JLabel(sensor.getFluid().getType().getUnit()));

    // Création du groupLayout
    GroupLayout groupLayout = new GroupLayout(this);
    setLayout(groupLayout);
    Group horizontalGroup = groupLayout.createParallelGroup().addComponent(sensorNameLabel).addComponent(fluidLabel)
        .addComponent(locationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addComponent(thresholdPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);

    Group verticalGroup = groupLayout.createSequentialGroup().addComponent(sensorNameLabel).addComponent(fluidLabel)
        .addComponent(locationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addComponent(thresholdPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);

    groupLayout.setHorizontalGroup(horizontalGroup);
    groupLayout.setVerticalGroup(verticalGroup);
  }

  private void setMinSpinnerMax() {
    double minSpinnerMax = (double) spinnerMin.getValue();
    ((SpinnerNumberModel) spinnerMax.getModel()).setMinimum(minSpinnerMax);
  }

  private void setMaxSpinnerMin() {
    double maxSpinnerMin = (double) spinnerMax.getModel().getValue();
    ((SpinnerNumberModel) spinnerMin.getModel()).setMaximum(maxSpinnerMin);
  }

}
