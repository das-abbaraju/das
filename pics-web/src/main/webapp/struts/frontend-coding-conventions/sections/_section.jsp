<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="description">${param.description}</s:set>
<s:set var="accordian_parent_id">${param.accordian_parent_id}</s:set>
<s:set var="example_code">${param.example_code}</s:set>

<li class="section-list-item list-unstyled">
    <p class="description">
        ${description}
    </p>

    <% if (request.getParameter("example_code") != null) { %>
        <s:set var="description_no_example_class">description-no-example</s:set>
        <div class="row implementations">
            <div class="col-md-offset-1 col-md-11">
                <s:include value="/struts/frontend-coding-conventions/sections/_implementations.jsp">
                    <s:param name="parent_id">${accordian_parent_id}</s:param>
                    <s:param name="example_code">${example_code}</s:param>
                </s:include>
            </div>
        </div>
    <% } %>
</li>