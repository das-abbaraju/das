<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report" style="position: static">
	<s:set name="tagRemovable" value="false" />
	<s:iterator value="contractor.operatorTags">
		<s:if test="tag.active">
			<s:set name="tagRemovable" value="tag.operator.id == permissions.accountId || (permissions.corporateParent.contains(tag.operator.id) && tag.inheritable)" />
			<s:if test="#tagRemovable || permissions.admin">
				<tr>
					<td><s:if test="tag.operator.id != permissions.accountId"><s:property value="tag.operator.name" />: </s:if><s:property value="tag.tag" /></td>
					<td>Created by: <s:set var="o" value="tag" /><s:include value="../who.jsp" />
					</td>
					<s:if test="#tagRemovable">
						<td><img src="images/cross.png" width="18" height="18" /><a
								href="#" onclick="javascript:return removeTag(<s:property value="id"/>);">Remove</a>
						</td>
					</s:if>
				</tr>
			</s:if>
		</s:if>
	</s:iterator>
	<s:if test = "operatorTags.size() > 0 ">
		<tr>
			<td colspan="<s:property value="#tagRemovable ? 3 : 2" />"><s:select id="tagName" list="operatorTags" listKey="id" listValue="tag" headerKey="0" headerValue="- Operator Tag -"/><input
			type="button" onclick="javascript: return addTag();" value="Add"></td>
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
