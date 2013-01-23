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
package org.openml.knime.uploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.XmlOptions;
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
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.ICredentials;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.openml.implementation.BibliographicalReference;
import org.openml.implementation.Implementation;
import org.openml.implementation.ImplementationDocument;
import org.openml.knime.OpenMLWebservice;
import org.openml.knime.OpenMLWebservice.Param;
import org.openml.knime.uploader.UploaderConfiguration.NameVariablePair;
import org.openml.knime.uploader.UploaderConfiguration.Reference;
import org.openml.uploadImplementation.UploadImplementationDocument;

/**
 * This is the model implementation.
 * 
 * 
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class UploaderNodeModel extends NodeModel {

    private UploaderConfiguration m_config;

    /**
     * Constructor for the node model.
     */
    protected UploaderNodeModel() {
        super(new PortType[]{FlowVariablePortObject.TYPE}, new PortType[]{});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) throws Exception {
        boolean uploadWorkflow = m_config.getUploadWorkflow();
        String uploadResults = m_config.getUploadResult();
        if (uploadWorkflow
                || !uploadResults.equals(UploadPolicies.NO.getName())) {
            ICredentials credentials =
                    getCredentialsProvider().get(
                            m_config.getWorkflowcredentials());
            String user = credentials.getLogin();
            String password = credentials.getPassword();
            String workflowName = getWorkflowManager().getName();
            String name = "knime." + user + "." + workflowName;
            m_config.setWorkflowId(name + "_" + m_config.getVersion());
            File tmpDir = File.createTempFile("OpenMLTemp", "");
            try {
                tmpDir.delete();
                tmpDir.mkdir();
                if (uploadWorkflow) {
                    File implementationFile =
                            new File(tmpDir, "implementation.xml");
                    genWorkflowFile(tmpDir, exec);
                    genImplementationXML(name, implementationFile);
                    File workflowFile = new File(tmpDir, workflowName + ".zip");
                    String response =
                            OpenMLWebservice.sendImplementation(
                                    implementationFile, workflowFile, user,
                                    password);
                    UploadImplementationDocument uploadImpl =
                            UploadImplementationDocument.Factory
                                    .parse(response);
                    if (uploadImpl.validate()) {
                        m_config.setWorkflowId(uploadImpl
                                .getUploadImplementation().getId());
                    }
                    uploadWorkflow = false;
                    m_config.setUploadWorkflow(false);
                }
                if (!uploadResults.equals(UploadPolicies.NO.getName())) {
                    int taskId = peekFlowVariableInt("OpenML-TaskId");
                    String implementationId = m_config.getWorkflowId();
                    File[] files =
                            new File[]{new File(m_config.getResultFile())};
                    OpenMLWebservice.sendRuns(taskId, implementationId,
                            getParams(), files, user, password);
                    if (uploadResults.equals(UploadPolicies.ONCE.getName())) {
                        uploadResults = UploadPolicies.NO.getName();
                        m_config.setUploadResult(UploadPolicies.NO.getName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                m_config.setUploadWorkflow(uploadWorkflow);
                m_config.setUploadResult(uploadResults);
                throw e;
            } finally {
                FileUtils.deleteDirectory(tmpDir);
            }
        }
        return new PortObject[]{};
    }

    private Param[] getParams() throws Exception {
        NameVariablePair[] pairs = m_config.getPairs();
        Param[] params = new Param[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            String name = pairs[i].getName();
            String variable = pairs[i].getVariable();
            String value = "";
            try {
                value = peekFlowVariableString(variable);
            } catch (NoSuchElementException e) {
                try {
                    value += peekFlowVariableInt(variable);
                } catch (NoSuchElementException e2) {
                    try {
                        value += peekFlowVariableDouble(variable);
                    } catch (NoSuchElementException e3) {
                        throw new Exception("Variable " + variable
                                + " not available");
                    }
                }
            }
            params[i] = new Param(name, value);
        }
        return params;
    }

    /**
     * Generate the implementation XML file.
     * 
     * 
     * @param name Name for this implementation
     * @param file File to save into
     * @throws IOException If writing to the file failed
     */
    private void genImplementationXML(final String name, final File file)
            throws IOException {
        ImplementationDocument implDoc =
                ImplementationDocument.Factory.newInstance();
        Implementation impl = implDoc.addNewImplementation();
        impl.setName(name);
        impl.setVersion(m_config.getVersion());
        impl.setImplements(m_config.getImplements());
        impl.setType("workflow");
        impl.setDependency(m_config.getDependency());
        impl.setLicence(m_config.getLicence());
        impl.setOperatingSystem(System.getProperty("os.name") + "_"
                + System.getProperty("os.arch") + "_"
                + System.getProperty("os.version"));
        impl.setDescription(m_config.getDescription());
        String creator = m_config.getCreator();
        if (creator != null && creator.length() > 0) {
            impl.setCreator(creator);
        }
        String contributor = m_config.getContributor();
        if (contributor != null && contributor.length() > 0) {
            impl.setContributorArray(contributor.split(","));
        }
        String language = m_config.getLanguage();
        if (language != null && language.length() > 0) {
            impl.setLanguage(language);
        }
        String summary = m_config.getSummary();
        if (summary != null && summary.length() > 0) {
            impl.setSummary(summary);
        }
        String fullDescription = m_config.getFullDescription();
        if (fullDescription != null && fullDescription.length() > 0) {
            impl.setFullDescription(fullDescription);
        }
        Reference[] references = m_config.getReferences();
        for (int i = 0; i < references.length; i++) {
            BibliographicalReference ref =
                    impl.addNewBibliographicalReference();
            ref.setTitle(references[i].getTitle());
            ref.setUrl(references[i].getUrl());
            ref.setAuthors(references[i].getAuthors());
            ref.setYear(references[i].getYear());
            ref.setDoi(references[i].getDoi());
        }
        impl.setOwnedByLibrary(false);
        String date =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        impl.setDate(date);
        new WorkflowDescription(getWorkflowManager()).exportToOpenMLXML(impl);
        Map<String, String> prefixMap = new HashMap<String, String>();
        prefixMap.put("http://openml.org/implementation", "oml");
        implDoc.save(file, new XmlOptions().setSavePrettyPrint()
                .setSaveSuggestedPrefixes(prefixMap));
    }

    /**
     * Create a zip file of this workflow.
     * 
     * 
     * The file will be saved into the given directroy and have the name of the
     * workflow and the file extension zip.
     * 
     * @param dir Directory where the file will be put into
     * @param exec Execution context needet to save the workflow object
     * @return Name of this workflow
     * @throws Exception If a file operation failed
     */
    private String genWorkflowFile(final File dir, final ExecutionContext exec)
            throws Exception {
        WorkflowManager wfm = getWorkflowManager();
        String name = "";
        name = wfm.getName();
        File file = new File(dir, name);
        wfm.save(file, exec, true);
        File[] files = resolveDirectories(new File[]{file});
        FileOutputStream fos =
                new FileOutputStream(file.getAbsolutePath() + ".zip");
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (int i = 0; i < files.length; i++) {
            addFile(files[i], zos, file.getParentFile().getAbsolutePath());
        }
        zos.close();
        return name;
    }

    /**
     * Get the <code>WorkflowManager</code> of this workflow.
     * 
     * 
     * @return <code>WorkflowManager</code> of this workflow
     */
    private WorkflowManager getWorkflowManager() {
        for (NodeContainer nc : WorkflowManager.ROOT.getNodeContainers()) {
            if (nc instanceof WorkflowManager) {
                WorkflowManager wfm = (WorkflowManager)nc;
                for (UploaderNodeModel m : wfm.findNodes(
                        UploaderNodeModel.class, true).values()) {
                    if (m == this) {
                        return wfm;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Adds the given file into the zip stream.
     * 
     * 
     * @param file The file to add
     * @param zout The zip stream where the file will be added
     * @param progress Progress of this nodes execution
     * @param exec Execution context for <code>checkCanceled()</code> and
     *            <code>setProgress()</code>
     * @throws Exception If the file can not be read or user canceled
     */
    private void addFile(final File file, final ZipOutputStream zout,
            final String prefix) throws Exception {
        FileInputStream in = null;
        try {
            byte[] buffer = new byte[1024];
            in = new FileInputStream(file);
            String filename = file.getAbsolutePath().replaceFirst(prefix, "");
            zout.putNextEntry(new ZipEntry(filename));
            int length;
            while ((length = in.read(buffer)) > 0) {
                zout.write(buffer, 0, length);
            }
            zout.closeEntry();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Replaces directories in the given file array by all contained files.
     * 
     * 
     * @param files Array of files that potentially contains directories
     * @return List of all files with directories resolved
     */
    private File[] resolveDirectories(final File[] files) {
        List<File> allFiles = new LinkedList<File>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                // Get inner files through recursive call and add them
                File[] innerFiles = resolveDirectories(files[i].listFiles());
                for (int j = 0; j < innerFiles.length; j++) {
                    allFiles.add(innerFiles[j]);
                }
            } else {
                allFiles.add(files[i]);
            }
        }
        return allFiles.toArray(new File[allFiles.size()]);
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
        return new PortObjectSpec[]{};
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
        UploaderConfiguration config = new UploaderConfiguration();
        config.loadAndValidate(settings);
        m_config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        new UploaderConfiguration().loadAndValidate(settings);
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
