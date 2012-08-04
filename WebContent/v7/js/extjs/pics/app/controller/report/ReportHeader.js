Ext.define('PICS.controller.report.ReportHeader', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportHeader',
        selector: 'reportheader'
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

    init: function () {
        this.control({
            'reportheader': {
                render: this.onReportHeaderRender
            },

            'reportheader button[action=save]': {
                click: this.onReportSaveClick
            },

            'reportheader button[action=edit]': {
                click: this.onReportEditClick
            }
        });

        this.application.on({
            updatereportsummary: this.updateReportSummary,
            scope: this
        });
    },

    onReportEditClick: function (cmp, e, eOpts) {
        this.application.fireEvent('showsettingsmodal', 'edit');
    },

    onReportHeaderRender: function (cmp, eOpts) {
        this.application.fireEvent('updatereportsummary');
    },

    onReportSaveClick: function (cmp, e, eOpts) {
        var config = PICS.app.configuration;

        if (config.isEditable()) {
            this.application.fireEvent('savereport');
        } else {
            this.application.fireEvent('showsettingsmodal', 'copy');
        }
    },

    updateReportSummary: function () {
        var store = this.getReportReportsStore(),
            me = this;

        function updateSummaryFromStore(store) {
            var report = store.first(),
                report_name = report.get('name'),
                report_description = report.get('description');

            updateSummary(report_name, report_description);
        }

        function updateSummary(name, description) {
            var report_header_element = me.getReportHeader().getEl(),
                report_name = report_header_element.query('.name')[0],
                report_description = report_header_element.query('.description')[0];

            report_name.innerHTML = name;
            report_description.innerHTML = description;
        }

        // TODO: need better loading check
        if (store.isLoading()) {
            store.on('load', function (store) {
                updateSummaryFromStore(store);
            });
        } else {
            updateSummaryFromStore(store);
        }
    }
});