# graylog-plugin-dingtalk
graylog接入钉钉告警

## Installation

[Download the plugin](https://github.com/taniqng/graylog-plugin-dingtalk/releases)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.

## Usage

Create a "Dingtalk Alarm Callback" on the "Manage alerts" page of your stream. 
Enter the requested configuration and save. 
Make sure you also configured alert conditions for the stream so that the alerts are actually triggered.

## Build

This project is using Maven and requires Java 8 or higher.

You can build a plugin (JAR) with `mvn package`.

## Copyright

Copyright (c) 2016 Thiago Jackiw. See LICENSE for further details.
