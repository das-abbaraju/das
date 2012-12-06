<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- URL --%>
<s:url action="FrontendDevelopmentGuide" var="dashboard" />
<s:url action="FrontendDevelopmentGuide" method="technology" var="technology" />
<s:url action="FrontendDevelopmentGuide" method="routing" var="routing" />
<s:url action="FrontendDevelopmentGuide" method="file_structure" var="file_structure" />
<s:url action="FrontendDevelopmentGuide" method="style_guide" var="style_guide" />
<s:url action="FrontendDevelopmentGuide" method="page_layout" var="page_layout" />
<s:url action="FrontendDevelopmentGuide" method="component" var="component" />

<%-- URL --%>
<s:url action="FrontendDevelopmentGuide" method="css_javascript_conventions" var="css_javascript_conventions" />
<s:url action="FrontendDevelopmentGuide" method="html_conventions" var="html_conventions" />
<s:url action="FrontendDevelopmentGuide" method="file_structure_conventions" var="file_structure_conventions" />

<ul class="nav nav-pills">
    <li class="${methodName == null ? 'active' : ''}">
        <a href="${dashboard}">Dashboard</a>
    </li>
    <li class="${methodName == 'technology' ? 'active' : ''}">
        <a href="${technology}">Technology</a>
    </li>
    <li class="${methodName == 'routing' ? 'active' : ''}">
        <a href="${routing}">Routing</a>
    </li>
    <li class="${methodName == 'file_structure' ? 'active' : ''}">
        <a href="${file_structure}">File Structure</a>
    </li>
    <li class="dropdown">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Conventions &amp; Standards <b class="caret"></b></a>
        
        <ul class="dropdown-menu">
            <li class="${methodName == 'css_javascript_conventions' ? 'active' : ''}">
                <a href="${css_javascript_conventions}">CSS &amp; Javascript</a>
            </li>
            <li class="${methodName == 'html_conventions' ? 'active' : ''}">
                <a href="${html_conventions}">HTML</a>
            </li>
            <li class="${methodName == 'file_structure_conventions' ? 'active' : ''}">
                <a href="${file_structure_conventions}">File Structure</a>
            </li>
        </ul>
    </li>
    <li class="${methodName == 'style_guide' ? 'active' : ''}">
        <a href="${style_guide}">PICS Style Guide</a>
    </li>
    <li class="${methodName == 'page_layout' ? 'active' : ''}">
        <a href="${page_layout}">Page Layout</a>
    </li>
    <li class="${methodName == 'component' ? 'active' : ''}">
        <a href="${component}">Components</a>
    </li>
</ul>