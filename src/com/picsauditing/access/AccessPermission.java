package com.picsauditing.access;

public class AccessPermission {
    private OpPerms opPerm;
    private OpType opType;

    public AccessPermission() {
    }

    public AccessPermission(OpPerms opPerm) {
        this.opPerm = opPerm;
        this.opType=  OpType.View;
    }

    public AccessPermission(OpPerms opPerm, OpType opType) {
        this.opPerm = opPerm;
        this.opType = opType;
    }

    public OpPerms getOpPerm() {
        return opPerm;
    }

    public void setOpPerm(OpPerms opPerm) {
        this.opPerm = opPerm;
    }

    public OpType getOpType() {
        return opType;
    }

    public void setOpType(OpType opType) {
        this.opType = opType;
    }

}
