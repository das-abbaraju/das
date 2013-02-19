<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<title>
    <s:text name="global.AboutPICS"/>
</title>

<s:set var="version_detail_tooltip">
    <div class='version-detail-tooltip'>
        <ul class='unstyled'>
            <li>
                <span><s:text name="global.OperatingSystem.Acronym"/>:</span> ${operatingSystem}
            </li>
            <li>
                <span><s:text name="global.Browser"/>:</span> ${browserName}
            </li>
            <li>
                <span><s:text name="global.ServerEnvironment"/>:</span>${picsEnvironment}
            </li>
            <li>
                <span><s:text name="Login.Server"/>:</span> DB@<s:property
                    value="@com.picsauditing.search.Database@getDatabaseName()"/>
            </li>
            <li>
                <span><s:text name="global.ServerTime"/>:</span>${systemTime}
            </li>
        </ul>
    </div>
</s:set>

<div class="product-description">
    <img src="v7/img/logo/logo-large.png"/>

    <div class="version">
        <a href="#" data-title="${version_detail_tooltip}">Version ${version}</a>
    </div>
    <div class="copyright">
        Copyright &copy; 2013 PICS
    </div>

    <button class="btn show-privacy-policy"><s:text name="Footer.Privacy" /></button>
</div>

<div class="row">
    <div class="span8 offset2">
        <div class="privacy-policy"></div>
    </div>
</div>