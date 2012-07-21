Ext.define('PICS.controller.report.FilterController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportColumnSelector',
        selector: 'reportcolumnselector'
    }, {
        ref: 'filterOptions',
        selector: 'reportfilteroptions'
    }, {
        ref: 'filterToolbar',
        selector: 'reportfilteroptions reportfiltertoolbar'
    }, {
        ref: 'filterFormula',
        selector: 'reportfilteroptions reportfilterformula'
    }, {
        ref: 'filterFormulaExpression',
        selector: 'reportfilteroptions reportfilterformula textfield[name=filter_formula]'
    }, {
        ref: 'filters',
        selector: 'reportfilteroptions #report_filters'
    }],

    stores: [
        'report.AvailableFields',
        'report.AvailableFieldsByCategory',
        'report.Reports'
    ],

    views: [
        'PICS.view.report.filter.Filters',
        'PICS.view.report.filter.Formula',
        'PICS.view.report.ColumnSelector'
    ],

    init: function() {
        var that = this;

        this.control({
            // collapse filter options
            '#report_filter_options_collapse': {
                click: function (cmp, event, eOpts) {
                    this.getFilterOptions().collapse();
                }
            },

            // expand filter options
            '#report_filter_options_expand': {
                click: function (cmp, event, eOpts) {
                    this.getFilterOptions().expand();
                }
            },

            // add filter
            'reportfilteroptions button[action=add-filter]': {
                click: function (cmp, event, eOpts) {
                    that.application.fireEvent('showcolumnselector', {
                        columnSelectorType: 'filter'
                    });
                }
            },

            // show filter formula
            'reportfiltertoolbar button[action=show-filter-formula]': {
                click: function (cmp, event, eOpts) {
                    this.filterFormulaAdd();

                    this.getFilters().addCls('x-active');
                }
            },

            // hide filter formula
            'reportfilterformula button[action=cancel]': {
                click: function (cmp, event, eOpts) {
                    this.filterFormulaRemove();

                    this.getFilters().removeCls('x-active');
                }
            },

            // show filter-number and remove-filter on filter focus
            '\
            #report_filters combobox,\
            #report_filters textfield,\
            #report_filters numberfield,\
            #report_filters datefield,\
            #report_filters checkbox,\
            #report_filters comboboxselect,\
            ': {
                blur: function (cmp, event, eOpts) {
                    var filter = this.findParentFilter(cmp);

                    if (filter) {
                        filter.removeCls('x-form-focus');
                    }
                },
                focus: function (cmp, event, eOpts) {
                    var filter = this.findParentFilter(cmp);

                    if (filter) {
                        filter.addCls('x-form-focus');
                    }
                }
            },

            // remove filter
            'reportfilteroptions button[action=remove-filter]': {
                click: function (cmp, event, eOpts) {
                    var filter_store = this.getReportReportsStore().first().filters();
                    var record = cmp.up('reportfilterfilter').record;

                    if (record) {
                        filter_store.remove(record);

                        this.application.fireEvent('refreshfilters');
                        this.application.fireEvent('refreshreport');
                    }
                }
            }
        });

        this.application.on({
            refreshfilters: this.refreshFilters,
            scope: this
        });
    },

    onLaunch: function () {
        var report_store = this.getReportReportsStore();

        report_store.load({
            callback: function (store, records, successful, eOpts) {
                this.application.fireEvent('refreshfilters');
            },
            scope: this
        });
    },

    filterFormulaAdd: function () {
        var filter_options = this.getFilterOptions();
        var filter_toolbar = this.getFilterToolbar();
        var filter_formula = Ext.create('PICS.view.report.filter.Formula');

        filter_options.removeDocked(filter_toolbar);
        filter_options.addDocked(filter_formula);
    },

    filterFormulaRemove: function () {
        var filter_options = this.getFilterOptions();
        var filter_toolbar = Ext.create('PICS.view.report.filter.Toolbar');
        var filter_formula = this.getFilterFormula();

        filter_options.removeDocked(filter_formula);
        filter_options.addDocked(filter_toolbar);

        this.application.fireEvent('refreshreport');
    },

    findParentFilter: function (cmp) {
        return cmp.findParentBy(function (cmp) {
            return cmp.cls == 'filter';
        });
    },

    refreshFilters: function () {
        var filter_store = this.getReportReportsStore().first().filters();
        var filter_options = this.getFilterOptions();

        // remove all filters
        filter_options.removeAll();

        // create new list of filters
        var filters = Ext.create('PICS.view.report.filter.Filters', {
            store: filter_store
        });

        // add new filters
        filter_options.add(filters);
    },

    /*applyFilterFormula: function () {
        var report = this.getReportReportsStore().first(),
            formula = this.getFilterFormulaExpression().value;

        // TODO write a real grammar and parser for our filter formula DSL

        // Split into tokens
        var validTokenRegex = /[0-9]+|\(|\)|and|or/gi;
        formula = formula.replace(validTokenRegex, ' $& ');

        var tokens = formula.trim().split(/ +/);
        formula = '';

        // Check for invalid tokens and make sure parens are balanced
        var parenCount = 0;
        for (var i = 0; i < tokens.length; i += 1) {
            var token = tokens[i];

            if (token.search(validTokenRegex) === -1) {
                return false;
            }

            if (token === '(') {
                parenCount += 1;
                formula += '{';
            } else if (token === ')') {
                parenCount -= 1;
                formula += '}';
            } else if (token.toUpperCase() === 'AND') {
                formula += ' AND ';
            } else if (token.toUpperCase() === 'OR') {
                formula += ' OR ';
            } else if (token.search(/[0-9]+/) !== -1) {
                if (token === '0') {
                    return false;
                }

                // Convert from counting number to index
                var indexNum = new Number(token) - 1;
                formula += '{' + indexNum + '}';
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

        report.set('filterExpression', formula);

        this.application.fireEvent('refreshreport');
    },*/

    /*formatFormula: function (formula) {
        var formatted = formula.replace(/[{}]/g, '');

        formatted = formatted.replace(/\d+/g, function(val) { return parseInt(val) + 1; });

        return formatted;
    },

    getFilterFormulaExpressionFromReport: function () {
        var report = this.getReportReportsStore(),
            filter_formula = report.first().get('filterExpression');

        return filter_formula;
    },*/

    /*loadFilterFormula: function () {
        var expression = this.getFilterFormulaExpressionFromReport(),
            filter_options = this.getFilterOptions();

        if (expression !== '') {
            var formula = this.formatFormula(expression);
            var advanced_filter = Ext.create('PICS.view.report.filter.Formula', {
                formula: formula
            });

            filter_options.addDocked(advanced_filter);
        }
    },*/

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
