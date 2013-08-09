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
<h3><a href="Report.action?report=<s:property value="report.id" />"><s:property value="report.name"/></a></h3>
<div>
    <a href="ReportTester.action">Back to Report Tester Home</a>
</div>
<div>
    <a href="ReportTester.action?modelType=<s:property value="report.modelType"/>">Back to <s:property value="report.modelType" /> Model</a>
</div>

<div class="row">
    <div class="span3">
        <ul class="nav nav-list affix">
            <li><a href="#sql"><i class="icon-chevron-right"></i> SQL</a></li>
            <li><a href="#report"><i class="icon-chevron-right"></i> Report</a></li>
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
    </div>
</div>
