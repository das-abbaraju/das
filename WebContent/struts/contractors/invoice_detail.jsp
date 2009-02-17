<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="contractor.name" /> - Invoice <s:property value="invoice.id" /></title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />

<style type="text/css" media="all">

body {
	font: 10px normal Verdana, Arial, Helvetica, sans-serif;
	}

h2 {
	font: 16px bold "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
	padding: 0;
	margin: 0;
	}

table.allborder {
    border-width: 0 0 1px 1px;
    border-spacing: 0;
    border-collapse: collapse;
	border-color: #333;
    border-style: solid;
	}

table.allborder td {
    margin: 0;
    padding: 4px;
    border-width: 1px 1px 0 0;
    background-color: #fff;
	border-color: #333;
    border-style: solid;
	}
	
table.allborder td.nobottom {
    border-bottom: 1px solid #fff;
    background-color: #fff;
	border-collapse: collapse;
	}

table.noborder {
	border-bottom: 0px;
	}
	
table.noborder td {
	border-bottom: 0px;
	}

table.noborderatall {
	border: 0;
	}
	
table.noborderatall td {
	border: 0;
	}

div {
	padding: 0;
	margin: 0;
	}

</style>
</head>
<body>
<s:include value="conHeader.jsp"></s:include>

<table width="770" border="0" cellspacing="0" cellpadding="4">
  <tr>
    <td>
      <table width="770" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="50%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="110"><img src="pics_logo.png" width="110" height="80" /></td>
                <td>
                <div style="padding:10px;">(PICS) Pacific Industrial Contractor Screening<br />
                P.O. Box 51387<br />
                Irvine, CA 92619-1387</div>
                </td>
              </tr>
            </table>
          </td>
          <td align="right">
            <table width="150" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td align="right"><h2>Invoice</h2></td>
              </tr>
              <tr>
                <td>
                  <table width="150" border="0" cellspacing="0" cellpadding="4" class="allborder">
                    <tr>
                      <td align="center">Date</td>
                      <td align="center">Invoice#</td>
                    </tr>
                    <tr>
                      <td align="center">10/10/2008</td>
                      <td align="center">10183</td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="50%" border="0" cellspacing="0" cellpadding="0" class="allborder" style="margin: 10px 0 0 0;">
        <tr>
          <td>Bill To:</td>
        </tr>
        <tr>
          <td>BP West Coast Products LLC<br />
          Contacts Payable Department<br />
          4519 Grandview Road<br />
          Blaine, WA 98230</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="100%" border="0" cellspacing="0" cellpadding="0" style="margin: 10px 0 0 0;">
        <tr>
          <td align="right">
            <table width="90%" border="0" cellpadding="4" cellspacing="0" class="allborder noborder">
              <tr>
                <td width="20%">
                  <div align="center">P.O. No.</div>
                </td>
                <td width="20%">
                  <div align="center">Terms</div>
                </td>
                <td width="20%">
                  <div align="center">Due Date</div>
                </td>
                <td width="20%">
                  <div align="center">Project</div>
                </td>
                <td width="20%">
                  <div align="center">Payment Method</div>
                </td>
              </tr>
              <tr>
                <td width="20%" class="noborder">
                  <div align="center">Something</div>
                </td>
                <td width="20%" class="noborder">
                  <div align="center">Net 30</div>
                </td>
                <td width="20%" class="noborder">
                  <div align="center">11/9/2008</div>
                </td>
                <td width="20%" class="noborder">
                  <div align="center">Something</div>
                </td>
                <td width="20%" class="noborder">
                  <div align="center">Credit Card?</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td>
            <table width="100%" border="0" cellpadding="4" cellspacing="0" class="allborder">
              <tr>
                <td width="20%">
                  <div align="center">Item</div>
                </td>
                <td width="20%">
                  <div align="center">Description</div>
                </td>
                <td width="20%">
                  <div align="center">Qty</div>
                </td>
                <td width="20%">
                  <div align="center">Rate</div>
                </td>
                <td width="20%">
                  <div align="center">Amount</div>
                </td>
              </tr>
              <tr>
                <td width="20%" class="nobottom">line item</td>
                <td width="20%" class="nobottom">desc</td>
                <td width="20%" class="nobottom">100</td>
                <td width="20%" class="nobottom">$75</td>
                <td width="20%" class="nobottom">1</td>
              </tr>
              <tr>
                <td width="20%" class="nobottom">line item</td>
                <td width="20%" class="nobottom">desc</td>
                <td width="20%" class="nobottom">100</td>
                <td width="20%" class="nobottom">$75</td>
                <td width="20%" class="nobottom">1</td>
              </tr>
              <tr>
                <td width="20%" class="nobottom">line item</td>
                <td width="20%" class="nobottom">desc</td>
                <td width="20%" class="nobottom">100</td>
                <td width="20%" class="nobottom">$75</td>
                <td width="20%" class="nobottom">1</td>
              </tr>
              <tr>
                <td width="20%" class="nobottom">line item</td>
                <td width="20%" class="nobottom">desc</td>
                <td width="20%" class="nobottom">100</td>
                <td width="20%" class="nobottom">$75</td>
                <td width="20%" class="nobottom">1</td>
              </tr>
              <tr>
                <td width="20%" class="nobottom">line item</td>
                <td width="20%" class="nobottom">desc</td>
                <td width="20%" class="nobottom">100</td>
                <td width="20%" class="nobottom">$75</td>
                <td width="20%" class="nobottom">1</td>
              </tr>
              <tr>
                <td width="20%" class="nobottom">line item</td>
                <td width="20%" class="nobottom">desc</td>
                <td width="20%" class="nobottom">100</td>
                <td width="20%" class="nobottom">$75</td>
                <td width="20%" class="nobottom">1</td>
              </tr>
              <tr>
                <td width="20%" class="nobottom">line item</td>
                <td width="20%" class="nobottom">desc</td>
                <td width="20%" class="nobottom">100</td>
                <td width="20%" class="nobottom">$75</td>
                <td width="20%" class="nobottom">1</td>
              </tr>
              <tr>
                <td width="20%" class="nobottom">line item</td>
                <td width="20%" class="nobottom">desc</td>
                <td width="20%" class="nobottom">100</td>
                <td width="20%" class="nobottom">$75</td>
                <td width="20%" class="nobottom">1</td>
              </tr>
              <tr>
                <td width="20%" class="nobottom">line item</td>
                <td width="20%" class="nobottom">desc</td>
                <td width="20%" class="nobottom">100</td>
                <td width="20%" class="nobottom">$75</td>
                <td width="20%" class="nobottom">1</td>
              </tr>
              <tr>
                <td width="20%" class="nobottom">line item</td>
                <td width="20%" class="nobottom">desc</td>
                <td width="20%" class="nobottom">100</td>
                <td width="20%" class="nobottom">$75</td>
                <td width="20%" class="nobottom">1</td>
              </tr>
              <tr>
                <td width="20%" class="nobottom">line item</td>
                <td width="20%" class="nobottom">desc</td>
                <td width="20%" class="nobottom">100</td>
                <td width="20%" class="nobottom">$75</td>
                <td width="20%" class="nobottom">1</td>
              </tr>
            </table>
            
            <table width="100%" border="0" cellpadding="0" cellspacing="0" class="">
  <tr>
    <td width="60" style="border-right: 1px solid #333;border-top: 1px solid #333;">Thank you for your business!</td>
    <td width="40%" style="border-bottom: 1px solid #333; border-right: 1px solid #333;border-top: 1px solid #333;">
      <table width="100%" border="0" cellpadding="2" cellspacing="0" class="">
        <tr>
          <td width="50%">
            <h2>Total</h2>
          </td>
          <td align="right">$3,596.00</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width="60" style="border-right: 1px solid #333;">&nbsp;</td>
    <td width="33%" style="border-bottom: 1px solid #333; border-right: 1px solid #333;">
      <table width="100%" border="0" cellpadding="2" cellspacing="0" class="noborderatall">
        <tr>
          <td width="50%">
            <h2>Payments/Credits</h2>
          </td>
          <td align="right">$3,596.00</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width="60" style="border-right: 1px solid #333;">&nbsp;</td>
    <td width="33%" style="border-bottom: 1px solid #333; border-right: 1px solid #333;">
      <table width="100%" border="0" cellpadding="2" cellspacing="0" class="noborderatall">
        <tr>
          <td width="50%">
            <h2>Balance Due</h2>
          </td>
          <td align="right">$3,596.00</td>
        </tr>
      </table>
    </td>
  </tr>
