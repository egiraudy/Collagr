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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imgscalr.Scalr;

/**
 * Image coordinates: X:horizontal axis, from left to right Y:vertical axis,
 * from top to bottom
 *
 * @author etiennegiraudy
 */
public class Collage {

    private static final Logger LOGGER
            = LogManager.getLogger(Collage.class);

    private final int rowNum;
    private final int colNum;

    private final int spacing;
    private final int border;

    private int height;
    private int width;

    private final Row[] rows;
    private BufferedImage image;

    private int readDivRatio = 2;

    private ImageView shadowImage;

    public Collage(int r, int c, int _readRatio, int _spacing, int _border) {
        rowNum = r;
        colNum = c;
        readDivRatio = _readRatio;
        spacing = _spacing;
        border = _border;
        rows = new Row[rowNum];
        for (int i = 0; i < rowNum; i++) {
            rows[i] = new Row(colNum);
        }
    }

    void add(int r, int c, File file) {
        if (rows[r].getFiles() == null) {
            rows[r].setFiles(new File[colNum]);
        }
        rows[r].getFiles()[c] = file;
    }

    public void run(UIController ctrl, String fileOutPath, String fileNamePrefix, int[] saveRatios, Label status, ImageView _shadowImage) throws IOException, InterruptedException {
        shadowImage = _shadowImage;
        ctrl.setStatus("Reading pictures size");
        readPicturesSizes();
        if (Thread.interrupted()) {
            // We've been interrupted: no more crunching.
            LOGGER.info("@@@@@@@@@ interrupted!");
                    throw new InterruptedException();
        }
        ctrl.setStatus("Init image");
        initImage();
        if (Thread.interrupted()) {
            // We've been interrupted: no more crunching.
            LOGGER.info("@@@@@@@@@ interrupted!");
                    throw new InterruptedException();
        }
        ctrl.setStatus("Adding pictures");
        addPictures();
        if (Thread.interrupted()) {
            // We've been interrupted: no more crunching.
            LOGGER.info("@@@@@@@@@ interrupted!");
                    throw new InterruptedException();
        }
        ctrl.setStatus("Saving files");
        save(fileOutPath, fileNamePrefix, saveRatios);

    }

    private void readPicturesSizes() throws InterruptedException {
        int idxR = 0;
        int idxImg = 0;
        LOGGER.info("Reading pictures...");
        for (Row r : rows) {
            LOGGER.info("- row#" + (idxR + 1) + " is " + r.getWidth() + " * " + r.getHeight());
            int idxF = 0;
            for (File f : r.getFiles()) {
                if (Thread.interrupted()) {
                    // We've been interrupted: no more crunching.
                    LOGGER.info("@@@@@@@@@ interrupted!");
                    throw new InterruptedException();
                }
                try {
                    idxImg++;
                    if (f != null) {
                        BufferedImage img = ImageIO.read(f);
                        int h = img.getHeight() / readDivRatio;
                        int w = img.getWidth() / readDivRatio;
                        if (w > r.getWidth()) {
                            r.setWidth(w);
                        }
                        if (h > r.getHeight()) {
                            r.setHeight(h);
                        }
                        r.getFiles()[idxF] = f;
                        LOGGER.info("   {" + idxImg + "}[" + (idxR + 1) + "][" + (idxF + 1) + "]" + f + ": " + w + " * " + h);
                    } else {
                        r.getFiles()[idxF] = null;
                        LOGGER.info("   {" + idxImg + "}[" + (idxR + 1) + "][" + (idxF + 1) + "] no file");
                    }
                } catch (IOException ex) {
                    LOGGER.error("***issue reading file: " + f);
                    LOGGER.error(ex.getMessage());
                    r.getFiles()[idxF] = null;
                }
                idxF++;
            }
            LOGGER.info("  -->Row#" + (idxR + 1) + " is " + r.getWidth() + " * " + r.getHeight());
            idxR++;
        }
    }

