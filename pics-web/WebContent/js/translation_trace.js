(function ($) {
    PICS.define('translation.Trace', {
        methods: {
            init: function () {
            	$('#tracing_open').bind('click', {that: this}, this.trace);
            },
            
            trace: function (event) {
            	event.preventDefault();
            	
            	var that = event.data.that;
            	var ajaxUrl = $(this).attr('data-url');
            	var href = window.location.href;
            	var translationUrl = $(this).attr('href');
            	
            	var urls = new Array(ajaxUrl, href, translationUrl);
            	var ids = new Array('tracingClearCache', 'tracingThisPage', 'tracerWindow');
            	var callbacks = new Array(that.loadIframe, that.openWindow);
            	
            	that.loadIframe(urls, ids, callbacks);
            },
            
            loadIframe: function (urls, ids, callbacks) {
            	var url = urls.shift();
            	var id = ids.shift();
            	var callback = callbacks.shift();
            	
	            if (url) {
	            	$('body').append('<iframe id="' + id + '" style="display: none"></iframe>');
	            	$('#' + id).attr('src', url);
	            	
	            	$('#' + id).load(function() {
	            		$(this).remove();
	            		callback(urls, ids, callbacks);
	            	});
            	}
            },
            
            openWindow: function(url, id) {
            	var tracerWindow = window.open(url[0], id[0]);
            }
        }
    });
})(jQuery);