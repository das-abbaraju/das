Ext.define('PICS.view.report.settings.EditSettings', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingsedit'],

    requires: ['PICS.view.report.settings.FavoriteToggle'],

    border: 0,
    id: 'report_edit',
    // custom config
    modal_title: 'Edit Report',
    title: '<i class="icon-cog icon-large"></i>Settings',

    initComponent: function () {
        this.callParent(arguments);

        var config = PICS.app.configuration;

        if (config.isEditable()) {
            this.generateEditableSettings();
        } else {
            this.generateNonEditableSettings();
        }

        this.addEvents('favorite');
        this.addEvents('unfavorite');

    },

    generateEditableSettings: function () {
        this.addDocked({
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
        });

        this.add({
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
        }, {
            xtype: 'favoritetoggle'
        });

        this.layout = 'form';
    },

    generateNonEditableSettings: function () {
        this.add({
            xtype: 'component',
            html:  new Ext.Template([
                "<p class='permission-info'>You do not have permission to edit the settings of this report</p>",
                "<p class='duplicate-info'>You can <strong>Duplicate</strong> the report to save it to your reports.  After it's saved you'll be able to edit everything.</p>"
            ]),
        },{
            xtype: 'favoritetoggle'
        });

        this.id = 'settings_no_permission';
    },

    update: function (report) {
        if (!report || report.modelName != 'PICS.model.report.Report') {
            Ext.Error.raise('Invalid report record');
        }

        var data = report ? report.data : {},
            report_name_element = this.down('textfield[name=report_name]'),
            report_description_element = this.down('textarea[name=report_description]');

        if (data.name && report_name_element) {
            report_name_element.setValue(data.name);
        }

        if (data.description && report_description_element) {
            report_description_element.setValue(data.description);
        }

        this.callParent([data]);
    }
});