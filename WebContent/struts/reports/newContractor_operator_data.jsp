<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:if test="actionErrors.size > 0">
	<s:include value="../actionMessages.jsp" />
</s:if>
<s:elseif test="report.allRows == 0">
    <pics:toggle name="RequestNewContractorAccount">
        <div class="alert">
            <s:text name="NewContractorSearch.message.NoCompanyAccountsFound" />
        </div>

        <pics:permission perm="RequestNewContractor" type="Edit">
            <div class="info">
                <s:text name="NewContractorSearch.message.SubmitNewContractorAccountRequest" />
            </div>
        </pics:permission>
    </pics:toggle>
    <pics:toggleElse>
        <div class="alert">
            <s:text name="NewContractorSearch.message.NoCompaniesFound" />
        </div>

        <pics:permission perm="RequestNewContractor" type="Edit">
            <div class="info">
                <s:text name="NewContractorSearch.message.SubmitNewContractorRequest" />
            </div>
        </pics:permission>
    </pics:toggleElse>
</s:elseif>
<s:else>
    <pics:permission perm="ContractorDetails">
        <s:if test="!filter.allowMailMerge">
        	<div class="right">
                <a 
                    class="excel" 
            		href="javascript: download('NewContractorSearch');" 
            		title="<s:text name="javascript.DownloadAllRows"><s:param>${report.allRows}</s:param></s:text>"
                    <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param>${report.allRows}</s:param></s:text>');"</s:if>>
                    <s:text name="global.Download" />
                </a>
            </div>
        </s:if>
    </pics:permission>
    
    <div>
    	${report.pageLinksWithDynamicForm}
    </div>
    
    <table class="report">
    	<thead>
        	<tr>
        		<td>
                    #
                </td>
        		<td>
                    <a href="javascript: changeOrderBy('form1','a.name');"><s:text name="global.ContractorName" /></a>
                </td>
                
        		<s:if test="permissions.operator">
        			<td style="white-space: nowrap">
        				<a href="javascript:;" class="cluetip help" title="<s:text name="NewContractorSearch.label.Preflag" />" rel="#watchtip"></a>
                        <s:text name="NewContractorSearch.label.Preflag" />
        				
                        <div id="watchtip">
                            <s:text name="NewContractorSearch.label.Preflag.cluetip" />
                        </div>
        			</td>
                    
        			<s:if test="operatorAccount.approvesRelationships">
        				<pics:permission perm="ViewUnApproved">
        					<td>
                                <s:text name="global.Status" />
                            </td>
        				</pics:permission>
        			</s:if>
        		</s:if>
                
        		<pics:permission perm="PicsScore">
        			<td>
                        <s:text name="ContractorAccount.score" />
                    </td>
        		</pics:permission>
                
        		<td>
                    <s:text name="global.Action" />
                </td>
                
        		<s:if test="showContact">
        			<td>
                        <s:text name="global.ContactPrimary" />
                    </td>
        			<td>
                        <s:text name="User.phone" />
                    </td>
        			<td>
                        <s:text name="User.email" />
                    </td>
        			<td>
                        <s:text name="global.PrimaryAddress" />
                    </td>
        		</s:if>
                
        		<td>
        			<a href="javascript: changeOrderBy('form1','a.country, a.countrySubdivision, a.city, a.name');">
        				<s:text name="NewContractorSearch.label.CityCountrySubdivision" />
        			</a>
        		</td>
                
        		<s:if test="showTrade">
        			<td>
                        <s:text name="ContractorEdit.IndustryDetails.MainTrade" />
                    </td>
        			<td>
                        <s:text name="ContractorAccount.tradesSelf" />
                    </td>
        			<td>
                        <s:text name="ContractorAccount.tradesSub" />
                    </td>
        		</s:if>
                
        		<s:if test="hasInsuranceCriteria">
        			<td>
        				<s:text name="global.Insurance" />
        			</td>
        		</s:if>
        	</tr>
    	</thead>
    	<tbody>
    		<s:iterator value="data" status="stat" var="row">
    			<tr>
    				<td class="right">
    					${stat.index + report.firstRowNumber}
                    </td>
    				<td>
    					<s:if test="#row.get('showInDirectory') == 1">
    						<s:url action="ContractorView" var="limited_contractor_view">
    							<s:param name="id">
	    							${row.get('id')}
    							</s:param>
    						</s:url>
    						<a href="${limited_contractor_view}">
    							${row.get('name')}
    						</a>
    					</s:if>
    					<s:else>
    						${row.get('name')}
    					</s:else>
                        
    					<s:if test="get('dbaName') > '' && get('name') != get('dbaName')">
                            <br />
                            <s:text name="ContractorAccount.dbaName.short" />: ${row.get('dbaName')}
                        </s:if>
    				</td>
                    
    				<s:if test="permissions.operator">
    					<td class="center">
    						<s:if test="worksForOperator(get('id'))">
    							<img src="images/icon_${row.get('lflag')}Flag.gif" width="12" height="15" border="0" />
    						</s:if>
    						<s:else>
    							<img width="12" height="15" border="0" src="images/icon_<s:property value="getOverallFlag(get('id')).toString().toLowerCase()"/>Flag.gif" />
    						</s:else>
    					</td>
                        
    					<s:if test="operatorAccount.approvesRelationships">
    						<pics:permission perm="ViewUnApproved">
    							<td class="center">
    								${row.get('workStatus')}
                                </td>
    						</pics:permission>
    					</s:if>
    				</s:if>
                    
    				<pics:permission perm="PicsScore">
    					<td>
    						${row.get('score')}
                        </td>
    				</pics:permission>
                    
    				<td class="center">
   						<s:url action="ContractorFacilities" var="contractor_facilities">
   							<s:param name="id">
   								${row.get('id')}
   							</s:param>
   						</s:url>
    					<s:if test="get('genID') > 0">
    						<s:if test="get('status') == 'Requested'">
    							<s:url action="RequestNewContractorAccount" var="requested_contractor">
	    							<s:param name="contractor">
	    								${row.get('id')}
	    							</s:param>
	    						</s:url>
		  						
	    						<a href="${requested_contractor}" class="preview">
	    							<s:text name="button.View" />
	    						</a>
    						</s:if>
    						<s:else>
	    						<s:url action="ContractorView" var="contractor_view">
	    							<s:param name="id">
	    								${row.get('id')}
	    							</s:param>
	    						</s:url>
		  						
	    						<a href="${contractor_view}" class="preview">
	    							<s:text name="button.View" />
	    						</a>
    						</s:else>
                            
    						<pics:permission perm="RemoveContractors">
    							<br />
                                
    							<s:if test="permissions.corporate">
    								<a class="remove" href="${contractor_facilities}"><s:text name="button.Remove" /></a>
    							</s:if>
    							<s:else>
    								<s:url method="remove" var="remove_contractor">
    									<s:param name="contractor">
    										${row.get('id')}
    									</s:param>
    								</s:url>
    								<a class="remove" href="${remove_contractor}">
    									<s:text name="button.Remove" />
    								</a>
    							</s:else>
    						</pics:permission>
    					</s:if>
    					<s:else>
    						<pics:permission perm="AddContractors">
    							<s:if test="permissions.corporate">
    								<a class="add" href="${contractor_facilities}">
    									<s:text name="button.Add" />
    								</a>
    							</s:if>
    							<s:else>
		    						<s:if test="get('status') == 'Requested'">
		    							<pics:toggle name="RequestNewContractorAccount">
			   								<s:url action="RequestNewContractorAccount" var="request_contractor">
			   									<s:param name="contractor">
			   										${row.get('id')}
			   									</s:param>
			   								</s:url>
			   								<a href="${request_contractor}" class="add">
			   									<s:text name="NewContractorSearch.button.Request" />
			   								</a>
		    							</pics:toggle>
		   							</s:if>
		   							<s:else>
	    								<s:url method="add" var="add_contractor">
	    									<s:param name="contractor">
	    										${row.get('id')}
	    									</s:param>
	    								</s:url>
	    								<a class="add" href="${add_contractor}">
	    									<s:text name="button.Add" />
	    								</a>
    								</s:else>
   								</s:else>
    						</pics:permission>
    					</s:else>
    				</td>
                    
    				<s:if test="showContact">
    					<td>
    						${row.get('contactname')}
                        </td>
    					<td>
    						${row.get('contactphone')}
                        </td>
    					<td>
    						${row.get('contactemail')}
                        </td>
    					<td>
    						${row.get('address')}
                        </td>
    				</s:if>
                    
    				<td>
    					${row.get('city')}, ${row.get('countrySubdivision')}
                        
    					<s:if test="get('countrySubdivision') == ''">
    						${row.get('country')}
    					</s:if>
    				</td>
                    
    				<s:if test="showTrade">
    					<td>
    						${row.get('main_trade')}
    					</td>
    					<td class="tradeList">
    						${row.get('tradesSelf')}
    					</td>
    					<td class="tradeList">
    						${row.get('tradesSub')}
    					</td>
    				</s:if>
                    
    				<s:if test="hasInsuranceCriteria">
    					<td>
    						<s:if test="get('answer2074') != null">
    							<span style="font-size: 9px;">GL Each Occurrence = <s:property value="getFormattedDollarAmount(get('answer2074'))"/></span> <br/>
    						</s:if>
                            
    						<s:if test="get('answer2079') != null">
    							<span style="font-size: 9px;">GL General Aggregate = <s:property value="getFormattedDollarAmount(get('answer2079'))"/></span> <br/>
    						</s:if>
    						
                            <s:if test="get('answer2155') != null">
    							<span style="font-size: 9px;">AL Combined Single = <s:property value="getFormattedDollarAmount(get('answer2155'))"/></span> <br/>
    						</s:if>
    						
                            <s:if test="get('answer2149') != null">
    							<span style="font-size: 9px;">WC Each Accident = <s:property value="getFormattedDollarAmount(get('answer2149'))"/></span> <br/>
    						</s:if>
    						
                            <s:if test="get('answer2161') != null">
    							<span style="font-size: 9px;">EX Each Occurrence = <s:property value="getFormattedDollarAmount(get('answer2161'))"/></span>
    						</s:if>
    					</td>
    				</s:if>
    			</tr>
    		</s:iterator>
    	</tbody>
    </table>
    
    <div>
    	${report.pageLinksWithDynamicForm}
    </div>
    
    <div class="info">
    	<pics:toggle name="RequestNewContractorAccount">
	        <s:text name="NewContractorSearch.message.CompanyAccountNotListed" />
    	</pics:toggle>
    	<pics:toggleElse>
	        <s:text name="NewContractorSearch.message.CompanyNotListed" />
    	</pics:toggleElse>
    </div>
</s:else>

<script type="text/javascript">
	$('td.tradeList').each(function() {
		var trade = $.trim($(this).text());
		
		if (trade.length > 100) {
			var text = trade.substring(0, 100);
			text = text + '<a href="#" title="' + translate('JS.NewContractorSearch.ShowMore') + '" class="showMoreLink">...</a>';
			
			var rest = trade.substring(100, trade.length);
			rest = '<span class="hidden">' + rest + '<br /><a href="javascript:;" class="hideLink">Hide</a></span>';
			
			$(this).html(text + rest);
		}
	});
	
	$('td.tradeList').delegate('a.showMoreLink', 'click', function(e) {
		e.preventDefault();
		
		$(this).parent().find("span.hidden").show();
		$(this).hide();
	});
	
	$('td.tradeList').delegate('a.hideLink', 'click', function(e) {
		e.preventDefault();
		
		$(this).parent().hide();
		$(this).parent().parent().find("a.showMoreLink").show();
	});
</script>
