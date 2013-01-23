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
 *   Dec 21, 2012 (Patrick Winter): created
 */
package org.openml.knime.taskconfig;

import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.knime.core.node.NodeView;
import org.openml.dataSetDescription.DataSetDescriptionDocument;
import org.openml.knime.OpenMLWebservice;
import org.openml.util.OpenMLUtil;
import org.w3c.dom.Document;

import xmleditorkit.XMLDocument;
import xmleditorkit.XMLEditorKit;

/**
 * View that shows the files to the configured OpenML task.
 * 
 * 
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class TaskConfigNodeView extends NodeView<TaskConfigNodeModel> {

    /**
     * Create the view object.
     * 
     * 
     * @param nodeModel The node model
     */
    protected TaskConfigNodeView(final TaskConfigNodeModel nodeModel) {
        super(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
        // Not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
        // Not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        TaskConfigNodeModel nodeModel = getNodeModel();
        JTabbedPane tabbedPane = new JTabbedPane();
        JEditorPane taskEditorPane = new JEditorPane();
        taskEditorPane.setEditorKit(new XMLEditorKit());
        taskEditorPane.setEditable(false);
        JEditorPane datasetdescEditorPane = new JEditorPane();
        datasetdescEditorPane.setEditorKit(new XMLEditorKit());
        datasetdescEditorPane.setEditable(false);
        JEditorPane datasetEditorPane = new JEditorPane();
        datasetEditorPane.setEditable(false);
        JEditorPane splitEditorPane = new JEditorPane();
        splitEditorPane.setEditable(false);
        try {
            URL taskURL = OpenMLWebservice.getTaskURL(nodeModel.getTaskid());
            taskEditorPane.read(taskURL.openStream(), new XMLDocument());
            Document taskDoc = OpenMLUtil.readDocumentfromURL(taskURL);
            int datasetID = OpenMLUtil.getDataSetId(taskDoc);
            URL datasetdescURL = OpenMLWebservice.getDatasetDescURL(datasetID);
            datasetdescEditorPane.read(datasetdescURL.openStream(),
                    new XMLDocument());
            DataSetDescriptionDocument datasetdescDoc =
                    DataSetDescriptionDocument.Factory.parse(datasetdescURL
                            .openStream());
            URL datasetURL =
                    new URL(datasetdescDoc.getDataSetDescription().getUrl());
            datasetEditorPane.read(
                    new SizeLimitInputStream(datasetURL.openStream(), 4096),
                    null);
            URL splitsURL =
                    new URL(
                            "http://expdb.cs.kuleuven.be/expdb/api/?f=openml.task.splits");
            splitEditorPane.read(
                    new SizeLimitInputStream(splitsURL.openStream(), 4096),
                    null);
        } catch (Exception e) {
            // Do not display anything
        }
        tabbedPane.add("Task", new JScrollPane(taskEditorPane,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        tabbedPane.add("Data-Set-Description", new JScrollPane(
                datasetdescEditorPane,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        tabbedPane.add("Data-Set", new JScrollPane(datasetEditorPane,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        tabbedPane.add("Splits", new JScrollPane(splitEditorPane,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        setComponent(tabbedPane);
    }

}
