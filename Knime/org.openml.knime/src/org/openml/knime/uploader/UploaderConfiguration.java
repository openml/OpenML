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
package org.openml.knime.uploader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * Configuration for the node.
 * 
 * 
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class UploaderConfiguration {

    private boolean m_uploadWorkflow;

    private String m_version;

    private String m_implements;

    private String m_licence;

    private String m_uploadResult;

    private String m_resultFile;

    private String m_workflowId;

    private String m_dependency;

    private String m_workflowcredentials;

    private NameVariablePair[] m_pairs;

    private String m_creator;

    private String m_contributor;

    private String m_language;

    private String m_description;

    private String m_summary;

    private String m_fullDescription;

    private Reference[] m_references;

    private String m_uploadedWorkflow;

    /**
     * @return the workflowcredentials
     */
    public String getWorkflowcredentials() {
        return m_workflowcredentials;
    }

    /**
     * @param workflowcredentials the workflowcredentials to set
     */
    public void setWorkflowcredentials(final String workflowcredentials) {
        m_workflowcredentials = workflowcredentials;
    }

    /**
     * @return the uploadWorkflow
     */
    public boolean getUploadWorkflow() {
        return m_uploadWorkflow;
    }

    /**
     * @param uploadWorkflow the uploadWorkflow to set
     */
    public void setUploadWorkflow(final boolean uploadWorkflow) {
        m_uploadWorkflow = uploadWorkflow;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return m_version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(final String version) {
        m_version = version;
    }

    /**
     * @return the implements
     */
    public String getImplements() {
        return m_implements;
    }

    /**
     * @param implements1 the implements to set
     */
    public void setImplements(final String implements1) {
        m_implements = implements1;
    }

    /**
     * @return the licence
     */
    public String getLicence() {
        return m_licence;
    }

    /**
     * @param licence the licence to set
     */
    public void setLicence(final String licence) {
        m_licence = licence;
    }

    /**
     * @return the uploadResult
     */
    public String getUploadResult() {
        return m_uploadResult;
    }

    /**
     * @param uploadResult the uploadResult to set
     */
    public void setUploadResult(final String uploadResult) {
        m_uploadResult = uploadResult;
    }

    /**
     * @return the resultFile
     */
    public String getResultFile() {
        return m_resultFile;
    }

    /**
     * @param resultFile the resultFile to set
     */
    public void setResultFile(final String resultFile) {
        m_resultFile = resultFile;
    }

    /**
     * @return the workflowId
     */
    public String getWorkflowId() {
        return m_workflowId;
    }

    /**
     * @param workflowId the workflowId to set
     */
    public void setWorkflowId(final String workflowId) {
        m_workflowId = workflowId;
    }

    /**
     * @return the dependency
     */
    public String getDependency() {
        return m_dependency;
    }

    /**
     * @param dependency the dependency to set
     */
    public void setDependency(final String dependency) {
        m_dependency = dependency;
    }

    /**
     * @return the pairs
     */
    public NameVariablePair[] getPairs() {
        return m_pairs;
    }

    /**
     * @param pairs the pairs to set
     */
    public void setPairs(final NameVariablePair[] pairs) {
        m_pairs = pairs;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return m_creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(final String creator) {
        m_creator = creator;
    }

    /**
     * @return the contributor
     */
    public String getContributor() {
        return m_contributor;
    }

    /**
     * @param contributor the contributor to set
     */
    public void setContributor(final String contributor) {
        m_contributor = contributor;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return m_language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(final String language) {
        m_language = language;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        m_description = description;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return m_summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(final String summary) {
        m_summary = summary;
    }

    /**
     * @return the fullDescription
     */
    public String getFullDescription() {
        return m_fullDescription;
    }

    /**
     * @param fullDescription the fullDescription to set
     */
    public void setFullDescription(final String fullDescription) {
        m_fullDescription = fullDescription;
    }

    /**
     * @return the references
     */
    public Reference[] getReferences() {
        return m_references;
    }

    /**
     * @param references the references to set
     */
    public void setReferences(final Reference[] references) {
        m_references = references;
    }

    /**
     * @return the uploadedWorkflow
     */
    public String getUploadedWorkflow() {
        return m_uploadedWorkflow;
    }

    /**
     * @param uploadedWorkflow the uploadedWorkflow to set
     */
    public void setUploadedWorkflow(final String uploadedWorkflow) {
        m_uploadedWorkflow = uploadedWorkflow;
    }

    /**
     * Save this configuration into the settings.
     * 
     * 
     * @param settings The <code>NodeSettings</code> to write to
     */
    void save(final NodeSettingsWO settings) {
        settings.addBoolean("uploadworkflow", m_uploadWorkflow);
        settings.addString("version", m_version);
        settings.addString("implements", m_implements);
        settings.addString("licence", m_licence);
        settings.addString("uploadresult", m_uploadResult);
        settings.addString("resultfile", m_resultFile);
        settings.addString("workflowid", m_workflowId);
        settings.addString("dependency", m_dependency);
        settings.addString("workflowcredentials", m_workflowcredentials);
        settings.addString("creator", m_creator);
        settings.addString("contributor", m_contributor);
        settings.addString("language", m_language);
        settings.addString("description", m_description);
        settings.addString("summary", m_summary);
        settings.addString("fulldescription", m_fullDescription);
        settings.addString("uploadedworkflow", m_uploadedWorkflow);
        NodeSettingsWO pairs = settings.addNodeSettings("name_variable_pairs");
        if (m_pairs != null) {
            for (int i = 0; i < m_pairs.length; i++) {
                NodeSettingsWO pair =
                        pairs.addNodeSettings(m_pairs[i].getName());
                pair.addString("name", m_pairs[i].getName());
                pair.addString("variable", m_pairs[i].getVariable());
            }
        }
        NodeSettingsWO refs = settings.addNodeSettings("references");
        if (m_references != null) {
            for (int i = 0; i < m_references.length; i++) {
                NodeSettingsWO ref =
                        refs.addNodeSettings(m_references[i].getTitle());
                ref.addString("title", m_references[i].getTitle());
                ref.addString("url", m_references[i].getUrl());
                ref.addString("authors", m_references[i].getAuthors());
                ref.addInt("year", m_references[i].getYear());
                ref.addString("doi", m_references[i].getDoi());
            }
        }
    }

    /**
     * Load this configuration from the settings.
     * 
     * 
     * @param settings The <code>NodeSettings</code> to read from
     */
    void load(final NodeSettingsRO settings) {
        m_workflowcredentials = settings.getString("workflowcredentials", "");
        m_uploadWorkflow = settings.getBoolean("uploadworkflow", false);
        m_version = settings.getString("version", "");
        m_implements = settings.getString("implements", "");
        m_licence = settings.getString("licence", "");
        m_uploadResult =
                settings.getString("uploadresult", UploadPolicies.NO.getName());
        m_resultFile = settings.getString("resultfile", "");
        m_workflowId = settings.getString("workflowid", "");
        m_dependency = settings.getString("dependency", "");
        m_creator = settings.getString("creator", "");
        m_contributor = settings.getString("contributor", "");
        m_language = settings.getString("language", "");
        m_description = settings.getString("description", "");
        m_summary = settings.getString("summary", "");
        m_fullDescription = settings.getString("fulldescription", "");
        try {
            m_uploadedWorkflow = settings.getString("uploadedworkflow");
        } catch (InvalidSettingsException e) {
            m_uploadedWorkflow = null;
        }
        try {
            NodeSettingsRO pairs =
                    settings.getNodeSettings("name_variable_pairs");
            Set<String> keySet = pairs.keySet();
            List<NameVariablePair> pairList = new ArrayList<NameVariablePair>();
            m_pairs = new NameVariablePair[keySet.size()];
            for (String key : keySet) {
                try {
                    NodeSettingsRO p = pairs.getNodeSettings(key);
                    String name = p.getString("name");
                    String variable = p.getString("variable");
                    if (name != null && variable != null) {
                        pairList.add(new NameVariablePair(name, variable));
                    }
                } catch (InvalidSettingsException ise) {
                    // ignore
                }
            }
            m_pairs = pairList.toArray(new NameVariablePair[pairList.size()]);
        } catch (InvalidSettingsException ise) {
            m_pairs = new NameVariablePair[0];
        }
        try {
            NodeSettingsRO refs = settings.getNodeSettings("references");
            Set<String> keySet = refs.keySet();
            List<Reference> refList = new ArrayList<Reference>();
            m_references = new Reference[keySet.size()];
            for (String key : keySet) {
                try {
                    NodeSettingsRO ref = refs.getNodeSettings(key);
                    String title = ref.getString("title");
                    String url = ref.getString("url");
                    String authors = ref.getString("authors");
                    int year = ref.getInt("year");
                    String doi = ref.getString("doi");
                    if (title != null && url != null && authors != null
                            && doi != null) {
                        refList.add(new Reference(title, url, authors, year,
                                doi));
                    }
                } catch (InvalidSettingsException ise) {
                    // ignore
                }
            }
            m_references = refList.toArray(new Reference[refList.size()]);
        } catch (InvalidSettingsException ise) {
            m_references = new Reference[0];
        }
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
        m_uploadWorkflow = settings.getBoolean("uploadworkflow");
        m_version = settings.getString("version");
        m_implements = settings.getString("implements");
        m_dependency = settings.getString("dependency");
        m_licence = settings.getString("licence");
        m_creator = settings.getString("creator", "");
        m_contributor = settings.getString("contributor", "");
        m_language = settings.getString("language", "");
        m_description = settings.getString("description");
        if (m_uploadWorkflow) {
            validate(m_description, "Description");
        }
        m_summary = settings.getString("summary", "");
        m_fullDescription = settings.getString("fulldescription", "");
        validate(m_licence, "Licence");
        if (m_uploadWorkflow) {
            validate(m_version, "Version");
            validate(m_implements, "Implements");
            validate(m_dependency, "Dependencies");
        }
        m_uploadResult = settings.getString("uploadresult");
        validate(m_uploadResult, "Upload result");
        m_resultFile = settings.getString("resultfile");
        m_workflowId = settings.getString("workflowid", "");
        m_workflowcredentials = settings.getString("workflowcredentials");
        try {
            m_uploadedWorkflow = settings.getString("uploadedworkflow");
        } catch (InvalidSettingsException e) {
            m_uploadedWorkflow = null;
        }
        if (m_uploadWorkflow
                || !m_uploadResult.equals(UploadPolicies.NO.getName())) {
            validate(m_workflowcredentials, "Credentials");
        }
        NodeSettingsRO pairs = settings.getNodeSettings("name_variable_pairs");
        Set<String> keySet = pairs.keySet();
        List<NameVariablePair> pairList = new ArrayList<NameVariablePair>();
        m_pairs = new NameVariablePair[keySet.size()];
        for (String key : keySet) {
            NodeSettingsRO p = pairs.getNodeSettings(key);
            String name = p.getString("name");
            String variable = p.getString("variable");
            if (name != null && variable != null) {
                pairList.add(new NameVariablePair(name, variable));
            }
        }
        m_pairs = pairList.toArray(new NameVariablePair[pairList.size()]);
        NodeSettingsRO refs = settings.getNodeSettings("references");
        Set<String> refKeySet = refs.keySet();
        List<Reference> refList = new ArrayList<Reference>();
        m_references = new Reference[keySet.size()];
        for (String key : refKeySet) {
            NodeSettingsRO ref = refs.getNodeSettings(key);
            String title = ref.getString("title");
            String url = ref.getString("url");
            String authors = ref.getString("authors");
            int year = ref.getInt("year");
            String doi = ref.getString("doi");
            if (title != null && url != null && authors != null && doi != null) {
                refList.add(new Reference(title, url, authors, year, doi));
            }
        }
        m_references = refList.toArray(new Reference[refList.size()]);
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

    /**
     * Represents a pair of name and variable.
     * 
     * 
     * @author Patrick Winter, KNIME.com, Zurich, Switzerland
     */
    public static class NameVariablePair {

        private String m_name;

        private String m_variable;

        /**
         * Creates a pair of name and variable.
         * 
         * 
         * @param name
         * @param variable
         */
        public NameVariablePair(final String name, final String variable) {
            m_name = name;
            m_variable = variable;
        }

        /**
         * @return the name
         */
        public String getName() {
            return m_name;
        }

        /**
         * @return the variable
         */
        public String getVariable() {
            return m_variable;
        }

    }

    /**
     * Represents a reference.
     * 
     * 
     * @author Patrick Winter, KNIME.com, Zurich, Switzerland
     */
    public static class Reference {

        private String m_title;

        private String m_url;

        private String m_authors;

        private int m_year;

        private String m_doi;

        /**
         * Create a reference.
         * 
         * 
         * @param title
         * @param url
         * @param authors
         * @param year
         * @param doi
         */
        public Reference(final String title, final String url,
                final String authors, final int year, final String doi) {
            m_title = title;
            m_url = url;
            m_authors = authors;
            m_year = year;
            m_doi = doi;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return m_title;
        }

        /**
         * @return the url
         */
        public String getUrl() {
            return m_url;
        }

        /**
         * @return the authors
         */
        public String getAuthors() {
            return m_authors;
        }

        /**
         * @return the year
         */
        public int getYear() {
            return m_year;
        }

        /**
         * @return the doi
         */
        public String getDoi() {
            return m_doi;
        }

    }

}
