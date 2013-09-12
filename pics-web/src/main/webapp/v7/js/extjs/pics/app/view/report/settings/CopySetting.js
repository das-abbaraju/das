Ext.define('PICS.view.report.settings.CopySetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportcopysetting',

    border: 0,
    dockedItems: [{
        xtype: 'toolbar',
        defaults: {
            margin: '0 0 0 5'
        },
        dock: 'bottom',
        items: [{
            action: 'cancel',
            cls: 'cancel default',
            height: 28,
            text: PICS.text('Report.execute.copySetting.buttonCancel')
        }, {
            action: 'copy',
            cls: 'copy primary',
            formBind: true,
            height: 28,
            text: PICS.text('Report.execute.copySetting.buttonCopy')
        }],
        layout: {
            pack: 'end'
        },
        ui: 'footer'
    }],
    id: 'report_copy',
    items: [{
        xtype: 'textfield',
        allowBlank: false,
        fieldLabel: PICS.text('Report.execute.copySetting.formLabelName'),
        labelAlign: 'right',
        name: 'name'
    }, {
        xtype: 'textarea',
        allowBlank: false,
        fieldLabel: PICS.text('Report.execute.copySetting.formLabelDescription'),
        labelAlign: 'right',
        name: 'description'
    }, {
        xtype: 'reportfavoritetoggle'
    }],
    layout: 'form',
    // custom config
    modal_title: PICS.text('Report.execute.copySetting.title'),
    title: '<i class="icon-copy icon-large"></i>' + PICS.text('Report.execute.copySetting.tabName')
});