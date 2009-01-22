<%@page import="com.picsauditing.PICS.BillContractor"%>
<h2>PICS Annual Membership Price</h2>
<table class="report">
	<thead>
		<tr>
			<td>Facilities</td>
			<td>Audited</td>
			<td>PQF-Only</td>
		</tr>	
	</thead>
	<tr>
		<td>1</td>
		<td>$<%=BillContractor.price1Op%></td>
		<td>$99</td>
	</tr>
	<tr>
		<td>2-4</td>
		<td>$<%=BillContractor.price2Ops%></td>
		<td>$99</td>
	</tr>
	<tr>
		<td>5-8</td>
		<td>$<%=BillContractor.price5Ops%></td>
		<td>$99</td>
	</tr>
	<tr>
		<td>9-12</td>
		<td>$<%=BillContractor.price9Ops%></td>
		<td>$99</td>
	</tr>
	<tr>
		<td>13-19</td>
		<td>$<%=BillContractor.price13Ops%></td>
		<td>$99</td>
	</tr>
	<tr>
		<td>20+</td>
		<td>$<%=BillContractor.priceFull%></td>
		<td>$99</td>
	</tr>
</table>

<p class="redMain">* There is an account activation fee of $99 for new account registration, and $199 for reactivation</p>
