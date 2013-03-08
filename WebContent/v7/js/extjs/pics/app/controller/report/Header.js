Ext.define('PICS.controller.report.Header', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'successMessage',
        selector: 'reportalertsuccess'
    }, {
        ref: 'pageHeader',
        selector: 'reportpageheader'
    }],

    stores: [
        'report.Reports'
    ],
             
    views: [
        'PICS.view.report.alert.Success'
    ],
             
    init: function () {
        this.control({
            'reportheader': {
                beforerender: this.beforeHeaderRender
            },

            'reportheader button[action=save]': {
                click: this.saveReport
            },

            'reportheader button[action=edit]': {
                click: this.openSettingsModal
            }
        });
        
        this.application.on({
            opensuccessmessage: this.openSuccessMessage,
            scope: this
        });

        this.application.on({
            updatepageheader: this.updatePageHeader,
            scope: this
        });
    },
    
    beforeHeaderRender: function (cmp, eOpts) {
        this.application.fireEvent('updatepageheader');
    },
    
    openSuccessMessage: function (options) {
        var success_message_view = this.getSuccessMessage(),
            title = options.title,
            html = options.html;
        
        if (success_message_view) {
            success_message_view.destroy();
        }
        
        var success_message = Ext.create('PICS.view.report.alert.Success', {
            html: html,
            title: title
        });
        
        success_message.show();
    },

    openSettingsModal: function (cmp, e, eOpts) {
        this.application.fireEvent('opensettingsmodal', 'edit');
    },

    saveReport: function (cmp, e, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            is_editable = report.get('is_editable'),
            that = this;

        if (is_editable) {
            PICS.data.ServerCommunication.saveReport({
                success_callback: function () {
                    that.application.fireEvent('opensuccessmessage', {
                        title: 'Report Saved',
                        html: 'to My Reports in Reports Manager.'
                    });
                }
            });
        } else {
            this.application.fireEvent('opensettingsmodal', 'copy');
        }
    },

    updatePageHeader: function () {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            page_header_view = this.getPageHeader();
        
        page_header_view.update(report);
    }
});