<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="eulaBody" value="eulaBody" />

<div class="eula-container">
    <div class="container">
        <img src="v7/img/logo/logo-large.png" class="logo" alt="PICS"/>
        <div class="row">
            <div class="span6 offset3">
                <div class="panel-heading clearfix">
                    <strong class="pull-left title">End User License Agreement (EULA)</strong>
                    <div class="pull-right text-right version-container">
                        <span class="version">Version 1.0</span>
                        <a href="#"><i class="icon-print icon-large"></i></a>
                    </div>
                </div>
                <div class="panel-body">
                    <s:property value="eulaBody" escapeHtml="false" />
                </div>
                <div class="button-container">
                    <p>To continue, please agree to the terms above.</p>
                    <button type="submit" class="btn btn-success btn-agree">Agree</button>
                    <button class="btn btn-danger btn-exit">Exit</button>
                </div>
            </div>
        </div>
    </div>
</div>