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
 * Panneau de visualisation des donn�es des capteurs a posteriori.
 */
public class HistoryPanel extends JPanel {

  /**
   * Cha�ne repr�sentant le panneau contenant le graphique.
   */
  private static final String CHART_PANEL = "chart_panel";

  /**
   * Cha�ne repr�sentant le panneau indiquant l'absence de s�lection de capteur.
   */
  private static final String NO_CHART_PANEL = "no_chart_panel";

  /**
   * Donn�es actuellement affich�es dans le graphique.
   */
  private TimeSeriesCollection dataset = new TimeSeriesCollection();

  /**
   * Associe un bouton � un capteur.
   */
  private Map<AbstractButton, Sensor> btnToSensor = new HashMap<>();

  /**
   * Associe un capteur � une TimeSeries.
   */
  private Map<Sensor, TimeSeries> sensorToTimeSeries = new HashMap<>();

  /**
   * Spinner de s�lection de la date de d�but.
   */
  private JSpinner spinnerStart;

  /**
   * Spinner de s�lection de la date de fin.
   */
  private JSpinner spinnerEnd;

  /**
   * JPanel contenant le CHART_PANEL et le NO_CHART_PANEL.
   */
  private JPanel cards;

  /**
   * Graphique actuellement affich�.
   */
  private JFreeChart chart;

  /**
   * Mod�le de l'arbre contenant les capteurs.
   */
  private SensorTreeModel treeModel;

  /**
   * Barre de chargement.
   */
  private LoadingBar loadingBar;

  /**
   * Nombre de capteurs dont on veut les valeurs. Permet de ne pas r�initialiser
   * la barre de chargement en cas de r�cup�ration de plusieurs capteurs
   * simultan�ment (par ex. lors de l'update du graphique).
   */
  private int nbToLoad;

  /**
   * Constructeur.
   *
   * @param treeModel le mod�le de l'arbre des capteurs
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
    // Rafra�chissement du panneau des capteurs lors du clic sur un fluide
    buttons.getBtnWater().addActionListener(e -> sl.refresh(Fluid.Type.EAU));
    buttons.getBtnElec().addActionListener(e -> sl.refresh(Fluid.Type.ELECTRICITE));
    buttons.getBtnTemp().addActionListener(e -> sl.refresh(Fluid.Type.TEMPERATURE));
    buttons.getBtnAir().addActionListener(e -> sl.refresh(Fluid.Type.AIRCOMPRIME));

    createDateSpinners();

    // Panneau contenant les spinners de date et le JLabel
    JPanel datePanel = new JPanel();
    datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
    datePanel.add(new JLabel("P�riode"));

    // Paneau contenant les spinners
    JPanel spinnersPanel = new JPanel();
    spinnersPanel.add(spinnerStart);
    spinnersPanel.add(new JLabel(" - "));
    spinnersPanel.add(spinnerEnd);
    spinnersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    datePanel.add(spinnersPanel);

    // On met le panneau des dates dans un autre pour qu'il soit centr�
    JPanel outerDatePanel = new JPanel();
    outerDatePanel.add(datePanel);
    outerDatePanel.setBorder(new MatteBorder(1, 0, 0, 0, Config.BORDER_COLOR));

    // Cr�ation du graphique
    chart = ChartFactory.createTimeSeriesChart(null, "Temps", "", dataset);
    chart.setBackgroundPaint(Config.BACKGROUND_COLOR);
    chart.setPadding(new RectangleInsets(10, 0, 10, 10));

    // Cr�ation du panneau contenant le graphique et le panneau absence
    // de capteur s�lectionn�
    cards = new JPanel(new CardLayout());
    cards.add(new NoSensorPanel("Aucun capteur s�lectionn�", null), NO_CHART_PANEL);

    // Cr�ation du panneau contenant le graphique et la barre de chargement
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
   * Cr�e les spinners de date.
   */
  private void createDateSpinners() {
    spinnerStart = new JSpinner(new SpinnerDateModel());
    // Le premier spinner est plac� une semaine avant la date courante
    spinnerStart.setValue(new Date(System.currentTimeMillis() - 604800000L));

    // Action � effectuer lors de la modification d'un spinner
    AntiSpamClick asc = new AntiSpamClick(e -> updateChart());
    spinnerStart.addChangeListener(e -> {
      // On ajuste la valeur min de l'autre spinner
      Date minDateSpinnerEnd = (Date) spinnerStart.getValue();
      ((SpinnerDateModel) spinnerEnd.getModel()).setStart(minDateSpinnerEnd);
      // On demande la mise � jour du graphique via le AntiSpamClick
      asc.click();
    });
    spinnerEnd = new JSpinner(new SpinnerDateModel());
    spinnerEnd.addChangeListener(e -> {
      // On ajuste la valeur max de l'autre spinner
      Date maxDateSpinnerStart = (Date) spinnerEnd.getModel().getValue();
      ((SpinnerDateModel) spinnerStart.getModel()).setEnd(maxDateSpinnerStart);
      // On demande la mise � jour du graphique via le AntiSpamClick
      asc.click();
    });
  }

