<%@ taglib prefix="s" uri="/struts-tags"%>
<form id="criteriaEditForm">
	<input type="hidden" value="<s:property value="operator.id"/>" name="id"/>
	<input type="hidden" value="<s:property value="type"/>" name="type"/>
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
	<div>
	<table class="report">
	<thead><tr>
		<td></td>
		<th>Contractor</th>
		<td></td>
		<th>Hurdle</th>
		<th>Rate</th>
	</tr></thead>
	<tr>
		<td><label>Red: </label></td>
		<td><s:if test="lwcr">
			<s:radio list="#{'1':'Individual Yrs','3':'ThreeYearAverage'}"
				name="redOshaCriteria.lwcr.time" theme="pics" />
		</s:if> <s:elseif test="trir">
			<s:radio list="#{'1':'Individual Yrs','3':'ThreeYearAverage'}"
				name="redOshaCriteria.trir.time" theme="pics" />
		</s:elseif> <s:else>
				Individual Years	
		</s:else></td>
		<td> > </td>
		<td><s:if test="lwcr">
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="redOshaCriteria.lwcr.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'red'); return true;"/>
		</s:if> <s:elseif test="trir">
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute<br/>','NAICS':'NAICS'}" name="redOshaCriteria.trir.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'red'); return true;"/>
		</s:elseif> <s:else>
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="redOshaCriteria.fatalities.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'red'); return true;"/>
		</s:else></td>
		<td>
		<s:if test="redOshaCriteria.lwcr.hurdleFlag.toString() == 'None'">
			<s:set name="show_redhurdle" value="'none'"/>
		</s:if>
		<s:else>
			<s:set name="show_redhurdle" value="'inline'"/>
		</s:else>
		<s:if test="redOshaCriteria.lwcr.hurdleFlag.toString() == 'NAICS'">
			<s:set name="show_redhurdlepercent" value="'inline'"/>
		</s:if>
		<s:else>
			<s:set name="show_redhurdlepercent" value="'none'"/>
		</s:else>		
		<nobr>
		<span id="show_redhurdle" style="display: <s:property value="#attr.show_redhurdle"/>;">
		<s:if test="lwcr">
			<s:textfield name="redOshaCriteria.lwcr.hurdle" size="5"/>
		</s:if> <s:elseif test="trir">
			<s:textfield name="redOshaCriteria.trir.hurdle" size="5"/>
		</s:elseif> <s:else>
			<s:textfield name="redOshaCriteria.fatalities.hurdle" size="5"/>
		</s:else>
		</span>
		<span id="show_redhurdlepercent" style="display: <s:property value="#attr.show_redhurdlepercent"/>;">%</span>
		</nobr></td>
	</tr>
	<tr>
		<td>
			<label>Amber: </label>
		</td>
		<td><s:if test="lwcr">
			<s:radio list="#{'1':'Individual Yrs','3':'ThreeYearAverage'}"
				name="amberOshaCriteria.lwcr.time" theme="pics" />
		</s:if> <s:elseif test="trir">
			<s:radio list="#{'1':'Individual Yrs','3':'ThreeYearAverage'}"
				name="amberOshaCriteria.trir.time" theme="pics" />
		</s:elseif><s:else>
					Individual Years	
		</s:else></td>
		<td> > </td>
		<td><s:if test="lwcr">
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="amberOshaCriteria.lwcr.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'amber'); return true;"/>
		</s:if> <s:elseif test="trir">
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute<br/>','NAICS':'NAICS'}" name="amberOshaCriteria.trir.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'amber'); return true;"/>
		</s:elseif> <s:else>
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="amberOshaCriteria.fatalities.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'amber'); return true;"/>
		</s:else></td>		
		<td>
		<s:if test="amberOshaCriteria.lwcr.hurdleFlag.toString() == 'None'">
			<s:set name="show_amberhurdle" value="'none'"/>
		</s:if>
		<s:else>
			<s:set name="show_amberhurdle" value="'inline'"/>
		</s:else>
		<s:if test="amberOshaCriteria.lwcr.hurdleFlag.toString() == 'NAICS'">
			<s:set name="show_amberhurdlepercent" value="'inline'"/>
		</s:if>
		<s:else>
			<s:set name="show_amberhurdlepercent" value="'none'"/>
		</s:else>		
		<nobr>
		<span id="show_amberhurdle" style="display: <s:property value="#attr.show_amberhurdle"/>;">
		<s:if test="lwcr">
			<s:textfield name="amberOshaCriteria.lwcr.hurdle" size="5"/>
		</s:if> <s:elseif test="trir">
			<s:textfield name="amberOshaCriteria.trir.hurdle" size="5"/>
		</s:elseif> <s:else>
			<s:textfield name="amberOshaCriteria.fatalities.hurdle" size="5"/>
		</s:else>
		</span>
		<span id="show_amberhurdlepercent" style="display: <s:property value="#attr.show_amberhurdlepercent"/>;">%</span>
		</nobr>
		</td>
	</tr>
	</table>
	<div class="buttons">
		<input type="button" id="save_button" class="picsbutton positive" onclick="saveOshaCriteria(); return false;" value="Save"/>
		<input type="button" id="close_button" class="picsbutton negative" onclick="closeCriteriaEdit(); return false;" value="Close" />
	</div>
</form>
<br clear="all"/>
</div>
<div id="info">
If "Absolute" enter a cutoff value. If "NAICS" select a percentage.
</div> 
