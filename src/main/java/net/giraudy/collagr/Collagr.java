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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author etiennegiraudy
 */
public class Collagr {

    private static final Logger LOGGER
            = LogManager.getLogger(Collagr.class);

    public static void run(
            UIController ctrl,
            boolean previewMode,
            String srcPath,
            String outPath,
            String[] patterns,
            String filePrefix,
            int rows,
            int cols,
            int images,
            int readRatio,
            String[] writeRatios,
            int spacing,
            int border,
            Label status,
            ImageView shadowImage
    ) throws IOException, InterruptedException {
        LOGGER.info("Starting to build collage...");
        LOGGER.info("   building file list...");
        List<File> filesCollections = getFiles(srcPath, rows, cols, images, patterns);

        LOGGER.info("   More initialization...");
        collage = new Collage(rows, cols, readRatio, spacing, border);

        int squareSize = 25;
        int space = 5;
        // first shadow image, just add squares
        int width = cols * (squareSize + space) + space * 2;
        int height = rows * (squareSize + space) + space * 2;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        WritableImage img = SwingFXUtils.toFXImage(image, null);
        shadowImage.setImage(img);

        // then read files and update the shadow image with missing squares
        int f = 0;
        int x, y = space;
        File[] files = filesCollections.toArray(new File[0]);
        for (int r = 0; r < rows; r++) {
            x = space;
            for (int c = 0; c < cols; c++) {
                if (f < files.length) {
                    collage.add(r, c, files[f]);
                    if (files[f] != null) {
                        g2d.setColor(Color.BLUE);
                    } else {
                        g2d.setColor(Color.RED);
                    }
                } else {
                    collage.add(r, c, null);

                    g2d.setColor(Color.LIGHT_GRAY);
                }
                f++;

                g2d.fillRect(x, y, squareSize, squareSize);
                img = SwingFXUtils.toFXImage(image, null);
                shadowImage.setImage(img);

                x += squareSize + space;
            }
            y += squareSize + space;
        }
        g2d.dispose();

        if (previewMode) {
            return;
        }

        int[] saveRatios = new int[writeRatios.length];
        for (int i = 0; i < writeRatios.length; i++) {
            saveRatios[i] = Integer.parseInt(writeRatios[i]);
        }
        LOGGER.info("   Doing it...");
        collage.run(ctrl, outPath, filePrefix, saveRatios, status, shadowImage);
        LOGGER.info("~~~~All done~~~~");
        ctrl.setStatus("All Done");
        ctrl.stopProcessing();
    }

    private static List<File> getFiles(String srcPath, int rows, int cols, int maxImages, String[] patterns) {

        // pattern:  myfile[1..25]
        List<File> files = new ArrayList();

        int i = 1;
        for (int r = 0; i <= maxImages && r < rows; r++) {
            for (int c = 0; i <= maxImages && c < cols; c++) {
                File f;
                if (patterns[r].contains("%")) {
                    f = new File(srcPath + File.separator + String.format(patterns[r] + ".png", i++));
                } else {
                    f = new File(srcPath + File.separator + patterns[r] + i++ + ".png");

                }
                if (f.exists() && !f.isDirectory()) {
                    files.add(f);
                }
                if (!f.exists()) {
                    LOGGER.error("Cannot read file: " + f.getAbsolutePath());
                    files.add(null);
                }
            }
        }
        return files;
    }

    private static Collage collage;
}
