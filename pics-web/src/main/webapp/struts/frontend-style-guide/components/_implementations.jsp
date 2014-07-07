<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="parent_id">${param.parent_id}</s:set>
<s:set var="html_code">${param.html_code}</s:set>
<s:set var="struts_code">${param.struts_code}</s:set>

<div class="panel-group" id="${parent_id}_accordion">
    <div class="panel panel-default">

        <s:if test="#html_code != ''">
            <div class="panel-heading">
                <h4 class="panel-title">
                    <a data-toggle="collapse" data-parent="#${parent_id}_accordion" href="#${parent_id}_html">
                        &lt;/&gt; Show Markup
                    </a>
                </h4>
            </div>
            <div id="${parent_id}_html" class="panel-collapse collapse html-collapsible">
                <div class="panel-body">
                    <pre><code>${html_code}</code></pre>
                </div>
            </div>
        </s:if>

        <s:if test="#struts_code != ''">
             <div class="panel-heading">
                <h4 class="panel-title">
                    <a data-toggle="collapse" data-parent="#${parent_id}_accordion" href="#${parent_id}_struts">
                        <span class="heading">Struts</span>
                    </a>
                </h4>
            </div>
            <div id="${parent_id}_struts" class="panel-collapse collapse">
                <div class="panel-body">
                    <pre><code>${struts_code}</code></pre>
                </div>
            </div>
        </s:if>

    </div>
</div>