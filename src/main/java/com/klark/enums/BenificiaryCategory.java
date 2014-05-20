// =======================================================
// Copyright Mylife.com Inc., 2013. All rights reserved.
//
// =======================================================

package com.klark.enums;

/**
 * Used to distinguish what Type a given contact info
 * 
 * 
 * @author das
 */

public enum BenificiaryCategory {

    SON(1, "son");

    private final String type;
    private final int id;

    private BenificiaryCategory(int typeId, String t) {
        this.id = typeId;
        this.type = t;
    }

    public int getId() {
        return id;
    }

    public static BenificiaryCategory fromId(int typeId) {
        for (BenificiaryCategory t : BenificiaryCategory.values()) {
            if (typeId == t.getId()) {
                return t;
            }
        }
        /* Default return mobile phone */
        return SON;
    }

    public String getType() {
        return type;
    }
}
