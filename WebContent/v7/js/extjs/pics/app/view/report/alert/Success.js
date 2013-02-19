Ext.define('PICS.view.report.alert.Success', {
    extend: 'Ext.window.Window',
    alias: 'widget.reportalertsuccess',

    listeners: {
        show: function (cmp, eOpts) {
            var that = this;

            setTimeout(function () {
                that.close();
            }, 5000);
        }
    },

    draggable: false,
    id: 'success_message',
    resizable: false,
    shadow: false,
    width: 350
});