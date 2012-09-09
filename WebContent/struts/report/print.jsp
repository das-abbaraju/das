<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:property value="report.name"/></s:param>
</s:include>

<table class="table" id="report_data">
    <thead>
    <s:iterator value="report.definition.columns">
        <th class="<s:property value="fieldName"/>"><s:property value="field.text"/></th>
    </s:iterator>
    </thead>
    <tbody>
    <s:iterator value="data" var="row">
        <tr>
        <s:iterator value="report.definition.columns">
            <td class="<s:property value="fieldName"/>">
                <s:property value="#row.get(fieldName)"/>
            </td>
        </s:iterator>
        </tr>
    </s:iterator>
    </thead>
</table>

                <s:property value="%{fieldName.toUpperCase()}"/>
