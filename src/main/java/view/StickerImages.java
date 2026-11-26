package view;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Downloads sticker images in the background, scales them, and remembers them.
 * Shared by the sticker panel and the board so an image is only downloaded once.
 */
public final class StickerImages {
    private static final int THREADS = 6;
    private static final ExecutorService LOADER = Executors.newFixedThreadPool(THREADS, runnable -> {
        final Thread thread = new Thread(runnable, "sticker-image-loader");
        thread.setDaemon(true);
        return thread;
    });
    private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

    private StickerImages() {
    }

    /**
     * Loads an image scaled to fit in a square, then calls back on the UI thread.
     *
     * @param url the image address
     * @param size the largest width or height, in pixels
     * @param onLoaded called with the image, or with null if it couldn't be loaded
     */
    public static void load(String url, int size, Consumer<ImageIcon> onLoaded) {
        final String key = size + "|" + url;
        final ImageIcon cached = CACHE.get(key);
        if (cached != null) {
            onLoaded.accept(cached);
            return;
        }
        LOADER.submit(() -> {
            ImageIcon result = null;
            try {
                // ImageIO reads the whole image before returning (first frame for GIFs)
                final BufferedImage original = ImageIO.read(URI.create(url).toURL());
                if (original != null) {
                    result = scaleToFit(original, size);
                    CACHE.put(key, result);
                }
            } catch (IOException | IllegalArgumentException ex) {
                result = null;
            }
            final ImageIcon finalResult = result;
            SwingUtilities.invokeLater(() -> onLoaded.accept(finalResult));
        });
    }

    private static ImageIcon scaleToFit(BufferedImage image, int size) {
        final double scale = Math.min((double) size / image.getWidth(), (double) size / image.getHeight());
        final int width = Math.max(1, (int) (image.getWidth() * scale));
        final int height = Math.max(1, (int) (image.getHeight() * scale));
        final BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return new ImageIcon(scaled);
    }
}
