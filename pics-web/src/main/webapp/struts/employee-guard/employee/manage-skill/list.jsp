<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="skills/certificate" var="employee_skill_list_url"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Skills</s:param>
</s:include>

<s:if test="companySkillInfoList.size() > 0">
    <div class="row">
        <div class="col-md-3">
            <s:include value="/struts/employee-guard/employee/manage-skill/_sidenav.jsp"></s:include>
        </div>

        <div class="col-md-9">
            <s:iterator var="company_skill_info" value="companySkillInfoList" status="loopvar">
                <s:if test="#company_skill_info.hasSkills">
                    <section class="employee-guard-section" id="section${loopvar.count}">
                        <h1>${company_skill_info.name}</h1>

                        <div class="content">
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="list-group skill-list">
                                        <s:iterator var="expired_skill" value="expiredSkills">
                                            <s:url action="skill" var="employee_expired_skill_url">
                                                <s:param name="id">${expired_skill.id}</s:param>
                                            </s:url>
                                            <a href="${employee_expired_skill_url}" class="list-group-item danger">
                                                <i class="icon-minus-sign-alt"></i>${expired_skill.name}
                                            </a>
                                        </s:iterator>
                                    </div>
                                </div>

                                <div class="col-md-4">
                                    <div class="list-group skill-list">
                                        <s:iterator var="expiring_skill" value="aboutToExpireSkills">
                                            <s:url action="skill" var="employee_expiring_skill_url">
                                                <s:param name="id">${expiring_skill.id}</s:param>
                                            </s:url>
                                            <a href="${employee_expiring_skill_url}" class="list-group-item warning">
                                                <i class="icon-warning-sign"></i>${expiring_skill.name}
                                            </a>
                                        </s:iterator>
                                    </div>
                                </div>

                                <div class="col-md-4">
                                    <div class="list-group skill-list">
                                        <s:iterator var="completed_skill" value="completedSkills">
                                            <s:url action="skill" var="employee_completed_skill_url">
                                                <s:param name="id">${completed_skill.id}</s:param>
                                            </s:url>
                                            <a href="${employee_completed_skill_url}" class="list-group-item success">
                                                <i class="icon-ok-sign"></i>${completed_skill.name}
                                            </a>
                                        </s:iterator>


                                            <%-- TODO: Leave this here so we don't lose the class information for Pending skills --%>
                                            <%--<s:url action="skills/manage-skill" var="employee_manage_skill_url">
                                                <s:param name="id">1</s:param>
                                            </s:url>
                                            <a href="${employee_pending_skill_url}" class="list-group-item success">
                                                <i class="icon-ok-circle"></i>${pending_skill.name}
                                            </a>--%>

                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                </s:if>
            </s:iterator>
        </div>
    </div>
</s:if>
<s:else>
    <div class="col-md-6 col-md-offset-3">
        <div class="alert alert-info clearfix">
            <h4>No Required Skills</h4>

            <p>Right now, no one is requiring any specific skills for you.</p>

            <p>You can get a head start on your skills by adding certificates or other skill proof ahead of time. Just
                select the Certificates button at the top of the page and upload any certificates (files or photos) you
                already have. Those uploads can be easily applied to future required skills!</p>
        </div>
    </div>
</s:else>