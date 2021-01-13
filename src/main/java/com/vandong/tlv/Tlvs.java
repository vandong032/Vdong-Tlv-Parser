package com.vandong.tlv;

import java.util.ArrayList;
import java.util.List;

public class Tlvs {

    protected Tlvs(List<EmvTlv> aTlvs) {
        tlvs = aTlvs;
    }

    public EmvTlv find(Tag aTag) {
        for (EmvTlv tlv : tlvs) {
            EmvTlv found = tlv.find(aTag);
            if(found!=null) {
                return found;
            }
        }
        return null;
    }

    public List<EmvTlv> findAll(Tag aTag) {
        List<EmvTlv> list = new ArrayList<EmvTlv>();
        for (EmvTlv tlv : tlvs) {
            list.addAll(tlv.findAll(aTag));
        }
        return list;
    }


    public List<EmvTlv> getList() {
        return tlvs;
    }

    private final List<EmvTlv> tlvs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tlvs tlvs = (Tlvs) o;

        return tlvs != null ? tlvs.equals(tlvs.tlvs) : tlvs.tlvs == null;
    }

    @Override
    public int hashCode() {
        return tlvs != null ? tlvs.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BerTlvs{" +
                "tlvs=" + tlvs +
                '}';
    }
}
