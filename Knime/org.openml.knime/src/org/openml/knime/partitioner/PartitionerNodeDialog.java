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
 *   Oct 30, 2012 (Patrick Winter): created
 */
package org.openml.knime.partitioner;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.FlowVariableModelButton;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.workflow.FlowVariable;

/**
 * <code>NodeDialog</code> for the node.
 * 
 * 
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
class PartitionerNodeDialog extends NodeDialogPane {

    private JLabel m_repeatLabel;

    private FlowVariableModelButton m_repeat;

    private JLabel m_foldLabel;

    private FlowVariableModelButton m_fold;

    /**
     * New pane for configuring the node dialog.
     */
    public PartitionerNodeDialog() {
        m_repeatLabel = new JLabel("Repeat number");
        FlowVariableModel repeatfvm =
                createFlowVariableModel("repeat", FlowVariable.Type.INTEGER);
        m_repeat = new FlowVariableModelButton(repeatfvm);
        m_foldLabel = new JLabel("Fold number");
        FlowVariableModel foldfvm =
                createFlowVariableModel("fold", FlowVariable.Type.INTEGER);
        m_fold = new FlowVariableModelButton(foldfvm);
        addTab("Options", initLayout());
    }

    /**
     * Create and initialize the panel for this dialog.
     * 
     * 
     * @return The initialized panel
     */
    private JPanel initLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        resetGBC(gbc);
        JPanel panel = new JPanel(new GridBagLayout());
        gbc.weightx = 0;
        panel.add(m_repeatLabel, gbc);
        gbc.gridx++;
        panel.add(m_repeat, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(m_foldLabel, gbc);
        gbc.gridx++;
        panel.add(m_fold, gbc);
        return panel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings)
            throws InvalidSettingsException {
        PartitionerConfiguration config = new PartitionerConfiguration();
        config.save(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
            final PortObjectSpec[] specs) throws NotConfigurableException {
        PartitionerConfiguration config = new PartitionerConfiguration();
        config.load(settings);
    }

    /**
     * Reset the grid bag constraints to useful defaults.
     * 
     * 
     * The defaults are all insets to 5, anchor northwest, fill both, x and y 0
     * and x and y weight 0.
     * 
     * @param gbc The constraints object.
     */
    public static void resetGBC(final GridBagConstraints gbc) {
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
    }

}
