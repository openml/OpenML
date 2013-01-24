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
 *   Sep 5, 2012 (Patrick Winter): created
 */
package org.openml.knime.partitioner;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

/**
 * This is the model implementation.
 * 
 * 
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class PartitionerNodeModel extends NodeModel {

    private static final String TRAIN = "TRAIN";

    private static final String TEST = "TEST";

    private PartitionerConfiguration m_config;

    /**
     * Constructor for the node model.
     */
    protected PartitionerNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE, BufferedDataTable.TYPE},
                new PortType[]{BufferedDataTable.TYPE, BufferedDataTable.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        BufferedDataTable splitTable = (BufferedDataTable)inObjects[1];
        Set<String> trainset = getSplit(TRAIN, splitTable);
        Set<String> testset = getSplit(TEST, splitTable);
        BufferedDataTable inTable = (BufferedDataTable)inObjects[0];
        BufferedDataContainer trainContainer =
                exec.createDataContainer(inTable.getDataTableSpec());
        BufferedDataContainer testContainer =
                exec.createDataContainer(inTable.getDataTableSpec());
        for (DataRow row : inTable) {
            String key = row.getKey().getString();
            if (trainset.contains(key)) {
                trainContainer.addRowToTable(row);
            }
            if (testset.contains(key)) {
                testContainer.addRowToTable(row);
            }
        }
        trainContainer.close();
        testContainer.close();
        return new PortObject[]{trainContainer.getTable(),
                testContainer.getTable()};
    }

    private Set<String> getSplit(final String splitType,
            final BufferedDataTable splitTable) {
        Set<String> result = new HashSet<String>();
        int splitFold = m_config.getFold();
        int splitRepeat = m_config.getRepeat();
        DataTableSpec spec = splitTable.getDataTableSpec();
        int typeIndex = spec.findColumnIndex("type");
        int rowidIndex = spec.findColumnIndex("rowid");
        int foldIndex = spec.findColumnIndex("fold");
        int repeatIndex = spec.findColumnIndex("repeat");
        for (DataRow row : splitTable) {
            String type =
                    ((StringValue)row.getCell(typeIndex)).getStringValue();
            int fold =
                    (int)((DoubleValue)row.getCell(foldIndex)).getDoubleValue();
            int repeat =
                    (int)((DoubleValue)row.getCell(repeatIndex))
                            .getDoubleValue();
            if (type.equals(splitType) && fold == splitFold
                    && repeat == splitRepeat) {
                String rowid =
                        ((StringValue)row.getCell(rowidIndex)).getStringValue();
                result.add(rowid);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // Not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        return new PortObjectSpec[]{inSpecs[0], inSpecs[0]};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        if (m_config != null) {
            m_config.save(settings);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        PartitionerConfiguration config = new PartitionerConfiguration();
        config.loadAndValidate(settings);
        m_config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        new PartitionerConfiguration().loadAndValidate(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // Not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // Not used
    }

}
