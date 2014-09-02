package com.picsauditing.util.flow;

public class ResultSuccess<T> extends Result<T>{

    public ResultSuccess() {
        super(Status.SUCCESS);
    }

    public ResultSuccess(T t) {
        super(Status.SUCCESS, t);
    }
}
