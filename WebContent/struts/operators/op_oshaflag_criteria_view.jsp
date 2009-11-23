<%@ taglib prefix="s" uri="/struts-tags"%>
<div>
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
		<s:elseif test="fatalities">
			Fatalities:
		</s:elseif>
		<s:elseif test="cad7">
			Cad7:
		</s:elseif>
		<s:elseif test="neer">
			Neer:
		</s:elseif>
		<s:elseif test="dart">
			Dart:
		</s:elseif>
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
			<s:radio list="#{'1':'Individual Yrs','2':'Last Year Only','3':'ThreeYearAverage'}"
				name="redOshaCriteria.lwcr.time" theme="pics" />
		</s:if> <s:elseif test="trir">
			<s:radio list="#{'1':'Individual Yrs','2':'Last Year Only','3':'ThreeYearAverage'}"
				name="redOshaCriteria.trir.time" theme="pics" />
		</s:elseif>
		<s:elseif test="dart">
			<s:radio list="#{'1':'Individual Yrs','2':'Last Year Only','3':'ThreeYearAverage'}"
				name="redOshaCriteria.dart.time" theme="pics" />
		</s:elseif>
		<s:elseif test="neer">
			<s:radio list="#{'1':'Individual Yrs','2':'Last Year Only','3':'ThreeYearAverage'}"
				name="redOshaCriteria.neer.time" theme="pics" />
		</s:elseif>
		<s:else>
				Individual Years	
		</s:else></td>
		<td> > </td>
		<td><s:if test="lwcr">
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="redOshaCriteria.lwcr.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'red'); return true;"/>
		</s:if> <s:elseif test="trir">
			<s:radio list="#{'None':'None<br/>','NAICS':'NAICS<br/>','Absolute':'Absolute'}" name="redOshaCriteria.trir.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'red'); return true;"/>
		</s:elseif> <s:elseif test="fatalities"> 
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="redOshaCriteria.fatalities.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'red'); return true;"/>
		</s:elseif> <s:elseif test="cad7">		
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="redOshaCriteria.cad7.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'red'); return true;"/>
		</s:elseif> <s:elseif test="neer">
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="redOshaCriteria.neer.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'red'); return true;"/>
		</s:elseif>
		<s:elseif test="dart">
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="redOshaCriteria.dart.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'red'); return true;"/>
		</s:elseif>
		</td>
		<td>
		<s:if test="lwcr">
			<s:set name="redhurdleFlag" value="redOshaCriteria.lwcr.hurdleFlag"/>
		</s:if>
		<s:elseif test="trir">
			<s:set name="redhurdleFlag" value="redOshaCriteria.trir.hurdleFlag"/>
		</s:elseif>
		<s:elseif test="fatalities">
			<s:set name="redhurdleFlag" value="redOshaCriteria.fatalities.hurdleFlag"/>
		</s:elseif>
		<s:elseif test="cad7">
			<s:set name="redhurdleFlag" value="redOshaCriteria.cad7.hurdleFlag"/>
		</s:elseif>
		<s:elseif test="neer">
			<s:set name="redhurdleFlag" value="redOshaCriteria.neer.hurdleFlag"/>
		</s:elseif>
		<s:elseif test="dart">
			<s:set name="redhurdleFlag" value="redOshaCriteria.dart.hurdleFlag"/>
		</s:elseif>
	
		<s:if test="#redhurdleFlag.none">
			<s:set name="show_redhurdle" value="'none'"/>
		</s:if>
		<s:else>
			<s:set name="show_redhurdle" value="'inline'"/>
		</s:else>
		<s:if test="#redhurdleFlag.naics">
			<s:set name="show_redhurdlepercent" value="'inline'"/>
		</s:if>
		<s:else>
			<s:set name="show_redhurdlepercent" value="'none'"/>
		</s:else>		
		<nobr>
		<span id="show_redhurdle" style="display: <s:property value="#attr.show_redhurdle"/>;">
		<s:if test="lwcr">
			<s:textfield name="redOshaCriteria.lwcr.hurdle" size="5"/>
		</s:if><s:elseif test="trir">
			<s:if test="#redhurdleFlag.naics">
				<s:textfield name="redOshaCriteria.trir.hurdle" size="5" value="%{format(redOshaCriteria.trir.hurdle, '#')}"/>
			</s:if>
			<s:else>
				<s:textfield name="redOshaCriteria.trir.hurdle" size="5"/>
			</s:else>		
		</s:elseif> <s:elseif test="fatalities">
			<s:textfield name="redOshaCriteria.fatalities.hurdle" size="5"/>
		</s:elseif> <s:elseif test="cad7">
			<s:textfield name="redOshaCriteria.cad7.hurdle" size="5"/>
		</s:elseif> <s:elseif test="neer">
			<s:textfield name="redOshaCriteria.neer.hurdle" size="5"/>
		</s:elseif>
		<s:elseif test="dart">
			<s:textfield name="redOshaCriteria.dart.hurdle" size="5"/>
		</s:elseif>
		</span>
		<span id="show_redhurdlepercent" style="display: <s:property value="#attr.show_redhurdlepercent"/>;">%</span>
		</nobr></td>
	</tr>
	<tr>
		<td>
			<label>Amber: </label>
		</td>
		<td><s:if test="lwcr">
			<s:radio list="#{'1':'Individual Yrs','2':'Last Year Only','3':'ThreeYearAverage'}"
				name="amberOshaCriteria.lwcr.time" theme="pics" />
		</s:if> <s:elseif test="trir">
			<s:radio list="#{'1':'Individual Yrs','2':'Last Year Only','3':'ThreeYearAverage'}"
				name="amberOshaCriteria.trir.time" theme="pics" />
		</s:elseif>
		<s:elseif test="dart">
			<s:radio list="#{'1':'Individual Yrs','2':'Last Year Only','3':'ThreeYearAverage'}"
				name="amberOshaCriteria.dart.time" theme="pics" />
		</s:elseif>
		<s:elseif test="neer">
			<s:radio list="#{'1':'Individual Yrs','2':'Last Year Only','3':'ThreeYearAverage'}"
				name="amberOshaCriteria.neer.time" theme="pics" />
		</s:elseif>
		<s:else>
				Individual Years	
		</s:else></td>
		<td> > </td>
		<td><s:if test="lwcr">
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="amberOshaCriteria.lwcr.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'amber'); return true;"/>
		</s:if> <s:elseif test="trir">
			<s:radio list="#{'None':'None<br/>','NAICS':'NAICS<br/>','Absolute':'Absolute'}" name="amberOshaCriteria.trir.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'amber'); return true;"/>
		</s:elseif> <s:elseif test="fatalities"> 
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="amberOshaCriteria.fatalities.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'amber'); return true;"/>
		</s:elseif> <s:elseif test="cad7">
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="amberOshaCriteria.cad7.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'amber'); return true;"/>
		</s:elseif> <s:elseif test="neer">
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="amberOshaCriteria.neer.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'amber'); return true;"/>
		</s:elseif>
		<s:elseif test="dart">
			<s:radio list="#{'None':'None<br/>','Absolute':'Absolute'}" name="amberOshaCriteria.dart.hurdleFlag" theme="pics" onclick="javascript : showHudleType(this,'amber'); return true;"/>
		</s:elseif>
		</td>		
		<td>
		<s:if test="lwcr">
			<s:set name="amberhurdleFlag" value="amberOshaCriteria.lwcr.hurdleFlag"/>
		</s:if>
		<s:elseif test="trir">
			<s:set name="amberhurdleFlag" value="amberOshaCriteria.trir.hurdleFlag"/>
		</s:elseif>
		<s:elseif test="fatalities">
			<s:set name="amberhurdleFlag" value="amberOshaCriteria.fatalities.hurdleFlag"/>
		</s:elseif>
		<s:elseif test="cad7">
			<s:set name="amberhurdleFlag" value="amberOshaCriteria.cad7.hurdleFlag"/>
		</s:elseif>
		<s:elseif test="neer">
			<s:set name="amberhurdleFlag" value="amberOshaCriteria.neer.hurdleFlag"/>
		</s:elseif>
		<s:elseif test="dart">
			<s:set name="amberhurdleFlag" value="amberOshaCriteria.dart.hurdleFlag"/>
		</s:elseif>

		<s:if test="#amberhurdleFlag.none">
			<s:set name="show_amberhurdle" value="'none'"/>
		</s:if>
		<s:else>
			<s:set name="show_amberhurdle" value="'inline'"/>
		</s:else>
		<s:if test="#amberhurdleFlag.naics'">
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
			<s:if test="#amberhurdleFlag.naics">
				<s:textfield name="amberOshaCriteria.trir.hurdle" size="5" value="%{format(amberOshaCriteria.trir.hurdle, '#')}"/>
			</s:if>
			<s:else>
				<s:textfield name="amberOshaCriteria.trir.hurdle" size="5"/>
			</s:else>		
		</s:elseif> <s:elseif test="fatalities">
			<s:textfield name="amberOshaCriteria.fatalities.hurdle" size="5"/>
		</s:elseif> <s:elseif test="cad7">
			<s:textfield name="amberOshaCriteria.cad7.hurdle" size="5"/>
		</s:elseif> <s:elseif test="neer">
			<s:textfield name="amberOshaCriteria.neer.hurdle" size="5"/>
		</s:elseif><s:elseif test="dart">
			<s:textfield name="amberOshaCriteria.dart.hurdle" size="5"/>
		</s:elseif>
		</span>
		<span id="show_amberhurdlepercent" style="display: <s:property value="#attr.show_amberhurdlepercent"/>;">%</span>
		</nobr>
		</td>
	</tr>
	</table>
</form>
<br clear="all"/>
<s:if test="trir">
	<div class="info">
		<span style="font-size: 14px;">You have the option to now flag by either your own TRIR Avg(1) or industry based NAICS TRIR
		provided by BLS(2).</span><br/>
		<span style="font-size: 11px;">
		(1) All Contractors are ranked against one TRIR Avg.<br/>
		(2) Contractors are ranked against their own industry avg.
		ex: For Construction the NAICS code is 23 and Average TRIR is 5.4. 
		If the NAICS cut off is 150%(i.e 50% more than the exact value) all the contractors with that 
		code and TRIR greater than 8.1 will be flagged.
		</span>  
	</div>
</s:if>
</div>