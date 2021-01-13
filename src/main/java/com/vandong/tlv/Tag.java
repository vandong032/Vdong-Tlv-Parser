package com.vandong.tlv;

import java.util.Arrays;

public class Tag {
    public final byte[] bytes;

    /**
     * Creates a new tag from given byte array. Similar {@link Tag#BerTag(byte[], int, int)} but using
     * the full array.
     *
     * @param aBuf to create the tag
     */
    public Tag(byte[] aBuf) {
        this(aBuf, 0, aBuf.length);
    }

    public Tag(byte[] aBuf, int aOffset, int aLength) {
        byte[] temp = new byte[aLength];
        System.arraycopy(aBuf, aOffset, temp, 0, aLength);
        bytes = temp;
    }

    public Tag(int aFirstByte, int aSecondByte) {
        bytes = new byte[]{(byte) (aFirstByte), (byte) aSecondByte};
    }

    public Tag(int aFirstByte, int aSecondByte, int aFirth) {
        bytes = new byte[]{(byte) (aFirstByte), (byte) aSecondByte, (byte) aFirth};
    }

    public Tag(int aFirstByte) {
        bytes = new byte[]{(byte) aFirstByte};
    }

    public boolean isConstructed() {
        return (bytes[0] & 0x20) != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return Arrays.equals(bytes, tag.bytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return (isConstructed() ? "+ " : "- ") + HexUtil.toHexString(bytes, 0, bytes.length);
    }
}

