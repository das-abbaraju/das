Ext.define('PICS.controller.report.Header', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'pageHeader',
        selector: 'reportpageheader'
    }],

    stores: [
        'report.Reports'
    ],
             
    views: [
        'PICS.view.report.alert-message.AlertMessage'
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
            updatepageheader: this.updatePageHeader,
            scope: this
        });
    },
    
    beforeHeaderRender: function (cmp, eOpts) {
        this.application.fireEvent('updatepageheader');
    },

    openSettingsModal: function (cmp, e, eOpts) {
        this.application.fireEvent('opensettingsmodal', 'edit');
    },

    saveReport: function (cmp, e, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            is_editable = report.get('is_editable');

        if (is_editable) {
            PICS.data.ServerCommunication.saveReport();
        } else {
            this.application.fireEvent('opensettingsmodal', 'copy');
        }
    },

    updatePageHeader: function () {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            page_header = this.getPageHeader();
        
        page_header.update(report);
    }
});