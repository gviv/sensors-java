package views;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import components.EnhancedButtonGroup;
import components.FluidButtons;
import components.LoadingBar;
import components.SubPanel;
import config.Config;
import container.ManagerContainer;
import entities.Building;
import entities.Fluid;
import entities.Sensor;
import entities.Value;
import laf.LargeToggleButtonUI;
import managers.ValueManager;
import tree.SensorTreeModel;
import utilities.AntiSpamClick;

/**
 * Panneau de visualisation des données des capteurs a posteriori.
 */
public class HistoryPanel extends JPanel {

  /**
   * Chaîne représentant le panneau contenant le graphique.
   */
  private static final String CHART_PANEL = "chart_panel";

  /**
   * Chaîne représentant le panneau indiquant l'absence de sélection de capteur.
   */
  private static final String NO_CHART_PANEL = "no_chart_panel";

  /**
   * Données actuellement affichées dans le graphique.
   */
  private TimeSeriesCollection dataset = new TimeSeriesCollection();

  /**
   * Associe un bouton à un capteur.
   */
  private Map<AbstractButton, Sensor> btnToSensor = new HashMap<>();

  /**
   * Associe un capteur à une TimeSeries.
   */
  private Map<Sensor, TimeSeries> sensorToTimeSeries = new HashMap<>();

  /**
   * Spinner de sélection de la date de début.
   */
  private JSpinner spinnerStart;

  /**
   * Spinner de sélection de la date de fin.
   */
  private JSpinner spinnerEnd;

  /**
   * JPanel contenant le CHART_PANEL et le NO_CHART_PANEL.
   */
  private JPanel cards;

  /**
   * Graphique actuellement affiché.
   */
  private JFreeChart chart;

  /**
   * Modèle de l'arbre contenant les capteurs.
   */
  private SensorTreeModel treeModel;

  /**
   * Barre de chargement.
   */
  private LoadingBar loadingBar;

  /**
   * Nombre de capteurs dont on veut les valeurs. Permet de ne pas réinitialiser
   * la barre de chargement en cas de récupération de plusieurs capteurs
   * simultanément (par ex. lors de l'update du graphique).
   */
  private int nbToLoad;

  /**
   * Constructeur.
   *
   * @param treeModel le modèle de l'arbre des capteurs
   */
  public HistoryPanel(SensorTreeModel treeModel) {
    super(new BorderLayout());
    this.treeModel = treeModel;

    // Panneau de gauche
    JPanel leftPanel = new JPanel(new BorderLayout());
    add(leftPanel, BorderLayout.WEST);
    leftPanel.setBorder(new MatteBorder(0, 0, 0, 2, Config.BORDER_COLOR));

    // Liste des capteurs du panneau de gauche
    SensorList sl = new SensorList();
    leftPanel.add(sl, BorderLayout.CENTER);

    // Ajout des boutons de fluide
    FluidButtons buttons = new FluidButtons(1);
    leftPanel.add(buttons, BorderLayout.NORTH);
    // Rafraîchissement du panneau des capteurs lors du clic sur un fluide
    buttons.getBtnWater().addActionListener(e -> sl.refresh(Fluid.Type.EAU));
    buttons.getBtnElec().addActionListener(e -> sl.refresh(Fluid.Type.ELECTRICITE));
    buttons.getBtnTemp().addActionListener(e -> sl.refresh(Fluid.Type.TEMPERATURE));
    buttons.getBtnAir().addActionListener(e -> sl.refresh(Fluid.Type.AIRCOMPRIME));

    createDateSpinners();

    // Panneau contenant les spinners de date et le JLabel
    JPanel datePanel = new JPanel();
    datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
    datePanel.add(new JLabel("Période"));

    // Paneau contenant les spinners
    JPanel spinnersPanel = new JPanel();
    spinnersPanel.add(spinnerStart);
    spinnersPanel.add(new JLabel(" - "));
    spinnersPanel.add(spinnerEnd);
    spinnersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    datePanel.add(spinnersPanel);

    // On met le panneau des dates dans un autre pour qu'il soit centré
    JPanel outerDatePanel = new JPanel();
    outerDatePanel.add(datePanel);
    outerDatePanel.setBorder(new MatteBorder(1, 0, 0, 0, Config.BORDER_COLOR));

    // Création du graphique
    chart = ChartFactory.createTimeSeriesChart(null, "Temps", "", dataset);
    chart.setBackgroundPaint(Config.BACKGROUND_COLOR);
    chart.setPadding(new RectangleInsets(10, 0, 10, 10));

    // Création du panneau contenant le graphique et le panneau absence
    // de capteur sélectionné
    cards = new JPanel(new CardLayout());
    cards.add(new NoSensorPanel("Aucun capteur sélectionné", null), NO_CHART_PANEL);

    // Création du panneau contenant le graphique et la barre de chargement
    JPanel chartAndLoadingBarPanel = new JPanel(new BorderLayout());
    loadingBar = new LoadingBar();
    chartAndLoadingBarPanel.add(loadingBar, BorderLayout.NORTH);
    chartAndLoadingBarPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
    cards.add(chartAndLoadingBarPanel, CHART_PANEL);

    // Panneau contenant le graphique et le panneau de date
    JPanel chartAndDatePanel = new JPanel(new BorderLayout());
    chartAndDatePanel.add(cards, BorderLayout.CENTER);
    chartAndDatePanel.add(outerDatePanel, BorderLayout.SOUTH);
    add(chartAndDatePanel, BorderLayout.CENTER);
  }

