<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- URL --%>
<s:url action="GoSomewhere" method="doSomething" var="reset_password" />

<div class="notice">
    <!--[if lte IE 8]><img class="logo" src="/v7/img/logo/logo-small.png"><!--<![endif]-->
    <!--[if gt IE 8]><!--><img class="logo" src="/v7/img/logo/logo-small.svg"><!--<![endif]-->

    <h1><s:text name="ProfileEdit.Password.heading" /></h1>

    <form action="${reset_password}" method="post">
        <label for="username"><s:text name="global.Password.new" /></label>
        <input type="text" name="password1" />

        <label for="username"><s:text name="ProfileEdit.ConfirmPassword" /></label>
        <input type="text" name="password2" />

        <div class="form-actions">
            <button type="submit" class="btn btn-success" tabindex="6"><s:text name="button.Save" /></button>
        </div>
    </form>
</div>
