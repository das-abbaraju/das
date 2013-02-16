<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:property value="report.name"/></s:param>
</s:include>

<table class="table" id="report_data">
    <thead>
    <s:iterator value="report.columns">
        <th class="<s:property value="name"/>"><s:property value="field.text"/></th>
    </s:iterator>
    </thead>
    <tbody>
    <s:iterator value="reportResults.rows" var="row">
        <tr>
        <s:iterator value="report.columns" var="column">
            <td class="<s:property value="name"/>">
                <s:property value="#row.getCellByColumn(#column).value"/>
            </td>
        </s:iterator>
        </tr>
    </s:iterator>
    </tbody>
</table>
