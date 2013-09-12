Ext.define('PICS.view.report.alert.Error', {
    extend: 'Ext.window.Window',
    alias: 'widget.reportalerterror',

    closeAction: 'destroy',
    draggable: false,
    id: 'error_message',
    modal: true,
    resizable: false,
    shadow: false,
    width: 500
});