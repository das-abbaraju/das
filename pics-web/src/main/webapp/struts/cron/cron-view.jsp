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
                <p class="subtitle"><a href="Cron!list.action">Task List</a> | <a href="Cron!view.action?task=<s:property value="task"/>"><s:property value="task"/></a></p>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="span6">
            <form action="Cron!run.action" method="GET">
                <s:hidden name="task" />
                <button type="submit" class="btn btn-default">Run Task</button>
            </form>
            Description:
            <s:property value="taskDescription"/>
            <br />
            <s:if test="steps.size > 0">
                Next Steps:
                <ul>
                    <s:iterator value="steps" var="step">
                        <li><s:property value="#step" /></li>
                    </s:iterator>
                </ul>
            </s:if>
        </div>
    </div>
</div>
