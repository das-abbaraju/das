<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title>
		<s:property value="contractor.name" />
	</title>
	
	<s:include value="../reports/reportHeader.jsp"/>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/trades.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/dashboard.css?v=<s:property value="version"/>" />
	
	<style type="text/css">
		<s:set name="isWatched" value="%{watched ? 'inline' : 'none'}" />
		<s:set name="stopWatch" value="%{watched ? 'none' : 'inline'}" />
		.watch
		{
			display: <s:property value="#isWatched" />;
		}
		
		.stop
		{
			display: <s:property value="#stopWatch" />;
		}
	</style>
	
	<script type="text/javascript">
		function removeTag(tagId) {
			var data = {button: 'RemoveTag', tagId: tagId, id: <s:property value="id"/>};
			$('#conoperator_tags').html('<img src="images/ajax_process.gif"/>')
				.load('TagNameEditAjax.action', data, function(text, status) {
						if (status=='success')
							$(this).effect('highlight', {color: '#FFFF11'}, 1000);
					});
			return false;
		}
	
		function addTag() {
			var data = {button: 'AddTag', tagId: $('#tagName').val(), id: <s:property value="id"/>};
			$('#conoperator_tags').html('<img src="images/ajax_process.gif"/>')
				.load('TagNameEditAjax.action', data, function(text, status) {
						if (status=='success')
							$(this).effect('highlight', {color: '#FFFF11'}, 1000);
					});
			return false;
		}
	
		function limit(id, pat) {
			var data = $('#' + id).text();
			var size = 500;
			var count = index = prev = 0;
			while(count < 15){
				prev = index;
				index = data.indexOf(pat, prev+pat.length);
				if(index!=-1)
					count++;
				else break;
			}
			if(count>=15){
				if(prev<size)
					size = prev;
			}
	
			if (data.length > size) {
				var data1 = data.substring(0,size).replace(/\n/gi, "<br>") + '<span id="' + id + '_ext">...<br> <a href="#" onclick="$(\'#'
					+ id + '_more\').show(); $(\'#' + id + '_ext\').hide(); return false;" style="font-weight: normal;"'
					+ '>Show more</a><br><br></span>';
				var data2 = '<span id="' + id + '_more" style="display: none;">' + data.substring(size, data.length).replace(/\n/gi, "<br>")
					+ ' <a href="#" onclick="$(\'#' + id + '_ext\').show(); $(\'#' + id
					+ '_more\').hide(); return false;" style="font-weight: normal;">Hide</a><br><br></span>';
				data = data1 + data2;
			};
	
			$('#' + id).html(data);
		}
		
		$(function() {
			limit('description', '\n');

			$('a.trade').each(function() {
				$(this).attr('href', 'ContractorTrades.action?id=<s:property value="id"/>');
			});
		});
	</script>
