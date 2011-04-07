package org.mozilla.browser.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.mozilla.browser.MozillaAutomation;
import org.mozilla.browser.MozillaWindow;
import org.mozilla.browser.common.Platform;

/**
 * Render web page to an image
 */
public class Example10_RenderToImage {

    public static void main(String[] args) throws Exception {
        //load a web page in a hidden window
        MozillaWindow win = new MozillaWindow();
    	win.setSize(1024, 768);
        if (Platform.platform!=Platform.OSX) {
        	win.addNotify();
        } else {
        	//on osx the window must be visible,
        	//so move it offscreen
            win.setUndecorated(true);
        	win.setLocation(2000, 2000);
            win.setVisible(true);
        }
        MozillaAutomation.blockingLoad(win, "http://www.yahoo.com"); //$NON-NLS-1$
        //MozillaAutomation.blockingLoad(win, "about:");
        //render the web page to an image
        byte[] pngImage = MozillaAutomation.renderToImage(win);
        ByteArrayInputStream bis = new ByteArrayInputStream(pngImage);
        BufferedImage img = ImageIO.read(bis);

        //display the new image
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(300, 300);
        TransPanel p = new TransPanel(img);
        f.getContentPane().add(p, BorderLayout.CENTER);
        f.setVisible(true);
    }

    static class TransPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        BufferedImage img;
        double angle = 0;

        public TransPanel(BufferedImage img){
            this.img = img;
            setBackground(Color.white);
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            sleep(1000/30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        angle+=Math.PI/60;
                        repaint();
                    }
                }
            }.start();
        }


        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            Dimension d = getSize();
            double w = d.width;
            double h = d.height;

            double ih = img.getHeight();
            double iw = img.getWidth();

            AffineTransform at = new AffineTransform();

            double sx = 0.66d * w/iw;
            double sy = 0.66d * h/ih;

            iw = sx*iw;
            ih = sy*ih;
            at.translate((w-iw)/2d, (h-ih)/2d);
            at.rotate(Math.PI/4+angle, iw/2d, ih/2d);
            g2.setTransform(at);

            AffineTransform at2 = new AffineTransform();
            at2.scale(sx, sy);
            g2.drawImage(img, at2, null);
        }
    }
}

