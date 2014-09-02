package com.picsauditing.util.flow;

public class ResultFailure<T> extends Result<T>{

    public ResultFailure(String msg) {
        super(Status.FAILURE, msg);
    }

    public ResultFailure(Exception e, String msg) {
        super(Status.FAILURE, e);
    }

    public ResultFailure() {
        super(Status.FAILURE);
    }

    public ResultFailure(Exception e) {
        super(Status.FAILURE, e);
    }

    public ResultFailure(T mapInfo) {
        super(Status.FAILURE, mapInfo);
    }
}
