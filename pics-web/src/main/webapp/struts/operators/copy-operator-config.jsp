<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Copy Client Configuration</s:param>
</s:include>

<s:include value="../actionMessages.jsp"></s:include>

<style type="text/css">
td.waiting {
	background-color: gray;
}
td.success {
	background-color: green;
}
td.fail {
	background-color: red;
}
</style>

<s:form id="save" method="POST" enctype="multipart/form-data">

<fieldset class="form">
    <ol>
        <li>
            <label>
                Source Client:
            </label>
            <s:select
                    name="sourceID"
                    list="operatorList"
                    listKey="id"
                    listValue="name"
                    headerKey="0"
                    value="sourceID"
                    headerValue="- Select the Source Client -"
                    />
        </li>
        <li>
            <label>
                Target Client:
            </label>
            <s:select
                    name="targetID"
                    list="operatorList"
                    listKey="id"
                    listValue="name"
                    headerKey="0"
                    value="targetID"
                    headerValue="- Select the Target Client -"
                    />
        </li>
    </ol>
</fieldset>
<fieldset class="form submit">
    <s:submit cssClass="picsbutton positive" method="copy" value="Copy"/>
</fieldset>
</s:form>
