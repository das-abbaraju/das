Ext.define('PICS.controller.report.ReportHeader', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportHeader',
        selector: 'reportheader'
    }, {
        ref: 'reportHeaderSummary',
        selector: 'reportheadersummary'
    }, {
        ref: 'reportSettingsEdit',
        selector: 'reportsettingsedit'
    }, {
        ref: 'reportSettingsModal',
        selector: 'reportsettingsmodal'
    }, {
        ref: 'reportSettingsTabs',
        selector: 'reportsettingstabs'
    }, {
        ref: 'reportNameEdit',
        selector: 'reportsettingsedit [name=report_name]'
    }, {
        ref: 'reportDescriptionEdit',
        selector: 'reportsettingsedit [name=report_description]'
    }, {
        ref: 'reportNameCopy',
        selector: 'reportsettingscopy [name=report_name]'
    }, {
        ref: 'reportDescriptionCopy',
        selector: 'reportsettingscopy [name=report_description]'
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
                beforerender: this.onReportHeaderBeforeRender
            },

            'reportheader button[action=save]': {
                click: this.onReportSaveClick
            },

            'reportheader button[action=edit]': {
                click: this.onReportEditClick
            }
        });

        this.application.on({
            updatereportsummary: this.onUpdateReportSummary,
            scope: this
        });
    },

    onReportEditClick: function (cmp, e, eOpts) {
        this.application.fireEvent('showsettingsmodal', 'edit');
    },

    onReportHeaderBeforeRender: function (cmp, eOpts) {
        this.application.fireEvent('updatereportsummary');
    },

    onReportSaveClick: function (cmp, e, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            is_editable = report.get('is_editable');

        if (is_editable) {
            this.application.fireEvent('savereport');
        } else {
            this.application.fireEvent('showsettingsmodal', 'copy');
        }
    },

    onUpdateReportSummary: function () {
        var report_store = this.getReportReportsStore(),
            report_header_summary = this.getReportHeaderSummary();

        if (!report_store.isLoaded()) {
            report_store.on('load', function (store, records, successful, eOpts) {
                var report = report_store.first();

                report_header_summary.update(report);
            });
        } else {
            var report = report_store.first();

            report_header_summary.update(report);
        }
    }
});