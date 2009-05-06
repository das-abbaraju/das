<%@ taglib prefix="s" uri="/struts-tags"%>
<form id="criteriaEditForm">
	<input type="hidden" value="<s:property value="operator.id"/>" name="id"/>
	<div>
		<s:if test="lwcr">		
			Lost Workdays Case Rate (LWCR):
		</s:if>
		<s:elseif test="trir">
			Total Recordable Incident Rate (TRIR):
		</s:elseif>
		<s:else>
			Fatalities:
		</s:else>
		<br/>
	</div>
	<table>
	<tr>
		<td><label>Red:</label></td>
		<td><s:if test="lwcr">
			<s:textfield name="redOshaCriteria.lwcr.hurdle"/>
		</s:if> <s:elseif test="trir">
			<s:textfield name="redOshaCriteria.trir.hurdle" />
		</s:elseif> <s:else>
			<s:textfield name="redOshaCriteria.fatalities.hurdle" />
		</s:else></td>
		<td><s:if test="lwcr">
			<s:radio list="#{'1':'Individual Yrs','3':'ThreeYearAverage'}"
				name="redOshaCriteria.lwcr.time" theme="pics" />
		</s:if> <s:elseif test="trir">
			<s:radio list="#{'1':'Individual Yrs','3':'ThreeYearAverage'}"
				name="redOshaCriteria.trir.time" theme="pics" />
		</s:elseif> <s:else>
				Individual Years	
		</s:else></td>
		<td><s:if test="lwcr">
			<s:checkbox name="redOshaCriteria.lwcr.flag" value="redOshaCriteria.lwcr.flag.name() == 'Yes' ? true : false"/>
		</s:if> <s:elseif test="trir">
			<s:checkbox name="redOshaCriteria.trir.flag" value="redOshaCriteria.trir.flag.name() == 'Yes' ? true : false"/>
		</s:elseif> <s:else>
			<s:checkbox name="redOshaCriteria.fatalities.flag" value="redOshaCriteria.fatalities.flag.name() == 'Yes' ? true : false"/>
		</s:else></td>
	</tr>
	<tr>
		<td>
			<label>Amber:</label>
		</td>
		<td><s:if test="lwcr">
			<s:textfield name="amberOshaCriteria.lwcr.hurdle" />
		</s:if> <s:elseif test="trir">
			<s:textfield name="amberOshaCriteria.trir.hurdle" />
		</s:elseif> <s:else>
			<s:textfield name="amberOshaCriteria.fatalities.hurdle" />
		</s:else></td>
		<td><s:if test="lwcr">
			<s:radio list="#{'1':'Individual Yrs','3':'ThreeYearAverage'}"
				name="amberOshaCriteria.lwcr.time" theme="pics" />
		</s:if> <s:elseif test="trir">
			<s:radio list="#{'1':'Individual Yrs','3':'ThreeYearAverage'}"
				name="amberOshaCriteria.trir.time" theme="pics" />
		</s:elseif><s:else>
					Individual Years	
		</s:else></td>
		<td><s:if test="lwcr">
			<s:checkbox name="amberOshaCriteria.lwcr.flag" value="amberOshaCriteria.lwcr.flag.name() == 'Yes' ? true : false"/>
		</s:if> <s:elseif test="trir">
			<s:checkbox name="amberOshaCriteria.trir.flag" value="amberOshaCriteria.trir.flag.name() == 'Yes' ? true : false"/>
		</s:elseif> <s:else>
			<s:checkbox name="amberOshaCriteria.fatalities.flag" value="amberOshaCriteria.fatalities.flag.name() == 'Yes' ? true : false"/>
		</s:else></td>
	</tr>
	</table>
	
	<div class="buttons">
		<input type="button" id="save_button" class="picsbutton positive" onclick="saveOshaCriteria(); return false;" value="Save"/>
		<input type="button" id="close_button" class="picsbutton negative" onclick="closeCriteriaEdit(); return false;" value="Close" />
	</div>
</form>