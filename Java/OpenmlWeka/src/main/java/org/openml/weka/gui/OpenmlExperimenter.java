package org.openml.weka.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import weka.core.Memory;
import weka.experiment.Experiment;
import weka.gui.LookAndFeel;
import weka.gui.GUIChooser.GUIChooserMenuPlugin;
import weka.gui.experiment.ResultsPanel;

public class OpenmlExperimenter extends JPanel implements GUIChooserMenuPlugin {

	/** for serialization */
	private static final long serialVersionUID = -5751617505738193788L;

	/** The panel for configuring the experiment */
	protected OpenmlSetupModePanel m_SetupPanel;

	/** The panel for running the experiment */
	protected OpenmlRunPanel m_RunPanel;

	/** The panel for analysing experimental results */
	protected ResultsPanel m_ResultsPanel;

	/** The tabbed pane that controls which sub-pane we are working with */
	protected JTabbedPane m_TabbedPane = new JTabbedPane();

	/**
	 * True if the class attribute is the first attribute for all datasets
	 * involved in this experiment.
	 */
	protected boolean m_ClassFirst = false;

	/**
	 * Creates the experiment environment gui with no initial experiment
	 */
	public OpenmlExperimenter(boolean classFirst) {

		m_SetupPanel = new OpenmlSetupModePanel();
		m_ResultsPanel = new ResultsPanel();
		m_RunPanel = new OpenmlRunPanel();
		m_RunPanel.setResultsPanel(m_ResultsPanel);

		m_ClassFirst = classFirst;

		m_TabbedPane.addTab("Setup", null, m_SetupPanel,
				"Set up the experiment");
		m_TabbedPane.addTab("Run", null, m_RunPanel, "Run the experiment");
		m_TabbedPane.addTab("Analyse", null, m_ResultsPanel,
				"Analyse experiment results");
		m_TabbedPane.setSelectedIndex(0);
		m_TabbedPane.setEnabledAt(1, false);
		m_SetupPanel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				// System.err.println("Updated experiment");
				Experiment exp = m_SetupPanel.getExperiment();
				exp.classFirst(m_ClassFirst);
				m_RunPanel.setExperiment(exp);
				// m_ResultsPanel.setExperiment(exp);
				m_TabbedPane.setEnabledAt(1, true);
			}
		});
		setLayout(new BorderLayout());
		add(m_TabbedPane, BorderLayout.CENTER);
	}
	
	public OpenmlExperimenter() {
		this(true);
	}

	@Override
	public String getApplicationName() {
		return "OpenML Experimenter";
	}

	@Override
	public JMenuBar getMenuBar() {
		return null;
	}

	@Override
	public String getMenuEntryText() {
		return "OpenML Experimenter";
	}

	@Override
	public Menu getMenuToDisplayIn() {
		return GUIChooserMenuPlugin.Menu.TOOLS;
	}

	/**
	 * variable for the Experimenter class which would be set to null by the
	 * memory monitoring thread to free up some memory if we running out of
	 * memory
	 */
	private static OpenmlExperimenter m_experimenter;

	/** for monitoring the Memory consumption */
	protected static Memory m_Memory = new Memory(true);

	/**
	 * Tests out the experiment environment.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {
		weka.core.logging.Logger.log(weka.core.logging.Logger.Level.INFO,
				"Logging started");

		// make sure that packages are loaded and the GenericPropertiesCreator
		// executes to populate the lists correctly
		weka.gui.GenericObjectEditor.determineClasses();

		LookAndFeel.setLookAndFeel();

		try {
			// uncomment to disable the memory management:
			// m_Memory.setEnabled(false);

			boolean classFirst = false;
			if (args.length > 0) {
				classFirst = args[0].equals("CLASS_FIRST");
			}
			m_experimenter = new OpenmlExperimenter(classFirst);
			final JFrame jf = new JFrame("Weka Experiment Environment");
			jf.getContentPane().setLayout(new BorderLayout());
			jf.getContentPane().add(m_experimenter, BorderLayout.CENTER);
			jf.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					jf.dispose();
					System.exit(0);
				}
			});
			jf.pack();
			jf.setSize(800, 600);
			jf.setVisible(true);

			Image icon = Toolkit.getDefaultToolkit().getImage(
					m_experimenter.getClass().getClassLoader()
							.getResource("weka/gui/weka_icon_new_48.png"));
			jf.setIconImage(icon);

			Thread memMonitor = new Thread() {
				@Override
				public void run() {
					while (true) {
						// try {
						// Thread.sleep(10);

						if (m_Memory.isOutOfMemory()) {
							// clean up
							jf.dispose();
							m_experimenter = null;
							System.gc();

							// display error
							System.err.println("\ndisplayed message:");
							m_Memory.showOutOfMemory();
							System.err.println("\nexiting");
							System.exit(-1);
						}

						// } catch (InterruptedException ex) {
						// ex.printStackTrace();
						// }
					}
				}
			};

			memMonitor.setPriority(Thread.NORM_PRIORITY);
			memMonitor.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(ex.getMessage());
		}
	}
}