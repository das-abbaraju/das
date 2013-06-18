<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title>
		<s:property value="contractor.name"/>
	</title>

	<s:include value="../reports/reportHeader.jsp"/>

	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>"/>
	<link rel="stylesheet" type="text/css" media="screen" href="css/trades.css?v=<s:property value="version"/>"/>
	<link rel="stylesheet" type="text/css" media="screen" href="css/dashboard.css?v=<s:property value="version"/>"/>

	<style type="text/css">
		<s:set name="isWatched" value="%{watched ? 'inline' : 'none'}" />
		<s:set name="stopWatch" value="%{watched ? 'none' : 'inline'}" />
		.watch {
			display: <s:property value="#isWatched" />;
		}

		.stop {
			display: <s:property value="#stopWatch" />;
		}
	</style>

	<script type="text/javascript">
		function removeTag(tagId) {
			var data = {button: 'RemoveTag', tagId: tagId, id: <s:property value="id"/>};
			$('#conoperator_tags').html('<img src="images/ajax_process.gif"/>')
					.load('TagNameEditAjax.action', data, function (text, status) {
						if (status == 'success')
							$(this).effect('highlight', {color: '#FFFF11'}, 1000);
					});
			return false;
		}

		function addTag() {
			var data = {button: 'AddTag', tagId: $('#tagName').val(), id: <s:property value="id"/>};
			$('#conoperator_tags').html('<img src="images/ajax_process.gif"/>')
					.load('TagNameEditAjax.action', data, function (text, status) {
						if (status == 'success')
							$(this).effect('highlight', {color: '#FFFF11'}, 1000);
					});
			return false;
		}

		function limit(id, pat) {
			var data = $('#' + id).text();
			var size = 500;
			var count = index = prev = 0;

			while (count < 15) {
				prev = index;
				index = data.indexOf(pat, prev + pat.length);
				if (index != -1)
					count++;
				else break;
			}

			if (count >= 15) {
				if (prev < size)
					size = prev;
			}

			if (data.length > size) {
				var data1 = data.substring(0, size).replace(/\n/gi, "<br>") + '<span id="' + id + '_ext">...<br> <a href="#" onclick="$(\'#'
						+ id + '_more\').show(); $(\'#' + id + '_ext\').hide(); return false;" style="font-weight: normal;"'
						+ '>Show more</a><br><br></span>';

				var data2 = '<span id="' + id + '_more" style="display: none;">' + data.substring(size, data.length).replace(/\n/gi, "<br>")
						+ ' <a href="#" onclick="$(\'#' + id + '_ext\').show(); $(\'#' + id
						+ '_more\').hide(); return false;" style="font-weight: normal;">Hide</a><br><br></span>';

				data = data1 + data2;
			}

			$('#' + id).html(data);
		}

		$(function () {
			limit('description', '\n');

			$('a.trade').each(function () {
				$(this).attr('href', 'ContractorTrades.action?id=<s:property value="id"/>');
			});
		});
	</script>
</head>

<body>
<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
<s:if test="viewableByGC && !showBasicsOnly">
<s:include value="conHeader.jsp"/>

<s:if test="permissions.admin">
	<s:if test="contractor.hasPastDueInvoice()">
		<div class="alert">
			<s:text name="ContractorView.HasPastDueInvoice"/>
		</div>
	</s:if>
</s:if>

<s:if test="!supportedLanguages.isLanguageVisible(permissions.locale)">
	<div id="beta_translations_alert">
		<p>
			<s:text name="global.BetaTranslations"/>
		</p>
	</div>
</s:if>

<table id="contractor_dashboard">
<tr>
<td style="vertical-align:top; width: 48%">
	<%-- Operator Flag Status --%>
<div class="panel_placeholder">
<div class="panel">
<div class="panel_header">
	<s:text name="ContractorView.ContractorStatus">
		<s:param value="%{co == null ? 0 : 1}"/>
		<s:param value="%{co.operatorAccount.name}"/>
	</s:text>