  /**
   * Ajoute les donn�es du capteur au graphique.
   *
   * @param sensor le capteur dont les valeurs doivent �tre ajout�es
   */
  private void addToChart(Sensor sensor) {
    loadingBar.start();
    nbToLoad = 1;
    if (sensorToTimeSeries.isEmpty()) {
      // Le chart n'est pas affich�, on l'affiche
      CardLayout layout = (CardLayout) cards.getLayout();
      layout.show(cards, CHART_PANEL);
    }

    // On cr�e la s�rie contenant les valeurs du capteur
    TimeSeries series = createTimeSeries(sensor);
    sensorToTimeSeries.put(sensor, series);
    dataset.addSeries(series);
  }

  /**
   * Met � jour le graphe en r�cup�rant les valeurs courantes de chaque capteur
   * actuellement s�lectionn�.
   */
  private void updateChart() {
    // R�cup�ration des capteurs s�lectionn�s
    Set<Sensor> sensors = sensorToTimeSeries.keySet();
    loadingBar.start();
    nbToLoad = sensors.size();

    for (Sensor s : sensors) {
      // On enl�ve la s�rie actuelle et on ajoute la nouvelle
      dataset.removeSeries(sensorToTimeSeries.get(s));
      TimeSeries newTimeSeries = createTimeSeries(s);
      dataset.addSeries(newTimeSeries);
      sensorToTimeSeries.replace(s, newTimeSeries);
    }
  }

  /**
   * Supprime les valeurs actuellement affich�es.
   *
   * @param sensor le capteur dont on veut supprimer la courbe
   */
  private void removeFromChart(Sensor sensor) {
    dataset.removeSeries(sensorToTimeSeries.get(sensor));
    sensorToTimeSeries.remove(sensor);

    if (sensorToTimeSeries.isEmpty()) {
      // Plus aucun capteur s�lectionn�, on masque le chart
      CardLayout layout = (CardLayout) cards.getLayout();
      layout.show(cards, NO_CHART_PANEL);
    }
  }

