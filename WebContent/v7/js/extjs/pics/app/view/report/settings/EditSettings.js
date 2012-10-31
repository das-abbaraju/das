Ext.define('PICS.view.report.settings.EditSettings', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingsedit'],

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

    listeners: {
        afterrender: function (cmp, eOpts) {
            if (PICS.app.configuration.isFavorite()) {
                this.updateFavorite();
            }

            var config = PICS.app.configuration;

            if (config.isEditable()) {
                this.mon(this.el,'click', this.onEditableFavoriteClick, this, {
                    delegate: '.icon-star'
                });
            } else {
                this.mon(this.el,'click', this.onNonEditableFavoriteClick, this, {
                    delegate: '.icon-star'
                });
            }
        }
    },

    onEditableFavoriteClick: function (event, target) {
        event.stopEvent();

        var element = Ext.fly(target),
            favorite_class = element.hasCls('selected');

        if (favorite_class) {
            this.updateUnFavorite();
        } else {
            this.updateFavorite();
        }
    },

    onNonEditableFavoriteClick: function (event, target) {
        event.stopEvent();

        var element = Ext.fly(target),
            favorite_class = element.hasCls('selected'),
            config = PICS.app.configuration;

        if (favorite_class) {
            this.updateUnFavorite();
            this.fireEvent('unfavorite');
            config.setIsFavorite(false);
        } else {
            this.updateFavorite();
            this.fireEvent('favorite');
            config.setIsFavorite(true);
        }
    },

    checkFavoriteStatus: function () {
        var element = this.getEl(),
            favorite_icon = element.down('.icon-star');

        return favorite_icon.hasCls('selected');
    },

    updateFavorite: function () {
        var element = this.getEl(),
            favorite_icon = element.down('.icon-star'),
            favorite_text = element.down('.favorite-text');

        favorite_icon.addCls('selected');
        favorite_text.setHTML('is');
    },

    updateUnFavorite: function (element) {
        var element = this.getEl();
            favorite_icon = element.down('.icon-star'),
            favorite_text = element.down('.favorite-text');

        favorite_icon.removeCls('selected');
        favorite_text.setHTML('is not');
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
            xtype: 'displayfield',
            fieldLabel: '<a href="javascript:;" class="favorite"><i class="icon-star"></i></a>',
            labelAlign: 'right',
            labelSeparator: '',
            value: 'Report <strong class="favorite-text">is not</strong> a Favorite'
        }/*, {
            xtype: 'displayfield',
            fieldLabel: '<a href="javascript:;" class="private"><i class="icon-eye-open"></i></a>',
            labelAlign: 'right',
            labelSeparator: '',
            value: 'Report <strong>is not</strong> Private'
        }*/);

        this.layout = 'form';
    },

    generateNonEditableSettings: function () {
        this.html = new Ext.Template([
            "<p class='permission-info'>You do not have permission to edit the settings of this report</p>",
            "<p class='duplicate-info'>You can <strong>Duplicate</strong> the report to save it to your reports.  After it's saved you'll be able to edit everything.</p>",
            "<p><a href='javascript:;' class='favorite'><i class='icon-star'></i></a> Report <strong class='favorite-text'>is not</strong> a Favorite</p>"
        ]);

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