<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!reportUsers.isEmpty()">
    <section id="my_reports">
        <%-- cannot pass list as a include : param - bypass via setter --%>
        <s:set var="reports" value="reportUsers" />
        <s:include value="/struts/report/manage-report/_report-list.jsp" />
    </section>
</s:if>
<s:else>
    <div class="row">
        <div class="span6 offset3">
            <div class="alert alert-info alert-block">
                <button type="button" class="close" data-dismiss="alert">×</button>
                
                <h4><s:text name="ManageReports.NoReports.Info" /></h4>
                <p>
                    <s:text name="ManageReports.NoReports.Message" />
                </p>
            </div>
        </div>
    </div>
</s:else>