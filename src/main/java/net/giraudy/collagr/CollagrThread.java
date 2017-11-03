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

import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author derrickjoe
 */
public class CollagrThread implements Runnable {

    private static final Logger LOGGER
            = LogManager.getLogger(CollagrThread.class);
    private final Button button;
    private final Label status;

    private String inDir;
    private String outDir;
    private String[] patterns;
    private String filePrefix;
    private int rows;
    private int cols;
    private int maxImages;
    private int readRatio;
    private int spacing;
    private int border;
    private ImageView shadowImage;
    private UIController ctrl;
    private String[] writeRatios;
    
    private boolean previewMode = true;

    public CollagrThread(UIController _ctrl, Button b, Label statusLabel) {
        button = b;
        status = statusLabel;
        ctrl = _ctrl;
    }

    CollagrThread(
            UIController _ctrl,
            String pathField,
            String patternsField,
            String imagesField,
            String rowsField,
            String colsField,
            ImageView _shadowImage
    ) {
        inDir = pathField;
        outDir = null;
        patterns = patternsField.split(",");
        filePrefix = null;
        cols = Integer.parseInt(colsField);
        rows = Integer.parseInt(rowsField);
        maxImages = Integer.parseInt(imagesField);
        readRatio = 0;
        spacing = 0;
        border = 0;
        shadowImage = _shadowImage;
        ctrl = _ctrl;
        button = null;
        status = null;
        writeRatios = null;
    }

    CollagrThread(
            UIController _ctrl,
            Button runButton,
            Label statusLabel,
            String pathField,
            String pathOutField,
            String patternsField,
            String filePrefixField,
            String imagesField,
            String rowsField,
            String colsField,
            String readRatioField,
            String writeRatiosField,
            String spacingField,
            String borderField,
            ImageView _shadowImage
    ) {
        previewMode = false;
        button = runButton;
        status = statusLabel;
        inDir = pathField;
        outDir = pathOutField;
        patterns = patternsField.split(",");
        filePrefix = filePrefixField;
        cols = Integer.parseInt(colsField);
        rows = Integer.parseInt(rowsField);
        maxImages = Integer.parseInt(imagesField);
        readRatio = Integer.parseInt(readRatioField);
        writeRatios = writeRatiosField.split(",");
        spacing = Integer.parseInt(spacingField);
        border = Integer.parseInt(borderField);
        shadowImage = _shadowImage;
        ctrl = _ctrl;
    }

    @Override
    public void run() {
        try {
            Collagr.run(ctrl, previewMode, inDir, outDir, patterns, filePrefix, rows, cols, maxImages, readRatio, writeRatios, spacing, border, status, shadowImage);
            //button.setText("Run");
            ctrl.stopProcessing();
        } catch (InterruptedException ex) {
            LOGGER.error("Interrupted!", ex);
        } catch (IOException ex) {
            LOGGER.error("Soemnthing went wrong!!!!", ex);
        }
    }
}
