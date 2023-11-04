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

* Double click user button
* Open tab with conversation
* Send messages through socket
* If close, catch and send print message back
* Close click, close tab and conversation

jar tf your-app.jar

jar -xf your-app.jar (Generate MANIFEST)
Move META-INF to src/main/resources
Edit the MANIFEST.MF (Add: Main-Class: com.example.view.HomeView)

jar cf target/your-app-updated.jar /path/to/META-INF (Recreate a JAR file with the modified META-INF)

sudo docker build -t image .
sudo docker run --rm -it image:latest

# Session bugs
* `lsof -i :8084`

To kill forcelly
* `kill -9 <PID>` 
