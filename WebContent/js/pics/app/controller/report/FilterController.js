Ext.define('PICS.controller.report.FilterController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportColumnSelector',
        selector: 'reportcolumnselector'
    }, {
        ref: 'filterOptions',
        selector: 'filteroptions'
    }, {
        ref: 'filters',
        selector: 'filteroptions #report_filters'
    }, {
        ref: 'advancedFilter',
        selector: 'filteroptions advancedfilter'
    }, {
        ref: 'advancedExpression',
        selector: 'filteroptions advancedfilter textfield[name=filterexpression]'
    }],

    stores: [
        'report.AvailableFields',
        'report.AvailableFieldsByCategory',
        'report.Reports'
    ],

    init: function() {
        var that = this;

        this.control({
            'filteroptions #report_filters': {
                render: function () {
                    if (this.getReportReportsStore().isLoading()) {
                        this.getReportReportsStore().addListener({
                            load: function (store, records, successful, eOpts) {
                                that.application.fireEvent('refreshfilters');
                                Ext.defer(that.loadAdvancedFilter, 1, this) //TODO: refactor to take out defer
                            }
                        });
                    } else {
                        this.application.fireEvent('refreshfilters');
                        Ext.defer(that.loadAdvancedFilter, 1, this) //TODO: refactor to take out defer
                    }
                }
            },
            'filteroptions button[action=add-filter]': {
                click: function () {
                    that.application.fireEvent('showcolumnselector', {
                        columnSelectorType: 'filter'
                    });
                }
            },
            'filteroptions button[action=search]': {
                click: function () {
                    that.application.fireEvent('refreshreport');
                }
            },
            'filteroptions button[action=update]': {
                click: function () {
                    this.applyAdvancedFilter();
                }
            },
            'filteroptions button[action=remove-filter]': {
                click: this.removeFilter
            },
            'filteroptions menuitem[action=toggle-advanced-filtering]': {
                click: this.createAdvancedFilter
            },
            '#report_filter_expression button[action=hide]': {
                click: this.removeAdvancedFilter
            }
        });

        this.application.on({
            refreshfilters: this.refreshFilters,
            scope: this
        });
    },

    applyAdvancedFilter: function () {
        var report = this.getReportReportsStore().first(),
            expression = this.getAdvancedExpression().value;

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

        this.application.fireEvent('refreshreport');
    },

    createAdvancedFilter: function () {
        var filter_options = this.getFilterOptions(),
            advanced_filter = this.getAdvancedFilter();

        if (!advanced_filter) {
            advanced_filter = Ext.create('PICS.view.report.FilterExpression');
        }

        filter_options.addDocked(advanced_filter);
    },

    formatExpression: function (expression) {
        var formatted = expression.replace(/[{}]/g, '');

        formatted = formatted.replace(/\d+/g, function(val) { return parseInt(val) + 1; });

        return formatted;
    },

    getFilterExpression: function () {
        var report = this.getReportReportsStore(),
            filter_expression = report.first().get('filterExpression');

        return filter_expression;
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

            var availableFields = me.getReportAvailableFieldsStore();
            var sortName = record.get('name');
            var filter_name = availableFields.findRecord('name', sortName).get('text');

            if (filter_name.length >= 29) {
                filter_name = filter_name.substring(0, 29) + '...';
            }

            record.set('text', filter_name);

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

    loadAdvancedFilter: function () {
        var expression = this.getFilterExpression(),
            filter_options = this.getFilterOptions();

        if (expression !== '') {
            filter_expression = this.formatExpression(expression);
            advanced_filter = Ext.create('PICS.view.report.FilterExpression', {expression: filter_expression});
            filter_options.addDocked(advanced_filter);
        }
    },

    refreshFilters: function () {
        var filterContainer = null;
        var filterContainer = this.generateFilterPanels();

        this.getFilters().removeAll();
        this.getFilters().add(filterContainer);
    },

    removeFilter: function (component, e, options) {
        var record = component.up('basefilter').record,
        store = this.getReportReportsStore().first().filters();

        store.remove(record);

        this.application.fireEvent('refreshfilters');
        this.application.fireEvent('refreshreport');
    },

    removeAdvancedFilter: function (button, event, options) {
        var options = this.getFilterOptions(),
            advanced_filter = this.getAdvancedFilter(),
            report = this.getReportReportsStore();

        options.removeDocked(advanced_filter);

        report.first().set('filterExpression', '');

        this.application.fireEvent('refreshreport');
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
