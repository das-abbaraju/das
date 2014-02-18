<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="employee" method="photo" var="image_url">
    <s:param name="id">
        ${id}
    </s:param>
</s:url>

<%-- Employee Status --%>
<s:set var="employee_status">expired</s:set>
<s:if test="#employee_status == 'expired'">
    <s:set var="employee_status_icon">icon-minus-sign-alt</s:set>
    <s:set var="employee_status_class">danger</s:set>
</s:if>
<s:elseif test="#employee_status == 'expiring'">
    <s:set var="employee_status_icon">icon-warning-sign</s:set>
    <s:set var="employee_status_class">warning</s:set>
</s:elseif>
<s:elseif test="#employee_status == 'pending'">
    <s:set var="employee_status_icon">icon-ok-circle</s:set>
    <s:set var="employee_status_class">success</s:set>
</s:elseif>
<s:else>
    <s:set var="employee_status_icon">icon-ok-sign</s:set>
    <s:set var="employee_status_class">success</s:set>
</s:else>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Live ID</s:param>
    <s:param name="breadcrumbs">
        false
    </s:param>
</s:include>

<div class="operator-employee-page">
    <div class="row employee-info">
        <div class="col-md-3">
            <figure class="employee-image img-thumbnail">
                <%-- <img src="${image_url}" class="img-responsive" alt="${alt_text}" width="250" height="250"/> --%>
                <img src="http://media.screened.com/uploads/0/562/324177-ghostbusters_4_super.jpg" class="img-responsive" alt="${alt_text}" />
            </figure>
        </div>
        <div class="col-md-9">
            <div class="row">
                <div class="col-md-9">
                    <p class="name">Peter Venkman</p>
                    <ul class="list-unstyled">
                        <li class="company">Ghostbusters Inc.</li>
                        <li class="title">Public Relations</li>
                    </ul>
                </div>
                <div class="col-md-3">
                    <i class="${employee_status_icon} ${employee_status_class} pull-right"></i>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-3">
            <div class="bs-sidebar">
                <nav id="side-navigation" class="navbar bs-sidenav" role="navigation">
                    <ul class="nav" role="menu">
                        <li class="active">
                            <a href="#" data-filter="none">All
                                <i class="${employee_status_icon} ${employee_status_class} pull-right"></i>
                            </a>
                        </li>
                        <li class="nav-divider"></li>
                        <li>
                            <span class="nav-title">Job Roles</span>
                        </li>

                        <%-- Iterate over job roles --%>
                        <s:set var="employee_skill_status">pending</s:set>
                        <s:if test="#employee_skill_status == 'expired'">
                            <s:set var="employee_skill_status_icon">icon-minus-sign-alt</s:set>
                            <s:set var="employee_skill_status_class">danger</s:set>
                        </s:if>
                        <s:elseif test="#employee_skill_status == 'expiring'">
                            <s:set var="employee_skill_status_icon">icon-warning-sign</s:set>
                            <s:set var="employee_skill_status_class">warning</s:set>
                        </s:elseif>
                        <s:elseif test="#employee_skill_status == 'pending'">
                            <s:set var="employee_skill_status_icon">icon-ok-circle</s:set>
                            <s:set var="employee_skill_status_class">success</s:set>
                        </s:elseif>
                        <s:else>
                            <s:set var="employee_skill_status_icon">icon-ok-sign</s:set>
                            <s:set var="employee_skill_status_class">success</s:set>
                        </s:else>


                        <li>
                            <a href="#" data-filter="section1">Scientist
                                <i class="${employee_skill_status_icon} ${employee_skill_status_class} pull-right"></i>
                            </a>
                        </li>
                        <li>
                            <a href="#" data-filter="section2">Ghostbuster
                                <i class="${employee_skill_status_icon} ${employee_skill_status_class} pull-right"></i>
                            </a>
                        </li>

                        <li class="nav-divider"></li>
                        <li>
                            <span class="nav-title">Projects</span>
                        </li>

                        <%-- Iterate over projects --%>
                        <s:set var="employee_project_status">expiring</s:set>
                        <s:if test="#employee_project_status == 'expired'">
                            <s:set var="employee_project_status_icon">icon-minus-sign-alt</s:set>
                            <s:set var="employee_project_status_class">danger</s:set>
                        </s:if>
                        <s:elseif test="#employee_project_status == 'expiring'">
                            <s:set var="employee_project_status_icon">icon-warning-sign</s:set>
                            <s:set var="employee_project_status_class">warning</s:set>
                        </s:elseif>
                        <s:elseif test="#employee_project_status == 'pending'">
                            <s:set var="employee_project_status_icon">icon-ok-circle</s:set>
                            <s:set var="employee_project_status_class">success</s:set>
                        </s:elseif>
                        <s:else>
                            <s:set var="employee_project_status_icon">icon-ok-sign</s:set>
                            <s:set var="employee_project_status_class">success</s:set>
                        </s:else>

                        <li>
                            <a href="#" data-filter="section3">Hotel
                                <i class="${employee_project_status_icon} ${employee_project_status_class} pull-right"></i>
                            </a>
                        </li>
                        <li>
                            <a href="#" data-filter="section4">Stay Pufft Marshmallow Man
                                <i class="${employee_project_status_icon} ${employee_project_status_class} pull-right"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>

        <div class="col-md-9">
            <%-- <s:iterator var="company_skill_info" value="companySkillInfoList" status="loopvar"> --%>
                    <%-- <section class="employee-guard-section" id="section${loopvar.count}"></section> --%>
                    <section class="employee-guard-section" id="section1">
                        <h1>Scientist</h1>

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
                                        <a href="${employee_expiring_skill_url}" class="list-group-item warning">
                                            <i class="icon-warning-sign"></i>Hooking up with Research Students
                                        </a>
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
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
            <%-- </s:iterator> --%>
                    <section class="employee-guard-section" id="section2">
                        <h1>Ghostbuster</h1>

                        <div class="content">
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="list-group skill-list">
<%--                                         <s:iterator var="expired_skill" value="expiredSkills">
                                            <s:url action="skill" var="employee_expired_skill_url">
                                                <s:param name="id">${expired_skill.id}</s:param>
                                            </s:url>
                                            <a href="${employee_expired_skill_url}" class="list-group-item danger">
                                                <i class="icon-minus-sign-alt"></i>${expired_skill.name}
                                            </a>
                                        </s:iterator> --%>
                                            <a href="#" class="list-group-item danger">
                                                <i class="icon-minus-sign-alt"></i>Ghostbusting
                                            </a>
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
                                        <a href="${employee_expiring_skill_url}" class="list-group-item warning">
                                            <i class="icon-warning-sign"></i>Proton Pack Adjustment
                                        </a>
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
                                        <a href="${employee_completed_skill_url}" class="list-group-item success">
                                            <i class="icon-ok-sign"></i>Getting slimed
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                    <section class="employee-guard-section" id="section3">
                        <h1>Hotel</h1>

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
                                        <a href="${employee_expiring_skill_url}" class="list-group-item warning">
                                            <i class="icon-warning-sign"></i>Damage Control
                                        </a>
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
                                        <a href="${employee_completed_skill_url}" class="list-group-item success">
                                            <i class="icon-ok-sign"></i>Fee collection
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                    <section class="employee-guard-section" id="section4">
                        <h1>Stay Puft Marshmallow Man</h1>

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
                                        <a href="${employee_completed_skill_url}" class="list-group-item success">
                                            <i class="icon-ok-sign"></i>Crossing the streams
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
        </div>
    </div>
</div>