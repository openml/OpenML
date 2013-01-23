/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2011
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   Dec 13, 2012 (Patrick Winter): created
 */
package org.openml.knime.taskconfig;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * Configuration for the node.
 * 
 * 
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class TaskConfigConfiguration {

    private String m_taskid;

    /**
     * @return the taskid
     */
    public String getTaskid() {
        return m_taskid;
    }

    /**
     * @param taskid the taskid to set
     */
    public void setTaskid(final String taskid) {
        m_taskid = taskid;
    }

    /**
     * Save this configuration into the settings.
     * 
     * 
     * @param settings The <code>NodeSettings</code> to write to
     */
    void save(final NodeSettingsWO settings) {
        settings.addString("taskid", m_taskid);
    }

    /**
     * Load this configuration from the settings.
     * 
     * 
     * @param settings The <code>NodeSettings</code> to read from
     */
    void load(final NodeSettingsRO settings) {
        m_taskid = settings.getString("taskid", "");
    }

    /**
     * Load and validate this configuration from the settings.
     * 
     * 
     * @param settings The <code>NodeSettings</code> to read from
     * @throws InvalidSettingsException If one of the settings is not valid
     */
    void loadAndValidate(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_taskid = settings.getString("taskid");
        validate(m_taskid, "Task ID");
    }

    /**
     * Checks if the string is not null or empty.
     * 
     * 
     * @param string The string to check
     * @param settingName The name of the setting
     * @throws InvalidSettingsException If the string is null or empty
     */
    private void validate(final String string, final String settingName)
            throws InvalidSettingsException {
        if (string == null || string.length() == 0) {
            throw new InvalidSettingsException(settingName + " missing");
        }
    }

}
