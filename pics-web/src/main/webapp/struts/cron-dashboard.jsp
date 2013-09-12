<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="row">
    <div class="span6">
        <s:include value="/struts/layout/_page-header.jsp">
            <s:param name="title">Contractor Cron Dashboard</s:param>
        </s:include>
    </div>
    <div class="span6">
        <!-- <button class="btn btn-success btn-large pull-right"><i class="icon-off"></i>Start Contractor Cron</button> -->
    </div>
</div>

<div class="row">
    <div class="span6">
        <div id="totalTime">
            <i class="icon-time icon-large"></i>
            <div>
                <p id="totalHours">${totalHours} hours</p>
                <p>time to run through all active contractors</p>
            </div>
        </div>

        <table class="table table-striped">
            <caption>Average Contractors Run Per Minute</caption>
            <thead>
                <tr>
                    <th>Time Interval</th>
                    <th>Number of Contractors</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Last 5 Minutes</td>
                    <td>${cronDao.runCountGivenTimeMinutes(5)}</td>
                </tr>
                <tr>
                    <td>Last 15 Minutes</td>
                    <td>${cronDao.runCountGivenTimeMinutes(15)}</td>
                </tr>
                <tr>
                    <td>Last 60 Minutes</td>
                    <td>${cronDao.runCountGivenTimeMinutes(60)}</td>
                </tr>
            </tbody>
        </table>
        <table class="table table-striped">
            <caption>Contractors Run Per Server</caption>
            <thead>
                <tr>
                    <th>Server</th>
                    <th>Number of Contractors</th>
                </tr>
            </thead>
            <tbody>
                <s:iterator value="cronDao.contractorsPerServer().keySet()" var="server">
                    <tr>
                        <td>${server}</td>
                        <td>
                            <s:property value="%{cronDao.contractorsPerServer().get(#server)}" />
                        </td>
                    </tr>
                </s:iterator>
            </tbody>
        </table>    
    </div>
    <div class="span6">
        <table class="table table-striped">
            <caption>Recently Run Contractors</caption>
            <thead>
                <tr>
                    <th>Contractor</th>
                    <th>Seconds Ago</th>
                </tr>
            </thead>
            <tbody>
                <s:iterator value="cronDao.recentlyRunContractors(10)" var="con">
                    <tr>
                        <td>${con.name}</td>
                        <td>${con.lastRecalculation}</td>
                    </tr>
                </s:iterator>
            </tbody>
        </table>    
    
    </div>
</div>