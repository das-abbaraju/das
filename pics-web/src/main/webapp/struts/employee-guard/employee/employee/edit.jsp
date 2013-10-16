<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="profile" var="employee_profile_show_url">
    <s:param name="id">1</s:param>
</s:url>
<s:url action="profile" method="edit" var="employee_profile_edit_url">
    <s:param name="id">1</s:param>
</s:url>
<s:url action="skill" var="employee_skill_list_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Edit Profile</s:param>
</s:include>



<tw:form formName="employee_profile_edit" action="${employee_profile_edit_url}" method="post" class="form-horizontal" autocomplete="off">
    <fieldset>
        <div class="row">
            <div class="col-md-3">
                <figure class="employee-image img-polaroid">
                    <tw:input inputName="photo" type="file" />

                    <img src="/v7/img/employee-guard/dummy.jpg" alt="Profile photo" />

                    <div class="overlay-container">
                        <div class="overlay"></div>
                        <span class="edit-text">Select to edit</span>
                    </div>
                </figure>

                <%-- <section class="employee-guard-section">
                    <h1>
                        <a href="${employee_skill_list_url}"><i class="icon-external-link"></i></a>
                        <i class="icon-certificate"></i> Skills
                    </h1>

                    <ul class="unstyled skill-list">
                        <li class="skill">
                            <div class="complete">
                                <i class="icon-ok-sign"></i> PICS Orientation
                            </div>
                        </li>
                        <li class="skill">
                            <div class="pending">
                                <i class="icon-ok-circle"></i> Harassment Training
                            </div>
                        </li>
                        <li class="skill">
                            <div class="expire">
                                <i class="icon-warning-sign"></i> Creative Suite
                            </div>
                        </li>
                        <li class="skill">
                            <div class="incomplete">
                                <i class="icon-minus-sign-alt"></i> Product Owner Certification
                            </div>
                        </li>
                    </ul>
                </section> --%>
            </div>

            <div class="col-md-9">
                <section class="employee-guard-section">
                    <h1><i class="icon-user"></i> Personal</h1>

                    <div class="content">
                        <div class="control-group">
                            <tw:label labelName="firstName"><strong>First Name</strong></tw:label>
                            <div class="controls">
                                <tw:input inputName="firstName" type="text" />
                                <tw:error errorName="firstName" />
                            </div>
                        </div>

                        <div class="control-group">
                            <tw:label labelName="lastName"><strong>Last Name</strong></tw:label>
                            <div class="controls">
                                <tw:input inputName="lastName" type="text" />
                                <tw:error errorName="lastName" />
                            </div>
                        </div>

                        <div class="control-group">
                            <tw:label labelName="email"><strong>Email</strong></tw:label>
                            <div class="controls">
                                <tw:input inputName="email" type="text" />
                                <tw:error errorName="email" />
                            </div>
                        </div>

                        <div class="control-group">
                            <tw:label labelName="phoneNumber">Phone</tw:label>
                            <div class="controls">
                                <tw:input inputName="phoneNumber" type="text" />
                            </div>
                        </div>

                        <div class="control-group">
                            <div class="controls">
                                <tw:button type="submit" class="btn btn-success">Save</tw:button>
                                <a href="${employee_profile_show_url}" class="btn btn-default">Cancel</a>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="employee-guard-section">
                    <h1>
                        <i class="icon-file-text-alt"></i> Current Employment
                    </h1>

                    <div class="content">
                        <dl class="employee-guard-information">
                            <dt class="col-md-3">PICS</dt>
                            <dd class="col-md-9">
                                <ul class="employee-guard-list companies">
                                    <li>
                                        <a href="#"><span class="label">Technology</span></a>
                                    </li>
                                    <li>
                                        <a href="#"><span class="label">UX</span></a>
                                    </li>
                                </ul>
                            </dd>
                        </dl>
                    </div>
                </section>
            </div>
        </div>
    </fieldset>
</tw:form>