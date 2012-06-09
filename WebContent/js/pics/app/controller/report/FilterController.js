Ext.define('PICS.controller.report.FilterController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportColumnSelector',
        selector: 'reportcolumnselector'
    }, {
        ref: 'filters',
        selector: 'filteroptions #report_filters'
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

        // TODO write a real grammar and parser for our filter expression DSL

        // Split into tokens
        var validTokenRegex = /[0-9]+|\(|\)|and|or/gi;
        expression = expression.replace(validTokenRegex, ' $& ');

        var tokens = expression.trim().split(/ +/);
        expression = '';

        // Check for invalid tokens and make sure parens are balanced
        var parenCount = 0;
        for (var i = 0; i < tokens.length; i += 1) {
            var token = tokens[i];

            if (token.search(validTokenRegex) === -1) {
                return false;
            }

            if (token === '(') {
                parenCount += 1;
                expression += '{';
            } else if (token === ')') {
                parenCount -= 1;
                expression += '}';
            } else if (token.toUpperCase() === 'AND') {
                expression += ' AND ';
            } else if (token.toUpperCase() === 'OR') {
                expression += ' OR ';
            } else if (token.search(/[0-9]+/) !== -1) {
                if (token === '0') {
                    return false;
                }

                // Convert from counting number to index
                var indexNum = new Number(token) - 1;
                expression += '{' + indexNum + '}';
            } else {
                return false;
            }

            if (parenCount < 0) {
                return false;
            }
        }

        if (parenCount !== 0) {
            return false;
        }

        report.set('filterExpression', expression);

        PICS.app.fireEvent('refreshreport');
    },

    generateFilterPanels: function () {
        var filter_container = Ext.create('Ext.panel.Panel', {
            bodyCls: 'filter-container-body',
            border: 0,
            cls: 'filter-container'
        });

        var count = 0,
            me = this,
            store = this.getReportReportsStore().first().filters();

        store.each(function (record) {
            // TODO: change to 'Field'
            var type = record.getAvailableField().get('filterType') || record.get('filterType'),
                panel_class = me.setFilterPanelClass(type),
                filter_panel = null;

            if (panel_class !== null) {
                filter_panel = Ext.create(panel_class, {
                    record: record,
                    panelNumber: ++count
                });

                filter_container.add(filter_panel);
            }
        });

        return filter_container;
    },

    refreshFilters: function () {
        var filterContainer = null;

        this.getFilters().removeAll();

        filterContainer = this.generateFilterPanels();

        this.getFilters().add(filterContainer);
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
            case 'Enum': panelClass = 'PICS.view.report.filter.ListFilter'; break;
            case 'Autocomplete': panelClass = 'PICS.view.report.filter.AutocompleteFilter'; break;
            default: panelClass = null; break;
        }

        //if (type !== 'Float') {panelClass = null;}  Override to show only a specific filterType
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
