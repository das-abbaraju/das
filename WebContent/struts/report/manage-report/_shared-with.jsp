<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="false">
    <s:include value="/struts/report/manage-report/_filter.jsp">
        <s:param name="method">sharedWith</s:param>
    </s:include>
    
    <section id="shared_with_reports">
    
    </section>
</s:if>
<s:else>
    <div class="row">
        <div class="span6 offset3">
            <div class="alert alert-info alert-block">
                <button type="button" class="close" data-dismiss="alert">×</button>
                
                <h4><s:text name="ManageReports.NoSharedWithReports.Info" /></h4>
                <p>
                    <s:text name="ManageReports.NoSharedWithReports.Message" />
                </p>
            </div>
        </div>
    </div>
</s:else>