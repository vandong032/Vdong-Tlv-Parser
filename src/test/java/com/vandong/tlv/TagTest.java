package com.vandong.tlv;

import org.junit.Test;

import com.vandong.tlv.Tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TagTest {

    @Test
    public void testByteArrayConst() {
        assertEquals(new Tag(0x6f, 0x1c), new Tag(new byte[]{0x6f, 0x1c}));
        assertEquals(new Tag(new byte[]{0x6f, 0x1c}, 0, 2), new Tag(new byte[]{0x6f, 0x1c}));
        assertEquals(new Tag(0x6f, 0x1c).hashCode(), new Tag(new byte[]{0x6f, 0x1c}).hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(new Tag(0x01, 0x02, 0x03), new Tag(new byte[]{0x01, 0x02, 0x03}));
        assertEquals(new Tag(0x01, 0x02), new Tag(new byte[]{0x01, 0x02}));
        assertEquals(new Tag(0x01), new Tag(new byte[]{0x01}));

        assertNotEquals(new Tag(0x01), new Tag(new byte[]{0x02}));
        assertNotEquals(new Tag(0x01), new Tag(new byte[]{0x01, 0x01}));
        assertNotEquals(new Tag(0x01, 0x1), new Tag(new byte[]{0x02, 0x1}));
    }

    @Test
    public void testHashcode() {
        assertEquals(new Tag(0x01, 0x02, 0x03).hashCode(), new Tag(new byte[]{0x01, 0x02, 0x03}).hashCode());
        assertEquals(new Tag(0x01, 0x02).hashCode(), new Tag(new byte[]{0x01, 0x02}).hashCode());
        assertEquals(new Tag(0x01).hashCode(), new Tag(new byte[]{0x01}).hashCode());

        assertNotEquals(new Tag(0x01).hashCode(), new Tag(new byte[]{0x02}).hashCode());
        assertNotEquals(new Tag(0x01).hashCode(), new Tag(new byte[]{0x01, 0x01}).hashCode());
        assertNotEquals(new Tag(0x01, 0x1).hashCode(), new Tag(new byte[]{0x02, 0x1}).hashCode());
    }
}