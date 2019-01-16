# XL Release Plugin for IBM Rational Team Center #

[![Build Status][xlr-rtc-ccm-plugin-travis-image]][xlr-rtc-ccm-plugin-travis-url]
[![License: MIT][xlr-rtc-ccm-plugin-license-image]][xlr-rtc-ccm-plugin-license-url]
![Github All Releases][xlr-rtc-ccm-plugin-downloads-image]

[xlr-rtc-ccm-plugin-travis-image]: https://travis-ci.org/xebialabs-community/xlr-rtc-ccm-plugin.svg?branch=master
[xlr-rtc-ccm-plugin-travis-url]: https://travis-ci.org/xebialabs-community/xlr-rtc-ccm-plugin
[xlr-rtc-ccm-plugin-license-image]: https://img.shields.io/badge/License-MIT-yellow.svg
[xlr-rtc-ccm-plugin-license-url]: https://opensource.org/licenses/MIT
[xlr-rtc-ccm-plugin-downloads-image]: https://img.shields.io/github/downloads/xebialabs-community/xlr-rtc-ccm-plugin/total.svg

# Overview #

This plugin provides the ability to manipulate work items in IBM RTC-CCM.

See the **XL Release Reference Manual** for background information on XL Release and plugin concepts.

* **Requirements**
  * **XL Release** 7.5.0+
  * **IBM RTC** 6.0.3+

# Installation #

* Copy the latest JAR file from the [releases page](https://github.com/xebialabs-community/xlr-rtc-ccm-plugin/releases) into the `XL_RELEASE_SERVER/plugins/__local__` directory.
* Restart the XL Release server.

# Usage #

## Configure Server ##

Begin by configuring one or more rtc-ccm servers.  Navigate to **Settings -> Shared configuration** and add a rtc-ccm: Server.

![rtc-ccmServerConfig](images/rtc-ccm-server-config.png)



### Title ###

Enter a descriptive name for this server.

### URL ###

Enter the full URL to the server.  Include protocol (http or https) and port number if applicable.

### Username ###

The current versions of rtc-ccm (12.0 and below) do not use authentication so no username or password is needed.  When future versions of rtc-ccm require authentication, enter the username here.

### Password ###

When future versions of rtc-ccm require authentication, enter the password here.

### Domain ###

The NTLM domain for authentication if applicable.

### Proxy ###

Optional proxy information if you access the rtc-ccm server through a proxy.

---

## Create Work Item Task ##

In your SDLC templates, you can add a task of type **rtc-ccm -> Create Work Item** as shown below.  

![rtc-ccm-create-1](images/rtc-ccm-create-1.png)

### Server ###

The rtc-ccm Server that will run your tests.  

---

### Example Output ###

Once the task is complete you will see output like these shown below...

![rtc-ccm-create-2](images/rtc-ccm-create-2.png)

# Developers #

Build and package the plugins with...

```bash
./gradlew assemble
```

Run unit tests with...

```bash
./gradlew pyTest
```