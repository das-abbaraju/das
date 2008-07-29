<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/AutoComplete.js"></script>
<input type="text" id="contractor_select" name="contractor_select" size="60" />
<script type="text/javascript">
new AutoComplete('contractor_select', 'ContractorSelectAjax.action?accountName=', {
	delay: 0.25,
	resultFormat: AutoComplete.Options.RESULT_FORMAT_TEXT
});
</script>