</table>

            
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="100%" border="0" cellpadding="4" cellspacing="0" class="allborder" style="margin: 10px 0 0 0;">
        <tr>
          <td>
            Comments:</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
        </tr>
      </table>
    </td>
  </tr>
    <tr>
    <td>
      <table width="100%" border="0" cellpadding="4" cellspacing="0" class="allborder" style="margin: 10px 0 0 0;">
        <tr>
          <td>
            <div align="center">Phone#</div>
          </td>
          <td>
            <div align="center">Fax#</div>
          </td>
          <td>
            <div align="center">E-mail</div>
          </td>
          <td>
            <div align="center">Website</div>
          </td>
        </tr>
        <tr>
          <td>
            <div align="center">949-387-1940</div>
          </td>
          <td>
            <div align="center">949-269-9146</div>
          </td>
          <td>
            <div align="center">billing@picsauditing.com</div>
          </td>
          <td>
            <div align="center">www.picsauditing.com</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<table>
	<tr>
		<td>
		<fieldset class="form">
		<legend><span>PICS Information</span></legend>
		<ol>
			<li><label>Name:</label> PICS</li>
			<li><label>Address:</label> 17701 Cowan St. Ste 140</li>
			<li><label>City:</label>Irvine</li>
			<li><label>State:</label>CA</li>
			<li><label>Zip:</label>92614</li>
			</li>
		</ol>
		</fieldset>
		</td>
		<td>
		<fieldset class="form"><legend><span>Billing Information</span></legend>
		<ol>
			<li><label>Name: </label><a href="ContractorView.action?id=<s:property value="contractor.id"/>"><s:property value="contractor.name" /></a>
			<li><label>Address:</label> <s:property value="contractor.address" /></li>
			<li><label>City:</label><s:property value="contractor.city" /></li>
			<li><label>State:</label><s:property value="account.state" /></li> 
			<li><label>Zip:</label><s:property	value="account.zip" /></li>
			</li>
		</ol>
		</fieldset>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<s:form id="save" method="POST" enctype="multipart/form-data">
				<s:hidden name="id" />
				<fieldset class="form"><legend><span>Invoice</span></legend>
				<ol>
					<li>
						<label>Invoice #</label><s:property value="invoice.id" />
					</li>
					<li><label>Invoice Items:</label><br/>
						<s:iterator value="invoice.items">
							<br/><s:property value="invoiceFee.fee"/> $<s:property value="invoiceFee.amount"/> USD
						</s:iterator>
					</li>
					<li>
						<label>Invoice Total:</label> $<s:property value="invoice.totalAmount" /> USD
					</li>
				</ol>
				<s:if test="permissions.admin && contractor.paymentMethodStatus != 'Missing' && !invoice.paid">
					<div class="buttons">
						<input type="submit" class="positive" name="button" value="Charge" />
					</div>
				</s:if>
				</fieldset>
			</s:form>
		</td>
	</tr>
</table>
</body>
</html>
