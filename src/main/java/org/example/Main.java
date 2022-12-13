package org.example;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;

public class Main {

    private static final int panelSizeX = 128 * 2;
    private static final int panelSizeY = 128;
    public static byte[] rgb = new byte[panelSizeX * panelSizeY * 3];

    public static ArrayList<ArtNetSender> artNetSenders = new ArrayList<>();

    private static ch.bildspur.artnet.ArtNetClient artNetClient;

    public static void main(String[] args) throws SocketException, AWTException, InterruptedException {
        System.out.println("Starting");

        artNetClient = new ch.bildspur.artnet.ArtNetClient();
        artNetClient.start("192.168.178.187");

        int universes = panelSizeX * panelSizeY * 3 / 510;
        int subnets = universes / 16;
        for (int i = 0; i < subnets; i++) {
            for (int n = 0; n < 16; n++) {
                ArtNetSender artNetSender = new ArtNetSender(i, n);
                artNetSenders.add(artNetSender);
            }
        }

        screenAndVideo();
    }

    public static void screenAndVideo() throws AWTException, SocketException {
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        Robot robot = new Robot(screens[0]);
        Rectangle rectangle = new Rectangle();
        Rectangle screenBounds = screens[0].getDefaultConfiguration().getBounds();
        int x = screenBounds.x;
        int y = screenBounds.y;
        rectangle.setLocation(x, y);
        rectangle.setSize(panelSizeX + 1, panelSizeY + 1);

        while (true) {
            BufferedImage image = robot.createScreenCapture(rectangle);
            readImage(image);
            sendToArtNet();
        }
    }

    public static void readImage(BufferedImage image) {
        int rgbCounterNumber = 0;
        for (int y = 0; y < panelSizeY; y++) {
            for (int x = 0; x < panelSizeX; x++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                rgb[rgbCounterNumber] = (byte) blue;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) green;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) red;
                rgbCounterNumber++;
            }
        }
    }

    public static void sendToArtNet() {
        int pixel = 0;
        int pos = 0;
        for (int subnet = 0; subnet < 12; subnet++) {
            for (int universe = 0; universe < 16; universe++) {
                byte[] message = new byte[512];
                System.arraycopy(rgb, pos, message, 0, 510);
                artNetClient.unicastDmx("192.168.178.178", subnet, universe, message);
                pos = pos + 510;
            }
        }
    }
}