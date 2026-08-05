package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class MyTextGraphicsConverter implements TextGraphicsConverter {
    private double maxRatio = Double.POSITIVE_INFINITY;
    private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;
    private TextColorSchema schema = new MyTextColorSchema();

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        int width = img.getWidth();
        int height = img.getHeight();
        double ratio = (double) Math.max(width, height) / Math.min(width, height);


        if( ratio > maxRatio){
            throw new BadImageSizeException(ratio, maxRatio);
        }

        int newWidth = width;
        int newHeight = height;

        if (width > maxWidth) {
            double scale = (double) maxWidth / width;

            newWidth = (int) (scale * width);
            newHeight = (int) (scale * height);
        }

        if (newHeight > maxHeight) {
            double scale = (double) maxHeight / newHeight;

            newWidth = (int) (scale * newWidth);
            newHeight = (int) (scale * newHeight);
        }

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        WritableRaster bwRaster = bwImg.getRaster();


        StringBuilder resultText = new StringBuilder();
        for (int h = 0; h < newHeight; h++){
            for (int w = 0; w < newWidth; w++){
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                resultText.append(c);
                resultText.append(c);
            }
            resultText.append('\n');
        }
        return resultText.toString();
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}
