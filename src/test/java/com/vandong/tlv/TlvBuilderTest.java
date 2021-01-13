package com.vandong.tlv;

import org.junit.Assert;
import org.junit.Test;

import com.vandong.tlv.Tag;
import com.vandong.tlv.EmvTlv;
import com.vandong.tlv.EmvTlvBuilder;
import com.vandong.tlv.EmvTlvLogger;
import com.vandong.tlv.EmvTlvParser;
import com.vandong.tlv.Tlvs;
import com.vandong.tlv.HexUtil;

import static com.vandong.tlv.EmvTlvBuilder.template;
import static com.vandong.tlv.HexUtil.parseHex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TlvBuilderTest {

    public static final Tag TAG_E0 = new Tag(0xe0);
    public static final Tag TAG_86 = new Tag(0x86);
    public static final Tag TAG_71 = new Tag(0x71);

    /**
     * 1 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -50 = 56495341
     23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -57 = 1000023100000033D44122011003400000481F
     23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5A = 1000023100000033
     23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F20 = 43415244332F454D562020202020202020202020202020202020
     23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F24 = 441231
     23:52:48.051 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F28 = 0643
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F2A = 0643
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F30 = 0201
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -5F34 = 06
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -82 = 5C00
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -84 = A0000000031010
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -95 = 4080008000
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9A = 140210
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9B = E800
     23:52:48.052 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9C = 00
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F02 = 000000030104
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F03 = 000000000000
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F06 = A0000000031010
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F09 = 008C
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F10 = 06010A03A0A100
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F1A = 0826
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F1C = 3036303435333930
     23:52:48.053 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F1E = 3036303435333930
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F26 = CBFC767977111F15
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F27 = 80
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F33 = E0B8C8
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F34 = 5E0300
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F35 = 22
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F36 = 000E
     23:52:48.054 [main] DEBUG c.p.i.t.BerTlvLogger                               -      -9F37 = 461BDA7C
     */
    @Test
    public void test() {
        String hex =
            /*            0  1  2  3   4  5  6  7     8  9  a  b   c  d  e  f      0123 4567  89ab  cdef */
            /*    0 */   "50 04 56 49  53 41 57 13    10 00 02 31  00 00 00 33" // P.VI SAW.  ...1  ...3
            /*   10 */ + "d4 41 22 01  10 03 40 00    00 48 1f                " // .A". ..@.  .H.
        ;
        byte[] bytes =  new EmvTlvBuilder()
                .addHex(new Tag(0x50), "56495341")
                .addHex(new Tag(0x57), "1000023100000033D44122011003400000481F")
                .buildArray();
        Assert.assertArrayEquals(parseHex(hex), bytes);
    }

    @Test
    public void test91() {
        EmvTlvBuilder b = template(TAG_E0);

        for(int i=0; i<10; i++) {
            b.addHex(TAG_86, "F9128478E28F860D8424000008514C8F");
        }
        byte[] buf = b.buildArray();
        System.out.println(HexUtil.toFormattedHexString(buf));
    }

    @Test
    public void testComplex() {
        byte[] bytes = template(TAG_E0)
                .add(
                        template(TAG_71)
                                .addHex(TAG_86, "F9128478E28F860D8424000008514C01")
                                .addHex(TAG_86, "F9128478E28F860D8424000008514C02")
                )
                .addHex(TAG_86, "F9128478E28F860D8424000008514C03")
                .buildArray();
        System.out.println(HexUtil.toFormattedHexString(bytes));

        EmvTlv tlv = new EmvTlvParser().parseConstructed(bytes, 0, bytes.length);
        List<EmvTlv> list = tlv.findAll(TAG_86);
        System.out.println("list = " + list);

    }

    @Test
    public void testAddComplex() {
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

        byte[] expectedBuffer = parseHex(hex);
        EmvTlv source = new EmvTlvParser().parseConstructed(expectedBuffer);

        EmvTlvLogger.log("....", source, new VDEMVParser());
        {
            byte[] out = EmvTlvBuilder
                    .from(source)
                    .buildArray();

            EmvTlv outTlv = EmvTlvBuilder
                    .from(source)
                    .buildTlv();

            EmvTlvLogger.log("....", outTlv, new VDEMVParser());

            Assert.assertArrayEquals(expectedBuffer, out);
        }

        {
            EmvTlv outTlv = new EmvTlvBuilder().addBerTlv(source).buildTlv();
            byte[] out = new EmvTlvBuilder().addBerTlv(source).buildArray();

            EmvTlvLogger.log("....", outTlv, new VDEMVParser());
            Assert.assertArrayEquals(expectedBuffer, out);
        }

    }

    @Test
    public void addToTlvs() {
        //  [72]
        //  [9F18] 00009123
        //          [86] 84DA00CB0E0000000000008EBD6E174BAFE4E7
        //          [91] ED3C3B8B03928D0E0012
        String in = "721C9F180400009123861384DA00CB0E0000000000008EBD6E174BAFE4E7910AED3C3B8B03928D0E0012";

        // [72]
        // [9F18] 00009123
        //         [86] 84DA00CB0E0000000000008EBD6E174BAFE4E7
        //         [91] ED3C3B8B03928D0E0012
        // [8A] 00
        String out = "721C9F180400009123861384DA00CB0E0000000000008EBD6E174BAFE4E7910AED3C3B8B03928D0E00128A0100";

        Tlvs tlvs = new EmvTlvParser().parse(parseHex(in));


        EmvTlvBuilder builder = new EmvTlvBuilder(tlvs);
        builder.addBerTlv(new EmvTlv(new Tag(0x8a), new byte[] {0x00}));

        Assert.assertEquals(out, HexUtil.toHexString(builder.buildArray()));


    }

    @Test
    public void getContructedTagBytes() {
        EmvTlv constructedTag = new EmvTlv(new Tag(0xE4)
                , Arrays.asList(new EmvTlv(new Tag(0x86), HexUtil.parseHex("ED3C3B8B03928D0E0012")))
        );

        EmvTlvBuilder builder = new EmvTlvBuilder();
        builder.addBerTlv(constructedTag);
        byte[] bytes = builder.buildArray();
        Assert.assertEquals("[14] :  E4 0C 86 0A  ED 3C 3B 8B  03 92 8D 0E  00 12", HexUtil.toFormattedHexString(bytes));
    }

    @Test
    public void testBigData() {
        byte[] bigData = new byte[1024];

        EmvTlvBuilder builder = new EmvTlvBuilder();
        builder.addBytes(new Tag(0xd8), bigData);
        Tlvs tlvs = builder.buildTlvs();
        EmvTlvLogger.log("    ", tlvs, new VDEMVParser());

        Assert.assertEquals(1, tlvs.getList().size());
    }

    @Test
    public void test_large_value_with_error() {
        try {
            EmvTlvBuilder builder = new EmvTlvBuilder();
            EmvTlv emvTlv = new EmvTlv(new Tag(0x82), new byte[6 * 1024]);
            builder.addBerTlv(emvTlv);
            Assert.fail("Should throw ArrayIndexOutOfBoundsException because default buffer size is 5KB");
        } catch (ArrayIndexOutOfBoundsException e) {
            Assert.assertNull(e.getMessage());
        }
    }

    @Test
    public void test_large_value() {
        final int     sixKilobytes = 6 * 1024;
        final int     tagLength    = 1;
        final int     valueLength  = 3;
        final int     sequenceHeaderSize = 4;
        final int     bufferSize   = sequenceHeaderSize + sixKilobytes + tagLength + valueLength;

        EmvTlv sequenceContent = new EmvTlv(new Tag(0x82), new byte[sixKilobytes]);
        ArrayList<EmvTlv> emvTlvs = new ArrayList<EmvTlv>();
        emvTlvs.add(sequenceContent);
        EmvTlv sequence = new EmvTlv(new Tag(0x30),
                                     emvTlvs);

        EmvTlvBuilder builder = new EmvTlvBuilder(null, new byte[bufferSize], 0, bufferSize);
        builder.addBerTlv(sequence);

        byte[] bytes = builder.buildArray();
        Assert.assertEquals(bufferSize, bytes.length);
    }

    @Test
    public void test_empty() {
        EmvTlv tlv = new EmvTlv(new Tag(0x01), new byte[]{});

        byte[] bytes = new EmvTlvBuilder(new Tag(0xe3))
                .addBerTlv(tlv)
                .buildArray();

        EmvTlv emvTlv = new EmvTlvBuilder(new Tag(0xe3))
                .addBerTlv(tlv)
                .buildTlv();

        EmvTlvLogger.log("    ", emvTlv, new VDEMVParser());

        System.out.println("bytes = " + HexUtil.toFormattedHexString(bytes));
    }

    @Test
    public void test_empty_length() {
        EmvTlv tlv = new EmvTlv(new Tag(0x01),new byte[]{});

        EmvTlv some = new EmvTlvBuilder()
                .addBytes(new Tag(0xE3), new EmvTlvBuilder().addBerTlv(tlv).buildArray())
                .buildTlv();

        Assert.assertEquals(some.getTag(), new Tag(0xe3));
        Assert.assertNotNull(some.getValues());
        Assert.assertEquals(1, some.getValues().size());
        EmvTlv empty = some.getValues().get(0);
        Assert.assertEquals(new Tag(0x01), empty.getTag());
        Assert.assertEquals("", empty.getHexValue());
    }
}
