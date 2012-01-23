(function ($) {
    PICS.define('translation.Trace', {
        methods: {
            init: function () {
            	$('#tracing_open').bind('click', this.loadIframe);
            },
            
            loadIframe: function (event) {
            	event.preventDefault();
            	var ajaxUrl = $(this).attr('data-url');
            	var href = window.location.href;
            	var translationUrl = $(this).attr('href');
            
            	PICS.ajax({
            		url: ajaxUrl,
            		success: function() {
            			$('body').append('<script type="text/javascript">var tracer = new PICS.getClass("translation.Trace");</script>' 
            					+ '<iframe src="' + href + '" id="translationTracingFrame" onload="tracer.openTracer(\''
            					+ translationUrl + '\')" style="display: none"></iframe>');
            		}
            	});
            },
            
            openTracer: function(url) {
            	var tracerWindow = window.open(url, 'tracerWindow');
            }
        }
    });
})(jQuery);