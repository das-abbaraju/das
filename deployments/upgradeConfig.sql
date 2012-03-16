-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- changing app_translation msgKeys
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgrade.sql FOR NON-CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-4961
update app_translation t set t.msgValue = 'By checking this box, I understand that if I need to reschedule this audit,<br />I must do so before {0} or I will be subject to a {2}{1,number,#,###.00} rescheduling fee.'
where t.msgKey = 'ScheduleAudit.message.ConfirmMessage' and t.locale = 'en';
update app_translation t set t.msgValue = 'You have an <a href="https://www.picsorganizer.com/InvoiceDetail.action?invoice.id={0,number,#}">invoice of <b>{3}{1,number,#,##0.00}</b></a> due {2,date}'
where t.msgKey = 'ContractorWidget.message.OpenInvoiceReminder' and t.locale = 'en';
update app_translation t set t.msgValue = 'You have an invoice of <b>{3}{1,number,#,##0.00}</b> due {2,date}'
where t.msgKey = 'ContractorWidget.message.OpenInvoiceReminder.IsAdmin' and t.locale = 'en';
update app_translation t set t.msgValue = 'This audit is scheduled to be conducted within 48 hours. If the scheduled date is changed, the contractor will be charged a {1}{0,number,#,###.00} rescheduling fee.<br />'
where t.msgKey = 'ScheduleAudit.message.ReschedulingWarning' and t.locale = 'en';

update app_translation t set t.msgValue = 'En cochant cette case, je comprends que si j''ai besoin de remettre cet audit, <br> je dois le faire avant {0} ou je serai soumis à des frais de modification de {1,number,#,###.00} {2}.'
where t.msgKey = 'ScheduleAudit.message.ConfirmMessage' and t.locale = 'fr';
update app_translation t set t.msgValue = 'Vous avez une <a href="https://www.picsorganizer.com/InvoiceDetail.action?invoice.id={0,number,#}">facture de <b>{1,number,#,###.00} {3}</b></a> à payer d''ici le {2,date}'
where t.msgKey = 'ContractorWidget.message.OpenInvoiceReminder' and t.locale = 'fr';
update app_translation t set t.msgValue = 'Vous avez une facture de <b>{1,number,#,##0.00} {3}</b> à payer d''ici le {2,date}'
where t.msgKey = 'ContractorWidget.message.OpenInvoiceReminder.IsAdmin' and t.locale = 'fr';
update app_translation t set t.msgValue = 'Cet audit devrait être effectué dans les 48 heures. Si la date prévue est modifiée, des frais de modification de {0,number,#,###.00} {1} seront facturés à l''entrepreneur.<br />'
where t.msgKey = 'ScheduleAudit.message.ReschedulingWarning' and t.locale = 'fr';
--

-- PICS-4825
insert into app_translation(msgKey, msgValue, locale, createdBy, creationDate, applicable, qualityRating, contentDriven)
select concat('AuditQuestion.', id, '.title'), title, 'en', 23157, now(), 1, 2, 1
from audit_question
where length(title) > 0;

-- PICS-4826
insert into app_translation(msgKey, msgValue, locale, createdBy, creationDate, applicable, qualityRating, contentDriven)
select concat('AuditCategory.', id, '.helpText'), helpText, 'en', 23157, now(), 1, 2, 1
from audit_category
where length(helpText) > 0;

-- PICS-3967
-- Note this needs code updates as well
update audit_type set classType='Employee' where id in (17, 29, 99, 100);

-- PICS-4600
update app_translation t set t.msgValue = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
    width:86";
}
div#info {
    background:#DBE7F8 url(http://www.picsorganizer.com/images/icon-info.gif) no-repeat scroll 10px 10px;
    border-bottom:2px solid #B7D2F2;
    border-top:2px solid #B7D2F2;
    margin-bottom:10px;
    margin-top:10px;
    padding:1em 1em 1em 5em;
    width:86";
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
#if($invoice.status.unpaid && !${contractor.ccExpired})
    <div id="alert">The Credit Card on file has Expired. Please login to your account and add a valid payment method.</div>    
#elseif(${contractor.status.active} && ${contractor.ccValid} && ${contractor.paymentMethod} == "CreditCard" && $invoice.status.unpaid && ${contractor.ccExpired})
    <div id="alert">We currently have a ${contractor.creditCard.cardType} ending in ${contractor.creditCard.cardNumber} on file and it will be automatically charged on ${pics_dateTool.format("MMM d, yyyy", $invoice.dueDate)}</div>
