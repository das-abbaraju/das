<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title>
			${contractor.name}
		</title>
		<link rel="stylesheet" href="css/reports.css?version=<s:property value="version" />" />
		<style type="text/css">
			table.report
			{
				width: 100%;
			}
			
			table.report td
			{
				text-align: center;
				width: 33%;
			}
			
			.clip-container
			{
				position: relative;
			}
			
			tr.badges td
			{
				height: 300px !important;
			}
		</style>
		<script type="text/javascript" src="js/zeroclipboard/ZeroClipboard.js"></script>
		<script type="text/javascript">
			var clip80 = new ZeroClipboard.Client();
			var clip100 = new ZeroClipboard.Client();
			var clip150 = new ZeroClipboard.Client();
		
			$(function() {
				ZeroClipboard.setMoviePath( "js/zeroclipboard/ZeroClipboard.swf" );
				
				clip80.setHandCursor(true);
				clip100.setHandCursor(true);
				clip150.setHandCursor(true);
				
				clip80.glue('clip80_button', 'clip80_container');
				clip100.glue('clip100_button', 'clip100_container');
				clip150.glue('clip150_button', 'clip150_container');
				
				$('#clip80_container').live('mouseover', function(event) {
					clip80.setText($('#clip80_text').text());
				});
				
				$('#clip100_container').live('mouseover', function(event) {
					clip100.setText($('#clip100_text').text());
				});
				
				$('#clip150_container').live('mouseover', function(event) {
					clip150.setText($('#clip150_text').text());
				});
				
				clip80.addEventListener('complete', function(client) {
					$('#clip80_button').hide();
					$('#clip80_copied').show();
				});
				
				clip100.addEventListener('complete', function(client) {
					$('#clip100_button').hide();
					$('#clip100_copied').show();
				});
				
				clip150.addEventListener('complete', function(client) {
					$('#clip150_button').hide();
					$('#clip150_copied').show();
				});
			});
		</script>
	</head>
	<body>
		<s:include value="conHeader.jsp"/>
		
		<div id="Contractor_Badge">
			<div class="info">
				<s:text name="ContractorBadge.Information" />
			</div>

			<table class="report">
				<thead>
					<tr>
						<th colspan="3">
							<s:text name="button.Preview" />
						</th>
					</tr>
				</thead>
				<tbody>
					<tr class="badges">
						<s:iterator value="#{80,100,150}">
							<td>
								<s:property value="getScriptlet(key)" escape="false" />
							</td>
						</s:iterator>
					</tr>
					<tr>
						<s:iterator value="#{80,100,150}">
							<td>
								<s:textarea
									id="%{'clip' + key + '_text'}"
									value="%{getScriptlet(key)}"
									cols="35"
									rows="10"
								/>
								<br />
								<div id="clip${key}_container" class="clip-container">
									<a href="#" id="clip${key}_button" class="add">
										<s:text name="ContractorBadge.CopyToClipboard" />
									</a>
									<a href="#" id="clip${key}_copied" class="hide">
										<s:text name="ContractorBadge.CopiedToClipboard" />
									</a>
								</div>
							</td>
						</s:iterator>
					</tr>
				</tbody>
			</table>
		</div>
	</body>
</html>