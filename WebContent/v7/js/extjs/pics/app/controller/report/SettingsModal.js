Ext.define('PICS.controller.report.SettingsModal', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'settingsModal',
        selector: 'reportsettingsmodal'
    }, {
        ref: 'settingsModalTabs',
        selector: 'reportsettingsmodaltabs'
    }, {
        ref: 'editSetting',
        selector: 'reporteditsetting'
    }, {
        ref: 'copySetting',
        selector: 'reportcopysetting'
    }, {
        ref: 'shareSetting',
        selector: 'reportsharesetting'
    }, {
        ref: 'reportInfoSetting',
        selector: 'reportinfosetting'
    }, {
        ref: 'hideReportInfoButton',
        selector: 'button[action=hide-report-info]'
    }],

    stores: [
        'report.Reports'
    ],

    views: [
        'PICS.view.report.settings.SettingsModal'
    ],

    init: function () {
        this.control({
            'reportsettingsmodal': {
                beforerender: this.beforeSettingsModalRender,
                close: this.closeSettingsModal
            },

            'reportsettingsmodal button[action=show-report-info]': {
                click: this.getReportInfo
            },

            'reportsettingsmodal button[action=hide-report-info]': {
                click: this.hideReportInfo
            },

            'reportsettingsmodal button[action=cancel]':  {
                click: this.cancelSettingsModal
            },

            'reportsettingsmodaltabs': {
                tabchange: this.changeSettingsModalTab,
                render: this.addHideReportInfoEvent
            },

            'reportsettingsmodal reporteditsetting': {
                afterrender: this.afterEditSettingRender
            },

            'reportsettingsmodal reporteditsetting button[action=edit]':  {
                click: this.editReport
            },

            'reportsettingsmodal reportcopysetting button[action=copy]':  {
                click: this.copyReport
            },

            'reportsettingsmodal reportfavoritetoggle': {
                favorite: this.favoriteReport,
                unfavorite: this.unfavoriteReport
            },

            'reportsettingsmodal reportexportsetting button[action=export]':  {
                click: this.exportReport
            },

            'reportsettingsmodal reportprintsetting button[action=print-preview]':  {
                click: this.printReport
            },

            'reportsettingsmodal reportsharesetting sharesearchbox': {
                beforerender: this.onReportModalShareSearchboxRender,
                select: this.onReportModalShareSearchboxSelect,
                specialkey: this.onReportModalShareSearchboxSpecialKey
            },

            'reportsettingsmodal reportsharesetting button[action=share]': {
                click: this.onReportModalShareClick
            }
        });

        this.application.on({
            opensettingsmodal: this.openSettingsModal,
            scope: this
        });
    },

    addHideReportInfoEvent: function (cmp, eOpts) {
        var that = this;

        cmp.items.each(function(item) {
            item.tab.on('click', function(){
                that.hideReportInfoIfVisible();
            });
        });
    },

    afterEditSettingRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            is_favorite = report.get('is_favorite'),
            edit_favorite_toggle = cmp.down('reportfavoritetoggle');

        if (is_favorite) {
            edit_favorite_toggle.toggleFavorite();
        }
    },

    beforeSettingsModalRender: function (cmp, eOpts) {
            var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            edit_setting_view = this.getEditSetting(),
            edit_setting_form = edit_setting_view.getForm();
    
        if (edit_setting_form) {
            edit_setting_form.loadRecord(report);
        }
    },
    
    closeSettingsModal: function (cmp, eOpts) {
        var settings_modal_tabs_view = this.getSettingsModalTabs(),
            edit_setting_view = this.getEditSetting(),
            edit_setting_form = edit_setting_view.getForm(),
            copy_setting_view = settings_modal_tabs_view.setActiveTab(1),
            copy_setting_form = copy_setting_view.getForm(),
            copy_favorite = copy_setting_view.down('reportfavoritetoggle'),
            share_setting_view = this.getShareSetting(),
            share_editable_icon = Ext.select('.icon-edit'),
            report_store = this.getReportReportsStore(),
            report = report_store.first(),
            is_editable = report.get('is_editable'),
            report_info_container_el = Ext.query('.report-info-container')[0];

        // TODO: reject changes
        
        
        // reset the edit form
        edit_setting_form.loadRecord(edit_setting_form.getRecord());
        
        // reset the copy form
        copy_setting_form.reset();
        
        // reset the copy favorite regardless
        copy_favorite.toggleUnfavorite();

        if (report_info_container_el) {
            report_info_container_el.remove();            
        }

        // reset the share modal
        if (is_editable) {
            share_setting_view.updateAccountDisplayfield('');
            share_editable_icon.removeCls('selected');
        }
    },

    cancelSettingsModal: function (cmp, e, eOpts) {
        var settings_modal_view = this.getSettingsModal();

        settings_modal_view.close();
    },

    hideReportInfoIfVisible: function (cmp, eOpts) {
        var settings_modal_view = this.getSettingsModal(),
            report_info_setting_view = this.getReportInfoSetting();

        if (report_info_setting_view) {
            this.hideReportInfo();
        }
    },

    changeSettingsModalTab: function (cmp, nextCard, oldCard, eOpts) {
        var settings_modal_view = this.getSettingsModal(),
            title = nextCard.modal_title;


        settings_modal_view.setTitle(title);
    },

    copyReport: function (cmp, e, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            copy_setting_view = this.getCopySetting(),
            copy_setting_form = copy_setting_view.getForm();

        if (copy_setting_form.isValid()) {
            copy_setting_form.updateRecord(report);
        }

        PICS.data.ServerCommunication.copyReport();
    },

    editReport: function (cmp, e, eOpts) {
        var settings_modal_view = this.getSettingsModal(),
            edit_setting_view = this.getEditSetting(),
            edit_setting_form = edit_setting_view.getForm(),
            that = this;

        if (edit_setting_form.isValid()) {
            edit_setting_form.updateRecord();
        }

        PICS.data.ServerCommunication.saveReport({
            success_callback: function () {

                PICS.updateDocumentTitle();

                that.application.fireEvent('updatepageheader');

                settings_modal_view.close();

                that.application.fireEvent('opensuccessmessage', {
                    title: PICS.text('Report.execute.successAlert.title'),
                    html: PICS.text('Report.execute.successAlert.message')
                });
            }
        });
    },

    exportReport: function (cmp, e, eOpts) {
        PICS.data.ServerCommunication.exportReport();
    },

    favoriteReport: function (cmp, eOpts) {
        var edit_setting_view = this.getEditSetting();

        if (edit_setting_view.isVisible()) {
            PICS.data.ServerCommunication.favoriteReport();
        }
    },

    openSettingsModal: function (action) {
        var settings_modal_view = this.getSettingsModal();

        if (!settings_modal_view) {
            settings_modal_view = Ext.create('PICS.view.report.settings.SettingsModal');
        }

        settings_modal_view.updateActiveTabFromAction(action);

        settings_modal_view.show();
    },

    printReport: function (cmp, e, eOpts) {
        PICS.data.ServerCommunication.printReport();
    },

    unfavoriteReport: function (cmp, eOpts) {
        var edit_setting_view = this.getEditSetting();

        if (edit_setting_view.isVisible()) {
            PICS.data.ServerCommunication.unfavoriteReport();
        }
    },

    /*
     * Report Info
     */

    createReportInfo: function(values) {
        var report_info_container = new Ext.Element(document.createElement('div')),
            settings_modal_view = this.getSettingsModal(),
            settings_modal_tabs_view = this.getSettingsModalTabs(),
            active_tab_panel_el = settings_modal_tabs_view.getActiveTab().getEl();

        report_info_container.addCls('report-info-container');
        
        var report_info_setting_view = Ext.create('PICS.view.report.settings.ReportInfoSetting', {
            renderTo: report_info_container
        });

        report_info_setting_view.update(values);
        settings_modal_view.setTitle(report_info_setting_view.modal_title);
        report_info_container.appendTo(active_tab_panel_el);
    },

    getReportInfo: function (report_info_button, e, eOpts) {
        var that = this,
            settings_modal_view = this.getSettingsModal(),
            load_mask = new Ext.LoadMask(settings_modal_view);

        load_mask.show();

        PICS.data.ServerCommunication.getReportInfo({
            success_callback: function (values) {
                that.showReportInfo(report_info_button, values);
                load_mask.hide();
            }
        });
    },

    hideReportInfo: function (report_info_button) {
        var report_info_button = report_info_button || this.getHideReportInfoButton(), // Passed only when triggered by event
            report_info_button_el = report_info_button.getEl(),
            report_info_setting_view = this.getReportInfoSetting(),
            settings_modal_view = this.getSettingsModal(),
            settings_modal_tabs_view = this.getSettingsModalTabs(),
            active_tab = settings_modal_tabs_view.getActiveTab(),
            active_tab_body = report_info_button.up('window').body,
            active_tab_tab_el = active_tab_body.down('.active-suspended'),
            report_info_container_el = Ext.query('.report-info-container')[0];

        active_tab_tab_el.removeCls('active-suspended');
        active_tab_tab_el.addCls('x-active');
        settings_modal_view.setTitle(active_tab.modal_title);
        report_info_button.action = 'show-report-info';
        report_info_button_el.removeCls('active');
        report_info_container_el.remove();
        //report_info_setting_view.getEl().up('div').remove();
    },

    showReportInfo: function (report_info_button, values) {
        var report_info_button_el = report_info_button.getEl(),
            settings_modal_tabs_view = this.getSettingsModalTabs(),
            active_tab = settings_modal_tabs_view.getActiveTab(),
            active_tab_panel_el = active_tab.getEl(),
            active_tab_body = report_info_button.up('window').body,
            active_tab_tab_el = active_tab_body.down('.x-tab-active');

        report_info_button.action = 'hide-report-info';
        report_info_button_el.addCls('active');
        active_tab_tab_el.removeCls('x-active');
        active_tab_tab_el.addCls('active-suspended');
        this.createReportInfo(values);
    },

    /*
     * Share
     */

    onReportModalShareSearchboxRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_id = report.get('id');

        cmp.store.getProxy().url = 'Autocompleter!reportSharingAutocomplete.action?reportId=' + report_id;
        cmp.store.load();
    },

    onReportModalShareSearchboxSelect: function (combo, records, eOpts) {
        var record = records[0];

        combo.reset();

        if (record) {
            var share_setting_view = this.getShareSetting();

            var account_info = {
                name: record.get('name'),
                location: record.get('location')
            };

            // Save the record data needed for sharing.
            share_setting_view.request_data = {
                id: record.get('id'),
                access_type: record.get('access_type')
            };

            // Show the selection.
            share_setting_view.updateAccountDisplayfield(account_info);
        }
    },

    onReportModalShareSearchboxSpecialKey: function (cmp, e, eOpts) {
        if (e.getKey() === e.ENTER) {
            var term = cmp.getValue();

            this.search(term);
        } else if (e.getKey() === e.BACKSPACE && cmp.getRawValue().length <= 1) {
            cmp.collapse();
        }
    },

    onReportModalShareClick: function (cmp, e, eOpts) {
        var share_setting_view = this.getShareSetting(),
            request_data = share_setting_view.request_data,
            report_settings_modal = this.getSettingsModal(),
            that = this;

        // Abort if no account has been selected.
        if (!request_data) {
            return;
        }

        var share_setting_view_element = share_setting_view.getEl(),
            is_editable = share_setting_view_element.down('.icon-edit.selected') ? true : false;
            id = request_data.id,
            access_type = request_data.access_type;

        if (access_type == 'user') {
            success_message_body = "Your report has been added to the user's Shared with Me.";
        } else {
            success_message_body = "Your report has been added to the users' Shared with Me.";
        }

        var options = {
            id: id,
            access_type: access_type,
            is_editable: is_editable,
            success_callback: function (response) {
                report_settings_modal.close();

                that.application.fireEvent('opensuccessmessage', {
                    title: 'Report Shared',
                    html: success_message_body
                });
            }
        };

        PICS.data.ServerCommunication.shareReport(options);
    }
});