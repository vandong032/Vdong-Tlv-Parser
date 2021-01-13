package com.vandong.tlv;


import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class EmvTlvParser {

	private static final EmvTagFactory DEFAULT_TAG_FACTORY = new DefaultEMVTagFactory();

	private final EmvTagFactory tagFactory;
    private final IEMVTlvLogger log;

    public EmvTlvParser() {
        this(DEFAULT_TAG_FACTORY, EMPTY_LOGGER);
    }

    public EmvTlvParser(IEMVTlvLogger aLogger) {
    	this(DEFAULT_TAG_FACTORY, aLogger);
    }
    
    public EmvTlvParser(EmvTagFactory aTagFactory) {
    	this(aTagFactory, EMPTY_LOGGER);
    }
    
    public EmvTlvParser(EmvTagFactory aTagFactory, IEMVTlvLogger aLogger) {
    	tagFactory = aTagFactory;
    	log = aLogger;
    }
    
    public EmvTlv parseConstructed(byte[] aBuf) {
        return parseConstructed(aBuf, 0, aBuf.length);
    }

    public EmvTlv parseConstructed(byte[] aBuf, int aOffset, int aLen) {
        ParseResult result =  parseWithResult(0, aBuf, aOffset, aLen);
        return result.tlv;
    }

    public Tlvs parse(byte[] aBuf) {
        return parse(aBuf, 0, aBuf.length);
    }

    public Tlvs parse(byte[] aBuf, final int aOffset, int aLen) {
        List<EmvTlv> tlvs = new ArrayList<EmvTlv>();
        if(aLen==0) return new Tlvs(tlvs);

        int offset = aOffset;
        for(int i=0; i<100; i++) {
            ParseResult result =  parseWithResult(0, aBuf, offset, aLen-offset);
            tlvs.add(result.tlv);

            if(result.offset>=aOffset+aLen) {
                break;
            }

            offset = result.offset;

        }

        return new Tlvs(tlvs);
    }

    private ParseResult parseWithResult(int aLevel, byte[] aBuf, int aOffset, int aLen) {
        String levelPadding = createLevelPadding(aLevel);
        if(aOffset+aLen > aBuf.length) {
            throw new IllegalStateException("Length is out of the range [offset="+aOffset+",  len="+aLen+", array.length="+aBuf.length+", level="+aLevel+"]");
        }
        if(log.isDebugEnabled()) {
            log.debug("{}parseWithResult(level={}, offset={}, len={}, buf={})", levelPadding, aLevel, aOffset, aLen, HexUtil.toFormattedHexString(aBuf, aOffset, aLen));
        }

        // tag
        int tagBytesCount = getTagBytesCount(aBuf, aOffset);
        Tag tag        = createTag(levelPadding, aBuf, aOffset, tagBytesCount);
        if(log.isDebugEnabled()) {
            log.debug("{}tag = {}, tagBytesCount={}, tagBuf={}", levelPadding, tag, tagBytesCount, HexUtil.toFormattedHexString(aBuf, aOffset, tagBytesCount));
        }

        // length
        int lengthBytesCount  = getLengthBytesCount(aBuf, aOffset + tagBytesCount);
        int valueLength       = getDataLength(aBuf, aOffset + tagBytesCount);

        if(log.isDebugEnabled()) {
            log.debug("{}lenBytesCount = {}, len = {}, lenBuf = {}"
                    , levelPadding, lengthBytesCount, valueLength, HexUtil.toFormattedHexString(aBuf, aOffset + tagBytesCount, lengthBytesCount));
        }

        // value
        if(tag.isConstructed()) {

            ArrayList<EmvTlv> list = new ArrayList<EmvTlv>();
            addChildren(aLevel, aBuf, aOffset + tagBytesCount + lengthBytesCount, levelPadding, lengthBytesCount, valueLength, list);

            int resultOffset = aOffset + tagBytesCount + lengthBytesCount + valueLength;
            if(log.isDebugEnabled()) {
                log.debug("{}returning constructed offset = {}", levelPadding, resultOffset);
            }
            return new ParseResult(new EmvTlv(tag, list), resultOffset);
        } else {
            // value
            byte[] value = new byte[valueLength];
            System.arraycopy(aBuf, aOffset+tagBytesCount+lengthBytesCount, value, 0, valueLength);
            int resultOffset = aOffset + tagBytesCount + lengthBytesCount + valueLength;
            if(log.isDebugEnabled()) {
                log.debug("{}value = {}", levelPadding, HexUtil.toFormattedHexString(value));
                log.debug("{}returning primitive offset = {}", levelPadding, resultOffset);
            }
            return new ParseResult(new EmvTlv(tag, value), resultOffset);
        }

    }

    /**
     *
     * @param aLevel          level for debug
     * @param aBuf            buffer
     * @param aOffset         offset (first byte)
     * @param levelPadding    level padding (for debug)
     * @param aDataBytesCount data bytes count
     * @param valueLength     length
     * @param list            list to add
     */
    private void addChildren(int aLevel, byte[] aBuf, int aOffset, String levelPadding, int aDataBytesCount, int valueLength, ArrayList<EmvTlv> list) {
        int startPosition = aOffset;
        int len = valueLength;
        while (startPosition < aOffset + valueLength ) {
            ParseResult result = parseWithResult(aLevel+1, aBuf, startPosition, len);
            list.add(result.tlv);

            startPosition = result.offset;
            len           = (aOffset + valueLength) - startPosition;

            if(log.isDebugEnabled()) {
                log.debug("{}level {}: adding {} with offset {}, startPosition={}, aDataBytesCount={}, valueLength={}"
                        , levelPadding, aLevel, result.tlv.getTag(), result.offset, startPosition, aDataBytesCount, valueLength);
            }
        }
    }

    private String createLevelPadding(int aLevel) {
        if(!log.isDebugEnabled()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for(int i=0; i<aLevel*4; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static class ParseResult {
        public ParseResult(EmvTlv aTlv, int aOffset) {
            tlv = aTlv;
            offset = aOffset;
        }

        @Override
        public String toString() {
            return "ParseResult{" +
                    "tlv=" + tlv +
                    ", offset=" + offset +
                    '}';
        }

        private final EmvTlv tlv;
        private final int offset;
    }


    private Tag createTag(String aLevelPadding, byte[] aBuf, int aOffset, int aLength) {
        if(log.isDebugEnabled()) {
            log.debug("{}Creating tag {}...", aLevelPadding, HexUtil.toFormattedHexString(aBuf, aOffset, aLength));
        }
        return tagFactory.createTag(aBuf, aOffset, aLength);
    }

    private int getTagBytesCount(byte[] aBuf, int aOffset) {
        if((aBuf[aOffset] & 0x1F) == 0x1F) { // see subsequent bytes
            int len = 2;
            for(int i=aOffset+1; i<aOffset+10; i++) {
                if( (aBuf[i] & 0x80) != 0x80) {
                    break;
                }
                len++;
            }
            return len;
        } else {
            return 1;
        }
    }


    private int getDataLength(byte[] aBuf, int aOffset) {

        int length = aBuf[aOffset] & 0xff;

        if((length & 0x80) == 0x80) {
            int numberOfBytes = length & 0x7f;
            if(numberOfBytes>3) {
                throw new IllegalStateException(String.format("At position %d the len is more then 3 [%d]", aOffset, numberOfBytes));
            }

            length = 0;
            for(int i=aOffset+1; i<aOffset+1+numberOfBytes; i++) {
                length = length * 0x100 + (aBuf[i] & 0xff);
            }

        }
        return length;
    }

    private static int getLengthBytesCount(byte aBuf[], int aOffset) {

        int len = aBuf[aOffset] & 0xff;
        if( (len & 0x80) == 0x80) {
            return 1 + (len & 0x7f);
        } else {
            return 1;
        }
    }


    private static final IEMVTlvLogger EMPTY_LOGGER = new IEMVTlvLogger() {
        public boolean isDebugEnabled() {
            return false;
        }

        public void debug(String aFormat, Object... args) {
        }
    };


}