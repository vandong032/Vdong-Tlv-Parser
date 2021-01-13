package com.vandong.tlv;

public interface EmvTagFactory {
	Tag createTag(byte[] aBuf, int aOffset, int aLength);
}
