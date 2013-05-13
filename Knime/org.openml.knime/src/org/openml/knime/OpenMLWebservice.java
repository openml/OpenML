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
 *   Jan 7, 2013 (Patrick Winter): created
 */
package org.openml.knime;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.openml.error.ErrorDocument;
import org.openml.implementationLicences.ImplementationLicencesDocument;

/**
 * Static methods to interact with OpenMLs webservice.
 * 
 * 
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class OpenMLWebservice {

    /**
     * Path to the webservice.
     */
    public static final String WEBSERVICEURL =
            "http://www.openml.org/api/";

    /**
     * Returns URL to the specified task.
     * 
     * 
     * @param taskId ID of the task description
     * @return URL to task with taskId
     */
    public static URL getTaskURL(final String taskId) {
        URL url = null;
        try {
            url =
                    new URL(WEBSERVICEURL + "?f=openml.tasks.search&task_id="
                            + taskId);
        } catch (MalformedURLException e) {
            // return null
        }
        return url;
    }

    /**
     * Returns URL to the specified data set description.
     * 
     * 
     * @param datasetdescId ID of the data set description
     * @return URL to data set description with datasetdescId
     */
    public static URL getDatasetDescURL(final int datasetdescId) {
        URL url = null;
        try {
            url =
                    new URL(WEBSERVICEURL
                            + "?f=openml.data.description&data_id="
                            + datasetdescId);
        } catch (MalformedURLException e) {
            // return null
        }
        return url;
    }

    /**
     * Returns URL to the specified splits.
     * 
     * 
     * @param splitsId ID of the splits
     * @return URL to splits with splitsId
     */
    public static URL getSplitsURL(final String splitsId) {
        URL url = null;
        try {
            url =
                    new URL(WEBSERVICEURL + "?f=openml.task.splits&splits_id="
                            + splitsId);
        } catch (MalformedURLException e) {
            // return null
        }
        return url;
    }

    /**
     * Upload an implementation.
     * 
     * 
     * @param description Implementation description file
     * @param workflow Workflow file
     * @param user Name of the user
     * @param password Password of the user
     * @return Response from the server
     * @throws Exception If communication failed
     */
    public static String sendImplementation(final File description,
            final File workflow, final String user, final String password)
            throws Exception {
        String result = "";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            Credentials credentials =
                    new UsernamePasswordCredentials(user, password);
            AuthScope scope =
                    new AuthScope(new URI(WEBSERVICEURL).getHost(), 80);
            httpclient.getCredentialsProvider().setCredentials(scope,
                    credentials);
            String url = WEBSERVICEURL + "?f=openml.implementation.upload";
            HttpPost httppost = new HttpPost(url);
            MultipartEntity reqEntity = new MultipartEntity();
            FileBody descBin = new FileBody(description);
            reqEntity.addPart("description", descBin);
            FileBody workflowBin = new FileBody(workflow);
            reqEntity.addPart("source", workflowBin);
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (response.getStatusLine().getStatusCode() < 200) {
                throw new Exception(response.getStatusLine().getReasonPhrase());
            }
            if (resEntity != null) {
                result = convertStreamToString(resEntity.getContent());
            }
            ErrorDocument errorDoc = null;
            try {
                errorDoc = ErrorDocument.Factory.parse(result);
            } catch (Exception e) {
                // no error XML should mean no error
            }
            if (errorDoc != null && errorDoc.validate()) {
                ErrorDocument.Error error = errorDoc.getError();
                String errorMessage =
                        error.getCode() + " : " + error.getMessage();
                if (error.isSetAdditionalInformation()) {
                    errorMessage += " : " + error.getAdditionalInformation();
                }
                throw new Exception(errorMessage);
            }
            EntityUtils.consume(resEntity);
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
                // ignore
            }
        }
        return result;
    }

    /**
     * Upload a run.
     * 
     * @param description Description to the run
     * @param files Run files
     * @param user Name of the user
     * @param password Password of the user
     * @return Response from the server
     * @throws Exception
     */
    public static String sendRuns(final File description, final File[] files,
            final String user, final String password) throws Exception {
        String result = "";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        Credentials credentials =
                new UsernamePasswordCredentials(user, password);
        AuthScope scope = new AuthScope(new URI(WEBSERVICEURL).getHost(), 80);
        httpclient.getCredentialsProvider().setCredentials(scope, credentials);
        try {
            String url = WEBSERVICEURL + "?f=openml.run.upload";
            HttpPost httppost = new HttpPost(url);
            MultipartEntity reqEntity = new MultipartEntity();
            FileBody descriptionBin = new FileBody(description);
            reqEntity.addPart("description", descriptionBin);
            for (int i = 0; i < files.length; i++) {
                FileBody bin = new FileBody(files[i]);
                reqEntity.addPart("predictions", bin);
            }
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (response.getStatusLine().getStatusCode() < 200) {
                throw new Exception(response.getStatusLine().getReasonPhrase());
            }
            if (resEntity != null) {
                result = convertStreamToString(resEntity.getContent());
            }
            ErrorDocument errorDoc = null;
            try {
                errorDoc = ErrorDocument.Factory.parse(result);
            } catch (Exception e) {
                // no error XML should mean no error
            }
            if (errorDoc != null && errorDoc.validate()) {
                ErrorDocument.Error error = errorDoc.getError();
                String errorMessage =
                        error.getCode() + " : " + error.getMessage();
                if (error.isSetAdditionalInformation()) {
                    errorMessage += " : " + error.getAdditionalInformation();
                }
                throw new Exception(errorMessage);
            }
            EntityUtils.consume(resEntity);
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
                // ignore
            }
        }
        return result;
    }

    /**
     * Get the licences available for OpenML.
     * 
     * 
     * @return The licences available
     */
    public static String[] getLicences() {
        String[] licences;
        try {
            InputStream licencesIn =
                    new URL(WEBSERVICEURL + "?f=openml.implementation.licences")
                            .openStream();
            ImplementationLicencesDocument licencesDoc =
                    ImplementationLicencesDocument.Factory.parse(licencesIn);
            licences =
                    licencesDoc.getImplementationLicences().getLicences()
                            .getLicenceArray();
        } catch (Exception e) {
            licences = new String[0];
        }
        return licences;
    }

    /**
     * Convert an input stream into a string.
     * 
     * 
     * @param is The input stream
     * @return Content of the input stream as a string
     */
    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Parameter of a run.
     * 
     * 
     * @author Patrick Winter, KNIME.com, Zurich, Switzerland
     */
    public static class Param {

        private String m_key;

        private String m_value;

        /**
         * Create a parameter of a run.
         * 
         * 
         * This constructor will automatically escape the characters \,-,:
         * 
         * @param key
         * @param value
         */
        public Param(final String key, final String value) {
            m_key =
                    key.replace("\\", "\\\\").replace("-", "\\-")
                            .replace(":", "\\:");
            m_value =
                    value.replace("\\", "\\\\").replace("-", "\\-")
                            .replace(":", "\\:");
        }

        /**
         * @return the key
         */
        public String getKey() {
            return m_key;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return m_value;
        }

    }

}
