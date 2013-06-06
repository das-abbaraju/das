Ext.define('PICS.data.Exception', {
    statics: (function () {
        // List of user-friendly error messages.
        var error_codes = {
            401: {
                title: PICS.text('Report.execute.401.title'),
                message: PICS.text('Report.execute.401.message')
            },
            404: {
                title: PICS.text('Report.execute.404.title'),
                message: PICS.text('Report.execute.404.message')
            },
            500: {
                title: PICS.text('Report.execute.500.title'),
                message: PICS.text('Report.execute.500.message')
            }
        };
        
        var unknown_error = {
            title: PICS.text('Report.execute.unknownError.title'),
            message: PICS.text('Report.execute.unknownError.message')
        };

        function getErrorFromStatusCode(code) {
            return error_codes[code] || getErrorFromUnknownStatusCode(code);
        }

        function getErrorFromUnknownStatusCode(code) {
            var error = unknown_error;

            error.message += ' (code: ' + code + ')';

            return error;
        }

        function handleKnown2xxException(response, callback) {
            var response_text = response.responseText;

            PICS.createStackTrace();

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

            PICS.createStackTrace();
            showException(title, message, callback);
        }

        function handleUnknown2xxException(response, callback) {
            PICS.createStackTrace();
            showException(unknown_error.title, unknown_error.message);
        }

        function isSuccessWithEmptyResponse(response) {
            if (typeof response.status != 'undefined' && typeof response.responseText != 'undefined') {
                return response.status.toString().charAt(0) == '2' && response.responseText == '';
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
            return response && response.status.toString().charAt(0) != '2';
        }

        function hasUnknown2xxException(response) {
            var response_text = response && response.responseText,
                json;
        
            try {
                json = Ext.JSON.decode(response_text);
            } catch (e) {
                return response.status && response.status.toString().charAt(0) == '2';
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