  /**
   * Crée les spinners de date.
   */
  private void createDateSpinners() {
    spinnerStart = new JSpinner(new SpinnerDateModel());
    // Le premier spinner est placé une semaine avant la date courante
    spinnerStart.setValue(new Date(System.currentTimeMillis() - 604800000L));

    // Action à effectuer lors de la modification d'un spinner
    AntiSpamClick asc = new AntiSpamClick(e -> updateChart());
    spinnerStart.addChangeListener(e -> {
      // On ajuste la valeur min de l'autre spinner
      Date minDateSpinnerEnd = (Date) spinnerStart.getValue();
      ((SpinnerDateModel) spinnerEnd.getModel()).setStart(minDateSpinnerEnd);
      // On demande la mise à jour du graphique via le AntiSpamClick
      asc.click();
    });
    spinnerEnd = new JSpinner(new SpinnerDateModel());
    spinnerEnd.addChangeListener(e -> {
      // On ajuste la valeur max de l'autre spinner
      Date maxDateSpinnerStart = (Date) spinnerEnd.getModel().getValue();
      ((SpinnerDateModel) spinnerStart.getModel()).setEnd(maxDateSpinnerStart);
      // On demande la mise à jour du graphique via le AntiSpamClick
      asc.click();
    });
  }

  /**
   * Ajoute les données du capteur au graphique.
   *
   * @param sensor le capteur dont les valeurs doivent être ajoutées
   */
  private void addToChart(Sensor sensor) {
    loadingBar.start();
    nbToLoad = 1;
    if (sensorToTimeSeries.isEmpty()) {
      // Le chart n'est pas affiché, on l'affiche
      CardLayout layout = (CardLayout) cards.getLayout();
      layout.show(cards, CHART_PANEL);
    }

    // On crée la série contenant les valeurs du capteur
    TimeSeries series = createTimeSeries(sensor);
    sensorToTimeSeries.put(sensor, series);
    dataset.addSeries(series);
  }

  /**
   * Met à jour le graphe en récupérant les valeurs courantes de chaque capteur
   * actuellement sélectionné.
   */
  private void updateChart() {
    // Récupération des capteurs sélectionnés
    Set<Sensor> sensors = sensorToTimeSeries.keySet();
    loadingBar.start();
    nbToLoad = sensors.size();

    for (Sensor s : sensors) {
      // On enlève la série actuelle et on ajoute la nouvelle
      dataset.removeSeries(sensorToTimeSeries.get(s));
      TimeSeries newTimeSeries = createTimeSeries(s);
      dataset.addSeries(newTimeSeries);
      sensorToTimeSeries.replace(s, newTimeSeries);
    }
  }

