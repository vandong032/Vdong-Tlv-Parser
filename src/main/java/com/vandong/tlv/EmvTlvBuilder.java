package com.vandong.tlv;


import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class EmvTlvBuilder {

    private static final Charset ASCII = Charset.forName("US-ASCII");
    private static final BigDecimal HUNDRED = new BigDecimal(100);
    private static final int DEFAULT_SIZE = 5 * 1024;

    public EmvTlvBuilder() {
        this((Tag)null);
    }

    public EmvTlvBuilder(Tag aTemplate) {
        this(aTemplate, new byte[DEFAULT_SIZE], 0, DEFAULT_SIZE);
    }


    public EmvTlvBuilder(Tlvs tlvs) {
        this((Tag)null);
        for (EmvTlv tlv : tlvs.getList()) {
            addBerTlv(tlv);
        }
    }

    public EmvTlvBuilder(Tag aTemplate, byte[] aBuffer, int aOffset, int aLength) {
        theTemplate  = aTemplate;
        theBuffer = aBuffer;
        thePos = aOffset;
        theBufferOffset = aOffset;
    }

    public static EmvTlvBuilder from(EmvTlv aTlv) {
        return from(aTlv, DEFAULT_SIZE);
    }

    public static EmvTlvBuilder from(EmvTlv aTlv, int bufferSize) {
        if(aTlv.isConstructed()) {
            EmvTlvBuilder builder = template(aTlv.getTag(), bufferSize);
            for (EmvTlv tlv : aTlv.theList) {
                builder.addBerTlv(tlv);
            }
            return builder;
        } else {
            return new EmvTlvBuilder(null, new byte[bufferSize], 0, bufferSize).addBerTlv(aTlv);
        }
    }

    public static EmvTlvBuilder template(Tag aTemplate) {
        return template(aTemplate, DEFAULT_SIZE);
    }

    public static EmvTlvBuilder template(Tag aTemplate, int bufferSize) {
        return new EmvTlvBuilder(aTemplate, new byte[bufferSize], 0, bufferSize);
    }

    public EmvTlvBuilder addEmpty(Tag aObject) {
        return addBytes(aObject, new byte[]{}, 0, 0);
    }

    public EmvTlvBuilder addByte(Tag aObject, byte aByte) {
        // type
        int len = aObject.bytes.length;
        System.arraycopy(aObject.bytes, 0, theBuffer, thePos, len);
        thePos+=len;

        // len
        theBuffer[thePos++] = 1;

        // value
        theBuffer[thePos++] = aByte;
        return this;
    }

    public EmvTlvBuilder addAmount(Tag aObject, BigDecimal aAmount) {
        BigDecimal numeric = aAmount.multiply(HUNDRED);
        StringBuilder sb = new StringBuilder(12);
        sb.append(numeric.longValue());
        while(sb.length() < 12) {
            sb.insert(0, '0');
        }
        return addHex(aObject, sb.toString());
    }

    public EmvTlvBuilder addDate(Tag aObject, Date aDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
        return addHex(aObject, format.format(aDate));
    }

    public EmvTlvBuilder addTime(Tag aObject, Date aDate) {
        SimpleDateFormat format = new SimpleDateFormat("HHmmss");
        return addHex(aObject, format.format(aDate));
    }

    public int build() {

        if (theTemplate != null) {

            int tagLen = theTemplate.bytes.length;
            int lengthBytesCount = calculateBytesCountForLength(thePos);

            // shifts array
            System.arraycopy(theBuffer, theBufferOffset, theBuffer, tagLen + lengthBytesCount, thePos);

            // copies tag
            System.arraycopy(theTemplate.bytes, 0, theBuffer, theBufferOffset, theTemplate.bytes.length);

            fillLength(theBuffer, tagLen, thePos);

            thePos += tagLen + lengthBytesCount;
        }
        return thePos;
    }

    private void fillLength(byte[] aBuffer, int aOffset, int aLength) {

        if(aLength < 0x80) {
            aBuffer[aOffset] = (byte) aLength;

        } else if (aLength <0x100) {
            aBuffer[aOffset] = (byte) 0x81;
            aBuffer[aOffset+1] = (byte) aLength;

        } else if( aLength < 0x10000) {

            aBuffer[aOffset]   = (byte) 0x82;
            aBuffer[aOffset+1] = (byte) (aLength / 0x100);
            aBuffer[aOffset+2] = (byte) (aLength % 0x100);

        } else if( aLength < 0x1000000 ) {
            aBuffer[aOffset]   = (byte) 0x83;
            aBuffer[aOffset+1] = (byte) (aLength / 0x10000);
            aBuffer[aOffset+2] = (byte) (aLength / 0x100);
            aBuffer[aOffset+3] = (byte) (aLength % 0x100);
        } else {
            throw new IllegalStateException("length ["+aLength+"] out of range (0x1000000)");
        }
    }

    private int calculateBytesCountForLength(int aLength) {
        int ret;
        if(aLength < 0x80) {
            ret = 1;
        } else if (aLength <0x100) {
            ret = 2;
        } else if( aLength < 0x10000) {
            ret = 3;
        } else if( aLength < 0x1000000 ) {
            ret = 4;
        } else {
            throw new IllegalStateException("length ["+aLength+"] out of range (0x1000000)");
        }
        return ret;
    }

    public EmvTlvBuilder addHex(Tag aObject, String aHex) {
        byte[] buffer = HexUtil.parseHex(aHex);
        return addBytes(aObject, buffer, 0, buffer.length);
    }

    public EmvTlvBuilder addBytes(Tag aObject, byte[] aBytes) {
        return addBytes(aObject, aBytes, 0, aBytes.length);
    }

    public EmvTlvBuilder addBytes(Tag aTag, byte[] aBytes, int aFrom, int aLength) {
        int tagLength        = aTag.bytes.length;
        int lengthBytesCount = calculateBytesCountForLength(aLength);

        // TAG
        System.arraycopy(aTag.bytes, 0, theBuffer, thePos, tagLength);
        thePos+=tagLength;

        // LENGTH
        fillLength(theBuffer, thePos, aLength);
        thePos += lengthBytesCount;

        // VALUE
        System.arraycopy(aBytes, aFrom, theBuffer, thePos, aLength);
        thePos+=aLength;

        return this;
    }

    public EmvTlvBuilder add(EmvTlvBuilder aBuilder) {
        byte[] array = aBuilder.buildArray();
        System.arraycopy(array, 0, theBuffer, thePos, array.length);
        thePos+=array.length;
        return this;
    }


    public EmvTlvBuilder addBerTlv(EmvTlv aTlv) {
        if(aTlv.isConstructed()) {
            return add(from(aTlv, theBuffer.length));
        } else {
            return addBytes(aTlv.getTag(), aTlv.getBytesValue());
        }
    }

    /**
     * Add ASCII text
     *
     * @param aTag   tag
     * @param aText  text
     * @return builder
     */
    public EmvTlvBuilder addText(Tag aTag, String aText) {
        return addText(aTag, aText, ASCII);
    }

    /**
     * Add ASCII text
     *
     * @param aTag   tag
     * @param aText  text
     * @return builder
     */
    public EmvTlvBuilder addText(Tag aTag, String aText, Charset aCharset) {
        byte[] buffer = aText.getBytes(aCharset);
        return addBytes(aTag, buffer, 0, buffer.length);
    }

    public EmvTlvBuilder addIntAsHex(Tag aObject, int aCode, int aLength) {
        StringBuilder sb = new StringBuilder(aLength*2);
        sb.append(aCode);
        while(sb.length()<aLength*2) {
            sb.insert(0, '0');
        }
        return addHex(aObject, sb.toString());
    }

    public byte[] buildArray() {
        int count = build();
        byte[] buf = new byte[count];
        System.arraycopy(theBuffer, 0, buf, 0, count);
        return buf;
    }

    public EmvTlv buildTlv() {
        int count = build();
        return new EmvTlvParser().parseConstructed(theBuffer, theBufferOffset, count);
    }

    public Tlvs buildTlvs() {
        int count = build();
        return new EmvTlvParser().parse(theBuffer, theBufferOffset, count);
    }

    private final int theBufferOffset;
    private int theLengthPosition;
    private int thePos;
    private final byte[] theBuffer;
    private final Tag theTemplate;
}