#end
      <table width="100%" height="100%">
        <tbody>
        
        <tr>
          <td><table width="100%">
              <tbody>
                <tr>
                  <td width="146" height="146"><img src="http://www.picsorganizer.com/images/PICSLogo.png" alt="PICS Logo" /></td>
                  <td style="padding: 10px;">
		  #if($invoice.currency.CAD)
                    $i18nCache.getText("global.PICSCanadaMailingAddress",$locale)
                  #elseif($invoice.currency.GBP)
                    $i18nCache.getText("global.PICSUnitedKingdomMailingAddress",$locale)
                  #else
                    $i18nCache.getText("global.PICSUnitedStatesMailingAddress",$locale)
                  #end
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
#if(${invoice.status.unpaid})
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
                    <td style="border-left: 0pt none;">
                      $!{itemOne.description}
			#if($!{itemOne.paymentExpires})
				<span style="color: #444; font-style: italic; font-size: 10px;">
					#if(${itemOne.invoiceFee.membership})
						expires
					#else
						effective
					#end
					${pics_dateTool.format("MM/dd/yyyy hh:mm a", ${itemOne.paymentExpires})}
				</span>
			#end
                    </td>
                    <td class="right">${invoice.currency.symbol} ${itemOne.amount} ${invoice.currency}</td>
                  </tr>
                  #end
                  <tr>
                    <th colspan="2" class="big right">Invoice Total</th>
                    <td class="big right">${invoice.currency.symbol} ${invoice.totalAmount} ${invoice.currency}</td>
                  </tr>
                  #foreach( $paymentOne in $invoice.payments )
          <tr>
                    <th colspan="2" rowspan="2" class="big right">Payment</th>
                    <td class="big right">${invoice.currency.symbol} ${paymentOne.amount} ${invoice.currency}</td>
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
                    <td class="big right">${invoice.currency.symbol} ${invoice.balance} ${invoice.currency}</td>
                  </tr>
              </tbody>
            </table></td>
        </tr>
        #if($invoice.currency.GBP)
          <tr>
            <td style="padding: 15px;">
              $i18nCache.getText("global.UKRegisteredOffice",$locale)
            </td>
          </tr>
        #end
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
				  #if(!$invoice.currency.GBP)
                  <th width="25%">Fax#</th>
				  #end
                  <th width="25%">Email</th>
                  <th width="25%">Website</th>
                </tr>
                <tr>
                  <td class="center">
					#if($invoice.currency.CAD)
						$i18nCache.getText("global.PicsCanadaBillingPhone",$locale)
					#elseif($invoice.currency.GBP)
						$i18nCache.getText("global.PicsGreatBritainBillingPhone",$locale)
					#else
						$i18nCache.getText("PicsBillingPhone",$locale)
					#end
				  </td>
				  #if(!$invoice.currency.GBP)
                  <td class="center">$i18nCache.getText("PicsBillingFax",$locale)</td>
				  #end
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
where t.msgKey = 'EmailTemplate.45.translatedBody' and t.locale = 'en';
--

-- PICS-4209
-- GC Free
insert into widget_user (widgetID, userID, expanded, `column`, sortOrder)
select wu.widgetID, 61460, wu.expanded, wu.column, wu.sortOrder
from widget_user wu
join widget w on w.widgetID = wu.widgetID
join users u on u.id = wu.userID
where u.id = 616 -- regular operator
and w.caption not like '%flag%'
;

-- GC Full
insert into widget_user (widgetID, userID, expanded, `column`, sortOrder)
select wu.widgetID, 61461, wu.expanded, wu.column, wu.sortOrder
from widget_user wu
join widget w on w.widgetID = wu.widgetID
join users u on u.id = wu.userID
where u.id = 616 -- regular operator
and w.caption not like '%flag%'
;

-- GC Full Subcontractors Flag Matrix
insert into widget(caption, widgetType, url)
values ("Subcontractor Flag Matrix", "Html", "SubcontractorsFlagMatrix.action");

insert into widget_user (widgetID, userID, `column`, sortOrder)
values (36, 61461, 2, 30);
