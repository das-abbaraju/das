<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<s:set var="does_not_expire" value="%{skillForm.doesNotExpire ? 'checked' : ''}"/>

<div class="form-group">
    <tw:label labelName="intervalPeriod" class="col-md-3 control-label"><strong>Expires after&hellip;</strong></tw:label>
    <div class="col-md-4">
        <div class="row">
            <fieldset class="expiration-date">
                <div class="col-md-4 col-xs-4">
                    <tw:input inputName="intervalPeriod" type="text" class="form-control" placeholder="1" value="${skillForm.intervalPeriod}" tabindex="4" />
                </div>
                <div class="col-md-8 col-xs-8">
                    <tw:select selectName="intervalType" class="form-control select2Min" tabindex="5">
                        <s:iterator value="intervalTypes" var="intervalType">
                            <s:set var="is_selected" value="%{#intervalType == skillForm.intervalType}" />

                            <tw:option value="${intervalType.name()}" selected="${is_selected}">${intervalType.displayValue}</tw:option>
                        </s:iterator>
                    </tw:select>
                </div>
            </fieldset>
        </div>
        <div class="checkbox">
            <tw:label labelName="doesNotExpire" class="control-label">
                <s:if test="skillForm.doesNotExpire">
                    <tw:input inputName="doesNotExpire" type="checkbox" class="no-expiration" value="true" checked="checked" data-toggle="form-input" data-target=".expiration-date" tabindex="6"/>
                </s:if>
                <s:else>
                    <tw:input inputName="doesNotExpire" type="checkbox" class="no-expiration" value="true" data-toggle="form-input" data-target=".expiration-date" tabindex="6"/>
                </s:else>
                Does not expire
            </tw:label>
        </div>
    </div>
</div>