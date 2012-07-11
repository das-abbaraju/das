<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report" style="position: static">
	<s:set name="tagRemovable" value="false" />
	<s:iterator value="contractor.operatorTags" var="contractorTag">
		<s:if test="tag.active">
			<s:set name="tagRemovable" value="tag.operator.id == permissions.accountId || (permissions.corporateParent.contains(tag.operator.id) && tag.inheritable)" />
			<s:if test="#tagRemovable || permissions.admin || (permissions.contractor && tag.visibleToContractor)">
				<tr>
					<td><s:if test="tag.operator.id != permissions.accountId"><s:property value="tag.operator.name" />: </s:if><s:property value="tag.tag" /></td>
					<td><s:text name="global.TaggedBy"/>: <s:set var="o" value="contractorTag" /><s:set var="addBy" value="contractorTag.updatedBy" /><s:date var="addDate" name="contractorTag.updateDate"/><s:include value="../who.jsp" />
					</td>
					<s:if test="#tagRemovable">
						<td><img src="images/cross.png" width="18" height="18" /><a
								href="#" onclick="javascript:return removeTag(<s:property value="id"/>);"><s:text name="button.Remove"/></a>
						</td>
					</s:if>
				</tr>
			</s:if>
		</s:if>
	</s:iterator>
	<s:if test = "operatorTags.size() > 0 ">
		<tr>
			<td colspan="<s:property value="#tagRemovable ? 3 : 2" />"><s:select id="tagName" list="operatorTags" listKey="id" listValue="tag" headerKey="0" headerValue="- %{getText('Filters.header.Tag')} -"/><input
			type="button" onclick="javascript: return addTag();" value="<s:text name="global.Add" />"></td>
		</tr>
	</s:if>
	<s:if test="runTagConCronAjax">
		<script type="text/javascript">
			$('#opTagAjax').show();
			startThinking( {div: 'opTagAjax', message: 'Recalculating Requirements' } );	
			$.post('ContractorCron.action', {'button':'Run', 'conID':'<s:property value="id"/>', 'steps':['AuditCategory', 'AuditBuilder']}, function(data, status, xhr){				
				if(status='success'){
					$('#opTagAjax').html($('<span>', {'class':'okayCheck'}).append('Finished Recalculating Requirements')).append($('<br>'))
					.append($('<a>', {'class':'showPointer reloadPage refresh'}).append('Click to Refresh'));
				} else{
					$('#opTagAjax').html('Error with request');
				}
			});
		</script>
	</s:if>
</table>
<div id="opTagAjax"></div>
