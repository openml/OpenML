
package com.rapidminer.openml.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.openml.util.MissingValueException;
import org.openml.util.OpenMLUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.rapidminer.gui.tools.ResourceDockKey;
import com.rapidminer.openml.task.OpenMLTaskManager;
import com.rapidminer.repository.MalformedRepositoryLocationException;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.XMLException;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;

public class openMLTab extends JPanel implements Dockable, ActionListener {

	private static final long serialVersionUID = -6271613941168656709L;
	private GroupLayout layout;
	private JLabel taskLabel;
	private JTextField taskField;
	private JButton fetchTask;
	private JButton uploadResults;
	private JButton uploadProcess;
	private JTextPane textPane;
	private JScrollPane pane;
	private JLabel taskStatusLabel;

	private final DockKey DOCK_KEY = new ResourceDockKey("openml.openml_tab");

	public static String readFromBundle(String key) {
		String value;
		try {
			ResourceBundle rb = ResourceBundle.getBundle("com.rapidminer.resources.i18n.OpenML");
			value = rb.getString(key);
		} catch (MissingResourceException e) {
			return "???Key:" + key + " bundle is missing???";
		}
		if (value == null) {
			return "???Key:" + key + " Missing in the bundle???";
		}
		return value;
	}

	public openMLTab() {
		layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		taskLabel = new JLabel();
		taskLabel.setText(readFromBundle("gui.dockkey.openml.task_label.label"));
		taskLabel.setToolTipText(readFromBundle("gui.dockkey.openml.task_label.tip"));

		taskField = new JTextField();
		taskField.setToolTipText(readFromBundle("gui.dockkey.openml.task_field.tip"));

		fetchTask = new JButton();
		fetchTask.setText(readFromBundle("gui.dockkey.openml.fetch_task.name"));
		fetchTask.setToolTipText(readFromBundle("gui.dockkey.openml.fetch_task.tip"));
		fetchTask.addActionListener(this);

		uploadProcess = new JButton();
		uploadProcess.setText(readFromBundle("gui.dockkey.openml.upload_process.name"));
		uploadProcess.setToolTipText(readFromBundle("gui.dockkey.openml.upload_process.tip"));
		uploadProcess.addActionListener(this);

		uploadResults = new JButton();
		uploadResults.setText(readFromBundle("gui.dockkey.openml.upload_results.name"));
		uploadResults.setToolTipText(readFromBundle("gui.dockkey.openml.upload_results.tip"));
		uploadResults.addActionListener(this);

		taskStatusLabel = new JLabel();
		taskStatusLabel.setText(readFromBundle("gui.dockkey.openml.task_fetch_status.name"));

		textPane = new JTextPane();
		textPane.setEditable(false);

		pane = new JScrollPane(textPane);

		//@formatter:off
		layout.setHorizontalGroup(
				layout.createParallelGroup().addGroup(
							layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup().addComponent(taskLabel).addComponent(fetchTask))
							.addGroup(layout.createParallelGroup().addComponent(taskField).addGroup(layout.createSequentialGroup().addComponent(uploadProcess).addComponent(uploadResults)))
						  ).addComponent(taskStatusLabel)
						   .addComponent(pane));
		
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(taskLabel).addComponent(taskField).addGap(12))
				.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(fetchTask).addGroup(layout.createParallelGroup().addComponent(uploadProcess).addComponent(uploadResults)))
				.addComponent(taskStatusLabel)
				.addComponent(pane)	);
		//@formatter:on

	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public DockKey getDockKey() {
		return DOCK_KEY;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fetchTask) {
			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						textPane.setText("");
						textPane.setText("Fetching the task from the OpenML Server");
						Document task = OpenMLTaskManager.fetchTask(taskField.getText());
						textPane.setText(textPane.getText() + "\n" + "Task fetched successfully.");

						textPane.setText(textPane.getText() + "\n" + "fetching the data for the task.");
						Integer fetchedTaskId = OpenMLUtil.getTaskId(task);
						if (fetchedTaskId.toString().equals(taskField.getText())) {
							textPane.setText(textPane.getText() + "\n" + "Task is valid");
						} else {
							LogService.getRoot().warning("Please provide a valid openML Task ID");
							textPane.setText(textPane.getText() + "\n" + "Please provide a valid openML Task ID");
							throw new XMLException("Invalid Task");
						}
						OpenMLTaskManager.fetchDataForTask(task);
						textPane.setText(textPane.getText() + "\n" + "Data for the task fetched successfully.");

						textPane.setText(textPane.getText() + "\n" + "fetching the metadata for the task.");
						OpenMLTaskManager.fetchMetadataForTask(task);
						textPane.setText(textPane.getText() + "\n" + "Metadata for the task fetched successfully.");

						textPane.setText(textPane.getText() + "\n" + "Preparing the process for the OpenML Task");
						OpenMLTaskManager.prepareProcessforTask(task);
						textPane.setText(textPane.getText() + "\n" + "process for the OpenML Task prepared successfully.");
						String openMLDir = ParameterService.getParameterValue("OpenML Directory");
						textPane.setText(textPane.getText() + "\n" + "Template process is saved in the location " + openMLDir + "Tasks/" + fetchedTaskId + "/execute_task_" + fetchedTaskId);

					} catch (IOException e1) {
						//TODO bundle error messages
						LogService.getRoot().warning("Please provide a valid openML Task ID");
						textPane.setText(textPane.getText() + "\n" + "Please provide a valid openML Task ID");
						e1.printStackTrace();
					} catch (JAXBException e1) {
						LogService.getRoot().warning("Please provide a valid openML Task ID");
						textPane.setText(textPane.getText() + "\n" + "Please provide a valid openML Task ID");
						e1.printStackTrace();
					} catch (MalformedRepositoryLocationException e1) {
						LogService.getRoot().warning("Error in fetching the metadata for the task");
						textPane.setText(textPane.getText() + "\n" + "Please provide a valid openML Task ID");
						e1.printStackTrace();
					} catch (RepositoryException e1) {
						LogService.getRoot().warning("Error in fetching the metadata for the task");
						e1.printStackTrace();
					} catch (XMLException e1) {
						LogService.getRoot().warning("Error in preparing the process for the OpenML Task");
						e1.printStackTrace();
					} catch (MissingValueException e){
						LogService.getRoot().severe("Invalid Task configuration returned by the server, contact OpenML Server administrator");
					} catch (SAXException e){
						LogService.getRoot().severe("problem in parsing the task XML");
					} catch (ParserConfigurationException e){
						LogService.getRoot().severe("problem in parsing the task XML");
					}

				}

			}).start();

		} else if (e.getSource() == uploadResults) {

		}

	}

}