  /**
   * Supprime les valeurs actuellement affichées.
   *
   * @param sensor le capteur dont on veut supprimer la courbe
   */
  private void removeFromChart(Sensor sensor) {
    dataset.removeSeries(sensorToTimeSeries.get(sensor));
    sensorToTimeSeries.remove(sensor);

    if (sensorToTimeSeries.isEmpty()) {
      // Plus aucun capteur sélectionné, on masque le chart
      CardLayout layout = (CardLayout) cards.getLayout();
      layout.show(cards, NO_CHART_PANEL);
    }
  }

  /**
   * Crée une nouvelle TimeSeries.
   *
   * @param sensor le capteur dont on veut mettre les valeurs dans la TimeSeries
   * @return la nouvelle TimeSeries
   */
  private TimeSeries createTimeSeries(Sensor sensor) {
    TimeSeries series = new TimeSeries(sensor.getName());

    loadingBar.start();

    SwingWorker<List<Value>, Object> worker = new SwingWorker<List<Value>, Object>() {
      @Override
      protected List<Value> doInBackground() throws Exception {
        // Récupération des valeurs en arrière-plan
        return findFilteredValues(sensor);
      }

      @Override
      protected void done() {
        // On vient de finir la récupération des valeurs, un capteur
        // de moins est donc à charger
        nbToLoad--;

        List<Value> values = null;
        try {
          // Les valeurs sont arrivées, on les récupère
          values = get();
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }

        // Ajout des valeurs à la TimeSeries
        for (Value v : values) {
          series.addOrUpdate(new Second(new Date(v.getDateTime().getTime())), v.getValue());
        }

        // Si plus aucun capteur à charger, on arrête la barre de chargement
        if (nbToLoad <= 0) {
          loadingBar.stop();
        }
      }
    };
    worker.execute();

    return series;
  }

  /**
   * Récupère les valeurs du capteur en fonction des date choisies sur les
   * spinners.
   *
   * @param sensor le capteur dont on veut récupérer les valeurs
   * @return la liste des valeurs du capteur comprises dans l'intervalle de temps
   *         indiqué par les spinners
   */
  private List<Value> findFilteredValues(Sensor sensor) {
    ValueManager vm = ManagerContainer.getInstance().get(ValueManager.class);

    // Récupération des dates début/fin
    long startTime = ((Date) spinnerStart.getModel().getValue()).getTime();
    long endTime = ((Date) spinnerEnd.getModel().getValue()).getTime();

    return vm.findAllBySensorBetween(sensor.getId(), new Timestamp(startTime), new Timestamp(endTime));
  }

  /**
   * Sous-partie du panneau de gauche contenant les capteurs
   */
  private class SensorList extends JScrollPane implements ItemListener {

    /**
     * Panneau contenant les capteurs.
     */
    JPanel root;

    /**
     * Nombre maximal de capteurs sélectionnables simultanément.
     */
    private static final int NB_MAX_SIMULT_SENSORS = 3;

    /**
     * Constructeur.
     */
    public SensorList() {
      root = new JPanel();
      root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
      JLabel label = new JLabel("Sélectionnez un fluide");
      label.setAlignmentX(Component.CENTER_ALIGNMENT);
      root.add(label);
      root.setBorder(new EmptyBorder(5, 5, 5, 5));
      setBorder(null);
      setViewportView(root);
    }

    /**
     * Rafraîchit les boutons de capteur en fonction du fluide sélectionné.
     *
     * @param fluid le nouveau fluide sélectionné
     */
    private void refresh(Fluid.Type fluid) {
      // Plus aucun capteur n'est sélectionné, on vide les HashMap et le dataset
      btnToSensor.clear();
      sensorToTimeSeries.clear();
      dataset.removeAllSeries();

      // Mise à jour du chart pour prendre en compte le nouveau fluide
      XYPlot plot = chart.getXYPlot();
      plot.getRangeAxis().setLabel("Valeur (" + fluid.getUnit() + ")");

      // On masque le chart (aucun capteur n'est sélectionné)
      CardLayout layout = (CardLayout) cards.getLayout();
      layout.show(cards, NO_CHART_PANEL);
      buildSensorsList(fluid);
    }

