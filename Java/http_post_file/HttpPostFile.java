/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * altered by J. N. van Rijn
 */ 
package org.openml.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpPostFile {

	private static final String PREDICTIONS = "res/predictions.arff";
	private static final String URL = "http://localhost/expdb/api/?f=openml.run.upload&task_id=1&workflow_id=TestWorkflow&implementation_id=TestWorkflow1.0";
	
    public static void main(String[] args) throws Exception {
        
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost( URL );

            FileBody bin = new FileBody(new File(PREDICTIONS));

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("predictions", bin);

            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (resEntity != null) {
                System.out.println("Response content length: " + resEntity.getContentLength());
                BufferedReader br = new BufferedReader( new InputStreamReader( resEntity.getContent() ) );
                while( br.ready() ) System.out.println( br.readLine() );
            }
            EntityUtils.consume(resEntity);
        } finally {
            try { httpclient.getConnectionManager().shutdown(); } catch (Exception ignore) {}
        }
    }
    
}