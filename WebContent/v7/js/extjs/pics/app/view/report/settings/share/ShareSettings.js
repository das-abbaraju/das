Ext.define('PICS.view.report.settings.share.ShareSettings', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingsshare'],

    requires: [
        'PICS.view.report.settings.share.ShareSearchBox'
    ],

    border: 0,
    id: 'report_share',
    // custom config
    modal_title: 'Share Report',
    title: '<i class="icon-share icon-large"></i>Share',
    
    initComponent: function () {
        this.callParent(arguments);

        var config = PICS.app.configuration;

        if (config.isEditable()) {
            this.generateEditableSettings();
        } else {
            this.generateNonEditableSettings();
        }
    },

    listeners: {
        afterrender: function (cmp, eOpts) {
            var config = PICS.app.configuration;

            if (config.isEditable()) {
                this.mon(this.el,'click', this.onAllowEditClick, this, {
                    delegate: '.icon-edit'
                });
            }
        }
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
                action: 'share',
                cls: 'primary',
                formBind: true,
                height: 28,
                text: 'Share'
            }],
            layout: {
                pack: 'end'
            },
            ui: 'footer'
        });

        this.add({
            xtype: 'sharesearchbox'
        },{
            xtype: 'component',
            height: 50,
            padding: 6,
            border: 1,
            baseCls: 'selected-account',
            tpl: Ext.create('Ext.XTemplate',
                            '<p>',
                                '<strong class="selected-account-name">{name}</strong>',
                            '</p>',
                            '<p class="selected-account-at">',
                                '{at}',
                            '</p>',
                             '<p class="selected-account-id">',
                                '{id}',
                            '</p>'
                           ),
            id: 'selected_account'
        },{
            xtype: 'displayfield',
            fieldLabel: '<i class="icon-edit"></i>',
            labelWidth: 0,
            labelAlign: 'right',
            labelSeparator: '',
            value: '<p><strong>Allow</strong><br />user to edit, share, and delete report.</p>'
        });

        this.margin = '0 10 0 10';
        this.layout = 'form';
        this.id = 'report_share'
    },
    
    generateNonEditableSettings: function () {
        this.html = new Ext.Template([
            "<p class='permission-info'>You do not have permission to share this report</p>",
            "<p class='duplicate-info'>You can <strong>Duplicate</strong> the report to save it to your reports.  After it's saved you'll be able to share your duplicate report.</p>"
        ]);

        this.id = 'share_no_permission';
    },
    
    onAllowEditClick: function (event, target) {
        var edit_icon = Ext.fly(target);
        
        if (edit_icon.hasCls('selected')) {
            edit_icon.removeCls('selected');
        } else {
            edit_icon.addCls('selected');
        }
    },

    update: function (account) {
        var c = this.down('#selected_account');
        c.update(account);
    }
});