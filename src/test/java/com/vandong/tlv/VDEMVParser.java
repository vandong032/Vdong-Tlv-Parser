package com.vandong.tlv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vandong.tlv.IEMVTlvLogger;

public class VDEMVParser implements IEMVTlvLogger {

    private final static Logger LOG = LoggerFactory.getLogger(VDEMVParser.class);

    public boolean isDebugEnabled() {
        return LOG.isDebugEnabled();
    }

    public void debug(String aFormat, Object... args) {
        LOG.debug(aFormat, args);
    }
}
