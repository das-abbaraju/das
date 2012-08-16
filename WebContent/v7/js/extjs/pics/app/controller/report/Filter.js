Ext.define('PICS.controller.report.Filter', {
    extend: 'Ext.app.Controller',

    refs: [{
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
                beforerender: this.onFilterOptionsBeforeRender
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

            // load filter formula expression
            'reportfilterformula': {
                beforerender: this.onFilterFormulaBeforeRender
            },

            // save filter formula expression
            'reportfilterformula textfield[name=filter_formula]': {
                blur: this.onFilterFormulaBlur,
                specialkey: this.onFilterFormulaInputEnter
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
            'reportfilterbasedatefilter [name=filter_value]': {
                select: this.onFilterValueSelect
            },

            // saving edits to filter store + refresh
            '#report_filters [name=filter_value]': {
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
        var store = this.getReportReportsStore();

        if (!store.isLoaded()) {
            store.on('load', function (store, records, successful, eOpts) {
                var report = store.first();

                this.application.fireEvent('refreshfilters');

                if (report && report.get('filterExpression') != '') {
                    this.showFilterFormula();
                }
            }, this);
        } else {
            var report = store.first();

            this.application.fireEvent('refreshfilters');

            if (report && report.get('filterExpression') != '') {
                this.showFilterFormula();
            }
        }
    },

    onFilterOptionsCollapse: function (cmp, event, eOpts) {
        var filter_options = this.getFilterOptions();

        filter_options.collapse();
    },

    onFilterOptionsExpand: function (cmp, event, eOpts) {
        var filter_options = this.getFilterOptions();

        filter_options.expand();
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
        this.showFilterFormula();
    },

    showFilterFormula: function () {
        var filter_options = this.getFilterOptions(),
        filter_toolbar = this.getFilterToolbar();

        if (!filter_options) {
            return false;
        }

        var filter_formula = {
            xtype: 'reportfilterformula',
            dock: 'top'
        };

        filter_options.removeDocked(filter_toolbar);
        filter_options.addDocked(filter_formula);
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

    onFilterFormulaBeforeRender: function (cmp, eOpts) {
        var store = this.getReportReportsStore(),
            report = store.first(),
            filter_formula = report.get('filterExpression'),
            filter_formula_expression = this.getFilterFormulaExpression(),
            filters = this.getFilters();

        if (filter_formula != '') {
            filter_formula = this.formatFilterFormula(filter_formula);

            filter_formula_expression.setValue(filter_formula);
        }

        if (filters) {
            filters.addCls('x-active');
        }
    },

    formatFilterFormula: function (formula) {
        var formatted = formula.replace(/[{}]/g, '');

        formatted = formatted.replace(/\d+/g, function(val) {
            return parseInt(val) + 1;
        });

        return formatted;
    },

    onFilterFormulaBlur: function (cmp, event, eOpts) {
        this.saveFilterFormula();
    },

    onFilterFormulaInputEnter: function (cmp, event) {
        if (event.getKey() != event.ENTER) {
            return false;
        }

        this.saveFilterFormula();

        this.application.fireEvent('refreshreport');
    },

    saveFilterFormula: function () {
        var store = this.getReportReportsStore(),
            report = store.first(),
            filter_formula = this.getFilterFormulaExpression().getValue();

        // Hack: because this is broken
        if (filter_formula == '') {
            report.set('filterExpression', filter_formula);

            return false;
        }

        // TODO write a real grammar and parser for our filter formula DSL

        // Split into tokens
        var validTokenRegex = /[0-9]+|\(|\)|and|or/gi;
        filter_formula = filter_formula.replace(validTokenRegex, ' $& ');

        var tokens = filter_formula.trim().split(/ +/);
        filter_formula = '';

        // Check for invalid tokens and make sure parens are balanced
        var parenCount = 0;
        for (var i = 0; i < tokens.length; i += 1) {
            var token = tokens[i];

            if (token.search(validTokenRegex) === -1) {
                return false;
            }

            if (token === '(') {
                parenCount += 1;
                filter_formula += token;
            } else if (token === ')') {
                parenCount -= 1;
                filter_formula += token;
            } else if (token.toUpperCase() === 'AND') {
                filter_formula += ' AND ';
            } else if (token.toUpperCase() === 'OR') {
                filter_formula += ' OR ';
            } else if (token.search(/[0-9]+/) !== -1) {
                if (token === '0') {
                    return false;
                }

                // Convert from counting number to index
                var indexNum = new Number(token) - 1;
                filter_formula += '{' + indexNum + '}';
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

        report.set('filterExpression', filter_formula);
    },

    /**
     * Filters
     */

    refreshFilters: function () {
        var report_store = this.getReportReportsStore();
            report = report_store.first(),
            filter_store = report.filters(),
            filter_options = this.getFilterOptions();

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
});
