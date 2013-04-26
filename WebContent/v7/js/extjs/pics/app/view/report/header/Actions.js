Ext.define('PICS.view.report.header.Actions', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.reportactions',

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
            text: PICS.text('Report.execute.headerActions.buttonSave')
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