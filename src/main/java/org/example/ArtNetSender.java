package org.example;

import ch.bildspur.artnet.ArtNetClient;

public class ArtNetSender {

    private final byte[] dmxData = new byte[512];

    private int subnet;
    private int universe;

    public ArtNetSender(int subnet, int universe) {
        this.subnet = subnet;
        this.universe = universe;
    }

    public void sendArtNetData(byte[] data, ArtNetClient artNetClient) {
        artNetClient.unicastDmx("192.168.178.178",subnet, universe, data);
    }

}
