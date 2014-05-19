package com.picsauditing.struts.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.picsauditing.actions.PicsActionSupport;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class JsonActionSupport extends PicsActionSupport {

    protected final <T> T getModelFromJsonRequest(Class<T> clazz) throws IOException {
        String body = getBodyFromRequest();

        T model;

        try {
            model = new Gson().fromJson(body, clazz);
        } catch (JsonSyntaxException e) {
            throw new IOException(HttpStatus.getStatusText(HttpStatus.SC_BAD_REQUEST) + ": " + body, e);
        }

        if (model == null) {
            throw new IOException(HttpStatus.getStatusText(HttpStatus.SC_BAD_REQUEST) + ": " + body);
        }

        return model;
    }

    protected final String badRequestResponse() {
        ServletActionContext.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
        return BLANK;
    }

    private String getBodyFromRequest() throws IOException {
        HttpServletRequest request = getRequest();
        return getBody(request);
    }

    public final String getBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return stringBuilder.toString();
    }
}
