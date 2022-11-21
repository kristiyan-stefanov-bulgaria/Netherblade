package com.hawolt.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created: 31/07/2022 00:11
 * Author: Twitter @hawolt
 **/

public abstract class Graphics2DComponent extends JComponent {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        paint(graphics2D);
    }

    protected abstract void paint(Graphics2D g);
}
