package MyClock;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * Class for calculating sizes of text, which will be drawn.
 */
public class StringMetrics {

    /** Font of text. */
    private Font font;

    /** Context. */
    private FontRenderContext context;

    /**
     * Constructor initialize font and context.
     * @param g2  Graphics
     */
    public StringMetrics(Graphics2D g2) {
        font = g2.getFont();
        context = g2.getFontRenderContext();
    }

    /**
     * Get bounds of message.
     * @param message  Message
     * @return  Bounds
     */
    Rectangle2D getBounds(String message) {
        return font.getStringBounds(message, context);
    }

    /**
     * Get width of message in pixels.
     * @param message  Message
     * @return  Width in pixels
     */
    double getWidth(String message) {
        Rectangle2D bounds = getBounds(message);
        return bounds.getWidth();
    }

    /**
     * Get height of message in pixels.
     * @param message  Message
     * @return  Height in pixels
     */
    double getHeight(String message) {
        Rectangle2D bounds = getBounds(message);
        return bounds.getHeight();
    }
}