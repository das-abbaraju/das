package com.picsauditing.util.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Result<T> implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Object> extraInfo = new HashMap<>();

    private final ArrayList<Result<T>> ResultList = new ArrayList<>();

    private long resultCode;

    private T object;

    public Result() {
    }

    public Result(T object, String msg) {
        this.object = object;
        this.message = new StringBuilder(msg);
    }

    public Result(T object) {
        this.object = object;
    }

    public static <T> T getInstance(Class<T> theClass) throws IllegalAccessException, InstantiationException {
        return theClass.newInstance();
    }

    /**
     *Status could be ok even it hasFailedResults is true,
     * the user should change the status if hasFailedResults is true and matters on case by case basis
     */
    private boolean hasFailedResults = false;

    public Result addResult(Result<T> Result) {
        ResultList.add(Result);
        if (Result.getresultStatus().equals(Status.FAILURE)) {
            this.message.append(" ").append(Result.getMessage());
            this.hasFailedResults = true;
        }
        return this;
    }

    private Status status;

    private StringBuilder message = new StringBuilder();

    private boolean log;
    private Exception e;

    public Result(Status status) {
        this.status = status;
        if (this.status.equals(Status.FAILURE)) {
            this.hasFailedResults = true;
        }
    }

    public Result(Status status, Exception e) {
        setE(e);
    }

    public Result(Status status, T obj) {
        this.status = status;
        this.object = obj;
        if (this.status.equals(Status.FAILURE)) {
            this.hasFailedResults = true;
        }
    }

    public Result(Status status, long resultCode) {
        this.status = status;
        if (this.status.equals(Status.FAILURE)) {
            this.hasFailedResults = true;
        }
        this.resultCode = resultCode;
    }

    public Result(Status status, String message) {
        this.status = status;
        if (this.status.equals(Status.FAILURE)) {
            this.hasFailedResults = true;
        }
        this.message.append(message);
    }

    private Result(Status status, String message, boolean log) {
        this.status = status;
        if (this.status.equals(Status.FAILURE)) {
            this.hasFailedResults = true;
        }
        this.message.append(message);
        this.log = log;
        if (log) logger.debug(message);
    }

    private Result(Status status, String message, boolean log, Exception e) {
        this.status = status;
        if (this.status.equals(Status.FAILURE)) {
            this.hasFailedResults = true;
        }
        this.message.append(message);
        this.log = log;
        this.e = e;
        if (log) logger.debug(message, e);
    }

    public String getMessage() {
        return message.toString();
    }


    public boolean isOk() {
        return this.status == Status.SUCCESS;
    }

    public Exception getException() {
        return getE();
    }

    public Result setMessage(String message) {
        this.message.append(message);
        return this;
    }


    public String appendMessage(String message) {
        return this.message.append(message).toString();
    }


    public String appendMessage(String message, boolean newLine) {
        String nl = isUNIX() ? "\n" : "\r\n";
        return appendMessage(nl.concat(message));
    }

    public Status getresultStatus() {
        return status;
    }

    public void setResultStatus(Status status) {
        this.status = status;
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    public static Result make(Status status, String message) {
        return new Result(status, message);
    }

    public static Result make(Status status, String message, boolean log) {
        return new Result(status, message, log);
    }

    public static Result make(Status status, String message, boolean log, Exception e) {
        return new Result(status, message, log, e);
    }

    public static Result ok(long resultCode) {
        return new Result(Status.SUCCESS, resultCode);
    }

    public static Result ok() {
        return new Result(Status.SUCCESS);
    }

    public static Result ok(String msg) {
        return new Result(Status.SUCCESS, msg);
    }

    public static Result undefined() {
        return new Result(Status.UNDEFINED);
    }


    public static Result allGood() {
        return new Result(Status.SUCCESS);
    }

    public static Result notGood() {
        return new Result(Status.FAILURE);
    }

    public static Result failure() {
        return new Result(Status.FAILURE);
    }

    @SuppressWarnings("unchecked")
    public static Result success(Object obj) {
        Class c = obj.getClass();

        return new Result(Status.SUCCESS, obj);
    }

/*
    public Result success(T obj) {
        return new Result(Status.SUCCESS, obj);
    }
*/

    public static Result success() {
        return new Result(Status.SUCCESS);
    }

    public static Result notGood(String msg) {
        return new Result(Status.FAILURE, msg);
    }

    public static Result notGood(String msg, boolean log) {

        return new Result(Status.FAILURE, msg, log);
    }

    public static Result notGood(String msg, boolean log, Exception e) {
        return new Result(Status.FAILURE, msg, log, e);
    }

    public Result addExtraInfo(String key, Object value) {
        extraInfo.put(key, value);
        return this;
    }

    public Object getExtraInfo(String key) {
        return extraInfo.get(key);
    }

    public boolean hasFailedResults() {
        return this.hasFailedResults;
    }

    public Map<? extends String, ?> getInfo() {
        return extraInfo;
    }

    public boolean succeeded() {
        return status.toString().equals(Result.ok().toString());
    }

    public boolean failed() {
        return status.toString().equals(Result.notGood().toString());
    }

    public ArrayList<Result<T>> getResultList() {
        return ResultList;
    }

    public long getResultCode() {
        return resultCode;
    }

    public void setResultCode(long resultCode) {
        this.resultCode = resultCode;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return this.status.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Result)) return false;

        Result result = (Result) o;

        return resultCode == result.resultCode && log == result.log && !(e != null ? !e.equals(result.e) : result.e != null) && extraInfo.equals(result.extraInfo) && !(message != null ? !message.equals(result.message) : result.message != null) && ResultList.equals(result.ResultList) && status == result.status;

    }

    @Override
    public int hashCode() {
        int result = extraInfo.hashCode();
        result = 31 * result + (ResultList.hashCode());
        result = 31 * result + (int) (resultCode ^ (resultCode >>> 32));
        result = 31 * result + status.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (log ? 1 : 0);
        result = 31 * result + (e != null ? e.hashCode() : 0);
        return result;
    }

    public enum Status {

        UNDEFINED("undefined"),
        SUCCESS("success"),
        FAILURE("failure");

        private final String op;

        private Status(String op) {
            this.op = op;
        }

        public static Status fromString(String c) {
            if (c != null) {
                for (Status os : Status.values()) {
                    if (c.equalsIgnoreCase(os.op)) {
                        return os;
                    }
                }
                return UNDEFINED;
            }
            return UNDEFINED;
        }


        @Override
        public String toString() {
            return op;
        }
    }

    private static final boolean win32;
    static {
        String os = System.getProperty("os.name").toLowerCase();
        win32 = os.contains("windows");
    }

    public static boolean isUNIX() {
        return !win32;
    }

    public static boolean isWin32() {
        return win32;
    }

}
