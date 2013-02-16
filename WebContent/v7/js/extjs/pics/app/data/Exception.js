Ext.define('PICS.data.Exception', {
    statics: (function () {
        // List of user-friendly error messages.
        var error_codes = {
            401: {
                title: 'Server Communication Error',
                message: 'Your session has timed out. Please <a href="Login.action">relogin</a>'
            },
            404: {
                title: 'Server Communication Error',
                message: 'File not found.' 
            },
            500: {
                title: 'Server Communication Error',
                message: 'Server error occurred.'
            }
        };
        
        var unknown_error = {
            title: 'Server Communication Error',
            message: 'Unknown error.'
        };

        function getErrorFromStatusCode(code) {
            return error_codes[code] || unknown_error;
        }
        
        function handle2xxException(response, callback) {
            var response_text = response.responseText;
            
            try {
                var json = Ext.JSON.decode(response_text),
                    title = json.title,
                    message = json.message;
                
                showException(title, message, callback);
            } catch (e) {
                showException('Exception', e.msg, callback);
            }
        }
        
        function handleNon2xxException(response, callback) {
            var status_code = response.status,
                error = getErrorFromStatusCode(status_code),
                title = error.title,
                message = error.message;
            
            showException(title, message, callback);
        }

        function has2xxException(response) {
            var response_text = response && response.responseText,
                json;
            
            try {
                json = Ext.JSON.decode(response_text);
            } catch (e) {
                return false;
            }
            
            return json && json.success == false;
        }

        function hasNon2xxException(response) {
            return response && typeof error_codes[response.status] != 'undefined';
        }
        
        function showException(title, message, callback) {
            var exception = Ext.create('PICS.view.report.alert.Error', {
                title: title,
                html: message
            });
            
            exception.on('close', callback);
            
            exception.show();
        }
        
        return {
            handleException: function (options) {
                var response = options.response,
                    callback = typeof options.callback == 'function' ? options.callback : function () {};
                
                // success: false error
                if (has2xxException(response)) {
                    handle2xxException(response, callback);
                }
                
                // status code error
                if (hasNon2xxException(response)) {
                    handleNon2xxException(response, callback);
                }
            },
            
            hasException: function (response) {
                return has2xxException(response) || hasNon2xxException(response);
            }
        };
    }())
});