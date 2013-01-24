Ext.define('PICS.view.report.settings.EditSetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reporteditsetting',

    requires: [
        'PICS.view.report.settings.FavoriteToggle'
    ],

    border: 0,
    id: 'report_edit',
    // custom config
    modal_title: 'Edit Report',
    title: '<i class="icon-cog icon-large"></i>Settings',

    initComponent: function () {
        var report_store = Ext.StoreManager.get('report.Reports'),
            report = report_store.first(),
            is_editable = report.get('is_editable');

        if (is_editable) {
            this.generateEditableSettings();
        } else {
            this.generateNonEditableSettings();
        }

        this.addEvents('favorite');
        this.addEvents('unfavorite');
        
        this.callParent(arguments);
    },

    generateEditableSettings: function () {
        this.dockedItems = [{
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
                action: 'edit',
                cls: 'edit primary',
                formBind: true,
                height: 28,
                text: 'Apply'
            }],
            layout: {
                pack: 'end'
            },
            ui: 'footer'
        }];

        this.items = [{
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
        }];

        this.layout = 'form';
    },

    generateNonEditableSettings: function () {
        this.items = [{
            xtype: 'component',
            html:  new Ext.Template([
                "<p class='permission-info'>You do not have permission to edit the settings of this report</p>",
                "<p class='duplicate-info'>You can <strong>Duplicate</strong> the report to save it to your reports.  After it's saved you'll be able to edit everything.</p>"
            ])
        }, {
            xtype: 'reportfavoritetoggle'
        }];

        this.id = 'report_edit_no_permission';
    },

    loadFormRecord: function (report) {
        var form = this.getForm();
        
        form.loadRecord(report);    
    },

    updateFormRecord: function () {
        var form = this.getForm();

        if (form.isValid()) {
            form.updateRecord();
        }
    }
});