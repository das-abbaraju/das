<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page language="java" errorPage="/exception_handler.jsp" %>

<html>
	<head>
		<title><s:text name="OperatorTags.title.WithOperator"><s:param value="%{operator.name}" /></s:text></title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />

		<s:include value="../jquery.jsp"/>
		
		<script type="text/javascript" src="js/operator_tags.js?v=${version}"></script>
	</head>
	<body>
		<s:include value="../actionMessages.jsp" />
		
		<s:if test="permissions.admin">
			<s:include value="opHeader.jsp"></s:include>
		</s:if>
		<s:else>
			<h1><s:text name="OperatorTags.title.DefineContractorTags" /></h1>
		</s:else>
		
		<s:set var="globalOperator" value="operator" />
		
		<s:url action="OperatorTags.action" var="operator_tag_form_url">
			<s:param name="id" value="id" />
		</s:url>
		
		<form id="operatorTagForm" name="operatorTagForm" action="${operator_tag_form_url}" method="post">
			<div class="tag-info">
				<p><s:text name="OperatorTags.Info" /></p>
			</div>
			
			<table class="table">
				<thead>
					<tr>
						<th class="id tag-id">
							<s:text name="OperatorTags.header.TagID"/>
						</th>
						
						<th class="tag-name">
							<s:text name="OperatorTags.header.TagName"/>
						</th>
						
						<th class="visible-client-site">
							<s:text name="OperatorTags.header.VisibleToClientSite"/>
						</th>
						
						<th class="visible-contractor">
							<s:text name="OperatorTags.header.VisibleToContractor"/>
						</th>
						
						<th class="inherit-client-site">
							<s:if test="#globalOperator.corporate">
								<s:text name="OperatorTags.header.Inheritable" />
							</s:if>
							<s:else>
								<s:text name="OperatorTags.header.Inherited" />
							</s:else>
						</th>
						
						<s:if test="permissions.hasPermission('ContractorTags', 'Delete') 
							|| permissions.hasPermission('ManageAudits')">
							<th class="actions">
								<s:text name="global.Actions" />
							</th>
						</s:if>
					</tr>
				</thead>
				
				<s:iterator value="tags" var="tag" status="rowstatus">
					<tr class="<s:if test="#rowstatus.odd == true">odd</s:if><s:else>even</s:else>">
						<td class="id tag-id">
							<s:property value="id" />
							
							<s:hidden name="tags[%{#rowstatus.index}].id" value="%{id}" />
						</td>
						
						<%-- edit mode --%>
						<s:if test="((permissions.operatorCorporate && permissions.hasPermission('ContractorTags', 'Edit') && operator.id == permissions.accountId)
										|| (permissions.marketing))
										&& #tag.operator.id == #globalOperator.id">
							<td class="tag-name">
								<s:textfield name="tags[%{#rowstatus.index}].tag" value="%{tag}" />
							</td>
							<td class="visible-client-site">
								<s:checkbox name="tags[%{#rowstatus.index}].active" value="%{active}" />
							</td>
							<td class="visible-contractor">
								<s:checkbox name="tags[%{#rowstatus.index}].visibleToContractor" value="%{visibleToContractor}" />
							</td>
							<td class="inherit-client-site">
								<s:if test="operator.corporate">
									<s:checkbox name="tags[%{#rowstatus.index}].inheritable" value="%{inheritable}" />
								</s:if>
								<s:else>
									<img src="images/registration/cross.png" />
								</s:else>
							</td>
						</s:if>
						
						<%-- view mode --%>
						<s:else>
							<td class="tag-name">
								<s:property value="#tag.tag" />
							</td>
							<td class="visible-client-site">
								<s:if test="#tag.active">
									<img src="images/registration/check.png" />
								</s:if>
								<s:else>
									<img src="images/registration/cross.png" />
								</s:else>
							</td>
							<td class="visible-contractor">
								<s:if test="#tag.visibleToContractor">
									<img src="images/registration/check.png" />
								</s:if>
								<s:else>
									<img src="images/registration/cross.png" />
								</s:else>
							</td>
							<td class="inherit-client-site">
								<s:if test="#tag.operator.id != #globalOperator.id || (#tag.operator.corporate && #tag.inheritable)">
									<img src="images/registration/check.png" />
								</s:if>
								<s:else>
									<img src="images/registration/cross.png" />
								</s:else>
							</td>
						</s:else>
						
						<s:if test="permissions.hasPermission('ManageAudits')
							|| permissions.hasPermission('ContractorTags','Edit')">
							<td class="actions">
								<%-- Allow deletion for tags defined on 'this' operator by the current operator or pics admin --%>
								<s:if test="#tag.operator.id == #globalOperator.id
									&& auditTypeRules.empty 
									&& auditCategoryRules.empty 
									&& operatorFlagCriteria.empty
									&& ((permissions.hasPermission('ContractorTags', 'Delete') && permissions.operatorCorporate) 
										|| permissions.marketing)">
									<a href="OperatorTags!remove.action?id=${globalOperator.id}&tagID=${id}" class="btn error" rel=""><s:text name="button.Remove" /></a>
								</s:if>
								<s:else>
									<s:if test="#tag.operator.id != #globalOperator.id">
										<span class="btn info" title="<s:text name="OperatorTags.TagBelongsToAnotherOperator"><s:param value="operator"/></s:text>"><s:text name="button.Info"/></span>
										
										<pics:permission perm="ManageAudits">
											<a href="OperatorTags.action?id=${operator.id}" class="btn" title="<s:text name="OperatorTags.TagBelongsToAnotherOperator"><s:param value="operator"/></s:text>">Corporate</a>
										</pics:permission>
									</s:if>
									<s:elseif test="!auditTypeRules.empty || !auditCategoryRules.empty || !operatorFlagCriteria.empty">
										<span class="btn info" title="<s:text name="OperatorTags.TagInUseByConfig"/>"><s:text name="button.Info"/></span>
									</s:elseif>
									<s:elseif test="!contractorTags.empty">
										<span class="btn info" title="<s:text name="OperatorTags.TagAppliedToContractors"/>"><s:text name="button.Info"/></span>
									</s:elseif>
								</s:else>
								
								<%-- admin use only --%>
								<s:if test="#tag.operator.id == #globalOperator.id">
									<s:if test="permissions.isCanAddRuleForOperator(operator)">
										<s:if test="!auditTypeRules.empty">
											<a data-url="AuditTypeRuleTableAjax.action?comparisonRule.tag.id=${id}&button=tags" class="modal-link btn">Audit Rules</a>
										</s:if>
										
										<s:if test="!auditCategoryRules.empty">
											<a data-url="CategoryRuleTableAjax.action?comparisonRule.tag.id=${id}&button=tags" class="modal-link btn">Category Rules</a>
										</s:if>
									</s:if>
									
									<s:if test="permissions.hasPermission('ManageAudits') && !operatorFlagCriteria.empty">
										<a data-url="ManageFlagCriteriaOperator.action?id=<s:property value="operatorFlagCriteria.get(0).operator.id" />" class="modal-link btn">Flag Criteria</a>
									</s:if>
								</s:if>
							</td>
						</s:if>
					</tr>
				</s:iterator>
				
				<s:if test="permissions.hasPermission('ContractorTags','Delete') || permissions.marketing">
					<tfoot>
						<tr>
							<td colspan="6" class="add-new-tag">
								<span><s:text name="OperatorTags.label.AddNewTag" />:</span>
								<s:textfield name="tags[%{tags.size}].tag" />
							</td>
						</tr>
					</tfoot>
				</s:if>
			</table>
		
			<s:if test=" permissions.hasPermission('ContractorTags','Edit') || permissions.marketing">
				<ul class="actions">
					<li>
						<s:submit method="save" value="Save Changes" cssClass="btn success" />
					</li>
				</ul>
			</s:if>
		</form>
	</body>
</html>