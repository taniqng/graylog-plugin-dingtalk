/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Thiago Jackiw
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.github.taniqng.graylog2.plugin.dingtalk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class DingtalkClient {
    private static final Logger LOG = LoggerFactory.getLogger(DingtalkClient.class);

    private final String instanceURL;

    public DingtalkClient(String instanceURL) {
        this.instanceURL = instanceURL;
    }

    public class DingtalkException extends Exception {
		private static final long serialVersionUID = 9200645052920446932L;

		public DingtalkException(String msg) {
            super(msg);
        }

        public DingtalkException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public void send(String msg) throws DingtalkException {
        final URL url;
        try {
            url = new URL(instanceURL);
        } catch (MalformedURLException e) {
            throw new DingtalkException("[Dingtalk] Error while constructing instance URL.", e);
        }

        final HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "application/json");
        } catch (IOException e) {
            throw new DingtalkException("[Dingtalk] Could not open connection to " + instanceURL + ": ", e);
        }

        try (final OutputStream outputStream = conn.getOutputStream()) {
            outputStream.write(msg.getBytes(Charset.forName("UTF-8")));
            outputStream.flush();

            if(conn.getResponseCode()>=200 && conn.getResponseCode()<300) {
            	LOG.info("[Dingtalk] {} Dingtalk message has been successfully sended.{}", conn.getResponseMessage(), msg);
            } else {
            	throw new DingtalkException("[Dingtalk] Unexpected HTTP response status " + conn.getResponseCode());
            }
        } catch (IOException e) {
            throw new DingtalkException("[Dingtalk] Could not POST event to " + instanceURL + ": ", e);
        }
    }
    
    public void send(DingtalkMsg msg) throws DingtalkException {
        final URL url;
        try {
            url = new URL(instanceURL);
        } catch (MalformedURLException e) {
            throw new DingtalkException("[Dingtalk] Error while constructing instance URL.", e);
        }

        final HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "application/json");
        } catch (IOException e) {
            throw new DingtalkException("[Dingtalk] Could not open connection to " + instanceURL + ": ", e);
        }

        try (final OutputStream outputStream = conn.getOutputStream()) {
            outputStream.write(msg.toJSONString().getBytes(Charset.forName("UTF-8")));
            outputStream.flush();

            if(conn.getResponseCode()>=200 && conn.getResponseCode()<300) {
            	LOG.info("[Dingtalk] {} Dingtalk message has been successfully sended.{}", conn.getResponseMessage(), msg.toJSONString());
            } else {
            	throw new DingtalkException("[Dingtalk] Unexpected HTTP response status " + conn.getResponseCode());
            }
        } catch (IOException e) {
            throw new DingtalkException("[Dingtalk] Could not POST event to " + instanceURL + ": ", e);
        }
    }
    

}
