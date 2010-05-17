/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
**/

/** Update the requiresOQ for all contractors
 * we don't want to run this yet 
update accounts set requiresOQ = 1
where id in (select distinct conid from contractor_audit ca
join pqfdata pd on ca.id = pd.auditid
where pd.questionid = 894
and pd.answer = 'Yes');
**/

update email_template 
set subject = 'PICS Invoice ${invoice.id}.#if(${invoice.ccRebill}) Your credit card ending in ${contractor.ccNumber} will be charged on ${invoice.dueDateF}#end'
where id = 45;

update email_template
set body = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>PICS - Invoice</title>
</head>
<body>
<style type="text/css">
body {
	color: #4C4D4D;
	background-color: white;
	margin: 0 auto;
	padding: 20px;
	line-height: 24px;
	font-family: Helvetica, Arial, sans-serif;
	font-size: 14px;
}
table td, table th {
	vertical-align: middle;
	font-size: 14px;
	line-height: 1.5;
}
table.allborder {
	border-width: 1px;
	border-collapse: collapse;
	border-color: #333;
	border-style: solid;
}
table.allborder td, table.allborder th {
	font-family: "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
	border-width: 1px;
	border-color: #444;
	border-style: solid;
	margin: 0;
	padding: 6px;
}
table.allborder th {
	text-align: center;
	color: #444;
	padding: 10px;
	font-weight: normal;
}
table.allborder th.big, table.allborder td.big {
	font-size: 16px;
	font-weight: bold;
}
table.allborder th.center, table.allborder td.center {
	text-align: center;
}
table.allborder th.right, table.allborder td.right {
	text-align: right;
	float: none;
}
table {
	border-collapse: separate;
}
a img {
	border: 0px none;
}
p {
	padding-top: 1px;
	padding-bottom: 5px;
}
div#alert {
	background:#FBF2C9 url(http://www.picsorganizer.com/images/icon-warning.gif) no-repeat scroll 10px 10px;
	border-bottom:2px solid #D0AE10;
	border-top:2px solid #D0AE10;
	margin-bottom:10px;
	margin-top:10px;
	padding:1em 1em 1em 5em;
	width:86%;
}
div#info {
	background:#DBE7F8 url(http://www.picsorganizer.com/images/icon-info.gif) no-repeat scroll 10px 10px;
	border-bottom:2px solid #B7D2F2;
	border-top:2px solid #B7D2F2;
	margin-bottom:10px;
	margin-top:10px;
	padding:1em 1em 1em 5em;
	width:86%;
}
#name {
	margin-right: 5px;
	font-size: 10px;
}
</style>
#if($invoice.status.void)
      <div id="alert">This invoice has been CANCELED.</div>
#elseif($invoice.status.paid)
     <div id="info">This invoice is PAID. Please keep this receipt for your records.</div>
#elseif($invoice.overdue && ${contractor.activeB})
    <div id="alert">This invoice is currently OVERDUE!</div>	
#end
#if($invoice.status.unpaid && ${contractor.paymentMethod.creditCard})
    #if(!${contractor.ccValid})
        <div id="alert">The Credit Card on file has Expired. Please login to your account and add a valid payment method.</div>	
    #else
        <div id="alert">We currently have a ${contractor.creditCard.cardType} ending in ${contractor.creditCard.lastFour} on file and it will be automatically charged on $!{pics_dateTool.format("MMM d, yyyy", $invoice.dueDate)}</div>
    #end
#end
      <table width="100%" height="100%">
        <tbody>
        
        <tr>
          <td><table width="100%">
              <tbody>
                <tr>
                  <td width="146"><img src="http://www.picsorganizer.com/images/logo.gif" height="146" width="146"></td>
                  <td style="padding: 10px;">PICS <br>
                    P.O. Box 51387 <br>
                    Irvine, CA 92619-1387<br>
                    TIN: 26-3635236</td>
                  <td width="200"><table class="allborder" border="0" cellpadding="4" cellspacing="0" width="100%">
                      <tbody>
                        <tr>
                          <th>Date</th>
                          <th class="big">Invoice #</th>
                        </tr>
                        <tr>
                          <td class="center">$!{pics_dateTool.format("MMM d, yyyy", $invoice.creationDate)}</td>
                          <td class="center"><a href="http://www.picsorganizer.com/InvoiceDetail.action?invoice.id=${invoice.id}">${invoice.id}</a></td>
                        </tr>
                      </tbody>
                    </table></td>
                </tr>
              </tbody>
            </table></td>
        </tr>
