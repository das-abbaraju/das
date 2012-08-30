Ext.define('PICS.view.report.settings.CopySettings', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingscopy'],

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
        name: 'report_name'
    }, {
        xtype: 'textarea',
        allowBlank: false,
        fieldLabel: 'Description',
        labelAlign: 'right',
        name: 'report_description'
    }/*, {
        xtype: 'displayfield',
        fieldLabel: '<a href="javascript:;" class="favorite"><i class="icon-star"></i></a>',
        labelAlign: 'right',
        labelSeparator: '',
        value: 'Report <strong>is not</strong> a Favorite'
    }, {
        xtype: 'displayfield',
        fieldLabel: '<a href="javascript:;" class="private"><i class="icon-eye-open"></i></a>',
        labelAlign: 'right',
        labelSeparator: '',
        value: 'Report <strong>is not</strong> Private'
    }*/],
    layout: 'form',
    // custom config
    modal_title: 'Duplicate Report',
    title: '<i class="icon-copy icon-large"></i>Duplicate'
});