package com.vandong.tlv;

import org.junit.Assert;
import org.junit.Test;

import com.vandong.tlv.Tag;
import com.vandong.tlv.EmvTlv;
import com.vandong.tlv.EmvTlvLogger;
import com.vandong.tlv.EmvTlvParser;
import com.vandong.tlv.Tlvs;
import com.vandong.tlv.HexUtil;

public class TlvParserTest {

    public static final Tag TAG_DF0D_ID = new Tag(0xdf, 0x0d);
    public static final Tag TAG_DF7F_VERSION = new Tag(0xdf, 0x7f);
    private static final VDEMVParser LOG = new VDEMVParser();
    public static final Tag T_EF = new Tag(0xEF);


    @Test
    public void testParse() {
        String hex =
        /*            0  1  2  3   4  5  6  7     8  9  a  b   c  d  e  f      0123 4567  89ab  cdef */
        /*    0 */   "50 04 56 49  53 41 57 13    10 00 02 31  00 00 00 33" // P.VI SAW.  ...1  ...3
        /*   10 */ + "d4 41 22 01  10 03 40 00    00 48 1f 5a  08 10 00 02" // .A". ..@.  .H.Z  ....
        /*   20 */ + "31 00 00 00  33 5f 20 1a    43 41 52 44  33 2f 45 4d" // 1... 3_ .  CARD  3/EM
        /*   30 */ + "56 20 20 20  20 20 20 20    20 20 20 20  20 20 20 20" // V
        /*   40 */ + "20 20 5f 24  03 44 12 31    5f 28 02 06  43 5f 2a 02" //   _$ .D.1  _(..  C_*.
        /*   50 */ + "06 43 5f 30  02 02 01 5f    34 01 06 82  02 5c 00 84" // .C_0 ..._  4...  .\..
        /*   60 */ + "07 a0 00 00  00 03 10 10    95 05 40 80  00 80 00 9a" // .... ....  ..@.  ....
        /*   70 */ + "03 14 02 10  9b 02 e8 00    9c 01 00 9f  02 06 00 00" // .... ....  ....  ....
        /*   80 */ + "00 03 01 04  9f 03 06 00    00 00 00 00  00 9f 06 07" // .... ....  ....  ....
        /*   90 */ + "a0 00 00 00  03 10 10 9f    09 02 00 8c  9f 10 07 06" // .... ....  ....  ....
        /*   a0 */ + "01 0a 03 a0  a1 00 9f 1a    02 08 26 9f  1c 08 30 36" // .... ....  ..&.  ..06
        /*   b0 */ + "30 34 35 33  39 30 9f 1e    08 30 36 30  34 35 33 39" // 0453 90..  .060  4539
        /*   c0 */ + "30 9f 26 08  cb fc 76 79    77 11 1f 15  9f 27 01 80" // 0.&. ..vy  w...  .'..
        /*   d0 */ + "9f 33 03 e0  b8 c8 9f 34    03 5e 03 00  9f 35 01 22" // .3.. ...4  .^..  .5."
        /*   e0 */ + "9f 36 02 00  0e 9f 37 04    46 1b da 7c  9f 41 04 00" // .6.. ..7.  F..|  .A..
        /*   f0 */ + "00 00 63                                            " // ..c
                ;
        parse(hex);
    }

    private Tlvs parse(String hex) {
        byte[] bytes = HexUtil.parseHex(hex);

        EmvTlvParser parser = new EmvTlvParser(LOG);
        Tlvs tlvs = parser.parse(bytes, 0, bytes.length);
        EmvTlvLogger.log("    ", tlvs, LOG);
        return tlvs;
    }

    @Test
    public void testParseLen2() {
        String hex =
        /*            0  1  2  3   4  5  6  7     8  9  a  b   c  d  e  f      0123 4567  89ab  cdef */
        /*    0 */   "e0 81 91 71  7f 9f 18 04    12 34 56 78  86 0d 84 24" // ...q ....  .4Vx  ...$
        /*   10 */ + "00 00 08 5a  e9 57 e8 81    8d 95 a8 86  0d 84 24 00" // ...Z .W..  ....  ..$.
        /*   20 */ + "00 08 36 d2  44 95 47 47    ec 1e 86 0d  84 24 00 00" // ..6. D.GG  ....  .$..
        /*   30 */ + "08 38 63 b1  c1 79 be 38    ac 86 0d 84  24 00 00 08" // .8c. .y.8  ....  $...
        /*   40 */ + "25 4d b4 b4  ec db 21 74    86 0d 84 24  00 00 08 67" // %M.. ..!t  ...$  ...g
        /*   50 */ + "8d f9 12 84  78 e2 8f 86    0d 84 24 00  00 08 51 4c" // .... x...  ..$.  ..QL
        /*   60 */ + "8f d9 5a 21  6c 0b 86 0d    84 24 00 00  08 1f 62 34" // ..Z! l...  .$..  ..b4
        /*   70 */ + "65 db 0c 95  59 86 0d 84    24 00 00 08  2a 5a 0a 9a" // e... Y...  $...  *Z..
        /*   80 */ + "82 8a ba 0a  91 0a 42 50    0e 43 81 a4  67 7a 30 30" // .... ..BP  .C..  gz00
        /*   90 */ + "8a 02 30 30                                         " // ..00
        ;
        parse(hex);
    }


