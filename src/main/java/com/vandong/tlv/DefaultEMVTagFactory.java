package com.vandong.tlv;

public class DefaultEMVTagFactory implements EmvTagFactory {

	@Override
	public Tag createTag(byte[] aBuf, int aOffset, int aLength) {
		return new Tag(aBuf, aOffset, aLength);
	}

}
