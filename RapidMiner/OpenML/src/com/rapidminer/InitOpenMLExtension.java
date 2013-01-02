/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2010 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package com.rapidminer;

import com.rapidminer.gui.MainFrame;
import com.rapidminer.openml.gui.openMLTab;
import com.rapidminer.parameter.ParameterTypeRepositoryLocation;
import com.rapidminer.tools.ParameterService;

/**
 * Iniatilizes the openML Extension. 
 * Does nearly nothing. Well ... Does nothing.
 * 
 * @author Venkatesh Umaashankar
 * @version $Id$
 */
public class InitOpenMLExtension {

	public static void initPlugin() {}

	public static void initSplashTexts() {

	}

	public static void initGui(MainFrame mainframe) {
		final openMLTab simpleWindow = new openMLTab();
		mainframe.getDockingDesktop().registerDockable(simpleWindow);
		ParameterService.registerParameter(new ParameterTypeRepositoryLocation("OpenML Directory", "Directory for stroring openML Tasks", false, true, false), "openML");
	}
}
