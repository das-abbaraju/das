<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<h1>
<s:if test="permissions.loggedIn"><s:property value="contractor.name" /></s:if>
<s:else><s:text name="%{scope}.title" /></s:else>
<span class="sub"> <s:property value="subHeading" /> </span></h1>
<div id="internalnavcontainer">
<ul id="navlist">
	<s:iterator value="menu">
	<s:if test="isStringEmpty(url)">
		<li><span class="inactive"><s:property value="name"/></span></li>
	</s:if>
	<s:else>
		<li><a <s:if test="current">class="current"</s:if> href=""><s:property value="name"/></a></li>
	</s:else>
	</s:iterator>
</ul>
</div>

<s:include value="../actionMessages.jsp" />
