<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

	<div class="panel_placeholder">
						<div class="panel">
						<div class="panel_header"><span style="float: right;"> <a
							href="#" id="hurdleLinkShow"
							onclick="$('tr.hurdle').show(); $('#hurdleLinkShow').hide(); $('#hurdleLinkHide').show(); return false;"><s:text
							name="ContractorView.ShowHurdleRates" /></a> <a href="#"
							id="hurdleLinkHide"
							onclick="$('tr.hurdle').hide(); $('#hurdleLinkHide').hide(); $('#hurdleLinkShow').show(); return false;"
							style="display: none"><s:text
							name="ContractorView.HideHurdleRates" /></a> </span> <s:text
							name="global.Statistics" /></div>
						<div class="panel_content">
						<s:iterator value="stats.keySet()" var="stat">
							<table class="table">
								<thead>
									<tr>
										<th><s:property value="#stat" /></th>
										<s:iterator value="stats.get(#stat).get('columnNames')" var="colname">
											<th><s:property value="#colname" /></th>
										</s:iterator>
									</tr>
								</thead>
								<tbody>	
								 <s:set var="is_odd" value="true" />			
									<s:iterator value="stats.get(#stat).get('data')" var="row">
										<tr <s:if test="#is_odd == true">class="odd"</s:if>>
											<s:iterator value="#row" var="celldata">
												<td>
												<s:property value="#celldata" />
												</td>
											</s:iterator>
										</tr>
										<s:set var="is_odd" value="%{!#is_odd}" />
									</s:iterator>
								</tbody>
							</table>
						</s:iterator>
					
						<div class="clear"></div>
						</div>
					</div>
				</div>