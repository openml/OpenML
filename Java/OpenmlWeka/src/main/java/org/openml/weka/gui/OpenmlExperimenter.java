package org.openml.weka.gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import weka.core.Memory;
import weka.experiment.Experiment;
import weka.gui.GUIChooser.GUIChooserMenuPlugin;
import weka.gui.experiment.ResultsPanel;
import weka.gui.experiment.RunPanel;
import weka.gui.experiment.SetupModePanel;

public class OpenmlExperimenter extends JPanel implements GUIChooserMenuPlugin {

	/** for serialization */
	private static final long serialVersionUID = -5751617505738193788L;

	/** The panel for configuring the experiment */
	protected SetupModePanel m_SetupModePanel;

	/** The panel for running the experiment */
	protected RunPanel m_RunPanel;

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

		m_SetupModePanel = new OpenmlSetupModePanel();
		m_ResultsPanel = new ResultsPanel();
		m_RunPanel = new OpenmlRunPanel();
		m_RunPanel.setResultsPanel(m_ResultsPanel);

		m_ClassFirst = classFirst;

		m_TabbedPane.addTab("Setup", null, m_SetupModePanel, "Set up the experiment");
		m_TabbedPane.addTab("Run", null, m_RunPanel, "Run the experiment");
		m_TabbedPane.addTab("Analyse", null, m_ResultsPanel,
				"Analyse experiment results");
		m_TabbedPane.setSelectedIndex(0);
		m_TabbedPane.setEnabledAt(1, false);
		m_SetupModePanel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				// System.err.println("Updated experiment");
				Experiment exp = m_SetupModePanel.getExperiment();
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

	/** for monitoring the Memory consumption */
	protected static Memory m_Memory = new Memory(true);

}