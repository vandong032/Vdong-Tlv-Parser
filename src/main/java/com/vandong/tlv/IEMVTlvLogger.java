package com.vandong.tlv;

public interface IEMVTlvLogger {

    boolean isDebugEnabled();

    void debug(String aFormat, Object ...args);
}
