<%@page import="com.picsauditing.PICS.BillContractor"%>
<%
com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();
%>
<table border="0" cellspacing="1" cellpadding="1">
	<tr class="whiteTitle">
		<td colspan="3" bgcolor="#993300" align="center">PICS Annual Membership Price</td>
	</tr>
	<tr class="whiteTitle">
		<td bgcolor="#993300" align="center">Facilities</td>
		<td bgcolor="#003366" align="center">Audited</td>
		<td bgcolor="#003366" align="center"><nobr>PQF-Only</nobr></td>
	</tr>
	<tr class=blueMain <%=color.nextBgColor()%>>
		<td class="center">1</td>
		<td class="money">$<%=BillContractor.calculatePriceByFacilityCount(1)%></td>
		<td class="money">$99</td>
	</tr>
	<tr class=blueMain <%=color.nextBgColor()%>>
		<td class="center">2-4</td>
		<td class="money">$<%=BillContractor.calculatePriceByFacilityCount(4)%></td>
		<td class="money">$99</td>
	</tr>
	<tr class=blueMain <%=color.nextBgColor()%>>
		<td class="center">5-8</td>
		<td class="money">$<%=BillContractor.calculatePriceByFacilityCount(8)%></td>
		<td class="money">$99</td>
	</tr>
	<tr class=blueMain <%=color.nextBgColor()%>>
		<td class="center">9-12</td>
		<td class="money">$<%=BillContractor.calculatePriceByFacilityCount(12)%></td>
		<td class="money">$99</td>
	</tr>
	<tr class=blueMain <%=color.nextBgColor()%>>
		<td class="center">13-19</td>
		<td class="money">$<%=BillContractor.calculatePriceByFacilityCount(19)%></td>
		<td class="money">$99</td>
	</tr>
	<tr class=blueMain <%=color.nextBgColor()%>>
		<td class="center">20+</td>
		<td class="money">$<%=BillContractor.calculatePriceByFacilityCount(20)%></td>
		<td class="money">$99</td>
	</tr>
</table>
<p class="redMain">* There is an account activation fee of $99 for new account registration, and $199 for reactivation</p>

