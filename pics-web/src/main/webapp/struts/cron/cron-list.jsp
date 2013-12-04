<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<title>Cron Task List</title>

<div id="main" role="main" class="container">
    <div class="row">
        <div class="span6">
            <div class="page-header pics">
                <h1 class="title">Daily Cron</h1>
                <p class="subtitle">Task List</p>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="span6">
            <table class="table table-striped">
                <caption>Task List</caption>
                <thead>
                <tr>
                    <th>Task Name</th>
                </tr>
                </thead>
                <tbody>
                <s:iterator value="allTasks" var="task">
                    <tr>
                        <td><a href="Cron!view.action?task=<s:property value="#task"/>"><s:property value="#task"/></a></td>
                    </tr>
                </s:iterator>
                </tbody>
            </table>

        </div>
    </div>
</div>
