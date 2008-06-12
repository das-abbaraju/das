/**
*
*  AJAX IFRAME METHOD (AIM)
*  http://www.webtoolkit.info/
*
**/

AIM = {

    form : function(f, name) {
        f.setAttribute('target', 'upload_iframe_' + name);
    },

    submit : function(theId, c) {
        var i = document.getElementById('upload_iframe_' + theId);
        if (c && typeof(c.onComplete) == 'function') {
            i.onComplete = c.onComplete;
        }

        if (c && typeof(c.onStart) == 'function') {
            c.onStart(theId);
        }

    	var f = document.getElementById('file_upload_' + theId);

		return f.submit();
    },

    loaded : function(id) {
    
        var i = document.getElementById('upload_iframe_' + id);
        
        if (i.contentDocument) {
            var d = i.contentDocument;
        } else if (i.contentWindow) {
            var d = i.contentWindow.document;
        } else {
            var d = window.frames['upload_iframe_' + id].document;
        }


        if (d.location.href == "about:blank") {
            return;
        }
        
        if (typeof(i.onComplete) == 'function') {
            i.onComplete(id);
        }
    }

}