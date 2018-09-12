package net.giraudy.collagr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author etiennegiraudy
 */
public class Utils {

    private static final Logger LOGGER
            = LogManager.getLogger(Utils.class);

    public static ImgDim getImageDim(final String path) throws IOException {
        ImgDim result = null;
        String suffix = getFileSuffix(path);
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        if (iter.hasNext()) {
            ImageReader reader = iter.next();
            try {
                ImageInputStream stream = new FileImageInputStream(new File(path));
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                result = new ImgDim(height, width);
            } finally {
                reader.dispose();
            }
        } else {
            LOGGER.error("No reader found for given format: " + suffix);
        }
        return result;
    }

    private static String getFileSuffix(final String path) {
        String result = null;
        if (path != null) {
            result = "";
            if (path.lastIndexOf('.') != -1) {
                result = path.substring(path.lastIndexOf('.'));
                if (result.startsWith(".")) {
                    result = result.substring(1);
                }
            }
        }
        return result;
    }

    public static void writeCustomData(BufferedImage buffImg, String key, String value) throws IIOInvalidTreeException, IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();

        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);

        //adding metadata
        IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);

        IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
        textEntry.setAttribute("keyword", key);
        textEntry.setAttribute("value", value);

        IIOMetadataNode text = new IIOMetadataNode("tEXt");
        text.appendChild(textEntry);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_png_1.0");
        root.appendChild(text);

        metadata.mergeTree("javax_imageio_png_1.0", root);

        //writing the data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream stream = ImageIO.createImageOutputStream(baos);
        writer.setOutput(stream);
        writer.write(metadata, new IIOImage(buffImg, null, metadata), writeParam);
        stream.close();
    }
}
