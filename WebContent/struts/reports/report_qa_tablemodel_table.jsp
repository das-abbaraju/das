<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report">
	<thead>
		<tr>		
		  <s:iterator var="columnName" value="#data.columnNames">
			<th><s:property value="columnName"/></th>
		  </s:iterator>
		</tr>
	</thead>
	<tbody>
	  <s:iterator var="row" value="#data.rows">
		  <tr>
		  	<s:iterator var="datum" value="#row" status="stat">
				<s:set name="entityName" value="#data.getColumnEntityName(#stat.count)"/>	  		
		  		<s:if test="%{'Contractor' == #entityName}">
		  			<td>
	  						<a class="contractorQuick" 
	  							rel="ContractorQuick.action?id=<s:property value="#datum.getValue()"/>"
	  							href="ContractorView.action?id=<s:property value="#datum.getValue()"/>"
	  						 ><s:property value="#datum.getValue()"/></a>
	  					</td>
				</s:if>
		  		<s:elseif test="%{'Operator' == #entityName}">
		  			<td>
	  						<a class="operatorQuick" 
	  							rel="OperatorQuickAjax.action?id=<s:property value="#datum.getValue()"/>"
	  							href="FacilitiesEdit.action?operator=<s:property value="#datum.getValue()"/>"
	  						 ><s:property value="#datum.getValue()"/></a>
	  					</td>
				</s:elseif>			  		
		  		<s:elseif test="%{'Audit' == #entityName}">
		  			<td>
	  						<a class="operatorQuick" 
	  							rel="AuditQuickAjax.action?auditID=<s:property value="#datum.getValue()"/>"
	  							href="Audit.action?auditID=<s:property value="#datum.getValue()"/>"
	  						 ><s:property value="#datum.getValue()"/></a>
	  					</td>
				</s:elseif>			  		
		  		<s:else>
		  			<td><s:property value="#datum.getValue()"/></td>
		  		</s:else>
	
		  	</s:iterator>
		  </tr>
	  </s:iterator>
	</tbody>
</table>	