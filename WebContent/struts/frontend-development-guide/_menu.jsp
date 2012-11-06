<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- URL --%>
<s:url action="FrontendDevelopmentGuide" var="dashboard" />
<s:url action="FrontendDevelopmentGuide" method="alerts" var="alerts" />
<s:url action="FrontendDevelopmentGuide" method="buttons" var="buttons" />
<s:url action="FrontendDevelopmentGuide" method="forms" var="forms" />
<s:url action="FrontendDevelopmentGuide" method="pills" var="pills" />
<s:url action="FrontendDevelopmentGuide" method="conventions" var="conventions" />
<s:url action="FrontendDevelopmentGuide" method="file_structure" var="file_structure" />
<s:url action="FrontendDevelopmentGuide" method="page_layout" var="page_layout" />
<s:url action="FrontendDevelopmentGuide" method="style_guide" var="style_guide" />

<ul class="nav nav-pills">
    <li class="${methodName == null ? 'active' : ''}">
        <a href="${dashboard}">Dashboard</a>
    </li>
    <li class="${methodName == 'alerts' ? 'active' : ''}">
        <a href="${alerts}">Alerts</a>
    </li>
    <li class="${methodName == 'buttons' ? 'active' : ''}">
        <a href="${buttons}">Buttons</a>
    </li>
    <li class="${methodName == 'forms' ? 'active' : ''}">
        <a href="${forms}">Forms</a>
    </li>
    <li class="${methodName == 'pills' ? 'active' : ''}">
        <a href="${pills}">Pills</a>
    </li>
    <li class="${methodName == 'conventions' ? 'active' : ''}">
        <a href="${conventions}">Conventions</a>
    </li>
    <li class="${methodName == 'file_structure' ? 'active' : ''}">
        <a href="${file_structure}">File Structure</a>
    </li>
    <li class="${methodName == 'page_layout' ? 'active' : ''}">
        <a href="${page_layout}">Page Layout</a>
    </li>
    <li class="${methodName == 'style_guide' ? 'active' : ''}">
        <a href="${style_guide}">PICS Style Guide</a>
    </li>
</ul>