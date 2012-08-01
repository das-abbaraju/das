<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:if test="actionErrors.size > 0">
	<s:include value="../actionMessages.jsp" />
</s:if>
<s:elseif test="report.allRows == 0">
	<div class="alert">
        <s:text name="NewContractorSearch.message.NoCompaniesFound" />
    </div>
    
	<pics:permission perm="RequestNewContractor" type="Edit">
		<div class="info">
            <s:text name="NewContractorSearch.message.SubmitNewContractorRequest" />
        </div>
	</pics:permission>
</s:elseif>
<s:else>
    <pics:permission perm="ContractorDetails">
        <s:if test="!filter.allowMailMerge">
        	<div class="right">
                <a 
                    class="excel" 
            		href="javascript: download('NewContractorSearch');" 
            		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
                    <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if>>
                    <s:text name="global.Download" />
                </a>
            </div>
        </s:if>
    </pics:permission>
    
    <div>
        <s:property value="report.pageLinksWithDynamicForm" escape="false" />
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
        				<a href="#" class="cluetip help" title="<s:text name="NewContractorSearch.label.Preflag" />" rel="#watchtip"></a>
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
    		<s:iterator value="data" status="stat">
    			<tr>
    				<td class="right">
                        <s:property value="#stat.index + report.firstRowNumber" />
                    </td>
    				<td>
                        <s:property value="get('name')" />
                        
    					<s:if test="get('dbaName') > '' && get('name') != get('dbaName')">
                            <br />
                            <s:text name="ContractorAccount.dbaName.short" />: <s:property value="get('dbaName')" />
                        </s:if>
    				</td>
                    
    				<s:if test="permissions.operator">
    					<td class="center">
    						<s:if test="worksForOperator(get('id'))">
    							<img src="images/icon_<s:property value="get('lflag')"/>Flag.gif" width="12" height="15" border="0" />
    						</s:if>
    						<s:else>
    							<img width="12" height="15" border="0" src="images/icon_<s:property value="getOverallFlag(get('id')).toString().toLowerCase()"/>Flag.gif" />
    						</s:else>
    					</td>
                        
    					<s:if test="operatorAccount.approvesRelationships">
    						<pics:permission perm="ViewUnApproved">
    							<td class="center">
                                    <s:property value="get('workStatus')"/>
                                </td>
    						</pics:permission>
    					</s:if>
    				</s:if>
                    
    				<pics:permission perm="PicsScore">
    					<td>
                            <s:property value="get('score')"/>
                        </td>
    				</pics:permission>
                    
    				<td class="center">
    					<s:if test="get('genID') > 0">
    						<a href="ContractorView.action?id=<s:property value="get('id')"/>" class="preview"><s:text name="button.View" /></a>
                            
    						<pics:permission perm="RemoveContractors">
    							<br />
                                
    							<s:if test="permissions.corporate">
    								<a class="remove" href="ContractorFacilities.action?id=<s:property value="get('id')"/>"><s:text name="button.Remove" /></a>
    							</s:if>
    							<s:else>
    								<a class="remove" href="NewContractorSearch!remove.action?contractor=<s:property value="get('id')"/>"><s:text name="button.Remove" /></a>
    							</s:else>
    						</pics:permission>
    					</s:if>
    					<s:else>
    						<pics:permission perm="AddContractors">
    							<s:if test="permissions.corporate">
    								<a class="add" href="ContractorFacilities.action?id=<s:property value="get('id')"/>"><s:text name="button.Add" /></a>
    							</s:if>
    							<s:else>
    								<a class="add" href="NewContractorSearch!add.action?contractor=<s:property value="get('id')"/>"><s:text name="button.Add" /></a>
    							</s:else>
    						</pics:permission>
    					</s:else>
    				</td>
                    
    				<s:if test="showContact">
    					<td>
                            <s:property value="get('contactname')"/>
                        </td>
    					<td>
                            <s:property value="get('contactphone')"/>
                        </td>
    					<td>
                            <s:property value="get('contactemail')"/>
                        </td>
    					<td>
                            <s:property value="get('address')"/>
                        </td>
    				</s:if>
                    
    				<td>
    					<s:property value="get('city')"/>, <s:property value="get('countrySubdivision')"/>
                        
    					<s:if test="get('countrySubdivision') == ''">
    						<s:property value="get('country')"/>
    					</s:if>
    				</td>
                    
    				<s:if test="showTrade">
    					<td>
    						<s:property value="get('main_trade')"/>
    					</td>
    					<td class="tradeList">
    						<s:property value="get('tradesSelf')"/>
    					</td>
    					<td class="tradeList">
    						<s:property value="get('tradesSub')"/>
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
        <s:property value="report.pageLinksWithDynamicForm" escape="false" />
    </div>
    
    <div class="info">
        <s:text name="NewContractorSearch.message.CompanyNotListed" />
    </div>
</s:else>

<script type="text/javascript">
	$('td.tradeList').each(function() {
		var trade = $.trim($(this).text());
		
		if (trade.length > 100) {
			var text = trade.substring(0, 100);
			// window.console.log("Text: " + text);
			text = text + '<a href="#" title="' + translate('JS.NewContractorSearch.ShowMore') + '" class="showMoreLink">...</a>';
			
			var rest = trade.substring(100, trade.length);
			// window.console.log("Rest: " + rest);
			rest = '<span class="hidden">' + rest + '<br /><a href="#" class="hideLink">Hide</a></span>';
			
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