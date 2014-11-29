/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    SimpleSetupPanel.java
 *    Copyright (C) 2002-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package org.openml.weka.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.settings.Constants;
import org.openml.weka.experiment.TaskBasedExperiment;
import org.openml.weka.experiment.TaskResultListener;
import org.openml.weka.experiment.TaskResultProducer;

import weka.classifiers.Classifier;
import weka.experiment.CSVResultListener;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.DatabaseResultListener;
import weka.experiment.Experiment;
import weka.experiment.InstancesResultListener;
import weka.experiment.PropertyNode;
import weka.experiment.RandomSplitResultProducer;
import weka.experiment.RegressionSplitEvaluator;
import weka.experiment.SplitEvaluator;
import weka.gui.DatabaseConnectionDialog;
import weka.gui.experiment.SimpleSetupPanel;

/**
 * This panel controls the configuration of an experiment.
 * <p>
 * If <a href="http://koala.ilog.fr/XML/serialization/" target="_blank">KOML</a>
 * is in the classpath the experiments can also be serialized to XML instead of
 * a binary format.
 * 
 * @author Richard kirkby (rkirkby@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8034 $
 */
public class OpenmlSimpleSetupPanel extends SimpleSetupPanel {
	
	private static final long serialVersionUID = -4411056918296619225L;

	/** The experiment being configured */
	protected TaskBasedExperiment m_Exp;

	/** The strings used to identify the combo box choices */
	protected static String DEST_OPENML_TEXT = "OpenML.org";

	protected static String TYPE_OPENML_TASK_TEXT = "OpenML Task";

	/** The panel for configuring selected OpenML tasks */
	protected TaskListPanel m_TaskListPanel;

	protected OpenmlConnector apiconnector;
	
	protected Config openmlconfig;
	
	/**
	 * Creates the setup panel with the supplied initial experiment.
	 * 
	 * @param exp
	 *            a value of type 'Experiment'
	 */
	public OpenmlSimpleSetupPanel(Experiment exp) {

		this();
		setExperiment(exp);
	}

