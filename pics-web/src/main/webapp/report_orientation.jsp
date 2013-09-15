<html>
<head>
<title>Site Orientation Report</title>

<script type="text/javascript" src="js/jquery/util/jquery.utils.js?v=${version}"></script>
<script type="text/javascript" src="js/jquery/gritter/jquery.gritter.js?v=${version}"></script>

<script type="text/javascript" src="js/jquery/fancybox/jquery.fancybox-1.3.1.pack.js?v=${version}"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/fancybox/jquery.fancybox-1.3.1.css?v=${version}" />

<link rel="stylesheet" href="js/jquery/jquery-ui/jquery-ui-1.7.2.custom.css?v=${version}">
<link rel="stylesheet" type="text/css" href="js/jquery/gritter/css/gritter.css?v=${version}" />

<script type="text/javascript" src="js/ReportSearch.js?v=${version}"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=${version}" />

<script type="text/javascript" src="js/jquery/jquery.hoverIntent.js?v=${version}"></script>
<script type="text/javascript" src="js/jquery/cluetip/jquery.cluetip.min.js?v=${version}"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/cluetip/jquery.cluetip.css?v=${version}"/>

<script type="text/javascript">
$(document).ready(function() {
	wireClueTips();
	$('.datepicker').datepicker();

	$('#comp').focus(function() {if ($(this).val() == " - Company Name - ") $(this).val(''); });

	$('#emp').focus(function() {if ($(this).val() == " - Employee Name - ") $(this).val(''); });
});

function runSearch(search) {
	var ajax = $(search).find('[name="filter.ajax"]').val();
	if (ajax == "false") {
		$(search).submit();
	} else {
		startThinking({div:'report_data', type: 'large', message: 'finding search results'});

		var data = $(search).serialize();
		$.post('report_orientationAJAX.jsp', data, function(text, status) {
			$('#report_data').html(text);
			wireClueTips();
		});
	}
}

</script>
</head>
<body>
<h1>Site Orientation Report</h1>
<div id="search">
	<form id="form1" method="post" onsubmit="runSearch($('#form1')); return false;">
	<input type="hidden" name="showPage" value="1" />
	<input type="hidden" name="filter.ajax" value="true"/>
	<div>
		<input id="submit" type="submit" value="Search" class="picsbutton positive"/>
	</div>

	<div class="filterOption"><input id="comp" type="text" name="name" value=" - Company Name - " /></div>

	<div class="filterOption"><input id="emp" type="text" name="employee" value=" - Employee Name - " /></div>

	<br clear="all" />
	</form>
</div>

<div id="report_data">
</div>

</body>
</html>