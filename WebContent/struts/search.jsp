<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<script type="text/javascript">
	function changePage(form, start){
		var data = {
			button: 'search',
			startIndex: (start-1)*<s:property value="PAGEBREAK"/>,
			searchTerm: $('#hiddenSearchTerm').val()
		};
		startThinking( {div: 'pageResults', message: translate('JS.MainSearch.GettingResults'), type: 'large' } );
		$('#pageResults').load('SearchAjax.action #pageResults', data);
	}
</script>
<h2>
	<s:text name="MainSearch.title" />
</h2>
<s:hidden id="hiddenSearchTerm" value="%{searchTerm}" />
<div id="filterSuggest">
	<div id="info" style="">
		<s:text name="MainSearch.YouSearchedFor">
			<s:param>
				<s:property value="searchTerm" />
			</s:param>
		</s:text>
		<br/>
		<s:if test="searchEngine.commonFilterSuggest.size() > 0">
			<s:text name="MainSearch.TryAddingToSearch">
				<s:param>
					<s:iterator value="searchEngine.commonFilterSuggest" id="sug">
						<s:url var="additional_search_terms" action="Search">
							<s:param name="button" value="search" />
							<s:param name="searchTerm" value="%{searchTerm.replace(' ','+') + '+' + #sug.replace(' ','-')}" />
						</s:url>
						<a href="${additional_search_terms}">
							<s:property value="#sug.toLowerCase()" />
						</a> 
					</s:iterator>
				</s:param>
			</s:text>
		</s:if>
	</div>
</div>
<div id="pageResults">
	<div id="pageLinks"><s:property value="pageLinks" escape="false"/></div>
		<table class="report">
			<thead>
				<tr>
					<td>
						<s:text name="global.Type" />
					</td>
					<td>
						<s:text name="MainSearch.Result" />
					</td>
					<td>
						<s:text name="global.Action" />
					</td>
				</tr>
			</thead>
			<s:iterator value="fullList" id="result" status="row">
				<tr>
					<s:if test="#result.returnType=='account'">
						<td><s:property value="#result.type"/></td>
						<td>
							<s:if test="permissions.isCorporate()">
								<s:if test="#result.isContractor()">
										<s:if test="checkCon(#result.id)">
											<a href="<s:property value="#result.getViewLink()"/>">
												<s:property value="#result.name"/>
											</a>
										</s:if>	
										<s:else>
											<s:property value="#result.name"/>
										</s:else>
								</s:if>
								<s:else>
									<a href="<s:property value="#result.getViewLink()"/>">
										<s:property value="#result.name"/>
									</a>
								</s:else>
							</s:if>
							<s:else>
								<a href="<s:property value="#result.getViewLink()"/>">
									<s:property value="#result.name"/>
								</a>
							</s:else>
						</td>
						<td>
							<s:if test="permissions.isCorporate()">
								<s:if test="#result.isContractor()">
									<s:if test="checkCon(#result.id)">
										<s:text name="MainSearch.AlreadyInSystem" />
									</s:if>	
									<s:else>
										<a class="add" href="ContractorFacilities.action?id=<s:property value="#result.id"/>">
											<s:text name="button.Add" />
										</a>
									</s:else>
								</s:if>
								<s:else>
									<s:text name="MainSearch.NoAvailableAction" />
								</s:else>
							</s:if>
							<s:else>
								<s:text name="MainSearch.NoAvailableAction" />
							</s:else>					
						</td>
					</s:if>
					<s:if test="#result.returnType=='user'">
						<td>
							<s:text name="UsersManage.UserGroup" />
						</td>
						<td>
							<a href="<s:property value="#result.getViewLink()"/>">
								<s:text name="MainSearch.ResultAtAccount">
									<s:param value="%{#result.name}" />
									<s:param value="%{#result.account.name}" />
								</s:text>
							</a>
						</td>
						<td>							
							<pics:permission perm="SwitchUser">
								<a href="Login.action?button=login&switchToUser=<s:property value="#result.id"/>">
									<s:text name="OpPerms.SwitchUser.description" />
								</a>
							</pics:permission>
							<pics:permission perm="SwitchUser" negativeCheck="true">
								<s:text name="MainSearch.NoAvailableAction" />
							</pics:permission>
						</td>
					</s:if>
					<s:if test="#result.returnType=='employee'">
						<td>
							<s:text name="global.Employee" />
						</td>
						<td>
							<a href="<s:property value="#result.getViewLink()"/>">
								<s:text name="MainSearch.ResultAtAccount">
									<s:param value="%{#result.displayName}" />
									<s:param value="%{#result.account.name}" />
								</s:text>
							</a>
						</td>
						<td>
							<s:text name="MainSearch.NoAvailableAction" />
						</td>
					</s:if>
				</tr>
			</s:iterator>
		</table>
	<div id="pageLinks"><s:property value="pageLinks" escape="false"/></div>
</div>