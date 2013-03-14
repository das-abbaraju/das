<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page language="java" errorPage="/exception_handler.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Cache-Control" content="no-cache" />
		<meta http-equiv="Pragma" content="no-cache" />
		<meta http-equiv="Expires" content="0" />
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/style.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
		
		<script type="text/javascript">
			var questionID  = '<s:property value="questionID"/>';
			var certID = '<s:property value="certID"/>';
			var message = '<s:text name="Audit.message.CertNotSaved" />';
			
			<s:if test="changed">
			window.onload = function (event) {
				if (questionID > 0) { <% // Audit.action %>
					window.opener.setAnswer(questionID, certID);
					window.opener.$('#node_'+questionID).trigger('saveQuestion');
				} else { <% // ConInsureGUARD.action %>
					<s:if test="!duplicate">
						window.opener.location.reload();
					</s:if>
				}
				<s:if test="!duplicate">
					self.close();
				</s:if>
			}
			</s:if>
			
			function closePage() {
				self.close();
			}
		</script>
	</head>
	<body>
		<div id="main">
			<div id="bodyholder">
				<div id="content">
					<h1>
						<s:text name="Audit.header.UploadCertificate" />
						<span class="sub"><s:property value="contractor.name" /></span>
					</h1>
					
					<s:include value="../actionMessages.jsp" />
					
					<div>
						<s:form enctype="multipart/form-data" method="POST">
							<div style="background-color: #F9F9F9;">
								<s:hidden name="id" />
								<s:hidden name="certID" />
								<s:hidden name="caoID" />
								<s:hidden name="questionID" />
								<s:hidden name="auditID" />
								
									<div class="question">
										<label><s:text name="global.File" />:</label>
										<s:file id="fileTextbox" name="file" value="%{file}" size="50" ></s:file>
										<br />
									</div>
								
								<s:if test="file != null && file.exists()">
									<div class="question">
										<a href="CertificateUpload.action?id=<s:property value="id"/>&certID=<s:property value="certID"/>&button=download" target="_BLANK">
											<s:text name="CertificateUpload.OpenExistingFile">
												<s:param><s:text name="Audit.link.OpenExistingFile" /></s:param>
												<s:param><s:property value="fileSize" /></s:param>
											</s:text>
										</a>
									</div>
								</s:if>
								
								<div class="question shaded">
									<label><s:text name="global.Description" />:</label>
									<s:textfield name="fileName" value="%{certificate.description}" size="50"/>
									<br/>
									
									<div align="center" style="font-size: 10px;font-style: italic;">
										<table>
											<tr>
												<td align="left">
													<s:text name="global.Example" />:
												</td>
												<td>
													<s:text name="CertificateUpload.CertificateText" />
												</td>
											</tr>
											<tr>
												<td>
												</td>
												<td align="left">
													<s:text name="CertificateUpload.WorkersComp" />
												</td>
											</tr> 
											<tr>
												<td>
												</td>
												<td align="left">
													<s:text name="CertificateUpload.CertificateGeneric" />
												</td>
											</tr>
										</table>
									</div>
								</div>
								<div>
									<div>
										<button class="picsbutton" onclick="closePage()">
											<s:text name="button.Cancel" />
										</button>
										
										<s:if test="canDelete">
											<button class="picsbutton negative" name="button" value="Delete" type="submit" onclick="return confirm('Are you sure you want to delete this file?');">
												<s:text name="button.DeleteFile" />
											</button>
										</s:if>
										
										<button class="picsbutton positive" name="button" value="Save" type="submit">
											<s:text name="button.Save" />
										</button>
									</div>
								</div>
								
								<s:if test="certificate.caos != null && certificate.caos.size() > 0">
									<div class="alert">
										<s:text name="Audit.message.CertAttachedToPolicy" />
										<s:text name="CertificateUpload.Attached" />
									</div>
								</s:if>
							</div>
                            <div style="text-align:center; font-style:normal; font-weight:normal; font-size:75%;"><s:text name="global.maxFileUploadBytes">
                                <s:param><s:property value="maxFileUploadMBytes" /> </s:param>
                            </s:text></div>
						</s:form>
					</div>
					
					<br clear="all" />
				</div>
			</div>
		</div>
	</body>
</html>