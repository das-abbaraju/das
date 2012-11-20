<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="version_detail_tooltip">
    <div class='version-detail-tooltip'>
        <ul class='unstyled'>
            <li>
                <span><s:text name="global.OperatingSystem.Acronym" />:</span> <s:property value="operatingSystem" />
            </li>
            <li>
                <span><s:text name="global.Browser" />:</span> <s:property value="browserName" />
            </li>
            <li>
                <span><s:text name="global.ServerEnvironment" />:</span> <s:property value="picsEnvironment" />
            </li>
            <li>
                <span><s:text name="Login.Server" />:</span> DB@<s:property value="@com.picsauditing.search.Database@getDatabaseName()"/>
            </li>
        </ul>
    </div>
</s:set>

<div class="product-description">
    <img src="v7/img/logo-alternate.png" />
    
    <div class="version">
        <a href="#" data-title="${version_detail_tooltip}">Version ${version}</a>
    </div>
    <div class="copyright">
        Copyright &copy; 2012 PICS
    </div>
    
    <button class="btn show-privacy-policy">Show Privacy Policy</button>
</div>

<div class="row">
    <div class="span8 offset2">
        <div class="privacy-policy"></div>
    </div>
</div>