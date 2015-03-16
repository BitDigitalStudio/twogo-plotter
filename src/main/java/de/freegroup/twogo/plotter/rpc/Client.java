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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Client {
    static Log log = LogFactory.getLog(Client.class);

    private final String serverUrl;

    HttpHost proxy;

    public Client(String serverUrl) {
        proxy = new HttpHost("proxy", 8080, "http");
        this.serverUrl = serverUrl;
    }

    public JSONObject sendAndReceive(String endpoint, String method, Object[] args) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        RequestConfig config = RequestConfig
                .custom()
                .setProxy(proxy)
                .build();

        HttpPost request = new HttpPost(this.serverUrl+endpoint);
        request.setConfig(config);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("X-CSRF-Token", getToken(httpclient));


        JSONObject message = buildParam(method, args);
        StringEntity body = new StringEntity( message.toString(), ContentType.create("application/json", Consts.UTF_8));

        request.setEntity(body);

        System.out.println(">> Request URI: " + request.getRequestLine().getUri());
        try {
            CloseableHttpResponse response = httpclient.execute( request);

            JSONTokener tokener = new JSONTokener(new BasicResponseHandler().handleResponse(response));
            Object rawResponseMessage = tokener.nextValue();
            JSONObject responseMessage = (JSONObject) rawResponseMessage;
            if (responseMessage == null) {
                throw new ClientError("Invalid response type - " + rawResponseMessage.getClass());
            }
            return responseMessage;
        } catch (Exception e) {
            throw new ClientError(e);
        }
    }

    private String getToken(CloseableHttpClient httpclient) throws Exception {

        RequestConfig config = RequestConfig.custom()
                 .setProxy(proxy)
                .build();
        HttpPost request = new HttpPost(this.serverUrl+"Echo");
        request.setConfig(config);
        request.setHeader("Content-Type", "text/plain");
        request.setHeader("X-CSRF-Token", "Fetch");

        try {

            JSONObject message = buildParam("echo", new String[]{"any"});

            StringEntity body = new StringEntity(
                    message.toString(),
                    ContentType.create("application/json", Consts.UTF_8));

            request.setEntity(body);
            System.out.println(">> Request URI: " + request.getRequestLine().getUri());

            CloseableHttpResponse response = httpclient.execute( request);

            JSONTokener tokener = new JSONTokener(new BasicResponseHandler().handleResponse(response));
            Object rawResponseMessage = tokener.nextValue();
            JSONObject responseMessage = (JSONObject) rawResponseMessage;
            if (responseMessage == null) {
                throw new ClientError("Invalid response type - " + rawResponseMessage.getClass());
            }

            return (responseMessage.getJSONObject("result").getJSONObject("header").getString("value"));

        } catch (Exception e) {
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

}
