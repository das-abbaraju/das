<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
	<head>
		<title><s:property value="reportName" /></title>
		
		<s:include value="reportHeader.jsp" />
		
		<script type="text/javascript">
			$(function() {
				$('#selected_contractor').autocomplete('ContractorSelectAjax.action', 
						{
							minChars: 3,
							extraParams: {'filter.accountName': function() {return $('#selected_contractor').val();} }
						}
				);
			});
		</script>
	</head>
	<body>
		<h1><s:property value="reportName" /></h1>
		
		<div id="search">
			<s:form id="form1" method="post" cssStyle="background-color: #F4F4F4;">
				<s:hidden name="filter.ajax" />
				<s:hidden name="filter.destinationAction" />
				<s:hidden name="filter.allowMailMerge" />
				<s:hidden name="showPage" value="1" />
				<s:hidden name="filter.startsWith" />
				<s:hidden name="orderBy" />
			
				<div class="filterOption">
					<s:text name="%{scope}.label.SelectAContractor" />:
					<s:textfield id="selected_contractor" cssClass="forms" name="filter.accountName" size="60" onfocus="clearText(this)"/> 
				</div>
		
				<br clear="all"/>
				
				<div class="filterOption">
					<s:text name="%{scope}.label.InvoiceID" />:
					<s:textfield cssClass="forms" name="invoiceID" size="15" onfocus="clearText(this)"/> 
				</div>
		
				<div class="filterOption">
					<s:radio 
						list="#{'All':getText(scope+'.status.All'),'Paid':getText(scope+'.status.Paid'),'Unpaid':getText(scope+'.status.Unpaid'),'Void':getText(scope+'.status.Void')}" 
						name="transactionStatus" 
						theme="pics"
						cssClass="forms inline"
					/> 
				</div>
				
				<div>
					<input type="submit" class="picsbutton positive" name="button" value="<s:text name="button.Search" />"/>
				</div>
				
				<br clear="all" />
			</s:form>
		</div>
		
		<div>
			<s:property value="report.pageLinksWithDynamicForm" escape="false" />
		</div>
			
		<table class="report">
			<thead>
				<tr>
					<th></th>
				    <th>
				    	<a href="javascript: changeOrderBy('form1','a.name');" ><s:text name="global.Contractor" /></a>
			    	</th>
				    <th>
				    	<s:text name="global.Address" />
			    	</th>
					<th>
						<a href="javascript: changeOrderBy('form1','i.id');"><s:text name="%{scope}.label.InvoiceNumber" /></a>
					</th>
					<th>
						<a href="javascript: changeOrderBy('form1','totalAmount DESC');"><s:text name="%{scope}.label.InvoiceTotal" /></a>
					</th>
					<th>
						<s:text name="%{scope}.label.Balance" />
					</th>
					<th>
						<a href="javascript: changeOrderBy('form1','dueDate');"><s:text name="%{scope}.label.DueDate" /></a>
					</th>
					<th>
						<s:text name="global.Status" />
					</th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="data" status="stat">
					<tr>
						<td class="right">
							<s:property value="#stat.index + report.firstRowNumber" />
						</td>
						<td>
							<a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')" /></a>
						</td>
						<td>
							<s:property value="get('address')"/><br/>
							<s:property value="get('city')"/>, <s:property value="get('state')"/> <s:property value="get('zip')"/>
						</td>
						<td class="center">
							<s:if test="get('status') == 'Unpaid'">
								<a href="PaymentDetail.action?id=<s:property value="get('id')"/>">
									<s:property value="get('invoiceId')"/>
								</a>
							</s:if>
							<s:else>
								<a href="InvoiceDetail.action?invoice.id=<s:property value="get('invoiceId')"/>">
									<s:property value="get('invoiceId')"/>
								</a>
							</s:else>
						</td>
						<td class="right">
							$<s:property value="get('totalAmount')"/>
						</td>
						<td class="right">
							$<s:property value="get('totalAmount')- get('amountApplied')"/>
						</td>
						<td class="right">
							<s:date name="get('dueDate')" format="M/d/yy"/>
						</td>
						<td>
							<s:property value="get('status')"/>
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</body>
</html>