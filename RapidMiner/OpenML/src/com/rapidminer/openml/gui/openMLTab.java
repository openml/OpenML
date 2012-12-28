
package com.rapidminer.openml.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.xml.bind.JAXBException;

import com.rapidminer.gui.tools.ResourceDockKey;
import com.rapidminer.openml.task.OpenMLTaskManager;
import com.rapidminer.openml.task.jaxb.beans.Task;
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
	private JTextPane textPane;
	private JScrollPane pane;
	private JLabel taskStatusLabel;

	private final DockKey DOCK_KEY = new ResourceDockKey("opeml.openml_tab");

	public openMLTab() {
		layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		taskLabel = new JLabel();
		taskLabel.setText("Task-id");

		taskField = new JTextField();
		fetchTask = new JButton("Fetch Task");
		fetchTask.addActionListener(this);
		uploadResults = new JButton("upload Results");
		
		taskStatusLabel = new JLabel("Task status/log");

		textPane = new JTextPane();
		textPane.setEditable(false);

		pane = new JScrollPane(textPane);

		//@formatter:off
		layout.setHorizontalGroup(
				layout.createParallelGroup().addGroup(
							layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup().addComponent(taskLabel).addComponent(fetchTask))
							.addGroup(layout.createParallelGroup().addComponent(taskField).addComponent(uploadResults))
						  ).addComponent(taskStatusLabel)
						   .addComponent(pane));
		
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(taskLabel).addComponent(taskField).addGap(12))
				.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(fetchTask).addComponent(uploadResults))
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
			try {
				textPane.setText("");
				textPane.setText("Fetching the task from the OpenML Server");
				Task task = OpenMLTaskManager.fetchTask(taskField.getText());
				textPane.setText(textPane.getText()+"\n"+"Task fetched successfully.");
				
				textPane.setText(textPane.getText()+"\n"+"fetching the data for the task.");
				Integer fetchedTaskId = task.getTaskId();
				if (fetchedTaskId.toString().equals(taskField.getText())) {
					textPane.setText(textPane.getText()+"\n"+"Task is valid");
				} else {
					LogService.getRoot().warning("Please provide a valid openML Task ID");
					textPane.setText(textPane.getText()+"\n"+"Please provide a valid openML Task ID");
					throw new XMLException("Invalid Task");
				}
				OpenMLTaskManager.fetchDataForTask(task);
				textPane.setText(textPane.getText()+"\n"+"Data for the task fetched successfully.");
				
				textPane.setText(textPane.getText()+"\n"+"fetching the metadata for the task.");
				OpenMLTaskManager.fetchMetadataForTask(task);
				textPane.setText(textPane.getText()+"\n"+"Metadata for the task fetched successfully.");
				
				textPane.setText(textPane.getText()+"\n"+"Preparing the process for the OpenML Task");
				OpenMLTaskManager.prepareProcessforTask(task);
				textPane.setText(textPane.getText()+"\n"+"process for the OpenML Task prepared successfully.");
				String openMLDir = ParameterService.getParameterValue("OpenML Directory");
				textPane.setText(textPane.getText()+"\n"+"Template process is saved in the location "+openMLDir+ "Tasks/" + task.getTaskId() + "/execute_task_" + task.getTaskId());
				
							
				
			} catch (IOException e1) {
				//TODO bundle error messages
				LogService.getRoot().warning("Please provide a valid openML Task ID");
				textPane.setText(textPane.getText()+"\n"+"Please provide a valid openML Task ID");
				e1.printStackTrace();
			} catch (JAXBException e1) {
				LogService.getRoot().warning("Please provide a valid openML Task ID");
				textPane.setText(textPane.getText()+"\n"+"Please provide a valid openML Task ID");
				e1.printStackTrace();
			} catch (MalformedRepositoryLocationException e1) {
				LogService.getRoot().warning("Error in fetching the metadata for the task");
				textPane.setText(textPane.getText()+"\n"+"Please provide a valid openML Task ID");
				e1.printStackTrace();
			} catch (RepositoryException e1) {
				LogService.getRoot().warning("Error in fetching the metadata for the task");
				e1.printStackTrace();
			} catch (XMLException e1) {
				LogService.getRoot().warning("Error in preparing the process for the OpenML Task");
				e1.printStackTrace();
			}
		}

	}

}
