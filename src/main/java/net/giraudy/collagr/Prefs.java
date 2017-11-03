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

import java.util.prefs.Preferences;

/**
 *
 * @author etiennegiraudy
 */
public class Prefs {

    private final Preferences prefs;

    private String inputDir;
    private String outputDir;
    private String patterns;
    private String nImages;
    private String readRatio;
    private String nRows;
    private String nCols;
    private String spacing;
    private String border;
    private String outFilePrefix;
    private String writeRatios;

    public Prefs() {
        prefs = Preferences.userRoot().node(this.getClass().getName());
        readPrefs();
    }

    public void save(
            String inputDir,
            String outputDir,
            String patterns,
            String nImages,
            String readRatio,
            String nRows,
            String nCols,
            String spacing,
            String border,
            String outFilePrefix,
            String writeRatios
    ) {
        setInputDir(inputDir);
        setOutputDir(outputDir);
        setPatterns(patterns);
        setnImages(nImages);
        setReadRatio(readRatio);
        setnRows(nRows);
        setnCols(nCols);
        setSpacing(spacing);
        setBorder(border);
        setOutFilePrefix(outFilePrefix);
        setWriteRatios(writeRatios);
    }

    private void readPrefs() {
        inputDir = prefs.get("inputDir", "");
        outputDir = prefs.get("outputDir", "");
        patterns = prefs.get("patterns", "");
        readRatio = prefs.get("readRatio", "0");
        outFilePrefix = prefs.get("outFilePrefix", "");
        nImages = prefs.get("nImages", "100");
        nRows = prefs.get("nRows", "10");
        nCols = prefs.get("nCols", "10");
        spacing = prefs.get("spacing", "25");
        border = prefs.get("border", "0");
        writeRatios = prefs.get("writeRatios", "1,2,4");
    }

    public String getInputDir() {
        return inputDir;
    }

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
        prefs.put("inputDir", inputDir);
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
        prefs.put("outputDir", outputDir);
    }

    public String getPatterns() {
        return patterns;
    }

    public void setPatterns(String patterns) {
        this.patterns = patterns;
        prefs.put("patterns", patterns);
    }

    public String getReadRatio() {
        return readRatio;
    }

    public void setReadRatio(String readRatio) {
        this.readRatio = readRatio;
        prefs.put("readRatio", readRatio);
    }

    public String getnImages() {
        return nImages;
    }

    public void setnImages(String nImages) {
        this.nImages = nImages;
        prefs.put("nImages", nImages);
    }

    public String getnRows() {
        return nRows;
    }

    public void setnRows(String nRows) {
        this.nRows = nRows;
        prefs.put("nRows", nRows);
    }

    public String getnCols() {
        return nCols;
    }

    public void setnCols(String nCols) {
        this.nCols = nCols;
        prefs.put("nCols", nCols);
    }

    public String getSpacing() {
        return spacing;
    }

    public void setSpacing(String spacing) {
        this.spacing = spacing;
        prefs.put("spacing", spacing);
    }

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
        prefs.put("border", border);
    }

    public String getOutFilePrefix() {
        return outFilePrefix;
    }

    public void setOutFilePrefix(String outFilePrefix) {
        this.outFilePrefix = outFilePrefix;
        prefs.put("outFilePrefix", outFilePrefix);
    }

    public String getWriteRatios() {
        return writeRatios;
    }

    public void setWriteRatios(String writeRatios) {
        this.writeRatios = writeRatios;
        prefs.put("writeRatios", writeRatios);
    }
}
