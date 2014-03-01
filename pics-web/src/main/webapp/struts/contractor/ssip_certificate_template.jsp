<%@ page import="com.picsauditing.PICS.DateBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
	<head>
		<title>SSIP Certificate</title>
		<link href="/struts/contractor/ssip-certificate-styles.css" type="text/css" rel="stylesheet" />
	</head>

	<body>
		<img src="/struts/contractor/pics-logo.png" class="pics-logo" alt="PICS" />

		<p>
			<span class="certificate-awarded">This Certificate is Awarded to:</span><br />
			<span class="contractor-name">${contractorCertificate.contractor.name}</span><br />
			<span class="by-pics-auditing">
				by PICS Auditing<br />
				in Accordance with SSiP Forum Deemed to Satisfy Agreement<br />
			</span>
		</p>

		<dl>
			<dt>Certificate Issued:</dt>
			<dd>${contractorCertificate.issueDateString}</dd>

			<dt>Expiry Date:</dt>
            <dd>${contractorCertificate.expirationDateString}</dd>

            <dt>Certificate Number:</dt>
			<dd>${contractorCertificate.id}</dd>
		</dl>

		<dl>
			<dt>Authorised By:</dt>
			<dd><img src="/struts/contractor/signature.jpg" alt="signature" /></dd>
		</dl>

		<p class="check-validity">To Check the Validity of this Certificate please contact PICS direct: +44 0 808 234 0862 / http://www.picsauditing.com/uk/contact-3/</p>

		<img src="/struts/contractor/ssip.jpg" alt="ssip" />

		<p class="safety-schemes">The Safety Schemes in Procurement Competence Forum (SSiP) "acts as an umbrella organisation to facilitate mutual recognition between health and safety pre-qualification schemes wherever it is practicable to do so"</p>
	</body>
</html>
