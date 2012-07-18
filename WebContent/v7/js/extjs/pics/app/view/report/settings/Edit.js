Ext.define('PICS.view.report.settings.Edit', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingsedit'],

    border: 0,
    id: 'report_edit',
    // custom config
    modal_title: 'Edit Report',
    title: '<i class="icon-cog icon-large"></i>Settings',

    constructor: function () {
        this.callParent(arguments);

        var config = PICS.app.configuration;

        if (config.isEditable()) {
            this.generateEditableSettings();
        } else {
            this.generateNonEditableSettings();
        }
    },

    generateEditableSettings: function () {
        var panel = {
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
            }],
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
            }, {
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
            }],
            layout: 'form'
        };

        this.add(panel);
    },

    generateNonEditableSettings: function () {
        var panel = {
            html: new Ext.Template([
                "<p class='permission-info'>You do not have permission to edit the settings of this report</p>",
                "<p class='duplicate-info'>You can <strong>Duplicate</strong> the report to save it to your reports.  After it's saved you'll be able to edit everything.</p>",
                "<p><a href='javascript:;' class='favorite'><i class='icon-star'></i></a> Report <strong>is not</strong> a Favorite</p>"
            ]),
            id: 'settings_no_permission'
        };

        this.add(panel);
    }
});