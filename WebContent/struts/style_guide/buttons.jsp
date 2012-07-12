<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- URL --%>
<s:url action="PicsStyleGuide" var="style_guide_dashboard" />
<s:url action="PicsStyleGuide" method="buttons" var="style_guide_buttons" />
<s:url action="PicsStyleGuide" method="forms" var="style_guide_forms" />
<s:url action="PicsStyleGuide" method="pills" var="style_guide_pills" />

<title>PICS Style Guide</title>

<s:include value="../actionMessages.jsp" />

<h1 class="title">PICS Style Guide</h1>

<ul class="nav nav-pills">
	<li>
		<a href="${style_guide_dashboard}">Style Guide</a>
	</li>
	<li class="active">
		<a href="${style_guide_buttons}">Buttons</a>
	</li>
	<li>
		<a href="${style_guide_forms}">Forms</a>
	</li>
	<li>
		<a href="${style_guide_pills}">Pills</a>
	</li>
</ul>