</div>

<div class="panel_content">
<s:if test="contractor.status.pending">
	<div class="alert">
		<s:text name="ContractorView.StatusPending"/>
	</div>
</s:if>

<s:if test="contractor.soleProprietor">
	<div class="alert">
		<s:text name="ContractorView.SoleProprietor"/>
		<a href="#"
		   onclick="return false;"
		   class="cluetip help"
		   rel="#cluetip_sole_sync"
		   title="<s:text name="ContractorRegistration.SoleProprietor.heading" />"
				></a>

		<div style="display: none;" id="cluetip_sole_sync">
			<s:text name="ContractorView.SoleProprietor.fieldhelp"/>
		</div>
	</div>
</s:if>

<s:if test="contractor.status.deleted">
	<div class="alert">
		<s:text name="ContractorView.ContractorDeleted">
			<s:param value="%{contractor.reason.length() > 0 ? 1 : 0}"/>
			<s:param value="%{contractor.reason}"/>
		</s:text>

		<s:if test="contractor.lastPayment != null">
			<s:text name="ContractorView.ContractorDeletedLastPaid">
				<s:param value="%{contractor.lastPayment}"/>
			</s:text>
		</s:if>
	</div>
</s:if>

<s:if test="contractor.accountLevel.bidOnly">
	<s:if test="canUpgrade">
		<div class="info">
			<s:text name="ContractorView.BidOnlyUpgrade"/>
			<br/>

			<div style="margin-top: 7px;">
				<a href="ContractorView.action?id=<s:property value="id" />&button=Upgrade to Full Membership"
				   class="picsbutton positive"
				   onclick="return confirm('<s:text name="ContractorView.BidOnlyUpgradeConfirm"/>');"><s:text
						name="ContractorView.button.BidOnlyUpgrade"/></a>
			</div>
		</div>
	</s:if>
	<s:else>
		<div class="alert">
			<s:text name="ContractorView.BidOnlyUpgradeAlert"/>
		</div>
	</s:else>
</s:if>

<s:if test="permissions.admin && !contractor.mustPayB && contractor.status.active">
	<div class="alert">
		<s:text name="ContractorView.LifetimeFree"/>
	</div>
</s:if>

<s:if test="(permissions.admin || permissions.contractor) && hasPendingGeneralContractors">
	<div class="alert">
		<s:text name="ContractorView.PendingGeneralContractorsAlert"/>
	</div>
</s:if>

