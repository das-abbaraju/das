<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="version_detail_tooltip">
    <div class='version-detail-tooltip'>
        <ul class='unstyled'>
            <li>
                <span>OS:</span> Windows
            </li>
            <li>
                <span>Browser:</span> Chrome
            </li>
            <li>
                <span>Environment:</span> Alpha
            </li>
            <li>
                <span>Server:</span> DB@pics_alpha1
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