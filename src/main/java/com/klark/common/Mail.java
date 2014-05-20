// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.common;

import org.springframework.stereotype.Component;

/**
 * Description here!
 * 
 * 
 * @author
 */

@Component("mail")
public class Mail {

    private String host;
    private String port;

    private String username;
    private String password;

    private String from;

    private String regSubject;

    public String getRegSubject() {
        return regSubject;
    }

    public void setRegSubject(String regSubject) {
        this.regSubject = regSubject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
