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

import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DingtalkMsg {
    private final String msgtype;
    private final String content;
    private final String at;
    private final String title;
    
    private final String description;

    public DingtalkMsg(final String msgtype, String content, String at, String title, String description) {
        this.msgtype = msgtype;
        this.content = content;
        this.at = at;
        this.title = title;
        this.description = description;
    }

    public String toJSONString() {
        JSONObject message = new JSONObject();
        message.put("msgtype", msgtype);
        
        JSONObject contentObj = new JSONObject();
        StringBuffer sb = new StringBuffer();
        Stream.of(at.split("\\,")).forEach(e->{
        	sb.append("@").append(e);
        });
        sb.append("\n")
          .append(title)
          .append("\n")
          .append(content)
          .append("\n")
          .append(description);
        
        contentObj.put("content", sb.toString());
        message.put("text", contentObj);

        JSONArray atList = new JSONArray();
        String[] ats = at.split("\\,");
        for (String s : ats) {
        	atList.add(s);
        }
        
        JSONObject atMobiles = new JSONObject();
        atMobiles.put("atMobiles", atList);
        message.put("at", atMobiles);
        message.put("isAtAll", false);

        return message.toJSONString();
    }

}
