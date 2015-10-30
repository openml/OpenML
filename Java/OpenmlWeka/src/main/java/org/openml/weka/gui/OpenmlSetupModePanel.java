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
 *    SetupModePanel.java
 *    Copyright (C) 2002-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package org.openml.weka.gui;

import java.awt.BorderLayout;
import weka.gui.experiment.SetupModePanel;

/**
 * This panel switches between simple and advanced experiment setup panels.
 * 
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 8034 $
 */
public class OpenmlSetupModePanel extends SetupModePanel {

	/** for serialization */
	private static final long serialVersionUID = -3758035565520727822L;

	/**
	 * Creates the setup panel with no initial experiment.
	 */
	public OpenmlSetupModePanel() {
		super();
		m_defaultPanel = new OpenmlSimpleSetupPanel();
		m_advancedPanel = new OpenmlSetupPanel();
		
		m_defaultPanel.setModePanel(this);
		add(m_defaultPanel, BorderLayout.CENTER);
	}
}
