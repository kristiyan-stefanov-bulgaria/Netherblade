package com.hawolt.ui;

import com.hawolt.logger.Logger;
import com.hawolt.util.RunLevel;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import org.cef.CefApp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created: 31/07/2022 00:11
 * Author: Twitter @hawolt
 **/

public class Netherblade {

    public static void create() throws IOException {
        JFrame frame = new JFrame();
        String icon = "netherblade.png";
        frame.setIconImage(ImageIO.read(RunLevel.get(icon)));
        frame.setTitle("Netherblade");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (CefApp.getState() != CefApp.CefAppState.NONE) CefApp.getInstance().dispose();
                frame.dispose();
                System.exit(0);
            }
        });
        Container container = frame.getContentPane();
        container.setPreferredSize(new Dimension(350, 100));
        container.setLayout(new BorderLayout());
        VisualProgressHandler handler = new VisualProgressHandler();
        container.add(handler, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Path path = Paths.get(System.getProperty("java.io.tmpdir")).resolve("jcef-bundle");
        try {
            Chromium chromium = new Chromium("http://127.0.0.1:35199", path, handler);
            container.removeAll();
            container.setPreferredSize(new Dimension(1000, 620));
            container.add(chromium.getBrowserUI(), BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
        } catch (UnsupportedPlatformException | CefInitializationException | IOException | InterruptedException e) {
            Logger.error(e);
        }
    }
}
