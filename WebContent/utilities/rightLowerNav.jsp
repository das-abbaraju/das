<script language="JavaScript">
  var j=parseInt(Math.random()*5);
  j=(isNaN(j))?1:j+1;
  document.write("<img useMap='#Map' border='0' hspace='1' src= 'images/squareLogin_" + j + ".gif'>");
</script>
<map name="Map">
  <area shape="rect" coords="73,4,142,70" href="logout.jsp">
</map>