<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="report_search_list">
    <s:include value="../_action-messages.jsp" />

    <ul class="report-list">
        <s:iterator value="userReports" var="user_report">
            <s:set name="report" value="#user_report.report" />
            <s:set name="report_id" value="#report.id" />
    
            <%-- Url --%>
            <s:url action="Report" var="report_url">
                <s:param name="report">${report_id}</s:param>
            </s:url>
    
            <li class="report">
                <div class="summary">
                    <a href="${report_url}" class="name">
                        ${report.name}
                    </a>
    
                    <s:if test="#report.createdBy.id != permissions.userId">
                        <span class="created-by"><s:text name="ManageReports.report.createdBy" /> ${report.createdBy.name}</span>
                    </s:if>
    
                    <p class="description">${report.description}</p>
                </div>
    
                <span class="number-favorites">
                    ${report.numTimesFavorited} <s:text name="ManageReports.report.Favorites" />
                </span>
    
                <div class="clearfix"></div>
            </li>
        </s:iterator>
    </ul>
        
    <%-- if pagination --%>
    <s:if test="pagination.hasPagination()">    
    	<div class="pagination pagination-centered">
        	<ul>
            	<%-- if previous page --%>
            	<s:if test="pagination.hasPreviousPage">
            		<s:url action="ManageReports" method="searchList" var="previous_page_url" includeParams="all">
    					<s:param name="pagination.parameters.page" value="pagination.previousPage"/>
    					<s:param name="searchTerm">${searchTerm}</s:param>
    				</s:url>
            		<li>                		
    					<a href="${previous_page_url}"><i class="icon-caret-left"></i></a>
            		</li>
            	</s:if>
            
            	<%-- get pages, iterate over pages, 1 to 4; if iterating over the current page, then don't put a link --%>
            	<s:iterator var="pageNumber" value="pagination.getNavPages()">
            		<s:url action="ManageReports" method="searchList" var="page_url" includeParams="all">
    						<s:param name="pagination.parameters.page">${pageNumber}</s:param>
    						<s:param name="searchTerm">${searchTerm}</s:param>
    				</s:url>
    				<li>
    					<s:if test="%{#pageNumber != pagination.getPage()}">
    						<a href="${page_url}">${pageNumber}</a>
    					</s:if>
    					<s:else>
    						<span class="active">${pageNumber}</span>
    					</s:else>
    				</li>
            	</s:iterator>

            	<s:if test="pagination.hasNextPage">
            		<s:url action="ManageReports" method="searchList" var="next_page_url" includeParams="all">
    						<s:param name="pagination.parameters.page" value="pagination.nextPage"/>
    						<s:param name="searchTerm">${searchTerm}</s:param>
    				</s:url>
            		<li>            			
    					<a href="${next_page_url}"><i class="icon-caret-right"></i></a>
            		</li>
            	</s:if>
        	</ul>
    	</div>
    </s:if>
</div>