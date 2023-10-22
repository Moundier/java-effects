# P2P Project

* Run `mvn clean install`
* Run `mvn clean javafx:run`
* Run `mvn clean javafx:run -Dmaven.test.redirectTestOutputToFile=false`

Linux
* Run `lsof -i :<port_number>`
* Run `kill <process_number>`

Windows:
* Run `netstat -ano | findstr :<port_number>`
* Run `taskkill /F /PID 1234`

### Made with
* Java
* Maven
* JavaFX
* Docker

### Todo
- Make a list of computers IPs