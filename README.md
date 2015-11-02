# Java softphone client

**Overview**  
This java-based applicaction is a handler for Twilio client. This client allows you to handle a [Twilio javascript client](https://www.twilio.com/docs/quickstart/php/client) into a desktop application.

**Abilities :**  
* **Handle secure connection via login request.** A part of the client is made for login purposes. It will send an HTTP POST request with an authentication token. It will allow you to authenticate users and manage access to your twilio client.
* **Handles ring and "go front" functionalities.** The client is made to ring and go to the front of the desktop when a call is incoming to the softphone.

**Dependencies**  
This project uses some extra package to run :
* [Apache Common Codec](https://commons.apache.org/proper/commons-codec/)
* [Apache Common IO](https://commons.apache.org/proper/commons-io/)
* [Apache Common Logging](https://commons.apache.org/proper/commons-logging/)
* [Apache Fluent API for http client](https://hc.apache.org/httpcomponents-client-ga/fluent-hc/dependency-info.html)
* [Apache HTTPComponent](http://hc.apache.org/)
* [Google GSON](https://github.com/google/gson)
* [Java Native Access](https://github.com/java-native-access/jna)
* [JxBrowser](https://www.teamdev.com/jxbrowser)

**Contact**  
If you have any issues or questions about the application please contact [agr@fxforbiz.com](mailto://agr@fxforbiz.com).

**Licence**  
All parts of the code are free to use, as long as they are used in agreement with dependencies licences.

