package com.company.running;

import java.nio.ByteBuffer;

// TODO: TECH DEBT REFACTOR
public class Segment {
    int startPointID;
    int endPointID;

    public Segment(int startPointID, int endPointID) {
        this.startPointID = startPointID;
        this.endPointID = endPointID;
    }

    public byte[] toByte() {
        ByteBuffer buffer = ByteBuffer.allocate(2 * Integer.BYTES);

        buffer.putInt(this.startPointID);
        buffer.putInt(this.endPointID);

        byte[] bytes = buffer.array();
        return bytes;
    }

    public static Segment toSegment(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        Segment s = new Segment(buffer.getInt(), buffer.getInt());

        return s;
    }

    @Override
    public String toString() {
        return "Start: " + this.startPointID + " " + "End: " + this.endPointID;
    }
}
