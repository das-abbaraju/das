$.fn.timepicker = function() {
	function parse(time) {
		var t = time.split(':');
		var hour = t[0];
		var minute = t[1].split(' ')[0];
		var a = t[1].split(' ')[1];
		return {hour:hour, minute:minute, a:a};
	}
	var timepicker=$(this);
	var hid = timepicker.attr('name') + '_hour';
	var mid = timepicker.attr('name') + '_min';
	var aid = timepicker.attr('name') + '_a';

	var h = $('<select id=' + hid + '>');
	for (var i=1; i<=12; i++)
		h.append('<option>' + i + '</option>');

	var m = $('<select id=' + mid + '>');
	$.each(['00','15','30','45'], function(k, v) {m.append('<option>' + v + '</option>');});

	var a = $('<select id=' + aid + '>');
	$.each(['AM','PM'], function(k, v) {a.append('<option>' + v + '</option>');});
	
	var time = parse(timepicker.val());

	$(h).val(time.hour).change(function() {timepicker.val(h.val()+':'+ m.val()+' '+a.val());});
	$(m).val(time.minute).change(function() {timepicker.val(h.val()+':'+ m.val()+' '+a.val());});
	$(a).val(time.a).change(function() {timepicker.val(h.val()+':'+ m.val()+' '+a.val());});
	
	var s = $('<span>').append(h).append(m).append(a);
	
	timepicker.after(s).hide();
}