	/**
	 * Creates the setup panel with no initial experiment.
	 */
	public OpenmlSimpleSetupPanel() {
		try {
			openmlconfig = new Config();
			if( openmlconfig.getServer() != null ) {
				apiconnector = new OpenmlConnector( openmlconfig.getServer(), openmlconfig.getUsername(), openmlconfig.getPassword() );
				
			} else { 
				apiconnector = new OpenmlConnector( openmlconfig.getUsername(), openmlconfig.getPassword() );
			} 
		} catch( RuntimeException e ) {
			apiconnector = new OpenmlConnector();
		}

		m_Support = new PropertyChangeSupport(this);
		
		m_TaskListPanel = new TaskListPanel( apiconnector );
		
		/* Override some things from the original Weka Experimenter Layout */
		
		m_BrowseDestinationButton.removeActionListener( m_BrowseDestinationButton.getActionListeners()[0] );
		m_BrowseDestinationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// using this button for both browsing file & setting
				// username/password
				if (m_ResultsDestinationCBox.getSelectedItem() == DEST_DATABASE_TEXT) {
					chooseURLUsername();
				} else if (m_ResultsDestinationCBox.getSelectedItem() == DEST_OPENML_TEXT) {
					chooseUsernamePassword();
				} else {
					chooseDestinationFile();
				}
			}
		});
		
		m_ResultsDestinationCBox.addItem(DEST_OPENML_TEXT);
		m_ResultsDestinationCBox.removeActionListener( m_ResultsDestinationCBox.getActionListeners()[0] );
		m_ResultsDestinationCBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destinationTypeChanged();
			}
		});
		
		m_ExperimentTypeCBox.addItem(TYPE_OPENML_TASK_TEXT);
		m_ExperimentTypeCBox.removeActionListener( m_ExperimentTypeCBox.getActionListeners()[0] );
		m_ExperimentTypeCBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				expTypeChanged();
			}
		});

		JPanel datasets = new JPanel();
		datasets.setLayout(new BorderLayout());
		datasets.add(m_TaskListPanel, BorderLayout.CENTER);

		JPanel algorithms = new JPanel();
		algorithms.setLayout(new BorderLayout());
		algorithms.add(m_AlgorithmListPanel, BorderLayout.CENTER);

		JPanel schemes = new JPanel();
		schemes.setLayout(new GridLayout(1, 0));
		schemes.add(datasets);
		schemes.add(algorithms);

		add(schemes, BorderLayout.CENTER);
	}

	/**
	 * Gets te users consent for converting the experiment to a simpler form.
	 * 
	 * @return true if the user has given consent, false otherwise
	 */
	private boolean userWantsToConvert() {

		if (m_userHasBeenAskedAboutConversion)
			return true;
		m_userHasBeenAskedAboutConversion = true;
		return (JOptionPane.showConfirmDialog(this,
				"This experiment has settings that are too advanced\n"
						+ "to be represented in the simple setup mode.\n"
						+ "Do you want the experiment to be converted,\n"
						+ "losing some of the advanced settings?\n",
				"Confirm conversion", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION);
	}

	/**
	 * Sets the experiment to configure.
	 * 
	 * @param exp
	 *            a value of type 'Experiment'
	 * @return true if experiment could be configured, false otherwise
	 */
	public boolean setExperiment(Experiment exp_old) {

		TaskBasedExperiment exp = new TaskBasedExperiment( exp_old, apiconnector );
		m_userHasBeenAskedAboutConversion = false;
		m_Exp = null; // hold off until we are sure we want conversion
		m_SaveBut.setEnabled(true);

		if (exp.getResultListener() instanceof DatabaseResultListener) {
			m_ResultsDestinationCBox.setSelectedItem(DEST_DATABASE_TEXT);
			m_ResultsDestinationPathLabel.setText("URL:");
			m_destinationDatabaseURL = ((DatabaseResultListener) exp
					.getResultListener()).getDatabaseURL();
			m_ResultsDestinationPathTField.setText(m_destinationDatabaseURL);
			m_BrowseDestinationButton.setEnabled(true);
		} else if (exp.getResultListener() instanceof InstancesResultListener) {
			m_ResultsDestinationCBox.setSelectedItem(DEST_ARFF_TEXT);
			m_ResultsDestinationPathLabel.setText("Filename:");
			m_destinationFilename = ((InstancesResultListener) exp
					.getResultListener()).outputFileName();
			m_ResultsDestinationPathTField.setText(m_destinationFilename);
			m_BrowseDestinationButton.setEnabled(true);
		} else if (exp.getResultListener() instanceof CSVResultListener) {
			m_ResultsDestinationCBox.setSelectedItem(DEST_CSV_TEXT);
			m_ResultsDestinationPathLabel.setText("Filename:");
			m_destinationFilename = ((CSVResultListener) exp
					.getResultListener()).outputFileName();
			m_ResultsDestinationPathTField.setText(m_destinationFilename);
			m_BrowseDestinationButton.setEnabled(true);
		} else {
			// unrecognised result listener
			System.out.println("SimpleSetup incompatibility: unrecognised result destination");
			if (userWantsToConvert()) {
				m_ResultsDestinationCBox.setSelectedItem(DEST_ARFF_TEXT);
				m_ResultsDestinationPathLabel.setText("Filename:");
				m_destinationFilename = "";
				m_ResultsDestinationPathTField.setText(m_destinationFilename);
				m_BrowseDestinationButton.setEnabled(true);
			} else {
				return false;
			}
		}
		m_ResultsDestinationCBox.setEnabled(true);
		m_ResultsDestinationPathLabel.setEnabled(true);
		m_ResultsDestinationPathTField.setEnabled(true);

		if (exp.getResultProducer() instanceof CrossValidationResultProducer) {
			CrossValidationResultProducer cvrp = (CrossValidationResultProducer) exp
					.getResultProducer();
			m_numFolds = cvrp.getNumFolds();
			m_ExperimentParameterTField.setText("" + m_numFolds);

			if (cvrp.getSplitEvaluator() instanceof ClassifierSplitEvaluator) {
				m_ExpClassificationRBut.setSelected(true);
				m_ExpRegressionRBut.setSelected(false);
			} else if (cvrp.getSplitEvaluator() instanceof RegressionSplitEvaluator) {
				m_ExpClassificationRBut.setSelected(false);
				m_ExpRegressionRBut.setSelected(true);
			} else {
				// unknown split evaluator
				System.out.println("SimpleSetup incompatibility: unrecognised split evaluator");
				if (userWantsToConvert()) {
					m_ExpClassificationRBut.setSelected(true);
					m_ExpRegressionRBut.setSelected(false);
				} else {
					return false;
				}
			}
			m_ExperimentTypeCBox.setSelectedItem(TYPE_CROSSVALIDATION_TEXT);
		} else if (exp.getResultProducer() instanceof RandomSplitResultProducer) {
			RandomSplitResultProducer rsrp = (RandomSplitResultProducer) exp.getResultProducer();
			if (rsrp.getRandomizeData()) {
				m_ExperimentTypeCBox.setSelectedItem(TYPE_RANDOMSPLIT_TEXT);
			} else {
				m_ExperimentTypeCBox.setSelectedItem(TYPE_FIXEDSPLIT_TEXT);
			}
			if (rsrp.getSplitEvaluator() instanceof ClassifierSplitEvaluator) {
				m_ExpClassificationRBut.setSelected(true);
				m_ExpRegressionRBut.setSelected(false);
			} else if (rsrp.getSplitEvaluator() instanceof RegressionSplitEvaluator) {
				m_ExpClassificationRBut.setSelected(false);
				m_ExpRegressionRBut.setSelected(true);
			} else {
				// unknown split evaluator
				System.out.println("SimpleSetup incompatibility: unrecognised split evaluator");
				if (userWantsToConvert()) {
					m_ExpClassificationRBut.setSelected(true);
					m_ExpRegressionRBut.setSelected(false);
				} else {
					return false;
				}
			}
			m_trainPercent = rsrp.getTrainPercent();
			m_ExperimentParameterTField.setText("" + m_trainPercent);

		} else {
			// unknown experiment type
			System.out.println("SimpleSetup incompatibility: unrecognised resultProducer");
			if (userWantsToConvert()) {
				m_ExperimentTypeCBox.setSelectedItem(TYPE_CROSSVALIDATION_TEXT);
				m_ExpClassificationRBut.setSelected(true);
				m_ExpRegressionRBut.setSelected(false);
			} else {
				return false;
			}
		}

		m_ExperimentTypeCBox.setEnabled(true);
		m_ExperimentParameterLabel.setEnabled(true);
		m_ExperimentParameterTField.setEnabled(true);
		m_ExpClassificationRBut.setEnabled(true);
		m_ExpRegressionRBut.setEnabled(true);

		if (exp.getRunLower() == 1) {
			m_numRepetitions = exp.getRunUpper();
			m_NumberOfRepetitionsTField.setText("" + m_numRepetitions);
		} else {
			// unsupported iterations
			System.out.println("SimpleSetup incompatibility: runLower is not 1");
			if (userWantsToConvert()) {
				exp.setRunLower(1);
				if (m_ExperimentTypeCBox.getSelectedItem() == TYPE_FIXEDSPLIT_TEXT) {
					exp.setRunUpper(1);
					m_NumberOfRepetitionsTField.setEnabled(false);
					m_NumberOfRepetitionsTField.setText("1");
				} else {
					exp.setRunUpper(10);
					m_numRepetitions = 10;
					m_NumberOfRepetitionsTField.setText("" + m_numRepetitions);
				}

			} else {
				return false;
			}
		}
		m_NumberOfRepetitionsTField.setEnabled(true);

		m_OrderDatasetsFirstRBut.setSelected(exp.getAdvanceDataSetFirst());
		m_OrderAlgorithmsFirstRBut.setSelected(!exp.getAdvanceDataSetFirst());
		m_OrderDatasetsFirstRBut.setEnabled(true);
		m_OrderAlgorithmsFirstRBut.setEnabled(true);

		m_NotesText.setText(exp.getNotes());
		m_NotesButton.setEnabled(true);

		if (!exp.getUsePropertyIterator()
				|| !(exp.getPropertyArray() instanceof Classifier[])) {
			// unknown property iteration
			System.out.println("SimpleSetup incompatibility: unrecognised property iteration");
			if (userWantsToConvert()) {
				exp.setPropertyArray(new Classifier[0]);
				exp.setUsePropertyIterator(true);
			} else {
				return false;
			}
		}

		m_TaskListPanel.setExperiment(exp);
		m_AlgorithmListPanel.setExperiment(exp);

		m_Exp = exp;
		expTypeChanged(); // recreate experiment

		m_Support.firePropertyChange("", null, null);

		return true;
	}
	
	/**
	 * Responds to a change in the destination type.
	 */
	private void destinationTypeChanged() {

		if (m_Exp == null)
			return;

		String str = "";

		if (m_ResultsDestinationCBox.getSelectedItem() == DEST_DATABASE_TEXT) {
			m_ResultsDestinationPathLabel.setText("URL:");
			str = m_destinationDatabaseURL;
			m_BrowseDestinationButton.setEnabled(true); // !!!
			m_ResultsDestinationPathTField.setEnabled(true);
			m_BrowseDestinationButton.setText("User...");
		} else if (m_ResultsDestinationCBox.getSelectedItem() == DEST_OPENML_TEXT) {
			m_BrowseDestinationButton.setEnabled(true);
			m_ResultsDestinationPathTField.setEnabled(false);

			m_ResultsDestinationPathLabel.setText("OpenML Username: ");
			m_BrowseDestinationButton.setText("Login");
			m_ExperimentTypeCBox.setSelectedItem(TYPE_OPENML_TASK_TEXT);
		} else {
			m_ResultsDestinationPathLabel.setText("Filename:");
			if (m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT) {
				int ind = m_destinationFilename.lastIndexOf(".csv");
				if (ind > -1) {
					m_destinationFilename = m_destinationFilename.substring(0,
							ind) + ".arff";
				}
			}
			if (m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) {
				int ind = m_destinationFilename.lastIndexOf(".arff");
				if (ind > -1) {
					m_destinationFilename = m_destinationFilename.substring(0,
							ind) + ".csv";
				}
			}
			str = m_destinationFilename;
			if (m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT) {
				int ind = str.lastIndexOf(".csv");
				if (ind > -1) {
					str = str.substring(0, ind) + ".arff";
				}
			}
			if (m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) {
				int ind = str.lastIndexOf(".arff");
				if (ind > -1) {
					str = str.substring(0, ind) + ".csv";
				}
			}
			m_BrowseDestinationButton.setEnabled(true);
			m_ResultsDestinationPathTField.setEnabled(true);
			m_BrowseDestinationButton.setText("Browse...");
		}

		if (m_ResultsDestinationCBox.getSelectedItem() == DEST_DATABASE_TEXT) {
			DatabaseResultListener drl = null;
			try {
				drl = new DatabaseResultListener();
			} catch (Exception e) {
				e.printStackTrace();
			}
			drl.setDatabaseURL(m_destinationDatabaseURL);
			m_Exp.setResultListener(drl);
		} else {
			if (m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT) {
				InstancesResultListener irl = new InstancesResultListener();
				if (!m_destinationFilename.equals("")) {
					irl.setOutputFile(new File(m_destinationFilename));
				}
				m_Exp.setResultListener(irl);
			} else if (m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) {
				CSVResultListener crl = new CSVResultListener();
				if (!m_destinationFilename.equals("")) {
					crl.setOutputFile(new File(m_destinationFilename));
				}
				m_Exp.setResultListener(crl);
			} else if (m_ResultsDestinationCBox.getSelectedItem() == DEST_OPENML_TEXT) {
				TaskResultListener trl = new TaskResultListener(apiconnector, openmlconfig);
				try {
					File f = File.createTempFile("WekaOpenMLResults",
							Constants.DATASET_FORMAT);
					f.deleteOnExit();
					trl.setOutputFile(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				m_Exp.setResultListener(trl);

				// try to load default config
				if (openmlconfig.getUsername() != null) {
					if ( apiconnector.setCredentials(openmlconfig.getUsername(), openmlconfig.getPassword() ) ) {
						str = openmlconfig.getUsername();
					} else {
						str = null;
						JOptionPane.showMessageDialog(null,
								"Username/Password (Config file) incorrect",
								"Login error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}

		m_ResultsDestinationPathTField.setText(str);

		m_Support.firePropertyChange("", null, null);
	}

	/**
	 * Adds a PropertyChangeListener who will be notified of value changes.
	 * 
	 * @param l
	 *            a value of type 'PropertyChangeListener'
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		m_Support.addPropertyChangeListener(l);
	}

	/**
	 * Removes a PropertyChangeListener.
	 * 
	 * @param l
	 *            a value of type 'PropertyChangeListener'
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		m_Support.removePropertyChangeListener(l);
	}
	
	/**
	 * Gets the currently configured experiment.
	 * 
	 * @return the currently configured experiment.
	 */
	@Override
	public Experiment getExperiment() {

		return m_Exp;
	}
	
	/**
	 * Responds to a change in the experiment type.
	 */
	private void expTypeChanged() {
		if (m_Exp == null)
			return;

		// update parameter ui
		if (m_ExperimentTypeCBox.getSelectedItem() == TYPE_CROSSVALIDATION_TEXT) {
			m_ExperimentParameterLabel.setText("Number of folds:");
			m_ExperimentParameterTField.setText("" + m_numFolds);
		} else if (m_ExperimentTypeCBox.getSelectedItem() == TYPE_OPENML_TASK_TEXT) {
			// nothing. yet
		} else {
			m_ExperimentParameterLabel.setText("Train percentage:");
			m_ExperimentParameterTField.setText("" + m_trainPercent);
		}

		if (m_ExperimentTypeCBox.getSelectedItem() == TYPE_OPENML_TASK_TEXT) {
			m_ExpClassificationRBut.setEnabled(false);
			m_ExpRegressionRBut.setEnabled(false);
			m_TaskListPanel.setMode(false);

			m_ExperimentParameterTField.setEnabled(false);
			m_Exp.setMode(false);
		} else {
			m_ExpClassificationRBut.setEnabled(true);
			m_ExpRegressionRBut.setEnabled(true);
			m_TaskListPanel.setMode(true);
			m_ExperimentParameterTField.setEnabled(true);
			m_Exp.setMode(true);

			if (m_ResultsDestinationCBox.getSelectedItem() == DEST_OPENML_TEXT) {
				m_ResultsDestinationCBox.setSelectedItem(DEST_ARFF_TEXT);
			}
		}

		// update iteration ui
		if (m_ExperimentTypeCBox.getSelectedItem() == TYPE_FIXEDSPLIT_TEXT
				|| m_ExperimentTypeCBox.getSelectedItem() == TYPE_OPENML_TASK_TEXT) {
			m_NumberOfRepetitionsTField.setEnabled(false);
			m_NumberOfRepetitionsTField.setText("1");
			m_Exp.setRunLower(1);
			m_Exp.setRunUpper(1);
		} else {
			m_NumberOfRepetitionsTField.setText("" + m_numRepetitions);
			m_NumberOfRepetitionsTField.setEnabled(true);
			m_Exp.setRunLower(1);
			m_Exp.setRunUpper(m_numRepetitions);
		}

		SplitEvaluator se = null;
		Classifier sec = null;
		if (m_ExpClassificationRBut.isSelected()) {
			se = new ClassifierSplitEvaluator();
			sec = ((ClassifierSplitEvaluator) se).getClassifier();
		} else {
			se = new RegressionSplitEvaluator();
			sec = ((RegressionSplitEvaluator) se).getClassifier();
		}

		// build new ResultProducer
		if (m_ExperimentTypeCBox.getSelectedItem() == TYPE_CROSSVALIDATION_TEXT) {
			CrossValidationResultProducer cvrp = new CrossValidationResultProducer();
			cvrp.setNumFolds(m_numFolds);
			cvrp.setSplitEvaluator(se);

			PropertyNode[] propertyPath = new PropertyNode[2];
			try {
				propertyPath[0] = new PropertyNode(se, new PropertyDescriptor(
						"splitEvaluator", CrossValidationResultProducer.class),
						CrossValidationResultProducer.class);
				propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor(
						"classifier", se.getClass()), se.getClass());
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}

			m_Exp.setResultProducer(cvrp);
			m_Exp.setPropertyPath(propertyPath);
		} else if (m_ExperimentTypeCBox.getSelectedItem() == TYPE_OPENML_TASK_TEXT) {
			TaskResultProducer trp = new TaskResultProducer(apiconnector);

			PropertyNode[] propertyPath = new PropertyNode[2];
			try {
				propertyPath[0] = new PropertyNode(se, new PropertyDescriptor(
						"splitEvaluator", CrossValidationResultProducer.class),
						CrossValidationResultProducer.class);
				propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor(
						"classifier", se.getClass()), se.getClass());
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}

			m_Exp.setResultProducer(trp);
			m_Exp.setPropertyPath(propertyPath);
		} else {
			RandomSplitResultProducer rsrp = new RandomSplitResultProducer();
			rsrp.setRandomizeData(m_ExperimentTypeCBox.getSelectedItem() == TYPE_RANDOMSPLIT_TEXT);
			rsrp.setTrainPercent(m_trainPercent);
			rsrp.setSplitEvaluator(se);

			PropertyNode[] propertyPath = new PropertyNode[2];
			try {
				propertyPath[0] = new PropertyNode(se, new PropertyDescriptor(
						"splitEvaluator", RandomSplitResultProducer.class),
						RandomSplitResultProducer.class);
				propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor(
						"classifier", se.getClass()), se.getClass());
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}

			m_Exp.setResultProducer(rsrp);
			m_Exp.setPropertyPath(propertyPath);

		}

		m_Exp.setUsePropertyIterator(true);
		m_Support.firePropertyChange("", null, null);
	}
	
	/**
	 * Lets user enter username/password/URL.
	 */
	private void chooseURLUsername() {
		String dbaseURL = ((DatabaseResultListener) m_Exp.getResultListener())
				.getDatabaseURL();
		String username = ((DatabaseResultListener) m_Exp.getResultListener())
				.getUsername();
		DatabaseConnectionDialog dbd = new DatabaseConnectionDialog(null,
				dbaseURL, username);
		dbd.setVisible(true);

		// if (dbaseURL == null) {
		if (dbd.getReturnValue() == JOptionPane.CLOSED_OPTION) {
			return;
		}

		((DatabaseResultListener) m_Exp.getResultListener()).setUsername(dbd
				.getUsername());
		((DatabaseResultListener) m_Exp.getResultListener()).setPassword(dbd
				.getPassword());
		((DatabaseResultListener) m_Exp.getResultListener()).setDatabaseURL(dbd
				.getURL());
		((DatabaseResultListener) m_Exp.getResultListener()).setDebug(dbd
				.getDebug());
		m_ResultsDestinationPathTField.setText(dbd.getURL());
	}

	private void chooseUsernamePassword() {
		UsernameDialog ad = new UsernameDialog(null);
		ad.setVisible(true);

		// if (dbaseURL == null) {
		if (ad.getReturnValue() == JOptionPane.CLOSED_OPTION) {
			return;
		}
		if (apiconnector.setCredentials( ad.getUsername(), ad.getPassword())) {
			m_ResultsDestinationPathTField.setText(ad.getUsername());
		} else {
			JOptionPane.showMessageDialog(null, "Username/Password incorrect",
					"Login error", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Lets user browse for a destination file..
	 */
	private void chooseDestinationFile() {

		FileFilter fileFilter = null;
		if (m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) {
			fileFilter = m_csvFileFilter;
		} else {
			fileFilter = m_arffFileFilter;
		}
		m_DestFileChooser.setFileFilter(fileFilter);
		int returnVal = m_DestFileChooser.showSaveDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		m_ResultsDestinationPathTField.setText(m_DestFileChooser.getSelectedFile().toString());
	}
}
