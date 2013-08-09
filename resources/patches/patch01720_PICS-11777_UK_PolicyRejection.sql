UPDATE
  email_template t
SET t.html=1, t.subject='${contractor.name} Insurance Policies Must be Re-Submitted',t.body='<!DOCTYPE html>
<html>
 <head>
  <meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
  <meta content="width=device-width, initial-scale=1.0" name="viewport" />
  <title>PICS Organizer</title>
  <style media="screen" type="text/css">
.ReadMsgBody {width:100%;}
 .ExternalClass {width:100%;}
 .ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div {line-height:1.5;}
 img {outline:none; text-decoration:none; -ms-interpolation-mode: bicubic;}
 table td {border-collapse: collapse;}
 h1 {font-size:24px;color:#a84d0f !important;}
 a {color:#002441 !important;text-decoration:none;}
 .content p {font-size:15px;color:#333333;}
 .signature p {font-size:13px;color:#333333;}
 @media only screen and (orientation:portrait) and (max-width:480px) {
  table[class=container] {width:auto !important;}
 }  </style>
 </head>
 <body style="background:#002441;width:100% !important;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;margin:0;padding:0;">
  <table bgcolor="#002441" border="0" cellpadding="0" cellspacing="0" style="margin:0;padding:0;width:100% !important;line-height:1.5 !important;font-family:Helvetica,Arial,sans-serif;background:#002441;border-collapse:collapse;mso-table-lspace:0pt;mso-table-rspace:0pt;" width="100%">
   <tbody>
    <tr>
     <td style="padding:40px 10px;">
      <table align="center" bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" class="container" style="background:#ffffff" width="600">
       <tbody>
        <tr>
         <td bgcolor="#f1f2f2" style="padding:24px 30px 16px;background:#f1f2f2">
          <a href="http://www.picsauditing.com"><img align="none" alt="" border="0" height="55" hspace="0" src="https://df6357ff93-custmedia.vresp.com/8c91bda7a3/PICS-FINAL-LOGO.gif" style="width: 160px; height: 55px;" title="" vspace="0" width="160" /></a></td>
        </tr>
        <tr>
         <td bgcolor="#ffffff" class="content" style="padding:30px 30px 0;background:#ffffff;font-size:15px;color:#333333;">
          <span style="color: rgb(51, 51, 51); font-size: 15px; line-height: 1.5;">Hi <ContactName>,</span><br />
          <br />
          Please re-submit the ${contractor.name} insurance policies to PICS.&nbsp;<br />

#foreach ( $audit in $auditList )
          <div style="text-align: center;">
           <br />
           <a href="https://www.picsorganizer.com/Audit.action?auditID=$audit.id">$audit.auditType.name</a></div>
          <p>
           The PICS system did not accept the policy submitted, due to:<br />
#foreach ( $caow in $caowList)
#if ( $caow.cao.audit.auditType.id == $audit.auditType.id )
* $caow.notes<br />
#end
#end
#end
<br />
           Please correct the issue and re-upload any insurance certificate to your PICS account. If you have questions, or need assistance, please contact me directly at the information listed in my signature below.<br />
           <br />
           As a reminder, when you renew any policy, please be sure to upload the associated new certificate, to keep your PICS information up to date.<br />
           <br />
           Thank you,&nbsp;<br />
           &nbsp;</p>
         </td>
        </tr>
        <tr>
         <td bgcolor="#ffffff" class="signature" style="padding:0 30px 30px;background:#ffffff;font-size:13px;color:#333333;">
          <p>
           <strong><span style="line-height: 1.5;">${contractor.currentCsr.name}</span></strong></p>
          <p>
           PICS Auditing, LLC</p>
          <p style="font-weight:bold;color:#002441;">
           ${contractor.currentCsr.phone}<br />
           <a href="mailto:Info@PICSauditing.com" style="color:#002441 !important;text-decoration:none;">${contractor.currentCsr.email}</a></p>
         </td>
        </tr>
       </tbody>
      </table>
     </td>
    </tr>
   </tbody>
  </table>
  <br />
 </body>
</html>'
WHERE t.id=132