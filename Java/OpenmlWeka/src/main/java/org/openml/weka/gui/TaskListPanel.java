package org.openml.weka.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.Task;
import org.openml.weka.experiment.TaskBasedExperiment;

import weka.core.Utils;
import weka.core.ClassDiscovery.StringCompare;
import weka.core.converters.ConverterUtils;
import weka.core.converters.Saver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.Experiment;
import weka.gui.JListHelper;
import weka.gui.ViewerDialog;
import weka.gui.experiment.DatasetListPanel;

public class TaskListPanel extends DatasetListPanel {

	private static final long serialVersionUID = 1L;

	/** The experiment to set the dataset or task list of. */
	protected Experiment m_Exp;

	private boolean datasetBased = true;
	
	private final ApiConnector apiconnector;

	public TaskListPanel(Experiment exp) {
		this();
		setExperiment(exp);
	}

	public TaskListPanel() {
		super();
		
		Config config = new Config();
		if( config.getServer() != null ) {
			apiconnector = new ApiConnector( config.getServer() );
		} else { 
			apiconnector = new ApiConnector();
		}
	}

	/**
	 * sets the state of the buttons according to the selection state of the
	 * JList.
	 * 
	 * @param e
	 *            the event
	 */
	private void setButtons(ListSelectionEvent e) {
		if ((e == null) || (e.getSource() == m_List)) {
			m_DeleteBut.setEnabled(m_List.getSelectedIndex() > -1);
			m_EditBut.setEnabled(datasetBased
					&& m_List.getSelectedIndices().length == 1);
			m_UpBut.setEnabled(JListHelper.canMoveUp(m_List));
			m_DownBut.setEnabled(JListHelper.canMoveDown(m_List));
		}
	}

	/**
	 * Tells the panel to act on a new experiment.
	 * 
	 * @param exp
	 *            a value of type 'TaskBasedExperiment'
	 */
	@Override
	public void setExperiment(Experiment exp) {
		m_Exp = exp;
		m_List.setModel(getTasksControlled(m_Exp));
		m_AddBut.setEnabled(true);
		setButtons(null);
	}

	public void setMode(boolean datasetBased) {
		this.datasetBased = datasetBased;

		if (datasetBased) {
			m_relativeCheck.setEnabled(true);
			m_List.setModel(m_Exp.getDatasets());
			setBorder(BorderFactory
					.createTitledBorder("Datasets"));
		} else {
			m_relativeCheck.setEnabled(false);
			m_List.setModel(getTasksControlled(m_Exp));
			setBorder(BorderFactory.createTitledBorder("Tasks"));
		}
	}

