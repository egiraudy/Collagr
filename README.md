# Collagr
Desktop (JavaFX) application that generates a collage from collections of pictures.

## a few screenhsots

**when setting up the parameters, with a preview of the collage structure**
<img width="1342" alt="setting up co/rows/images" src="https://user-images.githubusercontent.com/666203/38143968-baf72a18-33f7-11e8-8b2c-13fc73d6200d.png">
**click the "preview" button to check which image file are potentially missing**
<img width="1342" alt="preview - shows missing images" src="https://user-images.githubusercontent.com/666203/38143967-bae2a886-33f7-11e8-8cb2-a7afe8099f42.png">
**Collagr hard at work**
<img width="1342" alt="running" src="https://user-images.githubusercontent.com/666203/38143966-baced4aa-33f7-11e8-9232-b4bcb2ef6eef.png">

## parameters

* **read ratio**: image size reducing factor when reading source files in order to be easier on the memory for large collages
* **images**: number of images to add to the collage
* **rows**: number of lines
* **cols**: number of columns
* **border**: border size (pixels)
* **spacing**: image spacing (pixels)
* **input directory**: source directory for image files
* **patterns**: image file name prefix, there should be one per row. <pattern><counter>.png
* **output directory**: where collages are written
* **out file prefix**: output file names are: <prefix><write ratio>.png
* **write ratio**: image size reducing factor(s) to avoid super large files

## build it!

mvn clean compile jfx:jar<br/>
   _produces a runnable jar file: target/jfx/Collagr2-1.0-SNAPSHOT-jfx.jar_

mvn clean compile jfx:native<br/>
   _produces a native app, installer in target/jfx/native_

-- more to come!