    private void initImage() {
        for (Row r : rows) {
            height += r.getHeight() + spacing;
            int w = (r.getWidth() + spacing) * r.getFiles().length;
            if (w > width) {
                width = w;
            }
        }
        height += (border + spacing) * 2 - spacing;
        width += (border + spacing) * 2 - spacing;

        LOGGER.info("=== init image: " + width + " * " + height);

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // paint the frame border
        if (border > 0) {
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.DARK_GRAY);
            BasicStroke bs = new BasicStroke(border);
            g2d.setStroke(bs);
            int xLeft = 0 + border / 2;
            int xRight = image.getWidth() - 1 - border / 2;
            int yTop = 0 + border / 2;
            int yBottom = image.getHeight() - 1 - border / 2;
            g2d.drawLine(xLeft, yTop, xRight, yTop);
            g2d.drawLine(xLeft, yTop, xLeft, yBottom);
            g2d.drawLine(xLeft, yBottom, xRight, yBottom);
            g2d.drawLine(xRight, yTop, xRight, yBottom);
            g2d.dispose();
        }

        WritableImage img = SwingFXUtils.toFXImage(image, null);
        shadowImage.setImage(img);

    }

    private void addPictures() throws InterruptedException {
        LOGGER.info("*** Working hard!");
        // paint images, preserving the alpha channels
        Graphics g = image.getGraphics();

        int i = 0;
        int y = 0;
        int count = 0;
        int hPrev = border + spacing;
        for (Row r : rows) {
            int x = 0;
            int j = 0;
            for (File f : r.getFiles()) {
                if (Thread.interrupted()) {
                    // We've been interrupted: no more crunching.
                    LOGGER.info("@@@@@@@@@ interrupted!");
                    throw new InterruptedException();
                }
                BufferedImage img;
                count++;
                try {
                    if (f == null) {
                        LOGGER.info(" {" + count + "} SKIPPING IMAGE (no file) file #" + j + ", row #" + i);
                        continue;
                    }
                    img = ImageIO.read(f);
                    if (img == null) {
                        LOGGER.info(" {" + count + "} SKIPPING IMAGE (no image) file #" + j + ", row #" + i);
                        continue;
                    }
                    if (readDivRatio > 1) {
                        img = Scalr.resize(img, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH,
                                img.getWidth() / readDivRatio, img.getHeight() / readDivRatio, Scalr.OP_ANTIALIAS);
                    }
                    int iX = img.getWidth();
                    int iY = img.getHeight();
                    LOGGER.info("    {" + count + "}[" + (i + 1) + "][" + (j + 1) + "] File size: " + iX + " * " + iY);
                    x = j * (r.getWidth() + spacing) + spacing + border + (iX < r.getWidth() ? (r.getWidth() - iX) / 2 : 0);
                    //y = i * (r.height + spacing) + spacing + border + (iY < r.height ? (r.height - iY) / 2 : 0);
                    y = hPrev + (iY < r.getHeight() ? (r.getHeight() - iY) / 2 : 0);
                    LOGGER.info("     Combining into the result image at " + x + "," + y);
                    g.drawImage(img, x, y, null);
                } catch (IOException ex) {
                    LOGGER.info("{" + count + "}Issue with file #" + j + ", row #" + i);
                    LOGGER.info(ex.getMessage());
                }

                j++;
                WritableImage img2 = SwingFXUtils.toFXImage(image, null);
                shadowImage.setImage(img2);

            }
            i++;
            hPrev += r.getHeight() + spacing;
        }

    }

    private void save(String filePath, String fileNamePrefix, int[] saveRatios) 
            throws IOException, InterruptedException {
        LOGGER.info("*** Saving the work");

        for (int i = 0; i < saveRatios.length; i++) {
            if (Thread.interrupted()) {
                // We've been interrupted: no more crunching.
                LOGGER.info("@@@@@@@@@ interrupted!");
                    throw new InterruptedException();
            }
            LOGGER.info("Writing the result file: " + fileNamePrefix + i + ".png");
            if (saveRatios[i] == 1) {
                ImageIO.write(image, "PNG", new File(filePath, fileNamePrefix + i + ".png"));
                LOGGER.info("   size: " + image.getWidth() + " * " + image.getHeight());
            } else {
                LOGGER.info("Resizing image...");
                BufferedImage img
                        = Scalr.resize(
                                image,
                                Scalr.Method.SPEED,
                                Scalr.Mode.FIT_TO_WIDTH,
                                image.getWidth() / saveRatios[i],
                                image.getHeight() / saveRatios[i],
                                Scalr.OP_ANTIALIAS);
                LOGGER.info("   size: " + img.getWidth() + " * " + img.getHeight());
                ImageIO.write(img, "PNG", new File(filePath, fileNamePrefix + i + ".png"));
            }
        }
    }

}