#if(!$invoice.status.paid)
        <tr><td class="center">
          <a href="http://www.picsorganizer.com/InvoiceDetail.action?invoice.id=${invoice.id}">Click to Pay Invoice Online</a>
        </td></tr>
#end
        <tr>
          <td style="padding-top: 15px;"><table class="allborder" width="100%">
              <tbody>
                <tr>
                  <th>Bill To</th>
                  <th width="16%">PO #</th>
                  <th width="16%">Due Date</th>
                </tr>
                <tr>
                  <td>${contractor.name}<br>
                    c/o ${billingUser.name}<br>
                    #if($contractor.billingAddress.length() > 0 )
                      ${contractor.billingAddress}<br>
                      ${contractor.billingCity}, ${contractor.billingState} ${contractor.billingZip}<br>
                    #else
                      ${contractor.address}<br>
                      ${contractor.city}, ${contractor.state} ${contractor.zip}
                    #end
                  </td>
                  <td>$!invoice.poNumber</td>
                  <td class="center">$!{pics_dateTool.format("MMM d, yyyy", $invoice.dueDate)}</td>
                </tr>
              </tbody>
            </table></td>
        </tr>
        <tr>
          <td style="padding-top: 15px;"><table class="allborder" width="100%">
              <tbody>
                   <tr>
                      <th colspan="2">Item &amp; Description</th>
                      <th width="200">Fee Amount</th>
                   </tr>
                  #foreach( $itemOne in $invoice.items )
                  <tr>
                    <td style="border-right: 0pt none;">$!{itemOne.invoiceFee.fee}</td>
                    <td style="border-left: 0pt none;">$!{itemOne.description}</td>
                    <td class="right">$${itemOne.amount} USD</td>
                  </tr>
                  #end
                  <tr>
                    <th colspan="2" class="big right">Invoice Total</th>
                    <td class="big right">$${invoice.totalAmount} USD</td>
                  </tr>
                  #foreach( $paymentOne in $invoice.payments )
		  <tr>
                    <th colspan="2" rowspan="2" class="big right">Payment</th>
                    <td class="big right">$${paymentOne.amount} USD</td>
                  </tr>
		  <tr>
                    <td>
                      Date/Time: ${paymentOne.payment.creationDate}<br />
                      #if($paymentOne.payment.paymentMethod.creditCard)
                        Transaction ID: $!{paymentOne.payment.transactionID}<br />
                        CC Number: $!{paymentOne.payment.ccNumber}
                      #else
                        Check: $!{paymentOne.payment.checkNumber}
                      #end
                    </td>
                  </tr>
		  #end
                  <tr>
                    <th colspan="2" class="big right">Balance</th>
                    <td class="big right">$${invoice.balance} USD</td>
                  </tr>
              </tbody>
            </table></td>
        </tr>
        <tr>
          <td style="padding: 15px;"> Comments: <br/>
            $!{invoice.notes} <br/>
            To view this invoice online, please go to: <a href="http://www.picsorganizer.com/InvoiceDetail.action?invoice.id=${invoice.id}" >http://www.picsorganizer.com/InvoiceDetail.action?invoice.id=${invoice.id}</a>
            <br/>
          </td>
        </tr>
        <tr>
          <td><table class="allborder" width="100%">
              <tbody>
                <tr>
                  <th width="25%">Phone#</th>
                  <th width="25%">Fax#</th>
                  <th width="25%">Email</th>
                  <th width="25%">Website</th>
                </tr>
                <tr>
                  <td class="center">(800) 506-PICS (7427)</td>
                  <td class="center">(949) 269-9146</td>
                  <td class="center">billing@picsauditing.com</td>
                  <td class="center">www.picsauditing.com</td>
                </tr>
              </tbody>
            </table></td>
        </tr>
        </tbody>
      </table>
</body>
</html>'
where id = 45;

update email_template set allowsVelocity = 1, html = 1, recipient = 'Admin' where id = 107;
insert into `app_properties` (`property`, `value`) values('subscription.ContractorAdded','1');
