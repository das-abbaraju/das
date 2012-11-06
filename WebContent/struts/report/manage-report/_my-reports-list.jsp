<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!reportPermissionUsers.isEmpty()">
    <%-- cannot pass list as a include : param - bypass via setter --%>
    <s:set var="reports" value="reportPermissionUsers" />
    <s:include value="/struts/report/manage-report/_report-list.jsp">
        <s:param name="list_id">report_my_reports_list</s:param>
        <s:param name="list_class">report-list</s:param>
        <s:param name="enable_sort" value="false" />
    </s:include>
</s:if>
<s:else>
    <div class="row">
        <div class="span6 offset3">
            <div class="alert alert-info alert-block">
                <button type="button" class="close" data-dismiss="alert">×</button>
                
                <h4>You have no reports.</h4>
                <p>
                    BANANA SUPERMAN
                </p>
            </div>
        </div>
    </div>
</s:else>