<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Frontend Development Guide</s:param>
</s:include>

<s:include value="/struts/frontend-development-guide/_menu.jsp" />

<section>
    <div class="page-header">
        <h1>File Structure</h1>
    </div>
    
    <%-- URL --%>
    <s:url action="FrontendDevelopmentGuide" method="file_structure" var="file_structure" />
    
    <ul class="nav nav-pills">
        <li class="${methodName == 'file_structure' ? 'active' : ''}">
            <a href="${file_structure}">File Structure</a>
        </li>
    </ul>
</section>

<section>
    <div class="page-header">
        <h1>Conventions & Standards</h1>
    </div>
    
    <%-- URL --%>
    <s:url action="FrontendDevelopmentGuide" method="css_javascript_conventions" var="css_javascript_conventions" />
    <s:url action="FrontendDevelopmentGuide" method="html_conventions" var="html_conventions" />
    <s:url action="FrontendDevelopmentGuide" method="file_structure_conventions" var="file_structure_conventions" />
    
    <ul class="nav nav-pills">
        <li class="${methodName == 'css_javascript_conventions' ? 'active' : ''}">
            <a href="${css_javascript_conventions}">CSS Javascript Conventions</a>
        </li>
        <li class="${methodName == 'html_conventions' ? 'active' : ''}">
            <a href="${html_conventions}">HTML Conventions</a>
        </li>
        <li class="${methodName == 'file_structure_conventions' ? 'active' : ''}">
            <a href="${file_structure_conventions}">File Structure Conventions</a>
        </li>
    </ul>
</section>

<section>
    <div class="page-header">
        <h1>PICS Style Guide</h1>
    </div>
    
    <%-- URL --%>
    <s:url action="FrontendDevelopmentGuide" method="style_guide" var="style_guide" />
    
    <ul class="nav nav-pills">
        <li class="${methodName == 'style_guide' ? 'active' : ''}">
            <a href="${style_guide}">PICS Style Guide</a>
        </li>
    </ul>
</section>

<section>
    <div class="page-header">
        <h1>Page Layout</h1>
    </div>

    <%-- URL --%>
    <s:url action="FrontendDevelopmentGuide" method="page_layout" var="page_layout" />
    
    <ul class="nav nav-pills">
        <li class="${methodName == 'page_layout' ? 'active' : ''}">
            <a href="${page_layout}">Page Layout</a>
        </li>
    </ul>
</section>

<section>
    <div class="page-header">
        <h1>Components</h1>
    </div>
    
    <%-- URL --%>
    <s:url action="FrontendDevelopmentGuide" method="alerts" var="alerts" />
    <s:url action="FrontendDevelopmentGuide" method="buttons" var="buttons" />
    <s:url action="FrontendDevelopmentGuide" method="forms" var="forms" />
    <s:url action="FrontendDevelopmentGuide" method="pills" var="pills" />
    
    <ul class="nav nav-pills">
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
    </ul>
</section>