  /**
   * Cr�e une nouvelle TimeSeries.
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
        // R�cup�ration des valeurs en arri�re-plan
        return findFilteredValues(sensor);
      }

      @Override
      protected void done() {
        // On vient de finir la r�cup�ration des valeurs, un capteur
        // de moins est donc � charger
        nbToLoad--;

        List<Value> values = null;
        try {
          // Les valeurs sont arriv�es, on les r�cup�re
          values = get();
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }

        // Ajout des valeurs � la TimeSeries
        for (Value v : values) {
          series.addOrUpdate(new Second(new Date(v.getDateTime().getTime())), v.getValue());
        }

        // Si plus aucun capteur � charger, on arr�te la barre de chargement
        if (nbToLoad <= 0) {
          loadingBar.stop();
        }
      }
    };
    worker.execute();

    return series;
  }

  /**
   * R�cup�re les valeurs du capteur en fonction des date choisies sur les
   * spinners.
   *
   * @param sensor le capteur dont on veut r�cup�rer les valeurs
   * @return la liste des valeurs du capteur comprises dans l'intervalle de temps
   *         indiqu� par les spinners
   */
  private List<Value> findFilteredValues(Sensor sensor) {
    ValueManager vm = ManagerContainer.getInstance().get(ValueManager.class);

    // R�cup�ration des dates d�but/fin
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
     * Nombre maximal de capteurs s�lectionnables simultan�ment.
     */
    private static final int NB_MAX_SIMULT_SENSORS = 3;

    /**
     * Constructeur.
     */
    public SensorList() {
      root = new JPanel();
      root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
      JLabel label = new JLabel("S�lectionnez un fluide");
      label.setAlignmentX(Component.CENTER_ALIGNMENT);
      root.add(label);
      root.setBorder(new EmptyBorder(5, 5, 5, 5));
      setBorder(null);
      setViewportView(root);
    }

    /**
     * Rafra�chit les boutons de capteur en fonction du fluide s�lectionn�.
     *
     * @param fluid le nouveau fluide s�lectionn�
     */
    private void refresh(Fluid.Type fluid) {
      // Plus aucun capteur n'est s�lectionn�, on vide les HashMap et le dataset
      btnToSensor.clear();
      sensorToTimeSeries.clear();
      dataset.removeAllSeries();

      // Mise � jour du chart pour prendre en compte le nouveau fluide
      XYPlot plot = chart.getXYPlot();
      plot.getRangeAxis().setLabel("Valeur (" + fluid.getUnit() + ")");

      // On masque le chart (aucun capteur n'est s�lectionn�)
      CardLayout layout = (CardLayout) cards.getLayout();
      layout.show(cards, NO_CHART_PANEL);
      buildSensorsList(fluid);
    }

    /**
     * Construction du panneau contenant les capteurs.
     *
     * @param type le type de fluide s�lectionn�
     */
    private void buildSensorsList(Fluid.Type type) {
      root.removeAll();
      setViewportView(root);
      EnhancedButtonGroup buttonGroup = new EnhancedButtonGroup(NB_MAX_SIMULT_SENSORS);

      // On va construire la liste des capteurs � partir des donn�es du
      // mod�le de l'arbre des capteurs
      int nbBuilding = treeModel.getChildCount(treeModel.getRoot());

      for (int i = 0; i < nbBuilding; ++i) {
        // R�cup�ration du b�timent
        DefaultMutableTreeNode buildingNode = (DefaultMutableTreeNode) treeModel.getChild(treeModel.getRoot(), i);
        Building b = (Building) buildingNode.getUserObject();
        // Cr�ation du panneau du b�timent
        SubPanel buildingPanel = new SubPanel(b.getName(), 0);
        root.add(buildingPanel);

        int nbFloors = treeModel.getChildCount(buildingNode);
        boolean buildingEmpty = true;
        for (int j = 0; j < nbFloors; ++j) {
          // R�cup�ration de l'�tage
          DefaultMutableTreeNode floorNode = (DefaultMutableTreeNode) treeModel.getChild(buildingNode, j);
          Integer floor = (Integer) floorNode.getUserObject();
          // Cr�ation du panneau de l'�tage
          SubPanel floorPanel = new SubPanel("�tage " + floor, 1);
          buildingPanel.add(floorPanel);
          floorPanel.add(Box.createRigidArea(new Dimension(0, 5)));

          int nbSensors = treeModel.getChildCount(floorNode);

          boolean floorEmpty = true;
          for (int k = 0; k < nbSensors; ++k) {
            // R�cup�ration du capteur
            DefaultMutableTreeNode sensorNode = (DefaultMutableTreeNode) treeModel.getChild(floorNode, k);
            Sensor s = (Sensor) sensorNode.getUserObject();

            // Si le capteur r�cup�r� est du bon type, on l'ajoute
            if (s.getFluid().getType().equals(type)) {
              buildingEmpty = false;
              floorEmpty = false;

              // Cr�ation du bouton
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
            // Pas de capteur, on enl�ve le panneau de l'�tage
            buildingPanel.remove(floorPanel);
          }
        }
        if (buildingEmpty) {
          // Pas de capteur ni d'�tages, on enl�ve le panneau du b�timent
          root.remove(buildingPanel);
        }
      }
    }

    /**
     * Ajoute/supprime une courbe quand un capteur est s�lectionn�/d�s�lectionn�.
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
