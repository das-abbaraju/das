Ext.define('PICS.view.report.header.ReportActions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportheaderactions'],

    border: 0,
    id: 'report_actions',
    width: 190,

    constructor: function () {
        this.callParent(arguments);

        var save = Ext.create('Ext.button.Button', {
            action: 'save',
            cls: 'save success',
            height: 40,
            scale: 'large',
            text: 'Save'
        });

        var edit = Ext.create('Ext.button.Button', {
            action: 'edit',
            cls: 'edit default',
            height: 40,
            scale: 'large',
            text: '<i class="icon-cog icon-large"></i>'
        });

        this.add(save, edit);
    }
});