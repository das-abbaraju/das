Ext.define('PICS.controller.report.Filter', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportColumnSelector',
        selector: 'reportcolumnselector'
    }, {
        ref: 'filterFooter',
        selector: '#report_filter_options_footer'
    }, {
        ref: 'filterFormula',
        selector: 'reportfilteroptions reportfilterformula'
    }, {
        ref: 'filterFormulaExpression',
        selector: 'reportfilteroptions reportfilterformula textfield[name=filter_formula]'
    }, {
        ref: 'filterHeader',
        selector: 'reportfilterheader'
    }, {
        ref: 'filterOptions',
        selector: 'reportfilteroptions'
    }, {
        ref: 'filters',
        selector: 'reportfilteroptions #report_filters'
    }, {
        ref: 'filterToolbar',
        selector: 'reportfilteroptions reportfiltertoolbar'
    }],

    stores: [
        'report.AvailableFields',
        'report.AvailableFieldsByCategory',
        'report.Reports'
    ],

    views: [
        'PICS.view.report.filter.Filters',
        'PICS.view.report.filter.FilterFormula',
        'PICS.view.report.filter.FilterToolbar'
    ],

    init: function() {
        this.control({
            // render filter options
            'reportfilteroptions': {
                afterlayout: this.onFilterOptionsAfterLayout,
                beforerender: this.onFilterOptionsBeforeRender,
            },

            // collapse filter options
            '#report_filter_options_collapse': {
                click: this.onFilterOptionsCollapse
            },

            // expand filter options
            '#report_filter_options_expand': {
                click: this.onFilterOptionsExpand
            },

            // add filter
            'reportfilteroptions button[action=add-filter]': {
                click: this.onAddFilter
            },

            // show filter formula
            'reportfiltertoolbar button[action=show-filter-formula]': {
                click: this.onFilterFormulaShow
            },

            // hide filter formula
            'reportfilterformula button[action=cancel]': {
                click: this.onFilterFormulaCancel
            },

            // show filter-number and remove-filter on filter focus
            '\
            #report_filters combobox,\
            #report_filters textfield,\
            #report_filters numberfield,\
            #report_filters datefield,\
            #report_filters checkbox,\
            #report_filters comboboxselect\
            ': {
                blur: this.onFilterBlur,
                focus: this.onFilterFocus
            },

            // saving edits to filter store + refresh
            '#report_filters combobox[name=filter_value]': {
                select: this.onFilterValueSelect
            },

            '#report_filters combobox[name=operator]': {
                select: this.onFilterOperatorSelect
            },

            // saving edits to filter store + refresh
            '#report_filters datefield': {
                select: this.onFilterValueSelect
            },

            // saving edits to filter store + refresh
            '\
            #report_filters textfield,\
            #report_filters numberfield,\
            #report_filters datefield\
            ': {
                blur: this.onFilterValueInputBlur,
                specialkey: this.onFilterValueInputEnter
            },

            // saving edits to filter store + refresh
            '#report_filters checkbox': {
                change: this.onFilterValueSelect
            },

            // remove filter
            'reportfilteroptions button[action=remove-filter]': {
                click: this.onFilterRemove
            }
        });

        this.application.on({
            refreshfilters: this.refreshFilters,
            scope: this
        });
    },

    /**
     * Filter Options
     */

    onFilterOptionsAfterLayout: function (cmp, eOpts) {
        var filters = this.getFilters();

        if (!filters) {
            return;
        }

        var body = Ext.getBody(),
            filter_footer = this.getFilterFooter(),
            filter_formula = this.getFilterFormula(),
            filter_header = this.getFilterHeader(),
            filter_toolbar = this.getFilterToolbar(),
            filter_offset;

        // if filters show fully on screen
        if (body.getHeight() > (filters.el.getY() + filters.getHeight())) {
            cmp.body.setHeight(filters.getHeight());

        // if filters bleed off screen
        } else {
            cmp.body.setHeight(filters.getHeight() - ((filters.el.getY() + filters.getHeight()) - body.getHeight()));
        }

        if (filter_toolbar) {
            filter_offset = filter_header.getHeight() + filter_toolbar.getHeight() + filters.getHeight();
        } else if (filter_formula) {
            filter_offset = filter_header.getHeight() + filter_formula.getHeight() + filters.getHeight();
        }

        if (filter_offset) {
            filter_footer.setPosition(0, filter_offset);
        }
    },

    onFilterOptionsBeforeRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore();

        if (report_store.isLoading()) {
            report_store.load({
                callback: function (store, records, successful, eOpts) {
                    this.application.fireEvent('refreshfilters');
                },
                scope: this
            });
        } else {
            this.application.fireEvent('refreshfilters');
        }
    },

    onFilterOptionsCollapse: function (cmp, event, eOpts) {
        this.getFilterOptions().collapse();
    },

    onFilterOptionsExpand: function (cmp, event, eOpts) {
        this.getFilterOptions().expand();
    },

    /**
     * Add Filter
     */

    onAddFilter: function (cmp, event, eOpts) {
        this.application.fireEvent('showavailablefieldmodal', 'filter');
    },

    /**
     * Filter Formula
     */

    onFilterFormulaShow: function (cmp, event, eOpts) {
        var filter_options = this.getFilterOptions(),
            filter_toolbar = this.getFilterToolbar(),
            filters = this.getFilters();

        if (!filter_options) {
            return false;
        }

        var filter_formula = {
            xtype: 'reportfilterformula',
            dock: 'top'
        };

        filter_options.removeDocked(filter_toolbar);
        filter_options.addDocked(filter_formula);

        filters.addCls('x-active');
    },

    onFilterFormulaCancel: function (cmp, event, eOpts) {
        var filter_options = this.getFilterOptions(),
            filter_formula = this.getFilterFormula();

        var filter_toolbar = {
            xtype: 'reportfiltertoolbar',
            dock: 'top'
        };

        filter_options.removeDocked(filter_formula);
        filter_options.addDocked(filter_toolbar);

        this.getFilters().removeCls('x-active');
    },

    /**
     * Filters
     */

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

    /**
     * Filter
     */

    onFilterBlur: function (cmp, event, eOpts) {
        var filter = this.findParentFilter(cmp);

        if (filter) {
            filter.removeCls('x-form-focus');
        }
    },

    onFilterFocus: function (cmp, event, eOpts) {
        var filter = this.findParentFilter(cmp);

        if (filter) {
            filter.addCls('x-form-focus');
        }
    },

    onFilterOperatorSelect: function (cmp, records, eOpts) {
        var filter = this.findParentFilter(cmp);
        filter.record.set('operator', cmp.getValue());

        if (filter.record.get('value') != '') {
            this.application.fireEvent('refreshreport');
        }
    },

    onFilterRemove: function (cmp, event, eOpts) {
        var filter_store = this.getReportReportsStore().first().filters(),
            filter = this.findParentFilter(cmp),
            record = filter.record;

        if (record) {
            filter_store.remove(record);

            this.application.fireEvent('refreshfilters');

            if (record.get('value') != '') {
                this.application.fireEvent('refreshreport');
            }
        }
    },

    onFilterValueInputBlur: function (cmp, event, eOpts) {
        var filter = this.findParentFilter(cmp);
        filter.record.set('value', cmp.getValue());
    },

    onFilterValueInputEnter: function (cmp, event) {
        if (event.getKey() == event.ENTER) {
            var filter = this.findParentFilter(cmp);
            filter.record.set('value', cmp.getValue());

            this.application.fireEvent('refreshreport');
        }
    },

    onFilterValueSelect: function (cmp, records, eOpts) {
        var filter = this.findParentFilter(cmp);
        filter.record.set('value', cmp.getValue());

        this.application.fireEvent('refreshreport');
    },

    /**
     * MISC
     */

    findParentFilter: function (cmp) {
        return cmp.findParentBy(function (cmp) {
            return cmp.cls == 'filter';
        });
    }

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
});
