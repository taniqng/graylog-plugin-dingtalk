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

import org.graylog2.plugin.Tools;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.plugin.streams.StreamRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class DingtalkPluginBase {
	
	private static final Logger LOG = LoggerFactory.getLogger(DingtalkPluginBase.class);

    public static final String CK_INSTANCE_URL = "instance_url";
    public static final String CK_DINGTALK_AT = "dingtalk_at";
    public static final String CK_DINGTALK_MSG = "dingtalk_msg";
    public static final String CK_GRAYLOG_URL = "graylog_url";

    public static ConfigurationRequest configuration() {
        final ConfigurationRequest configurationRequest = new ConfigurationRequest();

        configurationRequest.addField(new TextField(
                CK_INSTANCE_URL, "Dingtalk Instance URL", "", "Dingtalk server URL.",
                ConfigurationField.Optional.NOT_OPTIONAL)
        );
        
        configurationRequest.addField(new TextField(
        		CK_DINGTALK_MSG, "Dingtalk message", "", "Dingtalk message.",
                ConfigurationField.Optional.NOT_OPTIONAL)
        );
        
        configurationRequest.addField(new TextField(
        		CK_DINGTALK_AT, "Dingtalk At", "", "Dingtalk At.",
                ConfigurationField.Optional.NOT_OPTIONAL)
        );
        
        configurationRequest.addField(new TextField(
        		CK_GRAYLOG_URL, "Graylog url", "", "Graylog url.",
                ConfigurationField.Optional.OPTIONAL)
        );
        
        
        

        return configurationRequest;
    }

    public static void checkConfiguration(Configuration configuration) throws ConfigurationException {
        if (!configuration.stringIsSet(CK_INSTANCE_URL)) {
            throw new ConfigurationException(CK_INSTANCE_URL + " is mandatory and must not be empty.");
        }

        if (configuration.stringIsSet(CK_INSTANCE_URL) && !configuration.getString(CK_INSTANCE_URL).equals("null")) {
            try {
                final URI jiraUri = new URI(configuration.getString(CK_INSTANCE_URL));
                if (!"http".equals(jiraUri.getScheme()) && !"https".equals(jiraUri.getScheme())) {
                    throw new ConfigurationException(CK_INSTANCE_URL + " must be a valid HTTP or HTTPS URL.");
                }
            } catch (URISyntaxException e) {
                throw new ConfigurationException("Couldn't parse " + CK_INSTANCE_URL + " correctly.", e);
            }
        }

        if (!configuration.stringIsSet(CK_DINGTALK_AT)) {
            throw new ConfigurationException(CK_DINGTALK_AT + " is mandatory and must not be empty.");
        }
        
        if (!configuration.stringIsSet(CK_DINGTALK_MSG)) {
            throw new ConfigurationException(CK_DINGTALK_MSG + " is mandatory and must not be empty.");
        }

        if (configuration.stringIsSet(CK_GRAYLOG_URL) && !configuration.getString(CK_GRAYLOG_URL).equals("null")) {
            try {
                final URI graylogUri = new URI(configuration.getString(CK_GRAYLOG_URL));
                if (!"http".equals(graylogUri.getScheme()) && !"https".equals(graylogUri.getScheme())) {
                    throw new ConfigurationException(CK_GRAYLOG_URL + " must be a valid HTTP or HTTPS URL.");
                }
            } catch (URISyntaxException e) {
                throw new ConfigurationException("Couldn't parse " + CK_GRAYLOG_URL + " correctly.", e);
            }
        }
    }

    protected String buildTitle(Stream stream) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Alert] Graylog alert for stream: ").append(stream.getTitle());
        return sb.toString();
    }

    protected String buildStreamURL(String baseUrl, Stream stream) {
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        return baseUrl + "streams/" + stream.getId() + "/messages?q=*&rangetype=relative&relative=3600";
    }

    protected String buildStreamRules(Stream stream) {
        StringBuilder sb = new StringBuilder();
        for (StreamRule streamRule : stream.getStreamRules()) {
            sb.append("_").append(streamRule.getField()).append("_ ");
            sb.append(streamRule.getType()).append(" _").append(streamRule.getValue()).append("_").append("\n");
        }
        return sb.toString();
    }

    protected String buildDescription(Stream stream, AlertCondition.CheckResult checkResult, Configuration configuration) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(checkResult.getResultDescription());
        sb.append("\n\n");
        sb.append("*Date:* ").append("\n").append(Tools.iso8601().toString()).append("\n\n");
        sb.append("*Stream ID:* ").append("\n").append(stream.getId()).append("\n\n");
        sb.append("*Stream title:* ").append("\n").append(stream.getTitle()).append("\n\n");
        sb.append("*Stream URL:* ").append("\n").append(buildStreamURL(configuration.getString(CK_GRAYLOG_URL), stream)).append("\n\n");
        sb.append("*Stream rules:* ").append("\n").append(buildStreamRules(stream)).append("\n\n");
        sb.append("*Alert triggered at:* ").append("\n").append(checkResult.getTriggeredAt()).append("\n\n");
        sb.append("*Triggered condition:* ").append("\n").append(checkResult.getTriggeredCondition()).append("\n\n");

        return sb.toString();
    }

}