<s:if test="co != null">
	<s:include value="_con-dashboard-message.jsp"/>

	<div class="co_flag">
		<s:url action="ContractorFlag" var="contractor_flag">
			<s:param name="id">
				${id}
			</s:param>
			<s:param name="opID">
				${opID}
			</s:param>
		</s:url>
		<p>
			<a href="${contractor_flag}">
				<s:property value="co.flagColor.bigIcon" escape="false"/>
			</a>
		</p>

		<p>
			<a href="${contractor_flag}">
				<s:text name="%{co.flagColor.i18nKey}"/>
			</a>
		</p>

		<s:if test="co.forcedFlag || individualFlagOverrideCount > 0 || corporateFlagOverride != null">
			<div class="co_force" style="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
				<s:if test="co.forcedFlag">
					<s:text name="ContractorView.ManualForceFlag">
						<s:param><s:property value="co.forceFlag.smallIcon" escape="false"/></s:param>
						<s:param value="%{co.forceEnd}"/>
					</s:text>
				</s:if>
				<s:if test="corporateFlagOverride != null">
					<s:text name="ContractorView.ManualForceFlag">
						<s:param><s:property value="corporateFlagOverride.forceFlag.smallIcon"
						                     escape="false"/></s:param>
						<s:param value="%{corporateFlagOverride.forceEnd}"/>
					</s:text>
				</s:if>
				<s:if test="individualFlagOverrideCount > 0">
					<s:if test="co.forcedFlag"><br/></s:if>
					<s:text name="ContractorView.IndividualForceFlag">
						<s:param value="individualFlagOverrideCount"/>
						<s:param><s:date name="earliestIndividualFlagOverride" format="MMM dd, yyyy"/></s:param>
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
					<s:param value="%{contractor.currency.symbol}"/>
					<s:param value="%{contractor.balance}"/>
				</s:text>
			</p>
		</s:if>
	</s:if>

	<!-- List of Problems on "Contractor Status" Widget -->
	<s:if test="problems.categories.size() > 0">
		<p>
			<s:text name="ContractorView.Problems"/>:
		</p>

		<ul style="margin-left: 10px;">
			<s:iterator value="problems.categories" id="probcat">
				<s:iterator value="problems.getCriteria(#probcat)" id="probcrit">
					<li>
						<s:property value="problems.getWorstColor(#probcrit).smallIcon" escape="false"/>
                                                            
                                                            <span title="<s:property value="getPercentComplete(#probcrit, opID)" />">
                                                                <s:property value="label"/>
                                                            </span>

						<s:property value="getCriteriaLabel(#probcrit.id, opID)"/>
					</li>
				</s:iterator>
			</s:iterator>
		</ul>
	</s:if>

	<s:if test="opID > 0 && opID != permissions.accountId">
		<p>
			<s:text name="ContractorView.WaitingOn"/>:
			<s:text name="%{co.waitingOn.i18nKey}"/>
		</p>
	</s:if>

	<p>
		<s:text name="ContractorAccount.type"/>:
		<s:property value="commaSeparatedContractorTypes"/>
	</p>

	<p>
		<s:text name="global.SafetyRisk"/>:
		<strong>
			<s:if test="contractor.safetyRisk != null">
				<s:text name="%{contractor.safetyRisk.i18nKey}"/>
			</s:if>
			<s:else>
				<s:text name="ContractorAccount.safetyRisk.missing"/>
			</s:else>
		</strong>
	</p>

	<s:if test="contractor.materialSupplier && contractor.productRisk != null">
		<p>
			<s:text name="global.ProductRisk"/>:
			<strong><s:text name="%{contractor.productRisk.i18nKey}"/></strong>
		</p>
	</s:if>

	<p>
		<s:text name="ContractorView.LastLogin"/>:
		<s:property value="getFuzzyDate(contractor.lastLogin)"/>
	</p>

	<s:if test="activeOperators.size() > 1">
		<p>
			<a href="#all"><s:text name="global.Locations"/></a>:
			<s:property value="activeOperators.size()"/>

			<s:if test="flagCounts.size() > 0">
				(<s:iterator value="flagCounts" status="stat"><s:property value="value"/> <s:property
					value="key.smallIcon" escape="false"/><s:if test="!#stat.last">, </s:if></s:iterator>)
			</s:if>
		</p>
	</s:if>

	<pics:permission perm="ContractorWatch" type="Edit">
		<p id="contractorWatch">
                        						<span class="watch">
                        							<s:text name="ContractorView.WatchingContractor"/>
                        							<a href="javascript:;" id="stop_watch_link"
							                           data-conid="${contractor.id}">
								                        <s:text name="ContractorView.StopWatching"/>
							                        </a>
                        						</span>
                        						<span class="stop watch">
                        							<a href="javascript:;" id="start_watch_link"
							                           data-conid="${contractor.id}">
								                        <s:text name="ContractorView.WatchContractor"/>
							                        </a>
                        						</span>
		</p>
	</pics:permission>
</div>

<s:if test="permissions.generalContractor && generalContractorClientSites.size > 0">
	<div class="co_select nobr">
		<s:text name="global.SelectOperator"/>:
		<s:select
				data-contractor="contractor.id"
				id="active_operator_view"
				list="generalContractorClientSites"
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
		<s:text name="global.SelectOperator"/>:
		<s:select
				data-contractor="contractor.id"
				id="active_operator_view"
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

<s:iterator
		value="#{'global.DocuGUARD': docuGUARD, 'global.AuditGUARD': auditGUARD, 'global.InsureGUARD': insureGUARD, 'global.EmployeeGUARD': employeeGUARD}"
		var="widget">
	<s:if test="#widget.value.size() > 0">
		<div class="panel_placeholder">
			<div class="panel">
				<div class="panel_header">
					<s:text name="%{#widget.key}"/>
				</div>
				<div class="panel_content">
					<ul>
						<pics:permission perm="ContractorDetails">
							<s:if test="#widget.key == 'global.DocuGUARD'">
								<li>
									<strong>
										<a class="pdf" href="AuditPdfConverter.action?id=<s:property value="id"/>">
											<s:text name="ContractorView.DownloadPQF"/>
										</a>
									</strong>
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
												<s:property value="employee.name"/>
											</s:if>
											<s:if test="auditFor != null">
												<s:property value="auditFor"/>
											</s:if>
											<s:else>
												<s:date name="effectiveDate"
												        format="%{@com.picsauditing.util.PicsDateFormat@MonthAndYear}"/>
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
											<s:property value="employee.name"/>
										</s:if>

										<s:if test="auditFor != null">
											<s:property value="auditFor"/>
										</s:if>
										<s:else>
											<s:date name="effectiveDate"
											        format="%{@com.picsauditing.util.PicsDateFormat@MonthAndYear}"/>
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

<s:if test="anyOshasToDisplay">
	<s:include value="/struts/contractors/_contractor_safety_statistics.jsp"/>
</s:if>

<s:if test="criteriaList.categories.size() > 0">
	<%-- Flaggable Data --%>
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				<s:text name="ContractorView.FlaggableData"/>
			</div>
			<div class="panel_content">
				<div class="clear" style="height: 0px; overflow: hidden"></div>

				<s:iterator value="criteriaList.categories" id="datacat">
					<s:if test="#datacat != 'Insurance Criteria'">
						<div class="flagData">
							<strong><s:property value='%{getText("FlagCriteria.Category." + #datacat)}'/></strong>

							<ul>
								<s:iterator value="criteriaList.getCriteria(#datacat)" id="datacrit">
									<li>
                                                                <span title="<s:property value="criteriaList.getWorstFlagOperators(#datacrit)" />">
                                                                    <s:property
		                                                                    value="criteriaList.getWorstColor(#datacrit).smallIcon"
		                                                                    escape="false"/>
                                                                    <s:property value="label"/>
                                                                </span>
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
				<s:text name="ContractorView.PendingGeneralContractors"/>
	    								<span style="float: right;">
	    									<a href="#" class="cluetip help" rel="#cluetip_gc"
										       title="<s:text name="ContractorView.PendingGeneralContractors" />"></a>
	    								</span>

				<div id="cluetip_gc">
					<s:text name="ContractorView.PendingGeneralContractors.help"/>
				</div>
			</div>
			<div class="panel_content" id="con_pending_gcs">
				<s:include value="con_dashboard_pending_gc_operators.jsp"/>
			</div>
		</div>
	</div>
</s:if>

<pics:toggle name="<%= FeatureToggle.TOGGLE_CONTRACTOR_CAMPAIGN %>" contractor="contractor">
	<s:if test="permissions.contractor">
		<div class="panel_placeholder">
			<div class="panel referral-program">
				<div class="panel_header">
					<s:text name="ReferralProgram.title"/>
				</div>
				<div class="panel_content">
					<img src="images/tablet/ipad.png"/>

					<div class="summary">
						<p>
							<s:text name="ReferralProgram.summary"/>
						</p>
					</div>
				</div>
			</div>
		</div>
	</s:if>
</pics:toggle>

	<%-- Contractor Info --%>
<div class="panel_placeholder">
	<div class="panel">
		<div class="panel_header">
			<s:text name="ContractorView.ContractorInfo"/>
		</div>
		<div class="panel_content">
			<h4>
				<s:property value="contractor.name"/>

				<s:if test="contractor.dbaName.length() > 0">
					<br/>
					<s:text name="ContractorAccount.dbaName.short"/>
					<s:property value="contractor.dbaName"/>
				</s:if>
			</h4>

			<p>
				<s:text name="ContractorAccount.id"/>:
				<strong><s:property value="contractor.id"/></strong>
			</p>

			<pics:permission perm="PicsScore">
				<p>
					<s:text name="ContractorAccount.score"/>:
					<strong><s:property value="contractor.score"/></strong>
				</p>
			</pics:permission>

			<p>
				<s:text name="ContractorView.MemberSince"/>:
				<strong><s:date name="contractor.membershipDate"/></strong>

				<pics:toggle name="<%= FeatureToggle.TOGGLE_BADGE %>">
					<a href="ContractorBadge.action?contractor=<s:property value="contractor.id" />" class="preview">
						<s:text name="ContractorView.ClickToViewContractorBadge"/>
					</a>
				</pics:toggle>
			</p>

			<p>
				<a class="pdf" href="ContractorCertificate.action?id=<s:property value="contractor.id" />">
					<s:text name="ContractorDashboard.DownloadCertificate"/>
				</a>
			</p>

			<p>
				<s:text name="global.CSR"/>:
				<strong>
						${contractor.currentCsr.name}
				</strong>
				/
				<strong>
					<s:set var="contractor_current_csr_phone"
					       value="getLocalizedPhoneNumberForUser(contractor.currentCsr, contractor.country)"/>
						${contractor_current_csr_phone}
					<span id="CSRNote">(<s:text name="ContractorView.ContractorDashboard.CSRCallNote"/>)</span>
				</strong>
				/
				<s:text name="ProfileEdit.u.fax"/>:
				<s:property value="contractor.currentCsr.fax"/>
				/
				<a href="mailto:<s:property value="contractor.currentCsr.email"/>" class="email">
					<s:property value="contractor.currentCsr.email"/>
				</a>
			</p>

			<s:if test="contractor.status.pending && contractor.currentInsideSalesRepresentative != null">
				<p>
					<s:text name="ContractorView.InsideSales"/>:
					<strong>
							${contractor.currentInsideSalesRepresentative.name}
					</strong>
					/
					<strong>
						<s:set var="contractor_inside_sales_phone"
						       value="getLocalizedPhoneNumberForUser(currentInsideSalesRepresentative, contractor.country)"/>
							${contractor_inside_sales_phone}
					</strong>
					/
					<s:text name="ProfileEdit.u.fax"/>:
						${contractor.currentInsideSalesRepresentative.fax}
					/
					<a href="mailto:${contractor.currentInsideSalesRepresentative.email}" class="email">
							${contractor.currentInsideSalesRepresentative.email}
					</a>
				</p>
			</s:if>

			<s:if test="contractor.generalContractorOperatorAccounts.size > 0 && !permissions.generalContractor">
				<s:if test="!isStringEmpty(generalContractorsListing)">
					<p>
						<s:text name="ContractorView.SubcontractingUnder"/>:
						<strong>
								${generalContractorsListing}
						</strong>
					</p>
				</s:if>
			</s:if>

			<s:if test="hasOperatorTags">
				<s:if test="contractor.operatorTags.size() > 0 || operatorTags.size() > 0">
					<div>
						<span><s:text name="OperatorTags.title"/>: </span>

						<div id="conoperator_tags">
							<s:include value="contractorOperator_tags.jsp"/>
						</div>
					</div>
				</s:if>
			</s:if>

			<s:if test="permissions.picsEmployee || permissions.operatorCorporate">
				<div>
    										<span id="contractor_operator_numbers_label">
    											<s:text name="ContractorOperatorNumber"/>:
    										</span>

					<div id="contractor_operator_numbers">
						<s:include value="/struts/contractors/third-party-identifier/_identifier-table.jsp"/>
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
			<s:text name="ContractorView.ContactInfo"/>
		</div>
		<div class="panel_content">
			<p>
				<s:text name="global.Address"/>:
				[<a href="http://www.mapquest.com/maps/map.adp?country=<s:property value="contractor.country.isoCode" />&city=<s:property value="contractor.city" />&state=<s:property value="contractor.state" />&address=<s:property value="contractor.address" />&zip=<s:property value="contractor.zip" />&zoom=5"
				    target="_blank">
				<s:text name="ContractorView.ShowMap"/>
			</a>]
				<br/>
                                        
                						<span class="street-address">
                							<s:property value="contractor.address"/>
                						</span>
				<br/>
                                        
                						<span class="locality">
                							<s:property value="contractor.city"/>
                						</span>,

				<s:if test="contractor.country.hasCountrySubdivisions">
                							<span class="region">
                								<s:property value="contractor.countrySubdivision.simpleName"/>
                							</span>
				</s:if>
                                        
                						<span class="postal-code">
                							<s:property value="contractor.zip"/>
                						</span>
				<br/>
                                        
                						<span class="region">
                							<s:property value="contractor.country.name"/>
                						</span>
			</p>

			<div class="telecommunications">
				<p class="tel">
					<s:text name="ContractorView.MainPhone"/>:
					<span class="value"><s:property value="contractor.phone"/></span>
				</p>

				<s:if test="!isStringEmpty(contractor.fax)">
					<p class="tel">
						<s:text name="ContractorEdit.PrimaryAddress.CompanyFaxMain"/>:
						<span class="value"><s:property value="contractor.fax"/></span>
					</p>
				</s:if>

				<s:if test="contractor.webUrl.length() > 0">
					<p class="url">
						<s:text name="ContractorAccount.webUrl"/>:
						<strong>
							<a href="http://<s:property value="contractor.webUrl" />" class="value" target="_blank">
								<s:property value="contractor.webUrl"/>
							</a>
						</strong>
					</p>
				</s:if>

				<s:if test="contractor.primaryContact">
					<p class="contact">
						<s:text name="global.ContactPrimary"/>:
						<span class="value">${contractor.primaryContact.name}</span>
					</p>

					<p class="tabbed tel">
						<s:text name="User.email"/>:
						<a href="mailto:${contractor.primaryContact.email}"
						   class="email">${contractor.primaryContact.email}</a>

						<s:if test="contractor.primaryContact.phone.length() > 0">
							/ <s:text name="User.phone"/>: ${contractor.primaryContact.phone}
						</s:if>

						<s:if test="contractor.primaryContact.fax.length() > 0">
							/ <s:text name="User.fax"/>: ${contractor.primaryContact.fax}
						</s:if>
					</p>
				</s:if>

				<s:set name="contactNumber" value="1"/>
				<s:iterator step="1" value="contractor.getUsersByRole('ContractorAdmin')">
					<s:if test="contractor.primaryContact.id != id">
						<p class="contact">
							<s:text name="global.Contact"/>
								${contactNumber}:
							<s:set name="contactNumber" value="#contactNumber+1"/>
							<span class="value">${name}</span>
						</p>

						<p class="tabbed tel">
							<s:text name="User.email"/>:
							<a href="mailto:<s:property value="email" />" class="email"><s:property value="email"/></a>

							<s:if test="phone.length() > 0">
								/ <s:text name="User.phone"/>: <s:property value="phone"/>
							</s:if>

							<s:if test="fax.length() > 0">
								/ <s:text name="User.fax"/>: <s:property value="fax"/>
							</s:if>
						</p>
					</s:if>
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
			<s:text name="global.Description"/>
		</div>
		<div class="panel_content">
			<s:if test="showLogo">
				<img class="contractor_logo" src="ContractorLogo.action?id=<s:property value="id"/>"/>
			</s:if>

			<span id="description"><s:property value="contractor.descriptionHTML"/></span>

			<s:if test="@com.picsauditing.util.Strings@isEmpty(contractor.brochureFile) == false">
				<p class="web">
					<strong>
						<a href="DownloadContractorFile.action?id=<s:property value="id" />" target="_BLANK">
							<s:text name="ContractorEdit.CompanyIdentification.CompanyBrochure"/>
						</a>
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
<s:include value="../contractors/contractor_flag_matrix.jsp"/>

	<%-- All Locations --%>
<div class="panel_placeholder widget locations">
	<div class="panel" id="all">
		<div class="panel_header">
			<s:text name="ContractorView.AllLocations"/>

			<s:if test="permissions.admin || permissions.contractor || permissions.corporate">
				<a href="ContractorFacilities.action?id=${id}">
					<s:text name="ContractorFacilities.ContractorFacilities.AddFacilities"/>
				</a>
			</s:if>
			<s:elseif test="permissions.generalContractor">
				<a href="SubcontractorFacilities.action?id=${id}">
					<s:text name="ContractorFacilities.ContractorFacilities.AddFacilities"/>
				</a>
			</s:elseif>

		</div>
		<div class="panel_content">
			<s:iterator value="activeOperatorsMap">
				<ul style="float: left">
					<s:iterator value="value">
						<li>
                									<span class="other_operator">
                										<s:if test="!operatorAccount.generalContractorFree">
											                <s:url action="ContractorFlag" var="contractor_flag">
												                <s:param name="id">
													                ${contractor.id}
												                </s:param>
												                <s:param name="opID">
													                ${operatorAccount.id}
												                </s:param>
											                </s:url>

											                <a href="${contractor_flag}">
												                <s:property value="flagColor.smallIcon" escape="false"/>
											                </a>

											                <a href="${contractor_flag}"
													                <s:if test="permissions.admin">
														                title="<s:property
															                value="operatorAccount.name"/>: <s:text
															                name="global.WaitingOn"/> '<s:text
															                name="%{waitingOn.i18nKey}"/>'"
														                rel="OperatorQuickAjax.action?id=<s:property
															                value="operatorAccount.id"/>"
														                class="operatorQuick"
													                </s:if>
													                <s:else>
														                title="<s:text
															                name="global.WaitingOn"/> '<s:text
															                name="%{waitingOn.i18nKey}"/>'"
													                </s:else>>
												                <s:property value="operatorAccount.name"/>
											                </a>
										                </s:if>
                										<s:else>
											                <s:property value="operatorAccount.name"/>
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
			<s:text name="ContractorView.SynchronizeContractor"/>
                                    
    								<span style="float: right;">
    									<a href="#" class="cluetip help" rel="#cluetip_sync"
									       title="<s:text name="ContractorView.SynchronizeContractor" />"></a>
    								</span>

			<div id="cluetip_sync">
				<s:text name="ContractorView.SynchronizeContractorMessage"/>
			</div>
		</div>
		<div class="panel_content" style="text-align: center;">
			<s:form id="form_sync">
				<s:hidden name="id"/>
				<s:hidden name="button" value="Synchronize Contractor"/>

				<input type="submit"
				       class="picsbutton"
				       onclick="$(this).attr('disabled', true); $('#form_sync').submit();"
				       style="margin: 5px auto;"
				       value="<s:text name="ContractorView.Synchronize" />"/>

				<s:if test="contractor.lastRecalculation != null">
					<br/>

					<s:text name="ContractorView.LastSync">
						<s:param value="%{contractor.lastRecalculation}"/>
					</s:text>
				</s:if>
			</s:form>

			<s:if test="permissions.hasPermission('ManageCsrAssignment')">
				<a href="ContractorView!autoAssignCsr.action?id=<s:property value='contractor.id'/>" class="picsbutton">
					Auto Assign CSR
				</a>
			</s:if>

		</div>
	</div>
</div>
</td>
</tr>
</table>
</s:if>
<s:elseif test="showBasicsOnly">
	<s:include value="_con-dashboard-basics-only.jsp"/>
</s:elseif>
<s:else>
	<s:include value="con_dashboard_gc_limited.jsp"/>
</s:else>
</div>
</body>