Ext.define('PICS.data.Exception', {
    statics: (function () {
        // List of user-friendly error messages.
        var error_codes = {
            401: {
                title: 'Server Communication Error',
                message: 'Your session has timed out. Please <a href="Login.action">relogin</a>.'
            },
            404: {
                title: 'Server Communication Error',
                message: 'Page not found.' 
            },
            500: {
                title: 'Server Communication Error',
                message: 'Server error occurred.'
            }
        };
        
        var unknown_error = {
            title: 'Server Communication Error',
            message: 'An unknown error occurred.'
        };

        function getErrorFromStatusCode(code) {
            return error_codes[code] || unknown_error;
        }

        function handleKnown2xxException(response, callback) {
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

        function handleUnknown2xxException(response, callback) {
            showException(unknown_error.title, unknown_error.message);
        }

        function isSuccessWithEmptyResponse(response) {
            if (typeof response.status != 'undefined' && typeof response.responseText != 'undefined') {
                return response.status.toString()[0] == '2' && response.responseText == '';                
            } else {
                return false;
            }
        }

        function hasKnown2xxException(response) {
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
            return response && response.status.toString()[0] != '2';
        }

        function hasUnknown2xxException(response) {
            var response_text = response && response.responseText,
                json;
        
            try {
                json = Ext.JSON.decode(response_text);
            } catch (e) {
                return response.status && response.status.toString()[0] == '2';
            }
            
            return false;
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
            getUnknownError: function () {
                return unknown_error;
            },

            handleException: function (options) {
                var response = options.response,
                    callback = typeof options.callback == 'function' ? options.callback : function () {};
                
                if (hasKnown2xxException(response)) {
                    handleKnown2xxException(response, callback);
                } else if (hasUnknown2xxException(response)) {
                    handleUnknown2xxException(response, callback);
                } else if (hasNon2xxException(response)) {
                    handleNon2xxException(response, callback);
                }
            },
            
            hasException: function (response) {
                if (!response) {
                    Ext.Error.raise('Parameter response required');
                }

                if (isSuccessWithEmptyResponse(response)) {
                    return false;
                }

                return hasKnown2xxException(response) || hasUnknown2xxException(response) || hasNon2xxException(response);
            }
        };
    }())
});