    @Test
    public void testMulti() {
        String hex =
        /*            0  1  2  3   4  5  6  7     8  9  a  b   c  d  e  f      0123 4567  89ab  cdef */
        /*    0 */   "e1 35 9f 1e  08 31 36 30    32 31 34 33  37 ef 12 df" // .5.. .160  2143  7...
        /*   10 */ + "0d 08 4d 30  30 30 2d 4d    50 49 df 7f  04 31 2d 32" // ..M0 00-M  PI..  .1-2
        /*   20 */ + "32 ef 14 df  0d 0b 4d 30    30 30 2d 54  45 53 54 4f" // 2... ..M0  00-T  ESTO
        /*   30 */ + "53 df 7f 03  36 2d 35                               " // S... 6-5
        ;
        Tlvs tlvs = parse(hex);
        Assert.assertNotNull(tlvs.find(new Tag(0xe1)));
        Assert.assertEquals("1 6 0 2 1 4 3 7".replace(" ", ""), tlvs.find(new Tag(0x9f, 0x1e)).getTextValue());
        Assert.assertNotNull(tlvs.find(T_EF));
        Assert.assertEquals(2, tlvs.findAll(T_EF).size());

        // first EF
        {
            EmvTlv firstEf = tlvs.find(T_EF);
            Assert.assertNotNull(firstEf);
            Assert.assertNotNull(firstEf.find(TAG_DF0D_ID));
            Assert.assertEquals("M000-MPI", firstEf.find(TAG_DF0D_ID).getTextValue());
            Assert.assertNotNull("No EF / DF7F tag found", firstEf.find(TAG_DF7F_VERSION));
            Assert.assertEquals("1-22", firstEf.find(TAG_DF7F_VERSION).getTextValue());
        }

        // second EF
        EmvTlv secondEf = tlvs.findAll(T_EF).get(1);
        Assert.assertNotNull(secondEf);
        Assert.assertNotNull(secondEf.find(TAG_DF0D_ID));
        Assert.assertEquals("M000-TESTOS", secondEf.find(TAG_DF0D_ID).getTextValue());
        Assert.assertNotNull("No EF / DF7F tag found", secondEf.find(TAG_DF7F_VERSION));
        Assert.assertEquals("6-5", secondEf.find(TAG_DF7F_VERSION).getTextValue());


    }

    @Test
    public void test_empty_length() {
        EmvTlv tlv = new EmvTlvParser(LOG).parseConstructed(HexUtil.parseHex("E3 02 01 00"));
        EmvTlvLogger.log("    ", tlv, LOG);

        Assert.assertEquals(tlv.getTag(), new Tag(0xe3));
        Assert.assertNotNull(tlv.getValues());
        Assert.assertEquals(1, tlv.getValues().size());
        EmvTlv emptyTag = tlv.getValues().get(0);
        Assert.assertEquals(new Tag(0x01), emptyTag.getTag());
        Assert.assertEquals("", emptyTag.getHexValue());
    }

    @Test
    public void test_empty_hex() {
        byte[] bytes = HexUtil.parseHex("");
        EmvTlvParser parser = new EmvTlvParser(LOG);
        Tlvs tlvs = parser.parse(bytes, 0, bytes.length);
        Assert.assertEquals(0, tlvs.getList().size());
    }

    @Test
    public void test_issue_10_last_tag() {
    	System.out.println("===========================Begin EMV Tag Parser===============================");
        String hex = "9F02060000000001009F03060000000000009F1A020704950580000480005F2A0207049A031612319C01009F37045F17A56F820218009F360200019F10200FA501A000F8000000000000000000000F0000000000000000000000000000009F2608FAC3B60382F042D99F2701809F34030203008407A0000007271010";
        byte[] bytes = HexUtil.parseHex(hex);
        EmvTlvParser parser = new EmvTlvParser();
        Tlvs tlvs = parser.parse(bytes, 0, bytes.length);

        EmvTlvLogger.log("Tag:", tlvs, LOG);

        {
            EmvTlv emvTlv = tlvs.find(new Tag(0xDF, 0x05));
            Assert.assertNotNull(emvTlv);
            Assert.assertEquals("03", emvTlv.getHexValue());
        }

        {
            Assert.assertEquals(1, tlvs.getList().size());
            Assert.assertTrue(tlvs.getList().get(0).isConstructed());
            Assert.assertEquals(6, tlvs.getList().get(0).getValues().size());
        }
    }
}
