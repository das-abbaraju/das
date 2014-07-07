<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Url --%>
<s:url action="#" var="continue_url" />
<s:url action="#" var="back_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Edit Suggested Address</s:param>
</s:include>

<title>
    <s:text name="ContractorRegistration.title" />
</title>

<div class="row">
    <div class="col-md-8">
        <form action="${continue_url}" method="post" class="form-horizontal" role="form">
            <fieldset>
                <div class="form-group">
                    <label class="col-md-3 control-label"><strong>Address</strong></label>
                    <div class="col-md-4">
                       <textarea class="form-control" name="address" rows="3" tabindex="1"></textarea>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-3 control-label"><strong>Postal Code</strong></label>
                    <div class="col-md-4">
                       <input class="form-control" name="postalCode" type="text" tabindex="2" value=""></input>
                    </div>
                </div>

                <div class="form-group">
                    <s:set var="selected_country" value="'Canada'" />
                    <s:set var="countries" value="{'Australia', 'Canada', 'United States'}" />

                    <label class="col-md-3 control-label"><strong>Country</strong></label>
                    <div class="col-md-4">
                        <select name="country" class="form-control select2" tabindex="3">
                            <s:iterator value="#countries" var="country">
                                <s:if test="#selected_country == #country">
                                    <s:set var="is_selected" value="'selected'" />
                                </s:if>
                                <s:else>
                                    <s:set var="is_selected" value="''" />
                                </s:else>

                                <option value="${country}" ${is_selected}>${country}</option>
                            </s:iterator>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-md-9 col-md-offset-3 form-actions">
                        <button type="submit" class="btn btn-success" tabindex="4">Continue</button>
                        <a href="${back_url}" class="btn btn-default" tabindex="5">Back</a>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
    <div class="col-md-4">
        <div class="well">
            <div class="col-md-12">
                <p>You Entered:</p>
            </div>
            <dl class="employee-guard-information">
                <dt class="col-md-4">
                    <label>Address</label>
                </dt>
                <dd class="col-md-8">
                    17701 Cowan
                </dd>
                <dt class="col-md-4">
                    <label>Postal Code</label>
                </dt>
                <dd class="col-md-8">
                    92617
                </dd>
                <dt class="col-md-4">
                    <label>Country</label>
                </dt>
                <dd class="col-md-8">
                    United States
                </dd>
            </dl>
        </div>
    </div>
</div>
