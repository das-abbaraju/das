Ext.define('PICS.view.report.ReportSettings', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportsettings'],

    height: 300,
    items: [{
        xtype: 'tabpanel',
        border: 0,
        items: [{
            xtype: 'form',
            border: 0,
            buttons: [{
                text: 'Cancel'
            }, {
                text: 'Apply'
            }],
            items: [{
                xtype: 'textfield',
                fieldLabel: 'Report Name',
                name: 'report_name'
            }, {
                xtype: 'textfield',
                fieldLabel: 'Description',
                name: 'report_description'
            }],
            layout: 'form',
            title: '<i class="icon-cog icon-large"></i>Settings'
        }, {
            xtype: 'form',
            border: 0,
            buttons: [{
                text: 'Cancel'
            }, {
                text: 'Duplicate'
            }],
            items: [{
                xtype: 'textfield',
                fieldLabel: 'Report Name',
                name: 'report_name'
            }, {
                xtype: 'textfield',
                fieldLabel: 'Description',
                name: 'report_description'
            }],
            layout: 'form',
            title: '<i class="icon-copy icon-large"></i>Duplicate'
        }, {
            xtype: 'toolbar',
            border: 0,
            items: [{
                text: '<i class="icon-table icon-large"></i>Spread Sheet'
            }, {
                text: '<i class="icon-file icon-large"></i>PDF'
            }, {
                text: '<i class="icon-home icon-large"></i>To Dashboard'
            }],
            title: '<i class="icon-eject icon-large"></i>Export',
            vertical: true
        }, {
            xtype: 'form',
            border: 0,
            buttons: [{
                text: 'Cancel'
            }, {
                text: 'Share'
            }],
            items: [{
                xtype: 'textfield',
                fieldLabel: 'Report Name',
                name: 'report_name'
            }, {
                xtype: 'textfield',
                fieldLabel: 'Description',
                name: 'report_description'
            }],
            layout: 'form',
            title: '<i class="icon-share icon-large"></i>Share'
        }, {
            xtype: 'toolbar',
            border: 0,
            items: [{
                text: '<i class="icon-print icon-large"></i>Print'
            }],
            title: '<i class="icon-print icon-large"></i>Print',
            vertical: true
        }],
        tabPosition: 'bottom'
    }],
    layout: 'fit',
    modal: true,
    width: 500,

    constructor: function () {
        this.callParent(arguments);

        //var report = Ext.StoreManager.get('report.Reports').first();

        //this.child('[name=report_name]').setValue(report.get('name'));
        //this.child('[name=report_description]').setValue(report.get('description'));
    }
});