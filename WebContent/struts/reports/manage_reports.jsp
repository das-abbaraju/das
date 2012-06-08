<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page import="com.picsauditing.util.business.DynamicReportUtil" %>

<head>
    <title><s:property value="report.summary"/></title>
    <!-- <link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-theme.css"> -->
    <link rel="stylesheet" type="text/css" href="js/pics/resources/css/my-ext-custom.css?v=${version}">
    <link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/users_manage.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
	<s:include value="../jquery.jsp" />
	<style>
		/* Z-index of #mask must lower than #boxes .window */
		#mask {
		  position:absolute;
		  z-index:9000;
		  background-color:#000;
		  display:none;
		}
		#boxes .window {
		  position:fixed;
		  width:100px;
		  height:100px;
		  display:none;
		  z-index:9999;
		  padding:1px;
		}
		/* Customize your modal window here, you can add background image too */
		#boxes #dialog {
		  width:100px;
		  height:100px;
		}
	</style>
	<script>
		$(document).ready(function() {
		    //select all the a tag with name equal to modal
		    $('a[name=modal]').click(function(e) {
		        //Cancel the link behavior
		        e.preventDefault();
		        //Get the A tag

		        console.log($(this))

		        var popupWindow = $(this).next("#boxes").children(".ReportDialog")

		        //Get the screen height and width
		        var maskHeight = $(document).height();
		        var maskWidth = $(window).width();

		        //Set height and width to mask to fill up the whole screen
		        $('#mask').css({'width':maskWidth,'height':maskHeight});

		        //transition effect
		        $('#mask').fadeIn(1000);
		        $('#mask').fadeTo("slow",0.8);

		        //Get the window height and width
		        var winH = $(window).height();
		        var winW = $(window).width();

		        //Set the popup window to center
		        popupWindow.css('top',  winH/2-popupWindow.height()/2);
		        popupWindow.css('left', winW/2-popupWindow.width()/2);

		        //transition effect
		        popupWindow.fadeIn(2000);

		    });

		    //if close button is clicked
		    $('.window .close').click(function (e) {
		        //Cancel the link behavior
		        e.preventDefault();
		        $('#mask, .window').hide();
		    });

		    //if mask is clicked
		    $('#mask').click(function () {
		        $(this).hide();
		        $('.window').hide();
		    });
		});

	</script>
</head>

<body>
	<div id="user_edit">
		<s:include value="../actionMessages.jsp" />
		<fieldset class="form submit">
			<s:if test="user != null">
				<ul class="manage-users-actions">
					<li>
						<a class="btn" href="ManageReports.action?filterType=favorite">
							Favorites
						</a>
					</li>
					<li>
						<a class="btn" href="ManageReports.action?filterType=saved">
							Saved
						</a>
					</li>
					<li>
						<a class="btn" href="ManageReports.action?filterType=template">
							Base Reports
						</a>
					</li>
				</ul>
			</s:if>
		</fieldset>
	</div>
	<div id="reportTable">
		<table>
			<tr>
				<th></th>
				<Th>Report</Th>				
				<th></th>
				<th></th>
				<th></th>
				<th></th>
			</tr>
		    <s:iterator value="reportsByUser">
			    <tr>
			    	<td>
			    		<s:if test="favorite">
			    			<a href="ManageReports!changeFavorite.action?reportId=<s:property value="report.id" />&favorite=false">Favorite</a>&nbsp;
			    		</s:if>
			    		<s:else>
			    			<a href="ManageReports!changeFavorite.action?reportId=<s:property value="report.id" />&favorite=true">Normal</a>&nbsp;
			    		</s:else>
		    		</td>
			    	<td>
			    		<a href="ReportDynamic.action?report=<s:property value="report" />"><s:property value="report.name" /></a> 
			    		created by <s:property value="user.name" />
		    		</td>
					
					<td>
						<a href="javascript:" name="modal">Edit</a>&nbsp;
						<div id="boxes">
						    <!-- #customize your modal window here -->
						    <div class="ReportDialog window">
						        <s:form cssClass="form" id="userSaveReportName">
						    		<s:hidden name="report.id" />
									<fieldset>
									Change Name
										<li>
											<s:textfield name="name" value="%{report.name}" />
											<pics:fieldhelp title="Change Name">
												<p> Change Name </p>
											</pics:fieldhelp>
										</li>
										Change Description
										<li>
											<s:textfield name="description" value="%{report.description}" />
											<pics:fieldhelp title="Change Description">
												<p> Change Description </p>
											</pics:fieldhelp>
										</li>
									</fieldset>
									<fieldset class="form submit">
										<li>
										<s:submit value="%{getText('button.Save')}" cssClass="picsbutton positive" method="changeReportName" />
										<a href="#" class="close">Cancel</a>
										</li>
									</fieldset>
								</s:form>
						    </div>
						    <!-- Do not remove div#mask, because you'll need it to fill the whole screen --> 
						    <div id="mask"></div>
						</div>
					</td>
					<td>
						<a href="ManageReports!createReport.action?reportId=<s:property value="report.id" />">Copy</a>&nbsp;
					</td>
					<td>
						<a href="ManageReports!deleteReport.action?deleteType=remove&reportId=<s:property value="report.id" />">Remove</a>&nbsp;
						<s:if test="%{@com.picsauditing.util.business.DynamicReportUtil@userCanDelete(permissions.userId, report)}">
							<a href="ManageReports!deleteReport.action?deleteType=delete&reportId=<s:property value="report.id" />">Delete</a>
						</s:if>
					</td>
				</tr>
			</s:iterator>
		</table>
	</div>
</body>
