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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UIController implements Initializable {

    private static final Logger LOGGER
            = LogManager.getLogger(UIController.class);

    private Prefs prefs;

    @FXML
    private Button seeButton;

    @FXML
    private Button runButton;

    @FXML
    private Button previewButton;

    @FXML
    private TextArea console;

    @FXML
    private TextField imagesField;

    @FXML
    private TextField rowsField;

    @FXML
    private TextField colsField;

    @FXML
    private TextField patternsField;

    @FXML
    private TextField pathField;

    @FXML
    private TextField spacingField;

    @FXML
    private TextField borderField;

    @FXML
    private TextField readRatioField;

    @FXML
    private TextField filePrefixField;

    @FXML
    private TextField pathOutField;

    @FXML
    private TextField writeRatiosField;

    @FXML
    private ImageView shadowImage;

    @FXML
    private Label statusLabel;

    @FXML
    private Label totalImages;

    Thread t1;
    Thread t2;
    String status = "Ready!";
    String newStatus;
    String buttonText = "Run";
    String newButtonText;

    @FXML
    private void handlePreviewButtonAction(ActionEvent event) {
        prefs.save(
                pathField.getText(),
                pathOutField.getText(),
                patternsField.getText(),
                imagesField.getText(),
                readRatioField.getText(),
                rowsField.getText(),
                colsField.getText(),
                spacingField.getText(),
                borderField.getText(),
                filePrefixField.getText(),
                writeRatiosField.getText()
        );
        t1 = new Thread(
                new CollagrThread(
                        this,
                        pathField.getText(),
                        patternsField.getText(),
                        imagesField.getText(),
                        rowsField.getText(),
                        colsField.getText(),
                        shadowImage
                )
        );
        t1.start();
    }

    @FXML
    private void handleRunButtonAction(ActionEvent event) {

        if ("Run".equals(runButton.getText())) {
            prefs.save(
                    pathField.getText(),
                    pathOutField.getText(),
                    patternsField.getText(),
                    imagesField.getText(),
                    readRatioField.getText(),
                    rowsField.getText(),
                    colsField.getText(),
                    spacingField.getText(),
                    borderField.getText(),
                    filePrefixField.getText(),
                    writeRatiosField.getText()
            );
            status = "Starting";
            TextAreaAppender.setTextArea(console);
            runButton.setText("Stop");
            t1 = new Thread(
                    new CollagrThread(
                            this,
                            runButton,
                            statusLabel,
                            pathField.getText(),
                            pathOutField.getText(),
                            patternsField.getText(),
                            filePrefixField.getText(),
                            imagesField.getText(),
                            rowsField.getText(),
                            colsField.getText(),
                            readRatioField.getText(),
                            writeRatiosField.getText(),
                            spacingField.getText(),
                            borderField.getText(),
                            shadowImage
                    )
            );
            t1.start();

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (true) {
                        Thread.sleep(200);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (!status.equals(newStatus)) {
                                    status = newStatus;
                                    statusLabel.setText(status);
                                }
                                if (!buttonText.equals(newButtonText) && newButtonText != null) {
                                    buttonText = newButtonText;
                                    runButton.setText(buttonText);
                                }
                            }
                        });
                        if (Thread.interrupted()) {
                            return null;
                        }
                    }
                }
            };
            (t2 = new Thread(task)).start();

        } else {
            runButton.setText("Run");
            t1.interrupt();
            t2.interrupt();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        prefs = new Prefs();
        imagesField.setText(prefs.getnImages());
        rowsField.setText(prefs.getnRows());
        colsField.setText(prefs.getnCols());
        borderField.setText(prefs.getBorder());
        spacingField.setText(prefs.getSpacing());
        filePrefixField.setText(prefs.getOutFilePrefix());
        pathField.setText(prefs.getInputDir());
        pathOutField.setText(prefs.getOutputDir());
        patternsField.setText(prefs.getPatterns());
        readRatioField.setText(prefs.getReadRatio());
        writeRatiosField.setText(prefs.getWriteRatios());

        imagesField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                imagesField.setText(newValue.replaceAll("[^\\d]", "0"));
            } else {
                System.out.println("textfield changed from " + oldValue + " to " + newValue);
            }
            setShadowImage();
        });
        rowsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                rowsField.setText(newValue.replaceAll("[^\\d]", "0"));
            } else {
                System.out.println("textfield changed from " + oldValue + " to " + newValue);
            }
            setShadowImage();
            totalImages.setText(
                    "/ "
                    + String.valueOf(Integer.parseInt(rowsField.getText()) * Integer.parseInt(colsField.getText()))
            );
        });
        colsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                colsField.setText(newValue.replaceAll("[^\\d]", "0"));
            } else {
                System.out.println("textfield changed from " + oldValue + " to " + newValue);
            }
            setShadowImage();
            totalImages.setText(
                    "/ "
                    + String.valueOf(Integer.parseInt(rowsField.getText()) * Integer.parseInt(colsField.getText()))
            );
        });
        borderField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                borderField.setText(newValue.replaceAll("[^\\d]", "0"));
            }
        });
        spacingField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spacingField.setText(newValue.replaceAll("[^\\d]", "0"));
            }
        });
        readRatioField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                readRatioField.setText(newValue.replaceAll("[^\\d]", "0"));
            }
        });

        t1 = null;

        setShadowImage();
    }

    private void setShadowImage() {
        setShadowImage(
                Integer.parseInt(rowsField.getText()),
                Integer.parseInt(colsField.getText()),
                Integer.parseInt(imagesField.getText()));
    }

    private void setShadowImage(int rows, int cols, int images) {
        // first shadow image, just add squares
        int width = cols * (25 + 5) + 5 * 2;
        int height = rows * (25 + 5) + 5 * 2;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        WritableImage img = SwingFXUtils.toFXImage(image, null);
        shadowImage.setImage(img);

        g2d.setColor(Color.MAGENTA);
        int f = 0;
        int x = 5, y = 5;
        for (int r = 0; r < rows && f < images; r++) {
            x = 5;
            for (int c = 0; c < cols && f < images; c++) {
                f++;

                g2d.fillRect(x, y, 25, 25);
                img = SwingFXUtils.toFXImage(image, null);
                shadowImage.setImage(img);

                x += 25 + 5;
            }
            y += 25 + 5;
        }
        g2d.dispose();

    }

    public void setStatus(String s) {
        newStatus = s;
    }

    public void setButton(String s) {
        newButtonText = s;
    }

    public void stopProcessing() throws InterruptedException {
        setButton("Run");
        Thread.sleep(400);
        if (t2 != null) {
            t2.interrupt();
        }
    }
}
