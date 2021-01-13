package com.vandong.tlv;


public class EmvTlvLogger {


    public static void log(String aPadding, Tlvs aTlv, IEMVTlvLogger aLogger) {

        for (EmvTlv tlv : aTlv.getList()) {
            log(aPadding, tlv, aLogger);
        }
    }

    public static void log(String aPadding, EmvTlv aTlv, IEMVTlvLogger aLogger) {
        if (aTlv == null) {
            aLogger.debug("{} is null", aPadding);
            return;
        }

        if (aTlv.isConstructed()) {
            aLogger.debug("{} [{}]", aPadding, HexUtil.toHexString(aTlv.getTag().bytes));
            for (EmvTlv child : aTlv.getValues()) {
                log(aPadding + "    ", child, aLogger);
            }
        } else {
            aLogger.debug("{} [{}] {}", aPadding, HexUtil.toHexString(aTlv.getTag().bytes), aTlv.getHexValue());
        }

    }

}