	/**
	 * Handle actions when buttons get pressed.
	 * 
	 * @param e
	 *            a value of type 'ActionEvent'
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == m_AddBut) {
			if (datasetBased)
				actionPerformedDatasetBasedAdd(e);
			else
				actionPerformedTaskBasedAdd(e);
		} else if (e.getSource() == m_DeleteBut) {
			// Delete the selected files
			if (datasetBased) {
				int[] selected = m_List.getSelectedIndices();
				if (selected != null) {
					for (int i = selected.length - 1; i >= 0; i--) {
						int current = selected[i];
						m_Exp.getDatasets().removeElementAt(current);
						if (m_Exp.getDatasets().size() > current) {
							m_List.setSelectedIndex(current);
						} else {
							m_List.setSelectedIndex(current - 1);
						}
					}
				}
			} else {
				int[] selected = m_List.getSelectedIndices();
				if (selected != null) {
					for (int i = selected.length - 1; i >= 0; i--) {
						int current = selected[i];
						getTasksControlled(m_Exp).removeElementAt(current);
						if (getTasksControlled(m_Exp).size() > current) {
							m_List.setSelectedIndex(current);
						} else {
							m_List.setSelectedIndex(current - 1);
						}
					}
				}
			}
			setButtons(null);
		} else if (e.getSource() == m_EditBut) {
			if (datasetBased)
				actionPerformedDatasetBasedEdit(e);
			else
				actionPerformedTaskBasedEdit(e);
		} else if (e.getSource() == m_UpBut) {
			JListHelper.moveUp(m_List);
		} else if (e.getSource() == m_DownBut) {
			JListHelper.moveDown(m_List);
		}
	}

	public void actionPerformedDatasetBasedAdd(ActionEvent e) {
		boolean useRelativePaths = m_relativeCheck.isSelected();
		// Let the user select an arff file from a file chooser
		int returnVal = m_FileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (m_FileChooser.isMultiSelectionEnabled()) {
				File[] selected = m_FileChooser.getSelectedFiles();
				for (int i = 0; i < selected.length; i++) {
					if (selected[i].isDirectory()) {
						Vector files = new Vector();
						getFilesRecursively(selected[i], files);

						// sort the result
						Collections.sort(files, new StringCompare());

						for (int j = 0; j < files.size(); j++) {
							File temp = (File) files.elementAt(j);
							if (useRelativePaths) {
								try {
									temp = Utils.convertToRelativePath(temp);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
							m_Exp.getDatasets().addElement(temp);
						}
					} else {
						File temp = selected[i];
						if (useRelativePaths) {
							try {
								temp = Utils.convertToRelativePath(temp);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
						m_Exp.getDatasets().addElement(temp);
					}
				}
				setButtons(null);
			} else {
				if (m_FileChooser.getSelectedFile().isDirectory()) {
					Vector files = new Vector();
					getFilesRecursively(m_FileChooser.getSelectedFile(), files);

					// sort the result
					Collections.sort(files, new StringCompare());

					for (int j = 0; j < files.size(); j++) {
						File temp = (File) files.elementAt(j);
						if (useRelativePaths) {
							try {
								temp = Utils.convertToRelativePath(temp);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
						m_Exp.getDatasets().addElement(temp);
					}
				} else {
					File temp = m_FileChooser.getSelectedFile();
					if (useRelativePaths) {
						try {
							temp = Utils.convertToRelativePath(temp);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					m_Exp.getDatasets().addElement(temp);
				}
				setButtons(null);
			}
		}
	}

	public void actionPerformedDatasetBasedEdit(ActionEvent e) {
		// Delete the selected files
		int selected = m_List.getSelectedIndex();
		if (selected != -1) {
			ViewerDialog dialog = new ViewerDialog(null);
			String filename = m_List.getSelectedValue().toString();
			int result;
			try {
				DataSource source = new DataSource(filename);
				result = dialog.showDialog(source.getDataSet());
				// nasty workaround for Windows regarding locked files:
				// if file Reader in Loader is not closed explicitly, we
				// cannot
				// overwrite the file.
				source = null;
				System.gc();
				// workaround end
				if ((result == ViewerDialog.APPROVE_OPTION)
						&& (dialog.isChanged())) {
					result = JOptionPane
							.showConfirmDialog(
									this,
									"File was modified - save changes?");
					if (result == JOptionPane.YES_OPTION) {
						Saver saver = ConverterUtils.getSaverForFile(filename);
						saver.setFile(new File(filename));
						saver.setInstances(dialog.getInstances());
						saver.writeBatch();
					}
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(
					this, "Error loading file '" + filename + "':\n" + ex.toString(),
					"Error loading file",
					JOptionPane.INFORMATION_MESSAGE );
			}
		}
		setButtons(null);
	}

	public void actionPerformedTaskBasedAdd(ActionEvent e) {
		String s = (String) JOptionPane.showInputDialog(this,
				"A comma-separated list of the task id's from OpenML.org:",
				"OpenML Task id's", JOptionPane.PLAIN_MESSAGE);
		try {
			int[] input_task_ids = Conversion.commaSeparatedStringToIntArray(s);
			for (int i = 0; i < input_task_ids.length; ++i) {
				if (getTasksControlled(m_Exp).contains(new Task(input_task_ids[i])) == false) {
					try {
						Task t = apiconnector.openmlTaskSearch(input_task_ids[i]);
						// download all data necessary for task execution

						if( m_Exp instanceof TaskBasedExperiment ) {
							getTasksControlled(m_Exp).addElement(t);
						} else {
							System.err.println("Could not add task to Queue... ");
						}
					} catch (Exception downloadException) {
						downloadException.printStackTrace();
						JOptionPane
								.showMessageDialog(
										this,
										"There occured an error while downloading (the data of) Task "
												+ input_task_ids[i]
												+ ". Please double check whether this is a legal task id. Otherwise some input data might be missing. ",
										"Task download error",
										JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		} catch (NumberFormatException nfe) {
			JOptionPane
					.showMessageDialog(
							this,
							"Please insert a comma seperated list of task_id's. These are all numbers. ",
							"Wrong input", JOptionPane.ERROR_MESSAGE);
		} catch (NullPointerException npe) {
			// catch quietly. User probably pressed cancel.
		}
	}

	public void actionPerformedTaskBasedEdit(ActionEvent e) {
		System.out.println("TODO, function not yet implemented.");
	}
	
	private DefaultListModel getTasksControlled( Experiment exp ) {
		if( exp instanceof TaskBasedExperiment ) {
			if( datasetBased )
				return exp.getDatasets();
			else 
				return ((TaskBasedExperiment) exp).getTasks();
		} else {
			return exp.getDatasets();
		}
	}
}
