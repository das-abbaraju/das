<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- URL --%>
<s:url action="PicsStyleGuide" var="style_guide_dashboard" />
<s:url action="PicsStyleGuide" method="alerts" var="style_guide_alerts" />
<s:url action="PicsStyleGuide" method="buttons" var="style_guide_buttons" />
<s:url action="PicsStyleGuide" method="forms" var="style_guide_forms" />
<s:url action="PicsStyleGuide" method="pills" var="style_guide_pills" />

<ul class="nav nav-pills">
    <li class="${methodName == null ? 'active' : ''}">
        <a href="${style_guide_dashboard}">Style Guide</a>
    </li>
    <li class="${methodName == 'alerts' ? 'active' : ''}">
        <a href="${style_guide_alerts}">Alerts</a>
    </li>
    <li class="${methodName == 'buttons' ? 'active' : ''}">
        <a href="${style_guide_buttons}">Buttons</a>
    </li>
    <li class="${methodName == 'forms' ? 'active' : ''}">
        <a href="${style_guide_forms}">Forms</a>
    </li>
    <li class="${methodName == 'pills' ? 'active' : ''}">
        <a href="${style_guide_pills}">Pills</a>
    </li>
</ul>