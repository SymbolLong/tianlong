package com.zhang.util;

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    private Logger logger = LoggerFactory.getLogger(getClass());


    public String doGet(String url) {
        return doGet(url, 20000);
    }

    public String doGet(String url, String charset) {
        return doGet(url, 3000, 20000, charset);
    }


    public String doGet(String url, int readTimeout) {
        return this.doGet(url, 3000, readTimeout);
    }


    public String doGet(String url, int connTimeout, int readTimeout) {
        return this.doGet(url, connTimeout, readTimeout, null);
    }

    public String doGetByUTF8(String url, int connTimeout, int readTimeout) {
        return this.doGet(url, connTimeout, readTimeout, "UTF-8");
    }

    public String doGet(String url, int connTimeout, int readTimeout, String charset) {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(connTimeout);
        client.getHttpConnectionManager().getParams().setSoTimeout(readTimeout);
        String res = null;
        // Create a method instance.
        GetMethod method = null;
        try {
            method = new GetMethod(url);
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
            method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
            // Execute the method.
            int statusCode = client.executeMethod(method);
            if (statusCode == HttpStatus.SC_OK) {
                BufferedReader reader = null;
                if (charset == null) {
                    reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset));
                }
                StringBuffer stringBuffer = new StringBuffer();
                String str = "";
                while ((str = reader.readLine()) != null) {
                    stringBuffer.append(str);
                }
                res = stringBuffer.toString();
            } else {
                logger.info("Response Code: " + statusCode);
            }
        } catch (Exception e) {
            logger.error("url=" + url + "\r\n", e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }

        return res;
    }

    public String doPost(String url, Map<String, String> params) {
        return doPost(url, params, "", 20000);
    }

    public String doPost(String url, Map<String, String> params, String charset) {
        return doPost(url, params, charset, 20000);
    }


    public String doPost(String url, Map<String, String> params, String charset, int readTimeout) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(readTimeout);
        PostMethod post = new PostMethod(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> key : params.entrySet()) {
            NameValuePair e = new NameValuePair(key.getKey(), key.getValue());
            nvps.add(e);
        }
        NameValuePair[] nameValues = new NameValuePair[params.size()];
        nameValues = nvps.toArray(nameValues);
        post.setRequestBody(nameValues);
        if (StringUtils.isNotBlank(charset)) {
            post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
        }

        try {
            httpClient.executeMethod(post);
            // 获取二进制的byte流
            byte[] b = post.getResponseBody();
            String str = new String(b, "UTF-8");

            return str;
        } catch (Exception e) {
            logger.error("url=" + url + "\r\n", e);
            return null;
        } finally {
            post.releaseConnection();
        }
    }

    /**
     * @param url         url
     * @param params      请求参数
     * @param charset     编码格式
     * @param readTimeout 超时时间
     * @param header      报文头
     * @return String
     * create by tongqdf 2017年3月21日09:28:07
     */
    public String doPost(String url, Map<String, Object> params, String charset, int readTimeout, Map<String, String> header) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(readTimeout);
        PostMethod post = new PostMethod(url);
        String param = JSONObject.fromObject(params).toString();
        try {
            StringRequestEntity entity = new StringRequestEntity(param, "application/json", charset);
            post.setRequestEntity(entity);
            if (null != header && !header.isEmpty()) {
                for (Map.Entry<String, String> headerEntry : header.entrySet()) {
                    post.setRequestHeader(headerEntry.getKey(), headerEntry.getValue());
                }
            }
            post.setRequestHeader("Content-Type", "application/json; charset=UTF-8");

//		try {
            httpClient.executeMethod(post);
            // 获取二进制的byte流
            byte[] b = post.getResponseBody();
            String str = new String(b, "UTF-8");

            return str;
        } catch (Exception e) {
            logger.error("url=" + url + "\r\n", e);
            return null;
        } finally {
            post.releaseConnection();
        }
    }


    public String doPost(String url, String jsonString) {
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(url);

        try {

            StringRequestEntity entity = new StringRequestEntity(jsonString, "application/json", "UTF-8");

            post.setRequestEntity(entity);

            httpClient.executeMethod(post);
            // 获取二进制的byte流
            byte[] b = post.getResponseBody();
            String str = new String(b, "UTF-8");

            return str;
        } catch (Exception e) {
            logger.error("", e);

            return null;
        } finally {
            post.releaseConnection();
        }
    }

    public static String postRequest(String url, Map<String, String> params, HttpMethodParams attachParams) {
        HttpClient client = new HttpClient();
        client.setConnectionTimeout(5000);
        PostMethod method = new PostMethod(url);
        method.setParams(attachParams);
//		Set<String> keys = params.keySet();
        for (Map.Entry<String, String> key : params.entrySet()) {
            method.setParameter(key.getKey(), key.getValue());
        }
        try {
            client.executeMethod(method);
            return new String(method.getResponseBody(), "UTF-8");
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

}
