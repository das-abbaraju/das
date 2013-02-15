Ext.define('PICS.Ajax', {
    extend: 'Ext.data.Connection',
    
    autoAbort : false,
    listeners: {
        // FYI - successfully completed
        requestcomplete: function (conn, response, options, eOpts) {
            if (PICS.data.Exception.hasException(response)) {
                PICS.data.Exception.handleException({
                    response: response
                });
            }
        },

        // FYI - unsuccessfully completed
        requestexception: function (conn, response, options, eOpts) {
            PICS.data.Exception.handleException({
                response: response
            });
        }
    },
    singleton: true
});
