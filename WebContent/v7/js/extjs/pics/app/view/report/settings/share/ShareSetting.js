Ext.define('PICS.view.report.settings.share.ShareSetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportsharesetting',

    requires: [
        'PICS.view.report.settings.share.ShareSearchBox'
    ],

    border: 0,
    id: 'report_share',
    // custom config
    modal_title: 'Share Report',
    title: '<i class="icon-share icon-large"></i>Share',

    listeners: {
        afterrender: function (cmp, eOpts) {
            var report_store = Ext.StoreManager.get('report.Reports'),
                report = report_store.first(),
                is_editable = report.get('is_editable');

            if (is_editable) {
                this.mon(this.el,'click', this.onAllowEditClick, this, {
                    delegate: '.icon-edit'
                });
            }
        }
    },

    initComponent: function () {
        var report_store = Ext.StoreManager.get('report.Reports'),
            report = report_store.first(),
            is_editable = report.get('is_editable');

        if (is_editable) {
            this.generateEditableSettings();
        } else {
            this.generateNonEditableSettings();
        }

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
                action: 'share',
                cls: 'primary',
                formBind: true,
                height: 28,
                text: 'Share'
            }],
            margin: '5 0 0 0',
            layout: {
                pack: 'end'
            },
            ui: 'footer'
        }];

        this.items = [{
            xtype: 'sharesearchbox'
        }, {
            xtype: 'component',
            height: 65,
            padding: 6,
            border: 1,
            margin: '0 0 5 0',
            baseCls: 'selected-account',
            tpl: Ext.create('Ext.XTemplate',
                '<p>',
                    '<strong class="selected-account-name">{name}</strong>',
                '</p>',
                '<p class="selected-account-location">',
                    '{location}',
                '</p>',
                 '<p class="selected-account-id">',
                    '{id}',
                '</p>'
            ),
            id: 'selected_account'
        }, {
            xtype: 'displayfield',
            fieldLabel: '<i class="icon-edit"></i>',
            labelWidth: 0,
            labelSeparator: '',
            value: '<p><strong>Allow</strong><br />user to edit, share, and delete report.</p>'
        }];

        this.margin = '0 10 0 10';
        this.layout = 'form';
        this.id = 'report_share'
    },

    generateNonEditableSettings: function () {
        this.items = [{
            xtype: 'component',
            html:  new Ext.Template([
                '<p class="permission-info">You do not have permission to edit the settings of this report</p>',
                '<p class="duplicate-info">You can <strong>Duplicate</strong> the report to save it to your reports.  After it\'s saved you\'ll be able to share your duplicate report.</p>'
            ])
        }];

        this.id = 'report_share_no_permission';        
    },

    onAllowEditClick: function (event, target) {
        var edit_icon = Ext.fly(target);

        if (edit_icon.hasCls('selected')) {
            edit_icon.removeCls('selected');
        } else {
            edit_icon.addCls('selected');
        }
    },

    updateAccountDisplayfield: function (account_info) {
        var account_displayfield = this.down('#selected_account');

        account_displayfield.update(account_info);
    }
});