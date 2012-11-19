<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<head>
	<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js?v=${version}"></script>
	<script>window.jQuery || document.write('<script src="js/jquery/jquery-1.7.1.min.js">\x3C/script>')</script>
	<script type="text/javascript">
		$(function() {
			$('.closeButton').live('click', function(e) {
				e.preventDefault();
				window.opener.location.reload();
				self.close();
			});
		});
	</script>
</head>
<body>
	<div id="main">
		<div id="bodyholder">
			<div id="content">
				<h1>
					<s:text name="ReportNewReqConImport.title" />
				</h1>
				
				<s:include value="../actionMessages.jsp" />
				
				<s:url action="Resources" method="download" var="request_import_template">
					<s:param name="id" value="1854" />
					<s:param name="loc">
						en
					</s:param>
				</s:url>
				<a href="${request_import_template}">
					<s:text name="ReportNewReqConImport.Template" />
				</a>
				
				<div>
					<s:form enctype="multipart/form-data" method="POST">
						<div style="background-color: #F9F9F9;">
							<div class="question">
								<label>
									<s:text name="global.File" />
								</label>
								<s:file name="file" value="%{file}" size="50"></s:file>
								<br />
								<br />
								<input
									type="button"
									class="picsbutton negative closeButton"
									value="<s:text name="ReportNewReqConImport.CloseAndReturnToPage" />"
								/>
								<s:submit method="save" cssClass="picsbutton positive" value="%{getText('button.Upload')}" />
							</div>
						</div>
					</s:form>
				</div>
				<br clear="all" />
			</div>
		</div>
	</div>
</body>