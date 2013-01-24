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
 *   Jan 11, 2013 (Patrick Winter): created
 */
package org.openml.knime.uploader;

import java.io.Serializable;
import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.Config;
import org.knime.core.node.config.base.AbstractConfigEntry;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;
import org.openml.implementation.Components;
import org.openml.implementation.Implementation;
import org.openml.implementation.ParameterSetting;

/**
 * Represents information contained in a workflow.
 * 
 * 
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class WorkflowDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2607620130946020757L;

    private static final String USERNODE = "OpenML Workflow";

    private String m_name;

    private String[] m_annotations;

    private NodeDescription[] m_nodes;

    /**
     * Creates a workflow description based on the information in the given
     * workflow manager.
     * 
     * 
     * @param wfm Workflow manager containing the information
     */
    public WorkflowDescription(final WorkflowManager wfm) {
        m_name = wfm.getName();
        m_annotations = new String[wfm.getWorkflowAnnotations().size()];
        int i = 0;
        for (WorkflowAnnotation annotation : wfm.getWorkflowAnnotations()) {
            m_annotations[i++] = annotation.getText();
        }
        m_nodes = new NodeDescription[wfm.getNodeContainers().size()];
        i = 0;
        for (NodeContainer nodeContainer : wfm.getNodeContainers()) {
            m_nodes[i++] = new NodeDescription(nodeContainer);
        }
    }

    /**
     * Returns the node configured by the user.
     * 
     * 
     * @param wfm WorkflowManager
     * @return User configured node
     */
    public static NodeDescription getUserNode(final WorkflowManager wfm) {
        for (NodeContainer nodeContainer : wfm.getNodeContainers()) {
            if (nodeContainer.getName().equals(USERNODE)) {
                return new NodeDescription(nodeContainer);
            }
        }
        return null;
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @return the annotations
     */
    public String[] getAnnotations() {
        return m_annotations;
    }

    /**
     * @return the nodes
     */
    public NodeDescription[] getNodes() {
        return m_nodes;
    }

    /**
     * Represents information contained in a node.
     * 
     * 
     * @author Patrick Winter, KNIME.com, Zurich, Switzerland
     */
    public static class NodeDescription implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -3794934308779132724L;

        private String m_name;

        private String m_annotation;

        private boolean m_metanode;

        private String m_description;

        private NodeDescription[] m_nodes;

        private SettingDescription[] m_settings;

        /**
         * Creates a node description based on the information in the given node
         * container.
         * 
         * 
         * @param nodeContainer Node container containing the information
         */
        public NodeDescription(final NodeContainer nodeContainer) {
            m_name = nodeContainer.getName();
            m_annotation = nodeContainer.getNodeAnnotation().getText();
            if (nodeContainer instanceof SingleNodeContainer) {
                m_metanode = false;
                SingleNodeContainer snc = (SingleNodeContainer)nodeContainer;
                m_description = snc.getXMLDescription().getTextContent();
                try {
                    NodeSettings settings = new NodeSettings(m_name);
                    nodeContainer.getParent().saveNodeSettings(
                            nodeContainer.getID(), settings);
                    Config config =
                            settings.getNodeSettings(org.knime.core.node.Node.CFG_MODEL);
                    Set<String> keys = config.keySet();
                    m_settings = new SettingDescription[keys.size()];
                    int i = 0;
                    for (String key : keys) {
                        AbstractConfigEntry entry = config.getEntry(key);
                        m_settings[i++] = new SettingDescription(key, entry);
                    }
                } catch (InvalidSettingsException e) {
                    m_settings = new SettingDescription[0];
                }
            } else if (nodeContainer instanceof WorkflowManager) {
                m_metanode = true;
                WorkflowManager wfm = (WorkflowManager)nodeContainer;
                m_nodes = new NodeDescription[wfm.getNodeContainers().size()];
                int i = 0;
                for (NodeContainer node : wfm.getNodeContainers()) {
                    m_nodes[i++] = new NodeDescription(node);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NodeDescription)) {
                return false;
            }
            NodeDescription node = (NodeDescription)obj;
            if (!m_name.equals(node.m_name)
                    || !m_annotation.equals(node.m_annotation)
                    || this.m_metanode != node.m_metanode) {
                return false;
            }
            if (m_metanode) {
                if (m_nodes.length != node.m_nodes.length) {
                    return false;
                }
                for (int i = 0; i < m_nodes.length; i++) {
                    if (!m_nodes[i].equals(node.m_nodes[i])) {
                        return false;
                    }
                }
            } else {
                for (int i = 0; i < m_settings.length; i++) {
                    if (!m_settings[i].equals(node.m_settings[i])) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Exports the node into the implementation object.
         * 
         * 
         * @param impl The implementation object
         */
        public void exportNode(final Implementation impl) {
            impl.setName(m_name);
            impl.setDescription(m_annotation);
            if (m_metanode && m_nodes.length > 0) {
                Components comps = impl.addNewComponents();
                for (int i = 0; i < m_nodes.length; i++) {
                    Implementation impl2 = comps.addNewImplementation();
                    m_nodes[i].exportNode(impl2);
                }
            } else {
                for (int i = 0; i < m_settings.length; i++) {
                    ParameterSetting param = impl.addNewParameterSetting();
                    param.setParameter(m_settings[i].getKey());
                    param.setValue(m_settings[i].getValue());
                }
            }
        }

        /**
         * @return the name
         */
        public String getName() {
            return m_name;
        }

        /**
         * @return the annotation
         */
        public String getAnnotation() {
            return m_annotation;
        }

        /**
         * @return is metanode
         */
        public boolean isMetanode() {
            return m_metanode;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return m_description;
        }

        /**
         * @return the nodes
         */
        public NodeDescription[] getNodes() {
            return m_nodes;
        }

        /**
         * @return the settings
         */
        public SettingDescription[] getSettings() {
            return m_settings;
        }

    }

    /**
     * Represents information contained in a setting.
     * 
     * 
     * @author Patrick Winter, KNIME.com, Zurich, Switzerland
     */
    public static class SettingDescription implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -5272094071277588310L;

        private String m_key;

        private boolean m_complex;

        private String m_value;

        private SettingDescription[] m_settings;

        /**
         * Creates a setting description based on the information in the given
         * config entry container.
         * 
         * 
         * @param key Name of this setting
         * @param entry The config entry containing the information
         */
        public SettingDescription(final String key,
                final AbstractConfigEntry entry) {
            m_key = key;
            if (entry instanceof Config) {
                m_complex = true;
                Config config = (Config)entry;
                Set<String> keys = config.keySet();
                m_settings = new SettingDescription[keys.size()];
                int i = 0;
                for (String innerKey : keys) {
                    AbstractConfigEntry innerEntry = config.getEntry(innerKey);
                    m_settings[i++] =
                            new SettingDescription(innerKey, innerEntry);
                }
            } else {
                m_complex = false;
                m_value = entry.toStringValue();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SettingDescription)) {
                return false;
            }
            SettingDescription setting = (SettingDescription)obj;
            if (m_key != setting.m_key || m_complex != setting.m_complex) {
                return false;
            }
            if (m_complex) {
                if (m_settings.length != setting.m_settings.length) {
                    return false;
                }
                for (int i = 0; i < m_settings.length; i++) {
                    if (!m_settings[i].equals(setting.m_settings[i])) {
                        return false;
                    }
                }
            } else {
                if (m_value != setting.m_value) {
                    return false;
                }
            }
            return true;
        }

        /**
         * @return the key
         */
        public String getKey() {
            return m_key;
        }

        /**
         * @return is complex
         */
        public boolean isComplex() {
            return m_complex;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return m_value;
        }

        /**
         * @return the settings
         */
        public SettingDescription[] getSettings() {
            return m_settings;
        }

    }

    /**
     * Exports this workflow description to the OpenML implementation XML.
     * 
     * 
     * @param impl Implementation element of the XML
     */
    public void exportToOpenMLXML(final Implementation impl) {
        addSettingsForNodes(impl, m_nodes);
    }

    private static void addSettingsForNodes(final Implementation impl,
            final NodeDescription[] nodesArray) {
        for (int i = 0; i < nodesArray.length; i++) {
            if (nodesArray[i].isMetanode()) {
                addSettingsForNodes(impl, nodesArray[i].getNodes());
            } else {
                addSettings(impl, nodesArray[i].getSettings());
            }
        }
    }

    private static void addSettings(final Implementation impl,
            final SettingDescription[] settingArray) {
        for (int i = 0; i < settingArray.length; i++) {
            String key = settingArray[i].getKey();
            if (!key.endsWith("_Internals")) {
                if (settingArray[i].isComplex()) {
                    addSettings(impl, settingArray[i].getSettings());
                } else {
                    ParameterSetting setting = impl.addNewParameterSetting();
                    setting.setParameter(key);
                    setting.setValue(settingArray[i].getValue());
                }
            }
        }
    }

    /**
     * Exports the workflow into the implementation object.
     * 
     * 
     * @param impl The implementation object
     */
    public void exportWorkflow(final Implementation impl) {
        String desc = "";
        for (int i = 0; i < m_annotations.length; i++) {
            desc += m_annotations[i] + "\n";
        }
        impl.setDescription(desc);
        if (m_nodes.length > 0) {
            Components comps = impl.addNewComponents();
            for (int i = 0; i < m_nodes.length; i++) {
                Implementation impl2 = comps.addNewImplementation();
                m_nodes[i].exportNode(impl2);
            }
        }
    }
}
