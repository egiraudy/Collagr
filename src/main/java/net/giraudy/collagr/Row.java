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

import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author etiennegiraudy
 */
class Row {
    
    private int height;
    private int width;
    private int posHeight;
    private File[] files;
    private BufferedImage[] images;

    public Row(int colNum) {
        files = new File[colNum];
        images = new BufferedImage[colNum];
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getPosHeight() {
        return posHeight;
    }

    public void setPosHeight(int posHeight) {
        this.posHeight = posHeight;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public BufferedImage[] getImages() {
        return images;
    }

    public void setImages(BufferedImage[] images) {
        this.images = images;
    }
    
}
