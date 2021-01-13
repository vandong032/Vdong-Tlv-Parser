package com.vandong.tlv;

import org.junit.Test;

import com.vandong.tlv.HexUtil;

public class HexUtilTest {

    @Test
    public void testBytes() {
        for(int i=0; i<HexUtil.BYTES.length; i++) {
            if(i%(128/8)==0) {
                System.out.println();
            }
            System.out.print(String.format(", %2d", HexUtil.BYTES[i]));
        }
        System.out.println();
        System.out.println("HexUtil.BYTES.length = " + HexUtil.BYTES.length);
    }
}
