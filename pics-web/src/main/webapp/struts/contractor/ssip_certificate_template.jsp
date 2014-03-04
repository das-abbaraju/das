<%@ page import="com.picsauditing.PICS.DateBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>SSIP Certificate</title>

        <style type="text/css">
            @page {
                size: landscape;
                margin: 1cm;
            }

            body {
                color: #454545;
                font-family: Arial, "Helvetica Neue", Helvetica, sans-serif;
                font-weight: bold;
                text-align: center;
            }

            p {
                line-height: 1.3;
            }

            dt, dd {
                display: inline;
            }

            dt {
                margin-right: 5px;
            }

            dd {
                color: #73bbe8;
                font-size: 28px;
                margin: 0 30px 0 0;
            }

            .pics-logo {
                padding-bottom: 10px;
            }

            .certificate-awarded {
                color: #012142;
                font-size: 20px;
                font-weight: bold;
            }

            .contractor-name {
                color: #A84D10;
                display: inline-block;
                font-size: 38px;
                font-weight: bold;
                margin: 10px 0;
            }

            .by-pics-auditing {
                display: inline-block;
                margin: 10px 0;
            }

            .check-validity {
                font-size: 12px;
                font-weight: normal;
            }

            .safety-schemes {
                color: #cccccc;
            }
        </style>

	</head>

	<body>
	<!-- 
		<img src="/struts/contractor/pics-logo.png" class="pics-logo" alt="PICS" />
		-->
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
			<!--
			<dd><img src="/struts/contractor/signature.jpg" alt="signature" /></dd>
			-->
		</dl>

		<p class="check-validity">To Check the Validity of this Certificate please contact PICS direct: +44 0 808 234 0862 / http://www.picsauditing.com/uk/contact-3/ </p>
		<!--
		<img src="/struts/contractor/ssip.jpg" alt="ssip" />
-->
		<p class="safety-schemes">The Safety Schemes in Procurement Competence Forum (SSiP) "acts as an umbrella organisation to facilitate mutual recognition between health and safety pre-qualification schemes wherever it is practicable to do so"</p>
	</body>
</html>
