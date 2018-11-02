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
package com.github.taniqng.graylog2.plugin.dingtalk.callback;
import java.util.Map;

import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.AlarmCallback;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taniqng.graylog2.plugin.dingtalk.DingtalkClient;
import com.github.taniqng.graylog2.plugin.dingtalk.DingtalkMsg;
import com.github.taniqng.graylog2.plugin.dingtalk.DingtalkPluginBase;
import com.google.common.collect.Maps;

public class DingtalkAlarmCallback extends DingtalkPluginBase implements AlarmCallback {
    private Configuration configuration;
    private static final Logger LOG = LoggerFactory.getLogger(DingtalkAlarmCallback.class);

    @Override
    public void initialize(final Configuration config) throws AlarmCallbackConfigurationException {
        this.configuration = config;
        try {
            checkConfiguration(config);
        } catch (ConfigurationException e) {
            throw new AlarmCallbackConfigurationException("Configuration error. " + e.getMessage());
        }
    }

    @Override
    public void call(Stream stream, AlertCondition.CheckResult result) throws AlarmCallbackException {
        final DingtalkClient client = new DingtalkClient(
                configuration.getString(CK_INSTANCE_URL)
        );
        String msgPrefix = configuration.getString(CK_DINGTALK_MSG);
        String at = configuration.getString(CK_DINGTALK_AT);
    	DingtalkMsg msg = new DingtalkMsg(
    			"text",
    			msgPrefix,
    			at,
                buildTitle(stream),
                buildDescription(stream, result, configuration)
    			); 
    	try {
    		client.send(msg);
    	} catch (DingtalkClient.DingtalkException e) {
    		throw new RuntimeException(e.getMessage());
    	}
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Maps.transformEntries(configuration.getSource(), new Maps.EntryTransformer<String, Object, Object>() {
            @Override
            public Object transformEntry(String key, Object value) {
                return value;
            }
        });
    }

    @Override
    // Never actually called by graylog-server
    public void checkConfiguration() throws ConfigurationException {
    }

    @Override
    public ConfigurationRequest getRequestedConfiguration() {
        return configuration();
    }

    @Override
    public String getName() {
        return "Dingtalk Alarm Callback";
    }
}
