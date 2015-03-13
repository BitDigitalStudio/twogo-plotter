/*
 * JSON-RPC-Client, a Java client extension to JSON-RPC-Java
 *
 * (C) Copyright CodeBistro 2007, Sasha Ovsankin <sasha@codebistro.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.freegroup.twogo.plotter.rpc;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;

public class Client {
    static Log log = LogFactory.getLog(Client.class);

   private final String serverUrl;
    HttpClient client;
    HttpState state;

    String X_CSRF_Token = null;

    public Client(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public JSONObject sendAndReceive(String endpoint, String method, Object[] args) throws Exception{
        JSONObject message = buildParam(method, args);
        if (log.isDebugEnabled()) log.debug("Sending: " + message.toString(2));
        PostMethod postMethod = new PostMethod(new URI(this.serverUrl+endpoint).toString());
        postMethod.setRequestHeader("Content-Type", "text/plain");
        postMethod.setRequestHeader("X-CSRF-Token", getToken());

        RequestEntity requestEntity = new StringRequestEntity(message.toString());
        postMethod.setRequestEntity(requestEntity);
        try {
            http().executeMethod(null, postMethod);
            int statusCode = postMethod.getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new ClientError("HTTP Status - " + HttpStatus.getStatusText(statusCode) + " (" + statusCode + ")");
            }
            JSONTokener tokener = new JSONTokener(postMethod.getResponseBodyAsString());
            Object rawResponseMessage = tokener.nextValue();
            JSONObject responseMessage = (JSONObject) rawResponseMessage;
            if (responseMessage == null) {
                throw new ClientError("Invalid response type - " + rawResponseMessage.getClass());
            }
            return responseMessage;
        } catch (ParseException e) {
            throw new ClientError(e);
        } catch (HttpException e) {
            throw new ClientError(e);
        } catch (IOException e) {
            throw new ClientError(e);
        }
    }

    private String getToken() throws Exception
    {
        JSONObject message = buildParam("echo", new String[]{"any"});
        PostMethod postMethod = new PostMethod(new URI(this.serverUrl+"Echo").toString());
        postMethod.setRequestHeader("Content-Type", "text/plain");
        postMethod.setRequestHeader("X-CSRF-Token", "Fetch");

        RequestEntity requestEntity = new StringRequestEntity(message.toString());
        postMethod.setRequestEntity(requestEntity);
        try {
            http().executeMethod(null, postMethod);
            int statusCode = postMethod.getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new ClientError("HTTP Status - " + HttpStatus.getStatusText(statusCode) + " (" + statusCode + ")");
            }
            JSONTokener tokener = new JSONTokener(postMethod.getResponseBodyAsString());
            Object rawResponseMessage = tokener.nextValue();
            JSONObject responseMessage = (JSONObject) rawResponseMessage;
            if (responseMessage == null) {
                throw new ClientError("Invalid response type - " + rawResponseMessage.getClass());
            }

            System.out.println(responseMessage);
            return (responseMessage.getJSONObject("result").getJSONObject("header").getString("value"));

        } catch (ParseException e) {
            throw new ClientError(e);
        } catch (HttpException e) {
            throw new ClientError(e);
        } catch (IOException e) {
            throw new ClientError(e);
        }
    }

    private JSONObject buildParam(String method, Object[] args) {
        JSONObject object = new JSONObject();
        JSONArray params = new JSONArray();

        try {

            for (Object arg : args) {
                params.put(arg);
            }

            object.put("jsonrpc", "1.0");
            object.put("method", method);
            object.put("params", params);
            object.put("id", 1);

        } catch (Exception e) {
        }

        return object;
    }

    /**
     * An option to set state from the outside.
     * for example, to provide existing session parameters.
     */
    public void setState(HttpState state) {
        this.state = state;
    }

    HttpClient http() {
        if (client == null) {
            client = new HttpClient();
            if (state == null)
                state = new HttpState();
            client.setState(state);
        }
        return client;
    }

}
