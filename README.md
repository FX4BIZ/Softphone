# Java softphone client

**Overview**  
This java-based applicaction is a handler for Twilio client. This client allows you to handle a [Twilio javascript client](https://www.twilio.com/docs/quickstart/php/client) into a desktop application.

This repository contains the basic code to implement a client that will provide basic interactions with a [Twilio javascript client](https://www.twilio.com/docs/quickstart/php/client). Feel free to modify this code to use with your Twilio environment.

**Abilities :**  
* **Handle secure connection via login request.** A part of the client is made for login purposes. It will send an HTTP POST request with an authentication token. It will allow you to authenticate users and manage access to your twilio client.
* **Handles ring and "go front" functionalities.** The client is made to ring and go to the front of the desktop when a call is incoming to the softphone.
* **Self updating component.** You can use the update component to update some parts of your application. 

**Dependencies**  
This project uses some extra package to run :
* [Java JRE 1.8 or higher](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
* [Apache Common Codec](https://commons.apache.org/proper/commons-codec/)
* [Apache Common IO](https://commons.apache.org/proper/commons-io/)
* [Apache Common Logging](https://commons.apache.org/proper/commons-logging/)
* [Apache Fluent API for http client](https://hc.apache.org/httpcomponents-client-ga/fluent-hc/dependency-info.html)
* [Apache HTTPComponent](http://hc.apache.org/)
* [Google GSON](https://github.com/google/gson)
* [Java Native Access](https://github.com/java-native-access/jna)
* [JxBrowser](https://www.teamdev.com/jxbrowser)

**Disclamer**  
Java Softphone Client uses the JxBrowser [http://www.teamdev.com/jxbrowser](http://www.teamdev.com/jxbrowser), which is a proprietary software. The use of JxBrowser is governed by JxBrowser Product Licence Agreement [http://www.teamdev.com/jxbrowser-licence-agreement](http://www.teamdev.com/jxbrowser-licence-agreement). If you would like to use JxBrowser in your development, please contact TeamDev.

**Contact**  
If you have any issues or questions about the application please contact [agr@fxforbiz.com](mailto://agr@fxforbiz.com).

**Licence**  
All parts of the code are free to use, as long as they are used in agreement with dependencies's licences.

