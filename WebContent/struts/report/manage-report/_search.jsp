<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!reportList.isEmpty()">
    <section id="search_reports">
        <%-- cannot pass list as a include : param - bypass via setter --%>
        <s:set var="reports" value="reportList" />
        <s:include value="/struts/report/manage-report/_search-report-list.jsp" />
        <s:include value="/struts/report/manage-report/_pagination.jsp" />
    </section>
</s:if>
<s:else>
    <div class="row">
        <div class="span6 offset3">
            <div class="alert alert-info alert-block">
                <button type="button" class="close" data-dismiss="alert">×</button>
                
                <h4><s:text name="ManageReports.Search.NoResultsInfo" /></h4>
                <p>
                    <s:text name="ManageReports.Search.NoResultsMessage" />
                </p>
            </div>
        </div>
    </div>
</s:else>