</head>
<body>
    <div id="${actionName}_${methodName}_page" class="${actionName}-page page">
    	<s:if test="viewableByGC">
    		<s:include value="conHeader.jsp"/>
    		
    		<s:if test="permissions.admin">
    			<s:if test="contractor.hasPastDueInvoice()">
    				<div class="alert">
    					<s:text name="ContractorView.HasPastDueInvoice" />
    				</div>
    			</s:if>
    		</s:if>
    		
    		<table id="contractor_dashboard">
    			<tr>
    				<td style="vertical-align:top; width: 48%">
    					<%-- Operator Flag Status --%>
    					<div class="panel_placeholder">
    						<div class="panel">
    							<div class="panel_header">
    								<s:text name="ContractorView.ContractorStatus">
    									<s:param value="%{co == null ? 0 : 1}" />
    									<s:param value="%{co.operatorAccount.name}" />
    								</s:text>
    							</div>
    							
    							<div class="panel_content">
    								<s:if test="contractor.status.pending">
    									<div class="alert">
    										<s:text name="ContractorView.StatusPending" />
    									</div>
    								</s:if>
    								
    								<s:if test="contractor.soleProprietor">
    									<div class="alert">
    										<s:text name="ContractorView.SoleProprietor" />
    										<a
    											href="#"
    											onclick="return false;"
    											class="cluetip help"
    											rel="#cluetip_sole_sync"
    											title="<s:text name="ContractorRegistration.SoleProprietor.heading" />"
    										></a>
    										<div style="display: none;" id="cluetip_sole_sync">
    											<s:text name="ContractorView.SoleProprietor.fieldhelp" />
    										</div>
    									</div>
    								</s:if>
    								
    								<s:if test="contractor.status.deleted">
    									<div class="alert">
    										<s:text name="ContractorView.ContractorDeleted">
    											<s:param value="%{contractor.reason.length() > 0 ? 1 : 0}" />
    											<s:param value="%{contractor.reason}" />
    										</s:text>
    										
    										<s:if test="contractor.lastPayment != null">
    											<s:text name="ContractorView.ContractorDeletedLastPaid">
    												<s:param value="%{contractor.lastPayment}" />
    											</s:text>
    										</s:if>
    									</div>
    								</s:if>
    								
    								<s:if test="contractor.accountLevel.bidOnly">
    									<s:if test="canUpgrade">
    										<div class="info">
    											<s:text name="ContractorView.BidOnlyUpgrade" />
    											<br/>
    											<div style="margin-top: 7px;">
    												<a href="ContractorView.action?id=<s:property value="id" />&button=Upgrade to Full Membership" class="picsbutton positive" onclick="return confirm('<s:text name="ContractorView.BidOnlyUpgradeConfirm" />');" ><s:text name="ContractorView.button.BidOnlyUpgrade" /></a>
    											</div>
    										</div>
    									</s:if>
    									<s:else>
    										<div class="alert">
    											<s:text name="ContractorView.BidOnlyUpgradeAlert" />
    										</div>
    									</s:else>
    								</s:if>
    								
    								<s:if test="permissions.admin && !contractor.mustPayB && contractor.status.active">
    									<div class="alert">
    										<s:text name="ContractorView.LifetimeFree" />
    									</div>
    								</s:if>
    								
    								<s:if test="(permissions.admin || permissions.contractor) && hasPendingGeneralContractors">
    									<div class="alert">
    										<s:text name="ContractorView.PendingGeneralContractorsAlert" />
    									</div>
    								</s:if>
    				
    								<s:if test="co != null">
    									<s:if test="co.operatorAccount.approvesRelationships.toString() == 'Yes'">
    										<s:if test="co.workStatusPending">
    											<div class="alert">
    												<s:text name="ContractorView.NotApprovedYet" />
    											</div>
    										</s:if>
    										<s:elseif test="co.workStatusRejected">
    											<div class="alert">
    												<s:text name="ContractorView.NotApproved" />
    											</div>
    										</s:elseif>
    									</s:if>
    				
    									<div class="co_flag">
    										<p>
    											<a href="ContractorFlag.action?id=<s:property value="id"/>&opID=<s:property value="opID"/>">
    												<s:property value="co.flagColor.bigIcon" escape="false"/>
    											</a>
    										</p>
    										<p>
    											<a href="ContractorFlag.action?id=<s:property value="id"/>&opID=<s:property value="opID"/>">
    												<s:text name="%{co.flagColor.i18nKey}"/>
    											</a>
    										</p>
    										
    										<s:if test="co.forcedFlag || individualFlagOverrideCount > 0 || corporateFlagOverride != null">
    											<div class="co_force" style="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
    												<s:if test="co.forcedFlag" >
    													<s:text name="ContractorView.ManualForceFlag">
    														<s:param><s:property value="co.forceFlag.smallIcon" escape="false" /></s:param>
    														<s:param value="%{co.forceEnd}" />
    													</s:text>
    												</s:if>
    												<s:if test="corporateFlagOverride != null" >
    													<s:text name="ContractorView.ManualForceFlag">
    														<s:param><s:property value="corporateFlagOverride.forceFlag.smallIcon" escape="false" /></s:param>
    														<s:param value="%{corporateFlagOverride.forceEnd}" />
    													</s:text>
    												</s:if>
    												<s:if test="individualFlagOverrideCount > 0" >
    													<s:if test="co.forcedFlag" ><br /></s:if>
    													<s:text name="ContractorView.IndividualForceFlag" >
    														<s:param value="individualFlagOverrideCount" />
    														<s:param><s:date name="earliestIndividualFlagOverride" format="MMM dd, yyyy" /></s:param>
    													</s:text>
    												</s:if>
    											</div>
    										</s:if>
    									</div>
    								</s:if>
    								
    								<div class="co_problems">
    									<s:if test="permissions.admin">
    										<p>
    											<s:text name="global.AccountStatus"/>:
    											<strong><s:text name="%{contractor.status.i18nKey}"/></strong>
    										</p>
    										
    										<s:if test="contractor.accountLevel.full && contractor.balance > 0">
    											<p>
    												<s:text name="ContractorView.CurrentBalance">
    													<s:param value="%{contractor.currency.symbol}" />
    													<s:param value="%{contractor.balance}" />
    												</s:text>
    											</p>
    										</s:if>
    									</s:if>
    									
    									<!-- List of Problems on "Contractor Status" Widget -->
    									<s:if test="problems.categories.size() > 0">
    										<p>
    											<s:text name="ContractorView.Problems" />:
                                            </p>
    											
    										<ul style="margin-left: 10px;">
    											<s:iterator value="problems.categories" id="probcat">
    												<s:iterator value="problems.getCriteria(#probcat)" id="probcrit">
    													<li>
    														<s:property value="problems.getWorstColor(#probcrit).smallIcon" escape="false"/>
															<span title="<s:property value="getPercentComplete(#probcrit, opID)" />" ><s:property value="label" /></span>
    														<s:property value="getCriteriaLabel(#probcrit.id)"/>
    													</li>
    												</s:iterator>
    											</s:iterator>
    										</ul>
    									</s:if>
    									
    									<s:if test="opID > 0 && opID != permissions.accountId">
    										<p>
    											<s:text name="ContractorView.WaitingOn" />:
    											<s:text name="%{co.waitingOn.i18nKey}"/>
    										</p>
    									</s:if>
    									
    									<p>
											<s:text name="ContractorAccount.type" />:
											<s:property value="commaSeparatedContractorTypes" /> 
										</p>
    									
        								<p>
        									<s:text name="global.SafetyRisk" />:
        									<strong>
        										<s:if test="contractor.safetyRisk != null">
        											<s:text name="%{contractor.safetyRisk.i18nKey}" />
        										</s:if>
        										<s:else>
        											<s:text name="ContractorAccount.safetyRisk.missing" />
        										</s:else>
        									</strong>
        								</p>
        								
        								<s:if test="contractor.materialSupplier && contractor.productRisk != null">
        									<p>
        										<s:text name="global.ProductRisk" />:
        										<strong><s:text name="%{contractor.productRisk.i18nKey}" /></strong>
        									</p>
        								</s:if>
        								
        								<s:if test="contractor.transportationServices && contractor.transportationRisk != null">
        									<p>
        										<s:text name="global.TransportationRisk" />:
        										<strong><s:text name="%{contractor.transportationRisk.i18nKey}" /></strong>
        									</p>
        								</s:if>
    								
    									<p>
    										<s:text name="ContractorView.LastLogin" />:
    										<s:property value="getFuzzyDate(contractor.lastLogin)"/>
    									</p>
    									
    									<s:if test="activeOperators.size() > 1">
    										<p>
    											<a href="#all"><s:text name="global.Locations" /></a>:
    											<s:property value="activeOperators.size()"/>
    											
    											<s:if test="flagCounts.size() > 0">
    												(<s:iterator value="flagCounts" status="stat"><s:property value="value"/> <s:property value="key.smallIcon" escape="false"/><s:if test="!#stat.last">, </s:if></s:iterator>)
    											</s:if>
    										</p>
    									</s:if>
    									
    									<pics:permission perm="ContractorWatch" type="Edit">
    										<p id="contractorWatch">
    											<span class="watch">
    												<s:text name="ContractorView.WatchingContractor" />
    												<a href="#" id="stop_watch_link" data-conid="<s:property value="contractor.id" />">
    													<s:text name="ContractorView.StopWatching" />
    												</a>
    											</span>
    											<span class="stop watch">
    												<a href="#" id="start_watch_link" data-conid="<s:property value="contractor.id" />">
    													<s:text name="ContractorView.WatchContractor" />
    												</a>
    											</span>
    										</p>
    									</pics:permission>
    								</div>
    								
    								<s:if test="permissions.generalContractor && getGCOperators().size > 0">
    									<div class="co_select nobr">
    										<s:text name="global.SelectOperator" />:
    										<s:select 
    											list="getGCOperators()" 
    											listKey="operatorAccount.id" 
    											listValue="operatorAccount.name" 
    											name="opID"
    											headerKey="0" 
    											headerValue="- %{getText('global.Operator')} -"
    											onchange="location.href='ContractorView.action?id=%{id}&opID='+this.value"
    										/>
    									</div>
    								</s:if>
    								<s:elseif test="activeOperators.size() > 1">
    									<div class="co_select nobr">
    										<s:text name="global.SelectOperator" />:
    										<s:select 
    											list="activeOperators" 
    											listKey="operatorAccount.id" 
    											listValue="operatorAccount.name" 
    											name="opID"
    											headerKey="0" 
    											headerValue="- %{getText('global.Operator')} -"
    											onchange="location.href='ContractorView.action?id=%{id}&opID='+this.value"
    										/>
    									</div>
    								</s:elseif>
    								
    								<div class="clear"></div>
    							</div>
    						</div>
    					</div>
    					
    					<s:iterator value="#{'global.DocuGUARD': docuGUARD, 'global.AuditGUARD': auditGUARD, 'global.InsureGUARD': insureGUARD, 'global.EmployeeGUARD': employeeGUARD}" var="widget">
    						<s:if test="#widget.value.size() > 0">
    							<div class="panel_placeholder">
    								<div class="panel">
    									<div class="panel_header">
                                            <s:text name="%{#widget.key}" />
    									</div>
    									<div class="panel_content">
    										<ul>
    											<pics:permission perm="ContractorDetails">
    												<s:if test="#widget.key == 'global.DocuGUARD'">
    													<li>
    														<strong><a class="pdf" href="AuditPdfConverter.action?id=<s:property value="id"/>"><s:text name="ContractorView.DownloadPQF" /></a></strong>
    													</li>
    												</s:if>
    											</pics:permission>
    											<s:if test="#widget.key == 'global.EmployeeGUARD'">
	    											<select name="" multipleselect=false size=3>
    													<s:iterator value="value">
	    													<option onclick="location.href='Audit.action?auditID=<s:property value="id"/>'">
		    													<a href="Audit.action?auditID=<s:property value="id"/>">
		    														<s:property value="auditType.name"/>
		    														<s:if test="employee != null">
		    															<s:property value="employee.name" />
		    														</s:if>
		    														<s:if test="auditFor != null">
		    															<s:property value="auditFor"/>
		    														</s:if>
		    														<s:else>
		    															<s:date name="effectiveDate" format="%{getText('date.MonthAndYear')}" />
		    														</s:else>
		    													</a>
	    													</option>
	    												</s:iterator>
	    											</select>
    											</s:if>
    											<s:else>
													<s:iterator value="value">
    													<li>
	    													<a href="Audit.action?auditID=<s:property value="id"/>">
	    														<s:property value="auditType.name"/>
	    														<s:if test="employee != null">
	    															<s:property value="employee.name" />
	    														</s:if>
	    														<s:if test="auditFor != null">
	    															<s:property value="auditFor"/>
	    														</s:if>
	    														<s:else>
	    															<s:date name="effectiveDate" format="%{getText('date.MonthAndYear')}" />
	    														</s:else>
	    													</a>
    													</li>
    												</s:iterator>
   												</s:else>
   											</ul>
    										<div class="clear"></div>
    									</div>
    								</div>
    							</div>
    						</s:if>
    					</s:iterator>
    					
    					<s:if test="oshaDisplay.stats.size() > 0">
    						<s:include value="/struts/contractors/_contractor_safety_statistics.jsp" />
    					</s:if>
    					
    					<s:if test="criteriaList.categories.size() > 0">
    						<%-- Flaggable Data --%>
    						<div class="panel_placeholder">
    							<div class="panel">
    								<div class="panel_header">
    									<s:text name="ContractorView.FlaggableData" />
    								</div>
    								<div class="panel_content">
    									<div class="clear" style="height: 0px; overflow: hidden"></div>
    									
    									<s:iterator value="criteriaList.categories" id="datacat">
    										<s:if test="#datacat != 'Insurance Criteria'">
    											<div class="flagData">
    												<strong><s:property value="#datacat"/></strong>
    												
    												<ul>
    													<s:iterator value="criteriaList.getCriteria(#datacat)" id="datacrit">
    														<li>
    															<span  title="<s:property value="criteriaList.getWorstFlagOperators(#datacrit)" />" ><s:property value="criteriaList.getWorstColor(#datacrit).smallIcon" escape="false"/> <s:property value="label"/></span>
    														</li>
    													</s:iterator>
    												</ul>
    											</div>
    										</s:if>
    									</s:iterator>
    									
    									<div class="clear"></div>
    								</div>
    							</div>
    						</div>
    					</s:if>
    				</td>
    				<td width="15px"></td>
    				<td style="vertical-align:top; width: 48%">
    					<%-- Open Tasks --%>
    					<s:if test="permissions.admin || permissions.contractor || permissions.operatorCorporate">
    						<div class="panel_placeholder">
    							<div class="panel">
    								<div class="panel_header">
    									<s:text name="Widget.6.caption"/>
    								</div>
    								<div class="panel_content" id="con_tasks" data-conid="<s:property value="contractor.id" />">
    									<div class="inprogress"></div>
    								</div>
    							</div>
    						</div>
    					</s:if>
    					
    					<%-- Pending General Contractors --%>
    					<s:if test="(permissions.admin || permissions.contractor) && hasPendingGeneralContractors">
    						<div class="panel_placeholder">
    							<div class="panel">
    								<div class="panel_header">
    									<s:text name="ContractorView.PendingGeneralContractors" />
	    								<span style="float: right;">
	    									<a href="#" class="cluetip help" rel="#cluetip_gc" title="<s:text name="ContractorView.PendingGeneralContractors" />"></a>
	    								</span>
	    								<div id="cluetip_gc">
	    									<s:text name="ContractorView.PendingGeneralContractors.help" />
	    								</div>
    								</div>
    								<div class="panel_content" id="con_pending_gcs">
    									<s:include value="con_dashboard_pending_gc_operators.jsp" />
    								</div>
    							</div>
    						</div>
    					</s:if>
    					
    					<s:if test="permissions.contractor">
                            <div class="panel_placeholder">
                                <div class="panel referral-program">
                                    <div class="panel_header">
                                        <s:text name="ReferralProgram.title" />
                                    </div>
                                    <div class="panel_content">
                                        <img src="images/tablet/ipad.png" />
                                        
                                        <div class="summary">
                                            <p>
                                                <s:text name="ReferralProgram.summary" />
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </s:if>
    					
    					<%-- Contractor Info --%>
    					<div class="panel_placeholder">
    						<div class="panel">
    							<div class="panel_header">
    								<s:text name="ContractorView.ContractorInfo" />
    							</div>
    							<div class="panel_content">
    								<h4>
    									<s:property value="contractor.name" />
    									
    									<s:if test="contractor.dbaName.length() > 0">
    										<br/>
    										<s:text name="ContractorAccount.dbaName.short" />
    										<s:property value="contractor.dbaName" />
    									</s:if>
    								</h4>
    								
    								<p>
    									<s:text name="ContractorAccount.id" />:
    									<strong><s:property value="contractor.id" /></strong>
    								</p>
    								
    								<pics:permission perm="PicsScore">
    									<p>
    										<s:text name="ContractorAccount.score" />:
    										<strong><s:property value="contractor.score" /></strong>
    									</p>
    								</pics:permission>
    								
    								<p>
    									<s:text name="ContractorView.MemberSince" />:
                                        <strong><s:date name="contractor.membershipDate" /></strong>
                                        <pics:toggle name="Badge">
	                                        <a href="ContractorBadge.action?contractor=<s:property value="contractor.id" />" class="preview">
	                                            <s:text name="ContractorView.ClickToViewContractorBadge" />
	                                        </a>
                                        </pics:toggle>
    								</p>
                                    <p>
                                        <a class="pdf" href="ContractorCertificate.action?id=<s:property value="contractor.id" />">
                                            <s:text name="ContractorDashboard.DownloadCertificate" />
                                        </a>
                                    </p>
    								
    								<p>
    									<s:text name="global.CSR" />:
    									<strong>
    										<s:property value="contractor.auditor.name" />
    										/
    										<s:property value="contractor.auditor.phone" />
                                            <span id="CSRNote">(<s:text name="ContractorView.ContractorDashboard.CSRCallNote" />)</span>
    										/
    									</strong>
    									<s:text name="ProfileEdit.u.fax" />:
    									<s:property value="contractor.auditor.fax" />
    									/ 
    									<a href="mailto:<s:property value="contractor.auditor.email"/>" class="email">
    										<s:property value="contractor.auditor.email"/>
    									</a>
    								</p>
    								
    								<s:if test="contractor.generalContractorOperatorAccounts.size > 0">
    									<s:set name="gc_accounts" value="''" />
    									<s:iterator value="contractor.generalContractorOperatorAccounts" var="gc_op" status="gc_index">
    										<s:if test="!permissions.operatorCorporate || permissions.visibleAccounts.contains(#gc_op.id)">
		    									<s:set name="gc_accounts" value="#gc_accounts + #gc_op.name" />
		    									<s:if test="!#gc_index.last">
			    									<s:set name="gc_accounts" value="#gc_accounts + ', '" />
		    									</s:if>
	    									</s:if>
    									</s:iterator>
    									<s:if test="!isStringEmpty(#gc_accounts)">
	    									<p>
	    										<s:text name="ContractorView.SubcontractingUnder" />:
	    										<strong>
	    											<s:property value="#gc_accounts" />
	    										</strong>
	    									</p>
    									</s:if>
    								</s:if>
    								
    								<s:if test="hasOperatorTags">
    									<s:if test= "contractor.operatorTags.size() > 0 || operatorTags.size() > 0">
    										<div>
    											<span><s:text name="OperatorTags.title" />: </span>
    											<div id="conoperator_tags">
    												<s:include value="contractorOperator_tags.jsp" />
    											</div>
    										</div>
    									</s:if>
    								</s:if>
    								
    								<s:if test="permissions.picsEmployee || permissions.operatorCorporate">
    									<div>
    										<span id="contractor_operator_numbers_label">
    											<s:text name="ContractorOperatorNumber" />:
    										</span>
    										<div id="contractor_operator_numbers">
    											<s:include value="contractor_operator_numbers.jsp" />
    										</div>
    									</div>
    								</s:if>
    								
    								<div class="clear"></div>
    							</div>
    						</div>
    					</div>
    					
    					<%-- Contact Info --%>
    					<div class="panel_placeholder">
    						<div class="panel">
    							<div class="panel_header">
    								<s:text name="ContractorView.ContactInfo" />
    							</div>
    							<div class="panel_content">
    								<p>
    									<s:text name="global.Address" />:
    									[<a
    										href="http://www.mapquest.com/maps/map.adp?city=<s:property value="contractor.city" />&state=<s:property value="contractor.state" />&address=<s:property value="contractor.address" />&zip=<s:property value="contractor.zip" />&zoom=5"
    										target="_blank">
    											<s:text name="ContractorView.ShowMap" />
    									</a>]
    									<br/>
    									<span class="street-address">
    										<s:property value="contractor.address" />
    									</span>
    									<br />
    									<span class="locality">
    										<s:property value="contractor.city" />
    									</span>,
    									<span class="region">
    										<s:property value="contractor.state.isoCode" />
    									</span>
    									<span class="postal-code">
    										<s:property value="contractor.zip" />
    									</span>
    									<br />
    									<span class="region">
    										<s:property value="contractor.country.name" />
    									</span>
    								</p>
    								
    								<div class="telecommunications">
    									<p class="tel">
    										<s:text name="ContractorView.MainPhone" />:
    										<span class="value"><s:property value="contractor.phone" /></span>
    									</p>
    									
    									<s:if test="!isStringEmpty(contractor.fax)">
    										<p class="tel">
    											<s:text name="ContractorEdit.PrimaryAddress.CompanyFaxMain" />:
    											<span class="value"><s:property value="contractor.fax" /></span>
    										</p>
    									</s:if>
    									
    									<s:if test="contractor.webUrl.length() > 0">
    										<p class="url">
    											<s:text name="ContractorAccount.webUrl" />:
    											<strong><a href="http://<s:property value="contractor.webUrl" />" class="value" target="_blank"><s:property value="contractor.webUrl" /></a></strong>
    										</p>
    									</s:if>
    									
    									<s:iterator value="contractor.getUsersByRole('ContractorAdmin')">
    										<p class="contact">
    											<s:if test="contractor.primaryContact.id == id">
    												<s:text name="global.ContactPrimary" />
    											</s:if>
    											<s:else>
    												<s:text name="global.Contact" />
    											</s:else>:
    											<span class="value"><s:property value="name" /></span>
    										</p>
    										<p class="tel">
    											&nbsp;&nbsp;
    											<s:text name="User.email" />:
    											<a href="mailto:<s:property value="email" />" class="email"><s:property value="email" /></a>
    											
    											<s:if test="phone.length() > 0">
    												/ <s:text name="User.phone" />: <s:property value="phone" />
    											</s:if>
    											
    											<s:if test="fax.length() > 0">
    												/ <s:text name="User.fax" />: <s:property value="fax" />
    											</s:if>
    										</p>
    									</s:iterator>
    								</div>
    								
    								<div class="clear"></div>
    							</div>
    						</div>
    					</div>
    					
    					<%-- Description --%>
    					<div class="panel_placeholder">
    						<div class="panel">
    							<div class="panel_header">
    								<s:text name="global.Description" />
    							</div>
    							<div class="panel_content">
    								<s:if test="showLogo">
    									<img class="contractor_logo" src="ContractorLogo.action?id=<s:property value="id"/>"/>
    								</s:if>
    								
    								<span id="description"><s:property value="contractor.descriptionHTML" /></span>
    								
    								<s:if test="@com.picsauditing.util.Strings@isEmpty(contractor.brochureFile) == false">
    									<p class="web">
    										<strong>
    											<a href="DownloadContractorFile.action?id=<s:property value="id" />" target="_BLANK"><s:text name="ContractorEdit.CompanyIdentification.CompanyBrochure" /></a>
    										</strong>
    									</p>
    								</s:if>
    								
    								<s:if test="contractor.trades.size() > 0">
    									<s:include value="../trades/contractor_trade_cloud.jsp"/>
    								</s:if>
    								
    								<div class="clear"></div>
    							</div>
    						</div>
    					</div>
    					
    					<%-- Flag Matrix --%>
    					<s:include value="../contractors/contractor_flag_matrix.jsp" />
    					
    					<%-- All Locations --%>
    					<div class="panel_placeholder widget locations">
    						<div class="panel" id="all">
    							<div class="panel_header">
    								<s:text name="ContractorView.AllLocations" />
    								<s:if test="permissions.admin || permissions.contractor">
	    								<a href="ContractorFacilities.action?id=${id}">
	    									<s:text name="ContractorFacilities.ContractorFacilities.AddFacilities" />
	    								</a>
    								</s:if>
    								<s:elseif test="permissions.generalContractor">
	    								<a href="SubcontractorFacilities.action?id=${id}">
	    									<s:text name="ContractorFacilities.ContractorFacilities.AddFacilities" />
	    								</a>
    								</s:elseif>
    							</div>
    							<div class="panel_content">
    								<s:iterator value="activeOperatorsMap">
    									<ul style="float: left">
    										<s:iterator value="value">
    											<li>
    												<span class="other_operator">
    													<s:if test="!permissions.generalContractorFree">
    														<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />">
    															<s:property value="flagColor.smallIcon" escape="false" />
    														</a>
    														<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"
    															<s:if test="permissions.admin">
    																title="<s:property value="operatorAccount.name" />: <s:text name="global.WaitingOn"/> '<s:text name="%{waitingOn.i18nKey}"/>'"
    																rel="OperatorQuickAjax.action?id=<s:property value="operatorAccount.id"/>"
    																class="operatorQuick"
    															</s:if>
    															<s:else>
    																title="<s:text name="global.WaitingOn"/> '<s:text name="%{waitingOn.i18nKey}"/>'"
    															</s:else>>
    															<s:property value="operatorAccount.name" />
    														</a>
    													</s:if>
    													<s:else>
    														<s:property value="operatorAccount.name" />
    													</s:else>
    												</span>
    											</li>
    										</s:iterator>
    									</ul>
    								</s:iterator>
    								
    								<div class="clear"></div>
    							</div>
    						</div>
    					</div>
    					
    					<div class="panel_placeholder">
    						<div class="panel">
    							<div class="panel_header">
    								<s:text name="ContractorView.SynchronizeContractor" />
    								<span style="float: right;">
    									<a href="#" class="cluetip help" rel="#cluetip_sync" title="<s:text name="ContractorView.SynchronizeContractor" />"></a>
    								</span>
    								<div id="cluetip_sync">
    									<s:text name="ContractorView.SynchronizeContractorMessage" />
    								</div>
    							</div>
    							<div class="panel_content" style="text-align: center;">
    								<s:form id="form_sync">
    									<s:hidden name="id" />
    									<s:hidden name="button" value="Synchronize Contractor" />
    									
    									<input type="submit" class="picsbutton" onclick="$(this).attr('disabled', true); $('#form_sync').submit();" style="margin: 5px auto;" value="<s:text name="ContractorView.Synchronize" />" />
    									
    									<s:if test="contractor.lastRecalculation != null">
    										<br />
    										<s:text name="ContractorView.LastSync">
    											<s:param value="%{contractor.lastRecalculation}" />
    										</s:text>
    									</s:if>
    								</s:form>
    							</div>
    						</div>
    					</div>
    				</td>
    			</tr>
    		</table>
    	</s:if>
    	<s:else>
    		<s:include value="con_dashboard_gc_limited.jsp" />
    	</s:else>
    </div>
</body>
