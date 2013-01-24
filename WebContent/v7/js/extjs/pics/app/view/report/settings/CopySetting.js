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
            text: 'Cancel'
        }, {
            action: 'copy',
            cls: 'copy primary',
            formBind: true,
            height: 28,
            text: 'Duplicate'
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
        fieldLabel: 'Report Name',
        labelAlign: 'right',
        name: 'name'
    }, {
        xtype: 'textarea',
        allowBlank: false,
        fieldLabel: 'Description',
        labelAlign: 'right',
        name: 'description'
    }, {
        xtype: 'reportfavoritetoggle'
    }],
    layout: 'form',
    // custom config
    modal_title: 'Duplicate Report',
    title: '<i class="icon-copy icon-large"></i>Duplicate',

    updateFormRecord: function (report) {
        var form = this.getForm();

        if (form.isValid()) {
            form.updateRecord(report);
        }

        form.reset();
    }
});