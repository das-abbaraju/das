<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="role" var="operator_role_list_url"/>
<s:url action="role" method="create" var="operator_role_create_url"/>

<%-- Page title --%>
<s:if test="permissions.corporate">
    <s:include value="/struts/employee-guard/_page-header.jsp">
        <s:param name="title"><s:text name="OPERATOR.JOB_ROLES.LIST.PAGE.HEADER"/></s:param>
        <s:param name="actions">
            <a href="${operator_role_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> <s:text name="OPERATOR.JOB_ROLES.LIST.JOB_ROLE.BUTTON"/></a>
        </s:param>
    </s:include>
</s:if>
<s:else>
    <s:include value="/struts/employee-guard/_page-header.jsp">
        <s:param name="title"><s:text name="OPERATOR.JOB_ROLES.LIST.TABLE.HEADER.JOB_ROLE"/></s:param>
    </s:include>
</s:else>

    <s:if test="!roles.isEmpty()">
        <tw:form formName="operator_role_search" action="${operator_role_list_url}" class="search-query" role="form">
            <fieldset>
                <div class="search-wrapper col-md-4">
                    <s:set var="placeholderText"><s:text name="OPERATOR.JOB_ROLES.LIST.SEARCH.INPUT.DEFAULT_TEXT"/></s:set>
                    <tw:input inputName="searchTerm" type="text" class="form-control" placeholder="${placeholderText}"
                              value="${searchForm.searchTerm}"/>
                    <i class="icon-search"></i>
                    <ul id="operator_role_search" class="search-results"></ul>
                </div>
            </fieldset>
        </tw:form>
        <div class="table-responsive">
            <table class="table table-striped table-condensed table-hover">
                <thead>
                <tr>
                    <th><s:text name="OPERATOR.JOB_ROLES.LIST.TABLE.HEADER.JOB_ROLE"/></th>
                    <th><s:text name="OPERATOR.JOB_ROLES.LIST.TABLE.HEADER.REQUIRED_SKILLS"/></th>
                </tr>
                </thead>
                <tbody>
                <s:iterator value="roles" var="role">
                    <s:url action="role" var="operator_role_show_url">
                        <s:param name="id">${role.id}</s:param>
                    </s:url>
                    <tr>
                        <td><a href="${operator_role_show_url}">${role.name}</a></td>
                        <td>
                            <s:set name="operator_skills" value="#role.skills"/>
                            <s:include value="/struts/employee-guard/operator/skill/_list.jsp"/>
                        </td>
                    </tr>
                </s:iterator>
                </tbody>
            </table>
        </div>
    </s:if>
    <s:else>
        <section class="employee-guard-section">
            <h1>
                <i class="icon-group icon-large"></i><s:text name="OPERATOR.JOB_ROLES.LIST.SECTION.HEADER"/>
            </h1>
            <div class="content">
                <div class="col-md-8 col-md-offset-2">
                    <s:if test="permissions.corporate">
                        <div class="alert alert-info">
                            <h4><s:text name="CORPORATE.JOB_ROLES.LIST.NO_JOB_ROLES_MSG.TITLE"/></h4>

                            <p><s:text name="CORPORATE.JOB_ROLES.LIST.NO_JOB_ROLES_MSG.MSG1"/></p>

                            <p><s:text name="CORPORATE.JOB_ROLES.LIST.NO_JOB_ROLES_MSG.MSG2"/></p>

                            <p>
                                <a href="#"><i class="icon-question-sign"></i> <s:text name="CORPORATE.JOB_ROLES.LIST.NO_JOB_ROLES_MSG.MSG3"/></a>
                            </p>
                        </div>
                    </s:if>
                    <s:else>
                        <div class="alert alert-info">
                            <h4><s:text name="OPERATOR.JOB_ROLES.LIST.NO_JOB_ROLES_MSG.TITLE"/></h4>

                            <p><s:text name="OPERATOR.JOB_ROLES.LIST.NO_JOB_ROLES_MSG.MSG1"/></p>

                            <p>
                                <a href="#"><i class="icon-question-sign"></i> <s:text name="OPERATOR.JOB_ROLES.LIST.NO_JOB_ROLES_MSG.MSG2"/></a>
                            </p>
                        </div>
                    </s:else>
                </div>
            </div>
        </section>
    </s:else>