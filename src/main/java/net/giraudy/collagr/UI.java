/*
    Copyright 2017 Etienne Giraudy

    Licensed under the Apache License, Version 2.0 (the "License"); 
    you may not use this file except in compliance with the License. 
    You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, 
    software distributed under the License is distributed on an "AS IS" BASIS, 
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
    See the License for the specific language governing permissions and 
    limitations under the License.
 */
package net.giraudy.collagr;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;


public class UI extends Application {
    private static org.apache.logging.log4j.Logger logger;

    @Override
    public void start(Stage stage) throws Exception {
        String sessionId = UUID.randomUUID().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String logDir = "logs";
        if (System.getProperty("user.dir").endsWith("Contents/Java")) {
            logDir = "../../../".concat(logDir);
        }
        String fileName = logDir
                .concat(File.separator).concat("Collagr-")
                .concat(dateFormat.format(new Date()))
                .concat("-")
                .concat(sessionId)
                .concat(".log");
        System.setProperty("CollagrLogFilename", fileName);

        // If user adds external log4j config file (xml) to root, load it.
        // Else, use default config in classpath.
        File configFile = new File("./log4j2-ui.xml");
        if (configFile.exists()) {
            Configurator.initialize(null, "./log4j2-ui.xml");
        } else {
            System.setProperty("log4j.configurationFile", "log4j2-ui.xml");
        }
        logger = LogManager.getLogger(UI.class);
                        
        int screenWidth = (int) Screen.getPrimary().getVisualBounds().getWidth();
        int screenHeight = (int) Screen.getPrimary().getVisualBounds().getHeight();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        Scene scene = new Scene(root, screenWidth - 50, screenHeight - 50);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle("Collagr");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
