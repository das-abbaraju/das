Ext.define('PICS.controller.report.FilterController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportColumnSelector',
        selector: 'reportcolumnselector'
    }, {
        ref: 'filterOptions',
        selector: 'filteroptions #filterDetails'
    }, {
        ref: 'filterExpression',
        selector: 'filteroptions textfield[name=filterexpression]'
    }],

    stores: [
        'report.AvailableFieldsByCategory',
        'report.Reports'
    ],

    init: function() {
        this.control({
            'filteroptions button[action=add-filter]': {
                click: function () {
                    PICS.app.fireEvent('showcolumnselector', {columnSelectorType: 'filter'});
                }
            },
            'filteroptions button[action=search]': {
                click: function () {
                    PICS.app.fireEvent('refreshreport');
                }
            },
            'filteroptions button[action=update]': {
                click: function () {
                    this.applyFilterExpression();
                }
            },
            'filteroptions button[action=remove-filter]': {
                click: this.removeFilter
            }
        });
        this.application.on({
            refreshfilters: this.refreshFilters,
            scope: this
        });
    },

    applyFilterExpression: function () {
        var report = this.getReportReportsStore().first();

        var expression = this.getFilterExpression().value;

        //michael, do something here with 'expression' or I will disintegrate you with my sorceress


        report.set('filterExpression', expression);
        PICS.app.fireEvent('refreshreport');
    },

    generateFilterPanels: function () {
        var filterContainer = Ext.create('Ext.panel.Panel', {border: false}),
            count = 0,
            me = this,
            store = this.getReportReportsStore().first().filters();

        store.each(function (record) {
            var type = record.getAvailableField().get('filterType') || record.get('filterType'),
                panelClass = me.setFilterPanelClass(type),
                filterPanel = null;

            if (panelClass !== null) {
                filterPanel = Ext.create(panelClass, {record: record, panelNumber: ++count});
                filterContainer.add(filterPanel);
            }
        });
        return filterContainer;
    },

    refreshFilters: function () {
        var filterContainer = null;

        this.getFilterOptions().removeAll();

        filterContainer = this.generateFilterPanels();

        this.getFilterOptions().add(filterContainer);
    },

    removeFilter: function (component, e, options) {
        var record = component.up('basefilter').record,
        store = this.getReportReportsStore().first().filters();

        store.remove(record);
        PICS.app.fireEvent('refreshfilters');
        PICS.app.fireEvent('refreshreport');
    },

    setFilterPanelClass: function (type) {
        var panelClass = '';
        switch (type) {
            case 'AccountName': panelClass = 'PICS.view.report.filter.StringFilter'; break;
            case 'Boolean': panelClass = 'PICS.view.report.filter.BooleanFilter'; break;
            case 'Date': panelClass = 'PICS.view.report.filter.DateFilter'; break;
            case 'Float': panelClass = 'PICS.view.report.filter.FloatFilter'; break;
            case 'Integer': panelClass = 'PICS.view.report.filter.IntegerFilter'; break;
            case 'Number': panelClass = 'PICS.view.report.filter.IntegerFilter'; break;
            case 'String': panelClass = 'PICS.view.report.filter.StringFilter'; break;
            default: panelClass = null; break;
        }
        /*if (type !== 'Float') {
            panelClass = null;
        }*/
        return panelClass;
    },

    showColumnSelector: function(component, e, options) {
        var window = this.getReportColumnSelector();

        if (!window) {
            var store = this.getReportAvailableFieldsByCategoryStore();
            store.clearFilter();

            window = Ext.create('PICS.view.report.ColumnSelector');
            window._column_type = 'filter';
            window.show();
        }
    }
});
