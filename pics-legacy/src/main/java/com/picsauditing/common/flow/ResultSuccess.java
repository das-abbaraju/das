package com.picsauditing.common.flow;

public class ResultSuccess<T> extends Result<T>{

    public ResultSuccess() {

    }

    public ResultSuccess(T t) {
        super(Status.SUCCESS, t);
    }
}
