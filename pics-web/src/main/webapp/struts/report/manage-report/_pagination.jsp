<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- if pagination --%>
<s:if test="pagination.hasPagination()">
    <div class="pagination pagination-centered">
        <ul>
            <%-- if previous page --%>
            <s:if test="pagination.hasPreviousPage()">
                <s:url action="ManageReports" method="search" var="previous_page_url" includeParams="all">
                    <s:param name="pagination.parameters.page" value="pagination.previousPage"/>
                    <s:param name="searchTerm">${searchTerm}</s:param>
                </s:url>

                <li>
                    <a href="${previous_page_url}"><i class="icon-angle-left"></i></a>
                </li>
            </s:if>

            <%-- get pages, iterate over pages, 1 to 4; if iterating over the current page, then disable the link --%>
            <s:iterator var="pageNumber" value="pagination.getNavPages()">
                <s:url action="ManageReports" method="search" var="page_url" includeParams="all">
                    <s:param name="pagination.parameters.page">${pageNumber}</s:param>
                    <s:param name="searchTerm">${searchTerm}</s:param>
                </s:url>

                <s:if test="%{#pageNumber != pagination.getPage()}">
                    <li>
                        <a href="${page_url}">${pageNumber}</a>
                    </li>
                </s:if>
                <s:else>
                    <li class="active">
                        <span>${pageNumber}</span>
                    </li>
                </s:else>
            </s:iterator>

            <s:if test="pagination.hasNextPage()">
                <s:url action="ManageReports" method="search" var="next_page_url" includeParams="all">
                    <s:param name="pagination.parameters.page" value="pagination.nextPage"/>
                    <s:param name="searchTerm">${searchTerm}</s:param>
                </s:url>

                <li>
                    <a href="${next_page_url}"><i class="icon-angle-right"></i></a>
                </li>
            </s:if>
        </ul>
    </div>
</s:if>