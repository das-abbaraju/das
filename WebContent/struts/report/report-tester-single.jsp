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
<a href="ReportTester.action">Back to Report Tester Home</a>

<div class="row">
    <div class="span3">
        <ul class="nav nav-list affix">
            <li><a href="#sql"><i class="icon-chevron-right"></i> SQL</a></li>
            <li><a href="#report"><i class="icon-chevron-right"></i> Report</a></li>
            <li><a href="#model"><i class="icon-chevron-right"></i> Model</a></li>
        </ul>
    </div>

    <div class="span9">
        <section id="sql">
            <div class="page-header">
                <h2>SQL</h2>
            </div>

            <form>
                <textarea class="span9" rows="6"><s:property value="report.sql"/></textarea>
            </form>
            <a href="Report.action?report=<s:property value="report.id" />"><s:property value="report.name"/></a>
        </section>

        <section id="report">
            <h2>Report Columns, Filters, and Sorts</h2>

            <table id="report.fields" class="table table-striped table-bordered">
                <thead>
                <tr>
                    <th>Field Name</th>
                    <th>Category</th>
                    <th>Translation</th>
                    <th>Sql</th>
                </tr>
                </thead>
                <s:iterator value="reportElements">
                    <tr>
                        <td><s:property value="name" /></td>
                        <td><s:property value="field.category" /></td>
                        <td><s:property value="field.text" /></td>
                        <td><s:property value="sql" /></td>
                    </tr>
                </s:iterator>
            </table>
        </section>

        <section id="model">
            <h1>Available Fields for <s:property value="report.modelType" /> Model</h1>

            <table id="report.availableFields" class="table table-hover table-bordered">
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
        </section>
    </div>
</div>
