Ext.define('PICS.view.report.alert.Confirm', {
    extend: 'PICS.ux.window.Window',
    alias: 'widget.reportalertconfirm',

    closeAction: 'destroy',
    draggable: false,
    id: 'confirm_message',
    modal: true,
    resizable: false,
    shadow: false,
    width: 500,
    title: 'Confirm Report Renaming',

    items: [{
        xtype: 'component',
        html: new Ext.Template([
            '<p>',
                    'Renaming this report will update the name for all users of the report.',
            '</p>',
            '<p>',
                    'Alternatively, you can duplicate the report for any personal editing without affecting any existing users.',
            '</p>'
        ]),
        padding: '20'
    },{
        xtype: 'toolbar',
        defaults: {
            margin: '0 0 0 10'
        },
        dock: 'bottom',
        items: [{
            action: 'rename',
            cls: 'rename primary',
            formBind: true,
            height: 28,
            text: 'Confirm Rename'
        }, {
            action: 'copy',
            cls: 'copy default',
            formBind: true,
            height: 28,
            text: 'Duplicate Report'
        }, {
            action: 'cancel',
            cls: 'cancel default',
            formBind: true,
            height: 28,
            text: 'Cancel'
        }],
        layout: {
            pack: 'end'
        },
        ui: 'footer',
        padding: '0 20 20 0'
    }]
});