package org.openml.weka.gui;

import weka.gui.experiment.SetupPanel;

public class OpenmlSetupPanel extends SetupPanel {

	/**
	 * this class represents the advanced setup panel of Weka. the sole reason
	 * that it exists is because the removeNotesFrame function is protected in
	 * "SetupPanel", and we need to access it.
	 * 
	 */
	private static final long serialVersionUID = 860462461269081046L;

	/**
	 * Deletes the notes frame.
	 */
	protected void removeNotesFrame() {
		m_NotesFrame.setVisible(false);
	}
}
