<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set value="%{getAuditDataIfAnswerNotEmpty(18877)}" var="risk_level_audit_data" />
<s:set value="%{getAuditDataIfAnswerNotEmpty(18878)}" var="work_level_audit_data" />
<s:set value="{13207,13208,13209,13210,13211}" var="summary_question_ids" />
<s:set value="{13176,13178,13183,13184,13185}" var="system_gap_question_ids" />
<s:set value="{18903,18905,18906,18907,18908}" var="responsible_party_question_ids" />
<s:set value="{13224,13225,13226,13227,13228}" var="target_date_question_ids" />
<s:set value="%{getAuditDataIfAnswerNotEmpty(13212).answer}" var="list_of_subcontractors" />
<s:set value="%{getAuditDataIfAnswerNotEmpty(13213).answer}" var="frequency_of_review_answer" />
<s:set value="%{getQuestion(13213)}" var="rmp_stewardship" />
<s:set value="{13214,13215,13216,13217,13218}" var="approval_question_ids" />

<%-- first element of responsible party set and target date set are related to first element of system gaps set --%>

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
            <dd></dd>
            
            <dt>Risk Level:</dt>
			<dd>
				<s:if test="#risk_level_audit_data">
            		<s:text name="%{getTranslationKeyForAnswer(#risk_level_audit_data)}" />
				</s:if>
			</dd>
			
            <dt>Work Level:</dt>
            <dd>
     			<s:if test="#work_level_audit_data">
					<s:text name="%{getTranslationKeyForAnswer(#work_level_audit_data)}" />
				</s:if>
            </dd>
            
            <dt class="last-dt">Review date:</dt>
            <dd></dd>
        </dl>
    </section>

    <section class="executive-summary">
        <h1>Executive Summary</h1>
        
        <table class="table">
            <s:iterator value="#summary_question_ids" var="question_id">
            	<s:if test="%{getQuestion(#question_id)}">
	                <s:text name="%{getQuestion(#question_id).getI18nKey('name')}" var="question" />
	                <s:set value="%{getAuditDataIfAnswerNotEmpty(#question_id).answer}" var="answer" />
	                
	                <tr>
	                    <td>
	                        <b>${question}</b>
	                        <p>${answer}</p>
	                    </td>
	                </tr>
                </s:if>
            </s:iterator>
            
            <tr>
                <td>
                    <dl>
                        <dt>RMP Owner:</dt>
                        <dd></dd>
                        
                        <dt>BU/Site:</dt>
                        <dd></dd>
                        
                        <dt>Name:</dt>
                        <dd></dd>
                        
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
                    <th class="span2">Resp.</th>
                    <th class="span2">Target Date</th>
                    <th class="span4">Comments/Status</th>
                </tr>
            </thead>

            <tbody>
            	<s:set value="0" var="row_number"/>
	            <s:iterator value="#system_gap_question_ids" var="question_id" status="status">
	            	<s:if test="%{getQuestion(#question_id)}">
	           			<s:set value="%{getAuditDataIfAnswerNotEmpty(#question_id).answer}" var="system_gap_answer" />
	           			
	           			<s:if test="#system_gap_answer == 'No'">
	           				<!-- TODO: Row_number++ doesn't work? -->
	           				<s:set var="row_number" value="%{#row_number+1}"/>
	           				<s:set value="#responsible_party_question_ids[#status.index]" var="responsible_party_id" />
	           				<s:set value="%{getAuditDataIfAnswerNotEmpty(#responsible_party_id)}" var="responsible_party" />
	           				<s:set value="#target_date_question_ids[#status.index]" var="target_date_id" />
	           				
	         				<tr>
	         					<td>${row_number}</td>
	         					<td>
	         						<s:if test="#responsible_party">
	         							<s:text name="%{getTranslationKeyForTitle(#responsible_party_id)}" /></td>
         							</s:if>
								<td>
									<s:if test="#responsible_party">
										<s:text name="%{getTranslationKeyForAnswer(#responsible_party)}" /></td>
									</s:if>
								<td><s:property value="%{getAuditDataIfAnswerNotEmpty(#target_date_id).answer}" /></td>
								<td><s:property value="%{getAuditDataIfAnswerNotEmpty(#target_date_id).comment}" /></td>
							</tr>
						</s:if>
					</s:if>
				</s:iterator>
            </tbody>
        </table>
    </section>

    <section>
        <h1>List of Subcontractors</h1>
        <p>
            <s:property value="#list_of_subcontractors" />
        </p>
    </section>

    <section>
   		<h1>RMP Stewardship</h1>
   		<s:if test="#rmp_stewardship">
   			<table class="table table-striped">
	            <thead>
	                <tr>
	                    <th class="span4"></th>
	                    <th class="span2">Monthly</th>
	                    <th class="span2">Quarterly</th>
	                    <th class="span2">Semi-Annually</th>
	                    <th class="span2">Annually</th>
	                </tr>
	            </thead>
	            <tbody>
	                <tr>
	                    <td>
	                        <b><s:text name="%{#rmp_stewardship.getI18nKey('name')}" /></b>
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
        </s:if>
    </section>

    <section>
        <h1>Planned Date of Next Annual RMP Review/Update</h1>
        
        <dl>
            <dt class="last-dt">Date:</dt>
            <dd></dd>
        </dl>
	</section>
    
	<section>
        <h1>Approval/Endorsement of RMP</h1>

        <ol>
            <s:iterator value="#approval_question_ids" var="question_id">
            	<s:if test="%{getQuestion(#question_id)}">
	                <s:text name="%{getQuestion(#question_id).getI18nKey('name')}" var="question" />
	                <s:set value="%{getAuditDataIfAnswerNotEmpty(#question_id).answer}" var="answer" />
	                <s:set value="%{@com.picsauditing.PICS.DateBean@format(getAuditDataIfAnswerNotEmpty(#question_id).updateDate, @com.picsauditing.util.PicsDateFormat@Iso)}" var="date" />

	                <li>
	                	<b>${question}</b>
	                    <dl>
	                        <dt>Name/Title:</dt>
	                        <dd>${answer}</dd>

	                        <!-- TODO: Format date to remove time -->
	                        <dt class="last-dt">Date:</dt>
	                        <dd>${date}</dd>
	                    </dl>
	                </li>
                </s:if>
            </s:iterator>
        </ol>
    </section>
</body>