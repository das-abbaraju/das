Ext.define('PICS.view.report.header.Actions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportheaderactions'],

    border: false,
    id: 'report_actions',
    width: 190,

    constructor: function () {
        this.callParent(arguments);

        var save = Ext.create('Ext.button.Button', {
            action: 'save',
            cls: 'save success',
            height: 40,
            text: 'Save',
            width: 60
        });

        var edit = Ext.create('Ext.button.Button', {
            action: 'edit',
            cls: 'edit default',
            height: 40,
            text: '<i class="icon-cog icon-large"></i>',
            width: 40
        });

        this.add(save, edit);
    }
});