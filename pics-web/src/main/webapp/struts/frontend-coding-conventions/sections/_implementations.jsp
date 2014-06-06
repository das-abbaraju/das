<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="parent_id">${param.parent_id}</s:set>
<s:set var="example_code">${param.example_code}</s:set>

<div class="panel-group" id="${parent_id}_accordion">
    <div class="panel panel-default">

        <s:if test="#example_code != ''">
            <div class="panel-heading">
                <h4 class="panel-title">
                    <a data-toggle="collapse" data-parent="#${parent_id}_accordion" href="#${parent_id}_html">
                        &lt;/&gt; Show Example
                    </a>
                </h4>
            </div>
            <div id="${parent_id}_html" class="panel-collapse collapse html-collapsible">
                <div class="panel-body">
                    <pre><code>${example_code}</code></pre>
                </div>
            </div>
        </s:if>
    </div>
</div>