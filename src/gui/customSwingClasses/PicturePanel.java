package gui.customSwingClasses;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PicturePanel extends JPanel {

    private BufferedImage image = null;

    // Panel Constants
    private final static int PANEL_WIDTH = 305;
    private final static int PANEL_HEIGHT = 210;

    // Offsets
    private int xOffset = 0;
    private int yOffset = 0;
    private int panelWidthOffset = 0;
    private int panelHeightOffset = 0;

    private Color bgColor = new Color(242, 242, 242, 255);

    PicturePanel() {
        super();
    }

    public void setImage(BufferedImage image) {
        try {
            BufferedImage defaultImage = ImageIO.read(getClass().getResource("/Resources/defaultImage.png"));
            this.image = image != null ? image : defaultImage;
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    @Override
    public void paint(Graphics g) {
        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();

        g.clearRect(0, 0, panelWidth, panelHeight);

        if (image != null) {
            // Draw image
            imageCrop(image);

            g.setColor(bgColor);
            g.fillRect(0, 0, panelWidth, panelHeight);
            g.drawImage(image, xOffset, yOffset, panelWidth - panelWidthOffset, panelHeight - panelHeightOffset, bgColor,null);

            // Reset Offsets
            xOffset = 0;
            yOffset = 0;
            panelWidthOffset = 0;
            panelHeightOffset = 0;
        }
    }

    void imageCrop(BufferedImage image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        double widthPercent = (double)PANEL_WIDTH / imageWidth;
        double heightPercent = (double)PANEL_HEIGHT / imageHeight;
        double percentDifference;

        if (widthPercent > heightPercent) {
            percentDifference = widthPercent - heightPercent;

            widthPercent -= percentDifference;

            int minusPixels = PANEL_WIDTH - (int)(image.getWidth() * widthPercent);

            xOffset = minusPixels / 2;
            panelWidthOffset = minusPixels;
        }
        if (widthPercent < heightPercent) {
            percentDifference = heightPercent - widthPercent;

            heightPercent -= percentDifference;

            int minusPixels = PANEL_HEIGHT - (int)(image.getHeight() * heightPercent);

            yOffset = minusPixels / 2;
            panelHeightOffset = minusPixels;
        }
    }
}
