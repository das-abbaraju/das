<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<style>
    .translation-form {
        white-space: nowrap;
    }
</style>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Report Tester</s:param>
</s:include>
<h3><s:property value="modelType" /> Model</h3>
<div>
    <a href="ReportTester.action">Back to Report Tester Home</a>
</div>

<div class="row">
    <div class="span3">
        <ul class="nav nav-list affix">
            <li><a href="#reports"><i class="icon-chevron-right"></i> Reports</a></li>
            <li><a href="#model"><i class="icon-chevron-right"></i> Model</a></li>
        </ul>
    </div>

    <div class="span9">
        <section id="reports">
            <h2>Reports</h2>

            <s:if test="reports.empty">No Reports</s:if>
            <s:else>
                <table id="reports">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Report</th>
                        <th>Drill Down</th>
                    </tr>
                    </thead>
                    <s:iterator value="reports">
                        <tr id="reports-row-<s:property value="id" />">
                            <td><s:property value="id" /></td>
                            <td><a href="Report.action?report=<s:property value="id" />"><s:property value="name" /></a></td>
                            <td><a href="ReportTester.action?reportID=<s:property value="id" />">Expand</a></td>
                        </tr>
                    </s:iterator>
                </table>
            </s:else>

        <section id="model">
            <h1>Available Fields</h1>

            <s:if test="availableField.empty">No Fields</s:if>
            <s:else>
                <table id="availableFields" class="table table-hover table-bordered">
                    <thead>
                    <tr>
                        <th>Field Name</th>
                        <th>Category</th>
                        <th>Label</th>
                        <th>Help</th>
                    </tr>
                    </thead>
                    <s:iterator value="availableField">
                        <tr>
                            <td><s:property value="name" /></td>
                            <td><s:text name="%{'Report.Category.' + category}" /></td>

                            <s:set name="fieldKey" value="'Report.' + name" />
                            <s:if test="hasKey(#fieldKey)">
                                <td><a href="ManageTranslations.action?key=<s:property value="#fieldKey" />"
                                       target="_BLANK">
                                    <i class="icon-pencil"></i><s:text name="%{#fieldKey + ''}" /></a></td>
                            </s:if>
                            <s:else>
                                <td class="translation-form"><form action="ManageTranslations.action" target="_BLANK" class="form-search">
                                    <input type="hidden" name="translation.locale" value="en" />
                                    <s:hidden name="translation.key" value="%{#fieldKey + ''}" />
                                    <input type="text" name="translation.value" class="input-small" placeholder="Label" />
                                    <button type="submit" class="btn btn-small" name="button" value="save">Add</button>
                                </form></td>
                            </s:else>
                            <s:if test="hasKey(#fieldKey + '.help')">
                                <td><s:text name="%{#fieldKey + '.help'}" /></td>
                            </s:if>
                            <s:else>
                                <td class="translation-form"><form action="ManageTranslations.action" target="_BLANK" class="form-search">
                                    <input type="hidden" name="translation.locale" value="en" />
                                    <s:hidden name="translation.key" value="%{#fieldKey + '.help'}" />
                                    <input type="text" name="translation.value" class="input-small" placeholder="Help Text" />
                                    <button type="submit" class="btn btn-small" name="button" value="save">Add</button>
                                </form></td>
                            </s:else>
                        </tr>
                    </s:iterator>
                </table>
            </s:else>
        </section>
    </div>
</div>
