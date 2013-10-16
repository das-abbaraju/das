<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Live ID</s:param>
</s:include>



<div class="row">
    <div class="col-md-12">
        <section class="employee-guard-section">
            <h1>
                <a href="#"><i class="icon-list-ul"></i></a>
                Pics Auditing, LLC.
            </h1>

            <div class="content">
                <div class="row">
                    <span class="col-md-3">
                        <s:include value="/struts/employee-guard/employee/photo/_employee-photo-form.jsp"></s:include>
                    </span>
                    <span class="col-md-6">
                        <h2>${personalInfo.firstName} ${personalInfo.lastName}</h2>
                        <h4>${personalInfo.slug}</h4>
                    </span>

                    <span class="col-md-3">
                        <figure class="employee-image img-polaroid">
                            <img src="http://mockups.picsauditing.com/EmployeeGUARD/Live_ID_files/u2_normal.jpg" alt="PICS Live ID" />
                        </figure>
                    </span>
                </div>
            </div>
        </section>
    </div>
</div>