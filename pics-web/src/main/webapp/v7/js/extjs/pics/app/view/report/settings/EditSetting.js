Ext.define('PICS.view.report.settings.EditSetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reporteditsetting',

    requires: [
        'PICS.view.report.settings.FavoriteToggle'
    ],

    border: 0,
    id: 'report_edit',
    // custom config
    modal_title: PICS.text('Report.execute.editSetting.title'),
    title: '<i class="icon-cog icon-large"></i>' + PICS.text('Report.execute.editSetting.tabName'),

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
                text: PICS.text('Report.execute.editSetting.buttonCancel')
            }, {
                action: 'edit',
                cls: 'edit primary',
                formBind: true,
                height: 28,
                text: PICS.text('Report.execute.editSetting.buttonEdit')
            }],
            layout: {
                pack: 'end'
            },
            ui: 'footer'
        }];

        this.items = [{
            xtype: 'textfield',
            allowBlank: false,
            fieldLabel: PICS.text('Report.execute.editSetting.formLabelName'),
            labelAlign: 'right',
            name: 'name'
        }, {
            xtype: 'textarea',
            allowBlank: false,
            fieldLabel: PICS.text('Report.execute.editSetting.formLabelDescription'),
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
                '<p class="permission-info">' + PICS.text('Report.execute.editSetting.noEditTitle') + '</p>',
                '<p class="duplicate-info">' + PICS.text('Report.execute.editSetting.noEditDescription') + '</p>'
            ])
        }, {
            xtype: 'reportfavoritetoggle'
        }];

        this.id = 'report_edit_no_permission';
    }
});