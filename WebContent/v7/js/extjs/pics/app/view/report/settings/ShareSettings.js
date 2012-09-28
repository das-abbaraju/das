Ext.define('PICS.view.report.settings.Share', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingsshare'],

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

        this.addEvents('allow');
        this.addEvents('disallow');
    },

    listeners: {
        afterrender: function (cmp, eOpts) {
            var config = PICS.app.configuration;

            if (config.isEditable()) {
                this.mon(this.el,'click', this.onAllowEditClick, this, {
                    delegate: '.icon-edit'
                });
                this.mon(this.el,'click', this.onAllowEditClick, this, {
                    delegate: '.share-button'
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
                action: 'edit',
                cls: 'share primary disabled',
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
            xtype: 'displayfield',
            fieldLabel: '<a href="javascript:;" class="edit"><i class="icon-edit"></i></a>',
            labelAlign: 'right',
            labelSeparator: '',
            value: '<p><strong>Allow</strong><br />user to edit, share, and delete report.</p>'
        });

        this.layout = 'auto';
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

    onShareClick: function (event, target) {
    }
});