    /**
     * Construction du panneau contenant les capteurs.
     *
     * @param type le type de fluide sélectionné
     */
    private void buildSensorsList(Fluid.Type type) {
      root.removeAll();
      setViewportView(root);
      EnhancedButtonGroup buttonGroup = new EnhancedButtonGroup(NB_MAX_SIMULT_SENSORS);

      // On va construire la liste des capteurs à partir des données du
      // modèle de l'arbre des capteurs
      int nbBuilding = treeModel.getChildCount(treeModel.getRoot());

      for (int i = 0; i < nbBuilding; ++i) {
        // Récupération du bâtiment
        DefaultMutableTreeNode buildingNode = (DefaultMutableTreeNode) treeModel.getChild(treeModel.getRoot(), i);
        Building b = (Building) buildingNode.getUserObject();
        // Création du panneau du bâtiment
        SubPanel buildingPanel = new SubPanel(b.getName(), 0);
        root.add(buildingPanel);

        int nbFloors = treeModel.getChildCount(buildingNode);
        boolean buildingEmpty = true;
        for (int j = 0; j < nbFloors; ++j) {
          // Récupération de l'étage
          DefaultMutableTreeNode floorNode = (DefaultMutableTreeNode) treeModel.getChild(buildingNode, j);
          Integer floor = (Integer) floorNode.getUserObject();
          // Création du panneau de l'étage
          SubPanel floorPanel = new SubPanel("Étage " + floor, 1);
          buildingPanel.add(floorPanel);
          floorPanel.add(Box.createRigidArea(new Dimension(0, 5)));

          int nbSensors = treeModel.getChildCount(floorNode);

          boolean floorEmpty = true;
          for (int k = 0; k < nbSensors; ++k) {
            // Récupération du capteur
            DefaultMutableTreeNode sensorNode = (DefaultMutableTreeNode) treeModel.getChild(floorNode, k);
            Sensor s = (Sensor) sensorNode.getUserObject();

            // Si le capteur récupéré est du bon type, on l'ajoute
            if (s.getFluid().getType().equals(type)) {
              buildingEmpty = false;
              floorEmpty = false;

              // Création du bouton
              JToggleButton sensorButton = new JToggleButton(s.getName());
              sensorButton.setUI(new LargeToggleButtonUI());
              sensorButton.setPreferredSize(new Dimension(root.getWidth() - 35, LargeToggleButtonUI.HEIGHT));
              sensorButton.setToolTipText(s.getName());

              buttonGroup.add(sensorButton);
              btnToSensor.put(sensorButton, s);
              floorPanel.add(sensorButton);
              floorPanel.add(Box.createRigidArea(new Dimension(0, 5)));
              sensorButton.addItemListener(this);
            }
          }
          floorPanel.add(Box.createRigidArea(new Dimension(0, 5)));
          if (floorEmpty) {
            // Pas de capteur, on enlève le panneau de l'étage
            buildingPanel.remove(floorPanel);
          }
        }
        if (buildingEmpty) {
          // Pas de capteur ni d'étages, on enlève le panneau du bâtiment
          root.remove(buildingPanel);
        }
      }
    }

    /**
     * Ajoute/supprime une courbe quand un capteur est sélectionné/désélectionné.
     * {@inheritDoc}
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
      AbstractButton btn = (AbstractButton) e.getSource();
      Sensor sensor = btnToSensor.get(btn);

      if (e.getStateChange() == ItemEvent.SELECTED) {
        // Ajout du capteur au graphique
        addToChart(sensor);
      } else if (e.getStateChange() == ItemEvent.DESELECTED) {
        // Suppression du capteur du graphique
        removeFromChart(sensor);
      }
    }

  }

}
