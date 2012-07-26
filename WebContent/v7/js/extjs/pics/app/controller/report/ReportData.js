Ext.define('PICS.controller.report.ReportData', {
    extend: 'Ext.app.Controller',

    stores: [
        'report.Reports',
        'report.DataSets'
    ],

    init: function () {
        this.control({
            'reportpagingtoolbar': {
                beforerender: function (cmp, eOpts) {
                    var report_data_store = this.getReportDataSetsStore();

                    report_data_store.configureProxyUrl();
                    report_data_store.load({
                        callback: function () {
                            cmp.updateDisplayInfo();
                        }
                    });
                }
            },

            'reportpagingtoolbar button[itemId=refresh]': {
                click: function () {
                    this.application.fireEvent('refreshreport');
                }
            },

            'reportpagingtoolbar combo[name=rows_per_page]': {
                select: function (combo, records, options) {
                    this.getReportDataSetsStore().updateReportPaging(records[0].get('field1'));
                }
            },

            'reportpagingtoolbar button[action=add-column]': {
                click: function () {
                    this.application.fireEvent('showavailablefieldmodal', 'column');
                }
            },

            // sort_asc

            // sort_desc

            // remove_column

            'menu[name=report_data_header_menu] menuitem[name=sort_asc]': {
                click: function (cmp, event, eOpts) {
                    var sort_store = this.getReportReportsStore().first().sorts(),
                        name = cmp.up('menu').activeHeader.dataIndex;

                    sort_store.removeAll();
                    sort_store.add(Ext.create('PICS.model.report.Sort', {
                        name: name,
                        direction: 'SUPERMAN' // lawl could be anything (ASC)
                    }));

                    this.application.fireEvent('refreshreport');
                }
            },

            'menu[name=report_data_header_menu] menuitem[name=sort_desc]': {
                click: function (cmp, event, eOpts) {
                    var sort_store = this.getReportReportsStore().first().sorts(),
                        name = cmp.up('menu').activeHeader.dataIndex;

                    sort_store.removeAll();
                    sort_store.add(Ext.create('PICS.model.report.Sort', {
                        name: name,
                        direction: 'DESC'
                    }));

                    this.application.fireEvent('refreshreport');
                }
            },

            'menu[name=report_data_header_menu] menuitem[name=remove_column]': {
                click: function (cmp, event, eOpts) {
                    var column_store = this.getReportReportsStore().first().columns(),
                        column_name = cmp.up('menu').activeHeader.dataIndex,
                        column = column_store.findRecord('name', column_name);

                    column_store.remove(column);

                    this.application.fireEvent('refreshreport');
                }
            }
        });
    }
});
