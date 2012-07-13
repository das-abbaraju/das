<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<h1>
	<s:property value="operator.name" />
	
	<span class="sub"><s:property value="subHeading" escape="false"/></span>
</h1>

<s:if test="permissions.admin">
	<div id="internalnavcontainer">
		<ul id="navlist">
			<li>
				<a href="FacilitiesEdit.action?operator=<s:property value="operator.id"/>" id="facilitiesedit_edit" <s:if test="requestURI.contains('operator_edit')">class="current"</s:if> >
					<s:text name="global.Edit" />
				</a>
			</li>
			
			<pics:permission perm="ManageOperators" type="Edit">
				<li>
					<a href="OperatorConfiguration.action?id=<s:property value="operator.id"/>" id="facilitiesedit_configuration" <s:if test="requestURI.contains('op_config')">class="current"</s:if>>
						<s:text name="menu.Configuration" />
					</a>
				</li>
			</pics:permission>
			
			<li>
				<a href="OperatorNotes.action?id=<s:property value="operator.id"/>" id="facilitiesedit_notes" <s:if test="requestURI.contains('account_notes')">class="current"</s:if>>
					<s:text name="global.Notes" />
				</a>
			</li>
			<li>
				<a href="UsersManage.action?account=<s:property value="operator.id"/>" id="facilitiesedit_user">
					<s:text name="global.Users" />
				</a>
			</li>
			
			<pics:permission perm="ManageEmployees">
				<s:if test="!operator.id > 0">
					<li>
						<a href="ManageEmployees.action?id=<s:property value="operator.id"/>" id="facilitiesedit_employees">
							<s:text name="global.Employees" />
						</a>
					</li>
				</s:if>
			</pics:permission>
			
			<s:if test="!permissions.generalContractorFree">
				<li>
					<a href="ManageFlagCriteriaOperator.action?id=<s:property value="operator.id"/>" id="facilitiesedit_flagCriteria" <s:if test="requestURI.contains('op_manage_flag_criteria') && !insurance">class="current"</s:if>>
						<s:text name="FlagCriteria" />
					</a>
				</li>
				
				<s:if test="operator.canSeeInsurance.toString() == 'Yes'">
					<li>
						<a href="ManageInsuranceCriteriaOperator.action?id=<s:property value="operator.id"/>" id="facilitiesedit_flagCriteria_Insurance" <s:if test="requestURI.contains('op_manage_flag_criteria') && insurance">class="current"</s:if>>
							<s:text name="FlagCriteria.insurance" />
						</a>
					</li>
				</s:if>
			</s:if>
			
			<pics:permission perm="ManageProjects">
				<s:if test="operator.requiresOQ">
					<li>
						<a href="ManageProjects.action?operator=<s:property value="operator.id"/>" id="facilitiesedit_filter_label_project" <s:if test="requestURI.contains('op_job_sites')">class="current"</s:if>>
							<s:text name="Filters.label.Projects" />
						</a>
					</li>
				</s:if>
			</pics:permission>
			
			<pics:permission perm="ContractorTags">
				<li>
					<a href="OperatorTags.action?id=<s:property value="operator.id"/>" id="facilitiesedit_operatortags_title" <s:if test="requestURI.contains('operator_tags')">class="current"</s:if>>
						<s:text name="OperatorTags.title" />
					</a>
				</li>
			</pics:permission>
			
			<li>
				<a href="ContractorList.action?filter.status=Active&filter.status=Demo<s:property value="operatorIds"/>" id="facilitiesedit_contractor">
					<s:text name="global.Contractors" />
				</a>
			</li>
		</ul>
	</div>
</s:if>

<s:include value="../actionMessages.jsp"></s:include>