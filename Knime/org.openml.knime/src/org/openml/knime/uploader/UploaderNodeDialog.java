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
package org.openml.knime.uploader;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.FlowVariableModelButton;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.KeyValuePanel;
import org.knime.core.node.workflow.FlowVariable;
import org.openml.knime.OpenMLWebservice;
import org.openml.knime.uploader.UploaderConfiguration.NameVariablePair;
import org.openml.knime.uploader.UploaderConfiguration.Reference;

/**
 * <code>NodeDialog</code> for the node.
 * 
 * 
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
class UploaderNodeDialog extends NodeDialogPane {

    private JCheckBox m_uploadWorkflow;

    private JLabel m_versionLabel;

    private JTextField m_version;

    private JLabel m_licenceLabel;

    private JComboBox m_licence;

    private JRadioButton m_no;

    private JRadioButton m_once;

    private JRadioButton m_always;

    private ButtonGroup m_uploadResult;

    private JLabel m_resultFileLabel;

    private FlowVariableModelButton m_resultFile;

    private String m_workflowId;

    private JLabel m_dependencyLabel;

    private JTextField m_dependency;

    private JLabel m_workflowcredentialsLabel;

    private JComboBox m_workflowcredentials;

    private JLabel m_paramsLabel;

    private KeyValuePanel m_params;

    private JLabel m_creatorLabel;

    private JTextField m_creator;

    private JLabel m_contributorLabel;

    private JTextField m_contributor;

    private JLabel m_languageLabel;

    private JTextField m_language;

    private JLabel m_descriptionLabel;

    private JTextArea m_description;

    private JTable m_references;

    private DefaultTableModel m_referencesModel;

    private JButton m_add;

    private JButton m_remove;

    /**
     * New pane for configuring the node dialog.
     */
    public UploaderNodeDialog() {
        m_workflowcredentialsLabel = new JLabel("Workflow credentials");
        m_workflowcredentials = new JComboBox();
        m_uploadWorkflow = new JCheckBox("Upload workflow");
        m_uploadWorkflow.addChangeListener(new UpdateListener());
        m_versionLabel = new JLabel("Version");
        m_version = new JTextField();
        m_dependencyLabel = new JLabel("Dependencies");
        m_dependency = new JTextField();
        m_licenceLabel = new JLabel("Licence");
        m_licence = new JComboBox(OpenMLWebservice.getLicences());
        m_uploadResult = new ButtonGroup();
        m_no = new JRadioButton(UploadPolicies.NO.getName());
        m_no.setActionCommand(UploadPolicies.NO.getName());
        m_once = new JRadioButton(UploadPolicies.ONCE.getName());
        m_once.setActionCommand(UploadPolicies.ONCE.getName());
        m_always = new JRadioButton(UploadPolicies.ALWAYS.getName());
        m_always.setActionCommand(UploadPolicies.ALWAYS.getName());
        m_uploadResult.add(m_no);
        m_uploadResult.add(m_once);
        m_uploadResult.add(m_always);
        m_uploadResult.setSelected(m_no.getModel(), true);
        m_resultFileLabel = new JLabel("Result file location");
        FlowVariableModel resultfilefvm =
                createFlowVariableModel("resultfile", FlowVariable.Type.STRING);
        m_resultFile = new FlowVariableModelButton(resultfilefvm);
        m_paramsLabel = new JLabel("Parameters");
        m_params = new KeyValuePanel();
        m_params.setKeyColumnLabel("Name");
        m_params.setValueColumnLabel("Variable");
        m_params.getTable().setPreferredScrollableViewportSize(null);
        m_creatorLabel = new JLabel("Creator");
        m_creator = new JTextField();
        m_contributorLabel = new JLabel("Contributor");
        m_contributor = new JTextField();
        m_languageLabel = new JLabel("Language");
        m_language = new JTextField();
        m_descriptionLabel = new JLabel("Description");
        m_description = new JTextArea();
        m_description.setLineWrap(true);
        m_description.setRows(5);
        m_referencesModel =
                new DefaultTableModel(new Object[0][], new Object[]{"citation",
                        "url"});
        m_references = new JTable(m_referencesModel);
        m_no.addChangeListener(new UpdateListener());
        m_once.addChangeListener(new UpdateListener());
        m_always.addChangeListener(new UpdateListener());
        addTab("Options", new JScrollPane(initLayout(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        addTab("Additional Options", new JScrollPane(additionalTab(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        addTab("References", referencesTab());
        updateEnabledState();
    }

    /**
     * Create and initialize the panel for this dialog.
     * 
     * 
     * @return The initialized panel
     */
    private JPanel initLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        // Upload result panel
        resetGBC(gbc);
        JPanel uploadResultPanel = new JPanel(new GridBagLayout());
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 0;
        uploadResultPanel.add(m_no, gbc);
        gbc.gridx++;
        uploadResultPanel.add(m_once, gbc);
        gbc.gridx++;
        uploadResultPanel.add(m_always, gbc);
        uploadResultPanel.setBorder(new TitledBorder(new EtchedBorder(),
                "Upload result"));
        // Result file panel
        resetGBC(gbc);
        JPanel resultFilePanel = new JPanel(new GridBagLayout());
        gbc.weightx = 0;
        resultFilePanel.add(m_resultFileLabel, gbc);
        gbc.gridx++;
        resultFilePanel.add(m_resultFile, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        resultFilePanel.add(new JLabel(), gbc);
        // Outer panel
        resetGBC(gbc);
        JPanel panel = new JPanel(new GridBagLayout());
        gbc.weightx = 0;
        panel.add(m_workflowcredentialsLabel, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(m_workflowcredentials, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy++;
        panel.add(m_versionLabel, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(m_version, gbc);
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 2;
        gbc.gridy++;
        panel.add(m_uploadWorkflow, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(m_dependencyLabel, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(m_dependency, gbc);
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy++;
        panel.add(m_licenceLabel, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(m_licence, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(uploadResultPanel, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy++;
        panel.add(resultFilePanel, gbc);
        gbc.gridy++;
        panel.add(m_paramsLabel, gbc);
        gbc.gridy++;
        panel.add(m_params, gbc);
        return panel;
    }

    private JPanel additionalTab() {
        GridBagConstraints gbc = new GridBagConstraints();
        resetGBC(gbc);
        JPanel panel = new JPanel(new GridBagLayout());
        gbc.weightx = 0;
        panel.add(m_creatorLabel, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(m_creator, gbc);
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy++;
        panel.add(m_contributorLabel, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(m_contributor, gbc);
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy++;
        panel.add(m_languageLabel, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(m_language, gbc);
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy++;
        panel.add(m_descriptionLabel, gbc);
        gbc.weightx = 1;
        gbc.gridx++;
        panel.add(new JScrollPane(m_description), gbc);
        return panel;
    }

    private JPanel referencesTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        m_add = new JButton("Add");
        m_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                m_referencesModel.addRow(new Object[0]);
            }
        });
        m_remove = new JButton("Remove");
        m_remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    m_referencesModel.removeRow(m_references.getSelectedRow());
                } catch (Exception e) {
                    // ignore
                }
            }
        });
        resetGBC(gbc);
        gbc.weightx = 1;
        gbc.gridheight = 2;
        panel.add(new JScrollPane(m_references), gbc);
        gbc.gridx++;
        gbc.weightx = 0;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(m_add, gbc);
        gbc.gridy++;
        panel.add(m_remove, gbc);
        return panel;
    }

    /**
     * Update the enabled / disabled state of the UI elements.
     */
    private void updateEnabledState() {
        boolean uploadWorkflow = m_uploadWorkflow.isSelected();
        boolean uploadResult =
                !m_uploadResult.getSelection().getActionCommand()
                        .equals(UploadPolicies.NO.getName());
        m_dependency.setEnabled(uploadWorkflow);
        m_dependencyLabel.setEnabled(uploadWorkflow);
        m_licence.setEnabled(uploadWorkflow);
        m_licenceLabel.setEnabled(uploadWorkflow);
        m_resultFile.setEnabled(uploadResult);
        m_resultFileLabel.setEnabled(uploadResult);
        m_paramsLabel.setEnabled(uploadResult);
        m_params.setEnabled(uploadResult);
        m_creator.setEnabled(uploadWorkflow);
        m_creatorLabel.setEnabled(uploadWorkflow);
        m_contributor.setEnabled(uploadWorkflow);
        m_contributorLabel.setEnabled(uploadWorkflow);
        m_language.setEnabled(uploadWorkflow);
        m_languageLabel.setEnabled(uploadWorkflow);
        m_description.setEnabled(uploadWorkflow);
        m_descriptionLabel.setEnabled(uploadWorkflow);
        m_add.setEnabled(uploadWorkflow);
        m_remove.setEnabled(uploadWorkflow);
        m_references.setEnabled(uploadWorkflow);
    }

    /**
     * Listener that updates the states of the UI elements.
     * 
     * 
     * @author Patrick Winter, KNIME.com, Zurich, Switzerland
     */
    private class UpdateListener implements ChangeListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void stateChanged(final ChangeEvent e) {
            updateEnabledState();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings)
            throws InvalidSettingsException {
        UploaderConfiguration config = new UploaderConfiguration();
        config.setWorkflowcredentials((String)m_workflowcredentials
                .getSelectedItem());
        config.setUploadWorkflow(m_uploadWorkflow.isSelected());
        config.setUploadResult(m_uploadResult.getSelection().getActionCommand());
        config.setVersion(m_version.getText());
        config.setLicence(m_licence.getSelectedItem().toString());
        config.setWorkflowId(m_workflowId);
        config.setDependency(m_dependency.getText());
        config.setCreator(m_creator.getText());
        config.setContributor(m_contributor.getText());
        config.setLanguage(m_language.getText());
        config.setDescription(m_description.getText());
        String[] names = m_params.getKeys();
        String[] variables = m_params.getValues();
        NameVariablePair[] pairs = new NameVariablePair[names.length];
        for (int i = 0; i < names.length; i++) {
            pairs[i] = new NameVariablePair(names[i], variables[i]);
        }
        config.setPairs(pairs);
        Reference[] references = new Reference[m_referencesModel.getRowCount()];
        int citationIndex = m_referencesModel.findColumn("citation");
        int urlIndex = m_referencesModel.findColumn("url");
        for (int i = 0; i < references.length; i++) {
            try {
                String citation =
                        m_referencesModel.getValueAt(i, citationIndex)
                                .toString();
                String url =
                        m_referencesModel.getValueAt(i, urlIndex).toString();
                references[i] = new Reference(citation, url);
            } catch (Exception e) {
                throw new InvalidSettingsException("Reference " + i
                        + " is invalid");
            }
        }
        config.setReferences(references);
        config.save(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
            final PortObjectSpec[] specs) throws NotConfigurableException {
        Collection<String> credentials = getCredentialsNames();
        m_workflowcredentials.removeAllItems();
        for (String credential : credentials) {
            m_workflowcredentials.addItem(credential);
        }
        UploaderConfiguration config = new UploaderConfiguration();
        config.load(settings);
        m_workflowcredentials.setSelectedItem(config.getWorkflowcredentials());
        m_uploadWorkflow.setSelected(config.getUploadWorkflow());
        String uploadResult = config.getUploadResult();
        if (uploadResult.equals(m_no.getActionCommand())) {
            m_uploadResult.setSelected(m_no.getModel(), true);
        } else if (uploadResult.equals(m_once.getActionCommand())) {
            m_uploadResult.setSelected(m_once.getModel(), true);
        } else if (uploadResult.equals(m_always.getActionCommand())) {
            m_uploadResult.setSelected(m_always.getModel(), true);
        }
        m_version.setText(config.getVersion());
        m_licence.setSelectedItem(config.getLicence());
        m_workflowId = config.getWorkflowId();
        m_dependency.setText(config.getDependency());
        m_creator.setText(config.getCreator());
        m_contributor.setText(config.getContributor());
        m_language.setText(config.getLanguage());
        m_description.setText(config.getDescription());
        NameVariablePair[] pairs = config.getPairs();
        String[] names = new String[pairs.length];
        String[] variables = new String[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            names[i] = pairs[i].getName();
            variables[i] = pairs[i].getVariable();
        }
        m_params.setTableData(names, variables);
        Reference[] references = config.getReferences();
        int citationIndex = m_referencesModel.findColumn("citation");
        int urlIndex = m_referencesModel.findColumn("url");
        m_referencesModel.setRowCount(0);
        for (int i = 0; i < references.length; i++) {
            Object[] objects = new Object[5];
            objects[citationIndex] = references[i].getCitation();
            objects[urlIndex] = references[i].getUrl();
            m_referencesModel.addRow(objects);
        }
        updateEnabledState();
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
