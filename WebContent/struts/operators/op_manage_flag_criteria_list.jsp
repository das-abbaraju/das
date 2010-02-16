<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div style="width: 65%; clear: none; float: right; margin-right: 4%;">
	<table class="report">
		<thead>
			<tr>
				<th>Category</th>
				<th>Description</th>
				<th>Flag</th>
				<th>% Affected</th>
				<th>Remove</th>
			</tr>
		</thead>
		
		<s:iterator value="criteriaList">
			<pics:permission perm="AuditVerification">
			<tr>
				<td><s:property value="criteria.category" /></td>
				<td><s:property value="criteria.description" />
					<s:if test="criteria.allowCustomValue">
						<s:if test="criteria.dataType == 'boolean' && criteria.allowCustomValue">
							<s:select list="#{'true':'True','false':'False'}" value="criteria.defaultValue"></s:select>
						</s:if>
						<s:elseif test="criteria.dataType == 'number'">
							<input type="text" value="<s:property value="criteria.defaultValue" />" size="5" />
						</s:elseif>
						<s:elseif test="criteria.dataType == 'date'">
							<s:select list="#{'<':'<','>':'>','=':'='}" value="criteria.comparison"></s:select>
							<input type="text" class="datepicker" value="<s:property value="criteria.defaultValue" />" size="10" />
						</s:elseif>
						<s:else>
							<s:select list="#{'=':'=','!=':'!='}" value="criteria.comparison"></s:select>
							<input type="text" value="<s:property value="criteria.defaultValue" />" size="20" />
						</s:else>
					</s:if>
				</td>
				<td class="center" id="<s:property value="criteria.id" />">
					<s:select list="#{'Red':'Red','Amber':'Amber'}" value="flag" onchange="updateFlag(this)"></s:select>
					<span class="flagImage" style="background-image: url('');"></span>
				</td>
				<td><nobr>
				</nobr></td>
				<td class="center"><a href="#" class="remove" onclick="return confirm('Are you sure you want to remove this criteria?');"></a></td>
			</tr>
			</pics:permission>
		</s:iterator>
	</table>
	<a href="#" onclick="getAddQuestions('<s:property value="classType"/>');return false;" class="picsbutton">Add New Criteria</a>
	<span id="<s:property value="classType"/>_thinking"></span>
	<div id="<s:property value="classType"/>_questions" style="display:none"></div>
</div>
