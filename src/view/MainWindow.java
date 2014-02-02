package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import model.CoinTossingSimulation;

/**
 * GUI for the coin tossing simulation.
 * 
 * @author Fabian Foerg
 */
public final class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String START_BUTTON_TEXT = "Start simulation";
    private static final Color START_BUTTON_FOREGROUND = Color.BLACK;
    private static final Color START_BUTTON_BACKGROUND = Color.GREEN;
    private static final String STOP_BUTTON_TEXT = "Stop simulation";
    private static final Color STOP_BUTTON_FOREGROUND = Color.BLACK;
    private static final Color STOP_BUTTON_BACKGROUND = Color.RED;

    private JCheckBox randomSeed;
    private JSpinner seedSpinner;
    private JSpinner tossesSpinner;
    private JPanel graphPanel;
    private JLabel lowerTosses;
    private JLabel numberOfHeads;
    private JLabel headsWinnerSideTime;
    private JLabel numberOfTails;
    private JLabel tailsWinnerSideTime;
    private JButton startButton;

    /**
     * Creates the main window of the simulation GUI.
     */
    public MainWindow() {
        super("Coin Tossing Simulation by Fabian Foerg");
    }

    public void createAndShowGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    /**
     * Initializes all window elements.
     */
    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel contentPane = new JPanel();
        contentPane.setOpaque(true);
        contentPane.setLayout(new BorderLayout());

        SpinnerNumberModel seedSpinnerModel = new SpinnerNumberModel(0,
                Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
        seedSpinner = new JSpinner(seedSpinnerModel);
        seedSpinner.setEnabled(false);
        randomSeed = new JCheckBox("random", true);
        randomSeed.addItemListener(new CheckBoxListener());

        JPanel seedPanel = new JPanel();
        seedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        seedPanel.add(new JLabel("Seed:"));
        seedPanel.add(randomSeed);
        seedPanel.add(seedSpinner);

        SpinnerNumberModel tossesSpinnerModel = new SpinnerNumberModel(100, 1,
                Integer.MAX_VALUE, 1);
        tossesSpinner = new JSpinner(tossesSpinnerModel);

        JPanel tossesPanel = new JPanel();
        tossesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tossesPanel.add(new JLabel("Number of tosses:"));
        tossesPanel.add(tossesSpinner);

        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
        upperPanel.add(seedPanel);
        upperPanel.add(tossesPanel);
        contentPane.add(upperPanel, BorderLayout.PAGE_START);

        UpdateListener updateListener = new UpdateListener();

        graphPanel = new JPanel();
        graphPanel.setPreferredSize(new Dimension(800, 500));
        graphPanel.addComponentListener(updateListener);
        contentPane.add(graphPanel, BorderLayout.CENTER);

        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new GridLayout(0, 4, 1, 1));
        lowerPanel.add(new JLabel("Number of tosses:"));
        lowerTosses = new JLabel();
        lowerPanel.add(lowerTosses);
        lowerPanel.add(new JPanel());
        lowerPanel.add(new JPanel());

        lowerPanel.add(new JLabel("Number of heads:"));
        numberOfHeads = new JLabel();
        lowerPanel.add(numberOfHeads);
        lowerPanel.add(new JLabel("Head winner side time"));
        headsWinnerSideTime = new JLabel();
        lowerPanel.add(headsWinnerSideTime);

        lowerPanel.add(new JLabel("Number of tails:"));
        numberOfTails = new JLabel();
        lowerPanel.add(numberOfTails);
        lowerPanel.add(new JLabel("Tail winner side time"));
        tailsWinnerSideTime = new JLabel();
        lowerPanel.add(tailsWinnerSideTime);

        startButton = new JButton("Start simulation");
        changeButton(true);
        startButton.addActionListener(updateListener);
        lowerPanel.add(new JPanel());
        lowerPanel.add(startButton);

        contentPane.add(lowerPanel, BorderLayout.PAGE_END);

        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Flips the start/stop button.
     * 
     * @param start
     *            <code>true</code> if the start/stop button should display
     *            start. Otherwise, stop is displayed.
     */
    public void changeButton(final boolean start) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (start) {
                    startButton.setText(START_BUTTON_TEXT);
                    startButton.setForeground(START_BUTTON_FOREGROUND);
                    startButton.setBackground(START_BUTTON_BACKGROUND);
                } else {
                    startButton.setText(STOP_BUTTON_TEXT);
                    startButton.setForeground(STOP_BUTTON_FOREGROUND);
                    startButton.setBackground(STOP_BUTTON_BACKGROUND);
                }

                startButton.repaint();
            }
        });
    }

    /**
     * Listens for clicks on the start/stop button and starts/stops the
     * simulation accordingly.
     * 
     * @author Fabian Foerg
     */
    private final class UpdateListener implements ActionListener,
            ComponentListener, Observer {
        private Graph graph;
        private CoinTossingSimulation simulation;
        private SwingWorker<Void, Void> simulationThread;
        private int updateCounter = 0;
        private static final int UPDATE_INTERVAL = 1000;

        /**
         * Default constructor.
         */
        public UpdateListener() {
            graph = null;
            simulation = null;
            simulationThread = null;
        }

        /**
         * Responds to a push on the start/stop button by starting/stopping the
         * simulation and updating the graphs correspondingly.
         * 
         * @param arg0
         *            unused
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (simulation != null) {

                // a simulation is running
                simulation.stopSimulation();
                try {
                    simulationThread.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                // The simulation thread will stop and execute done().
                // Still show the graph.
                return;
            }

            graphPanel.removeAll();

            if (graph != null) {
                graph.free();
                graph = null;
            }

            graph = new Graph();
            graph.setPreferredSize(graphPanel.getSize());
            graphPanel.add(graph);
            graphPanel.repaint();

            // start the simulation
            if (randomSeed.isSelected()) {
                simulation = new CoinTossingSimulation(
                        (Integer) tossesSpinner.getValue());
            } else {
                simulation = new CoinTossingSimulation(
                        (Integer) tossesSpinner.getValue(),
                        (Integer) seedSpinner.getValue());
            }
            simulation.addObserver(graph);
            simulation.addObserver(this);
            changeButton(false);
            simulationThread = new SwingWorker<Void, Void>() {
                public Void doInBackground() {
                    simulation.startGameSimulation();
                    return null;
                }

                public void done() {
                    simulationStopped();
                }
            };
            simulationThread.execute();
        }

        /**
         * Called when the simulation was stopped.
         */
        private void simulationStopped() {
            simulation.deleteObservers();
            updateLowerPanel();
            simulation = null;
            updateCounter = 0;
            changeButton(true);
        }

        /**
         * Updates the statistics panel with the results from the simulation.
         */
        private void updateLowerPanel() {
            lowerTosses.setText(String.valueOf(simulation
                    .getNumberOfTossesRealized()));

            // head information
            numberOfHeads
                    .setText(String.valueOf(simulation.getNumberOfHeads()));
            headsWinnerSideTime
                    .setText(simulation.getHeadWinnerSideTimeAbsolute()
                            + " / "
                            + String.valueOf(simulation
                                    .getHeadWinnerSideTimeRelative() * 100.0)
                            + " %");

            // tail information
            numberOfTails
                    .setText(String.valueOf(simulation.getNumberOfTails()));
            tailsWinnerSideTime
                    .setText(simulation.getTailWinnerSideTimeAbsolute()
                            + " / "
                            + String.valueOf(simulation
                                    .getTailWinnerSideTimeRelatve() * 100.0)
                            + " %");
        }

        /**
         * Updates the statistics panel whenever UPDATE_INTERVAL coins have been
         * tossed.
         */
        @Override
        public void update(Observable arg0, Object arg1) {
            updateCounter++;

            if (updateCounter == UPDATE_INTERVAL) {
                updateLowerPanel();
                updateCounter = 0;
            }
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }

        @Override
        public void componentMoved(ComponentEvent e) {
        }

        @Override
        public void componentResized(ComponentEvent e) {
            if (graph != null) {
                graph.setPreferredSize(graphPanel.getSize());
                graphPanel.revalidate();
            }
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }
    }

    /**
     * Listener class for the seed spinner.
     * 
     * @author Fabian Foerg
     */
    private final class CheckBoxListener implements ItemListener {
        public CheckBoxListener() {
        }

        @Override
        public void itemStateChanged(ItemEvent arg0) {
            if (randomSeed.isSelected()) {
                seedSpinner.setEnabled(false);
            } else {
                seedSpinner.setEnabled(true);
            }
        }
    }
}
