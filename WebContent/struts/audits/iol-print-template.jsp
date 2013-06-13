<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set value="{13207,13208,13209,13210,13211}" var="risk_mitigation_plan_summary_question_ids" />
<s:set value="{13224,13225,13227,13229,13232,13234}" var="risk_mitigation_plan_question_ids" />
<s:set value="{13214,13215,13216,13217,13218}" var="risk_mitigation_plan_approval_question_ids" />
<s:set var="frequency_of_review_answer" value="%{getAnswer(13213).answer}" />

<head>
    <title><s:text name="%{audit.auditType.getI18nKey('name')}" /> for <s:property value="audit.contractorAccount.name" /></title>

    <link rel="stylesheet" type="text/css" href="css/style.css?v=${version}" />
    <link rel="stylesheet" href="css/print.css?v=${version}" />
    <link rel="stylesheet" type="text/css" href="v7/css/vendor/bootstrap.css?v=${version}" />
    <link rel="stylesheet" type="text/css" href="v7/css/vendor/bootstrap-responsive.css?v=${version}" />
</head>

<body id="${actionName}_${methodName}_page" class="${actionName}-page page">
    <header>
        <button href="#" class="btn pull-right print" onclick="window.print(); return false;"><s:text name="global.print"/></button>
        <h1><s:text name="%{audit.auditType.getI18nKey('name')}" /></h1>
    </header>
    
    <hr />

    <section class="summary">
        <dl>
            <dt>Company:</dt>
            <dd>${audit.contractorAccount.name}</dd>
            
            <dt>Developed By:</dt>
            <dd>James Walker (Angel), Kerry Pearson</dd>
            
            <dt>Risk level:</dt>
            <dd>Medium 7</dd>
            
            <dt>Work Level:</dt>
            <dd>2</dd>
            
            <dt>Review date:</dt>
            <dd>2012-11-21</dd>
        </dl>
    </section>

    <section class="executive-summary">
        <h1>Executive Summary</h1>
        
        <table class="table">
            <s:iterator value="#risk_mitigation_plan_summary_question_ids" var="question_id">
                <s:text name="%{getQuestion(#question_id).getI18nKey('name')}" var="question" />
                <s:set value="%{getAnswer(#question_id).answer}" var="answer" />
                
                <tr>
                    <td>
                        <b>${question}</b>
                        <p>${answer}</p>
                    </td>
                </tr>
            </s:iterator>
            
            <tr>
                <td>
                    <dl>
                        <dt>RMP Owner:</dt>
                        <dd></dd>
                        
                        <dt>BU/Site:</dt>
                        <dd>Technical</dd>
                        
                        <dt>Name:</dt>
                        <dd>Kerry Pearson</dd>
                        
                        <dt>RMP User Contacts: BU/Site:</dt>
                        <dd></dd>
                        
                        <dt>Name:</dt>
                        <dd></dd>
                    </dl>
                </td>
            </tr>
        </table>
    </section>

    <section>
        <h1>Risk Mitigation Plan (RMP)</h1>
        
        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="span1">#</th>
                    <th class="span3">Improvement Items</th>
                    <th class="span2">Target Date</th>
                    <th class="span5">Comments/Status</th>
                </tr>
            </thead>
            <tbody>
            	<s:iterator value="#risk_mitigation_plan_question_ids" var="question_id" status="status">
                    <s:text name="%{getQuestion(#question_id).getI18nKey('name')}" var="question" />
           			<s:set value="%{getAnswer(#question_id).answer}" var="answer" />
                    <s:set value="%{getAnswer(#question_id).comment}" var="comment"/>
                    
                    <s:if test="!#answer.isEmpty()">
                    	<tr>
                            <td>${status.index + 1}</td>
                            <td>${question}</td>
                            <td>${answer}</td>
                            <td>${comment}</td>
                        </tr>
                    </s:if>
                </s:iterator>
            </tbody>
        </table>
    </section>

    <section>
        <h1>List of Subcontractors</h1>
        <p>
            <s:property value="%{getAnswer(13212).answer}" />
        </p>
    </section>

    <section>
        <h1>RMP Stewardship</h1>
        
        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="span3"></th>
                    <th class="span2">Monthly</th>
                    <th class="span2">Quarterly</th>
                    <th class="span2">Semi-Annually</th>
                    <th class="span2">Annually</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <b><s:text name="%{getQuestion(13213).getI18nKey('name')}" /></b>
                    </td>
                    <td>
                        <s:if test="#frequency_of_review_answer == 1260">X</s:if>
                    </td>
                    <td>
                        <s:if test="#frequency_of_review_answer == 1261">X</s:if>
                    </td>
                    <td>
                        <s:if test="#frequency_of_review_answer == 1262">X</s:if>
                    </td>
                    <td>
                        <s:if test="#frequency_of_review_answer == 1263">X</s:if>
                    </td>
                </tr>
            </tbody>
        </table>
    </section>

    <section>
        <h1>Planned Date of Next Annual RMP Review/Update</h1>
        
        <dl>
            <dt>Date:</dt>
            <dd>02/02/52</dd>
        </dl>
	</section>
    
	<section>
        <h1>Approval/Endorsement of RMP</h1>
       
        <ol>
            <s:iterator value="#risk_mitigation_plan_approval_question_ids" var="question_id">
                <s:text name="%{getQuestion(#question_id).getI18nKey('name')}" var="question" />
                <s:set value="%{getAnswer(#question_id).answer}" var="answer" />
                
                <li>
                    <dl>
                        <dt>${question}</dt>
                        <dd>&nbsp;</dd>
                        
                        <dt>Name/Title:</dt>
                        <dd>${answer}</dd>
                        
                        <dt>Date:</dt>
                        <dd>02/02/17</dd>
                    </dl>
                </li>
            </s:iterator>
        </ol>
    </section>
</body>