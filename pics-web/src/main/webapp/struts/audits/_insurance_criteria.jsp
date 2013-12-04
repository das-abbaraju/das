<%@ page language="java" errorPage="/exception_handler.jsp" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


<s:if test="#q.category.uniqueCode == 'limits' && #a.isAnswered()">
    <s:set var="insuranceCriteria" value="getInsuranceCriteriaMap(#q)"/>
    <s:if test="#insuranceCriteria.size() > 0">
        <div class="insurance-criteria">
            <p><s:text name="Audit.InsuranceLimit.RequiredLimits"/></p>
            <ul>
                <s:iterator value="#insuranceCriteria.keySet()" var="criteriaLimit" status="status">
                    <s:set var="excess_criteria"></s:set>
                    <s:if test="#status.count > 3">
                        <s:set var="excess_criteria">excess-criteria hide</s:set>
                    </s:if>

                    <li class="criteria-list ${excess_criteria}">
                        <div class="icon">
                            <s:if test="meetsCriteria(#a, #insuranceCriteria.get(#criteriaLimit).get(0))">
                                <i class="icon-ok icon-large"></i>
                            </s:if>
                            <s:else>
                                <i class="icon-warning-sign icon-large"></i>
                            </s:else>
                        </div>
                        <div class="operators">
                            <s:set value="#insuranceCriteria.get(#criteriaLimit)" var="criteriaList"/>
                            <s:iterator value="criteriaList" var="criteria" status="anotherStatus">
                                ${criteria.operatorAccount.name} <s:if
                                    test="#anotherStatus.count < #criteriaList.size()">,</s:if>
                            </s:iterator>
                        </div>
                        <div class="limit">
                            <s:property
                                    value="@com.picsauditing.util.Strings@formatInternationalNumber(#criteriaLimit, getPermissions().getLocale())"/>
                        </div>
                    </li>
                </s:iterator>
            </ul>
            <s:if test="#insuranceCriteria.size() > 3">
                <button class="btn toggle-excess-criteria"><s:text name="Audit.InsuranceLimit.ShowAll"/></button>
            </s:if>
        </div>
    </s:if>
</s:if>
