<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

5 min : <s:property value="cronDao.runCountGivenTimeMinutes(5)" /> <br />
15 min : <s:property value="cronDao.runCountGivenTimeMinutes(15)" /> <br />
60 min : <s:property value="cronDao.runCountGivenTimeMinutes(60)" /> <br />
avg time per contractor : <s:property value="cronDao.averageTimePerContractor()" /> <br />
<br />
<s:iterator value="cronDao.recentlyRunContractors(10)" var="con">
    <s:property value="%{#con.name}" /> <br />
</s:iterator>
<br />
<s:iterator value="cronDao.contractorsPerServer().keySet()" var="server">
    <s:property value="%{#server.toString()}" /> : <s:property value="%{cronDao.contractorsPerServer().get(#server)}" />
</s:iterator>