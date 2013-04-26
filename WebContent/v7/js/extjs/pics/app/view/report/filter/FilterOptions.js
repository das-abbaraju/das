Ext.define('PICS.view.report.filter.FilterOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilteroptions'],

    requires: [
        'PICS.view.report.filter.FilterHeader',
        'PICS.view.report.filter.FilterPlaceholder',
        'PICS.view.report.filter.FilterFormula',
        'PICS.view.report.filter.FilterToolbar'
    ],

    autoScroll: true,
    bodyBorder: false,
    border: 0,
    collapsed: true,
    collapsible: true,
    dockedItems: [{
        xtype: 'reportfiltertoolbar',
        dock: 'top'
    }, {
        bodyBorder: false,
        border: 0,
        dock: 'bottom',
        height: 10,
        id: 'report_filter_options_footer'
    }],
    floatable: false,
    header: {
        xtype: 'reportfilterheader'
    },
    id: 'report_filter_options',
    margin: '0 20 0 0',
    placeholder: {
        xtype: 'reportfilterplaceholder'
    },
    title: PICS.text('Report.execute.filterOptions.title'),
    width: 320,
    
    showFormula: function () {
        var formula = this.down('reportfilterformula'), 
            toolbar = this.down('reportfiltertoolbar');
        
        if (formula) {
            return;
        }
        
        if (toolbar) {
            this.removeDocked(toolbar);
        }
        
        this.addDocked({
            xtype: 'reportfilterformula',
            dock: 'top'
        });
    },
    
    showToolbar: function () {
        var formula = this.down('reportfilterformula'), 
            toolbar = this.down('reportfiltertoolbar');
        
        if (toolbar) {
            return;
        }
        
        if (formula) {
            this.removeDocked(formula);
        }
        
        this.addDocked({
            xtype: 'reportfiltertoolbar',
            dock: 'top'
        });
    },
    
    updateBodyHeight: function () {
        var body = Ext.getBody(),
            body_height = body.getHeight(),
            filters = this.down('#report_filters'),
            filters_height = filters.getHeight(),
            filters_element = filters.getEl(),
            filters_offset_y = filters_element.getY(),
            filter_options_body_height;
        
        // if filters show fully on screen
        if (body_height > filters_offset_y + filters_height) {
            filter_options_body_height = filters_height;
        } else {
            filter_options_body_height = filters_height - ((filters_offset_y + filters_height) - body_height); 
        }
        
        this.body.setHeight(filter_options_body_height);
    },
    
    updateFooterPosition: function () {
        var filter_header = this.down('reportfilterheader'),
            filter_header_height = filter_header.getHeight(),
            filter_formula = this.down('reportfilterformula'),
            filter_toolbar = this.down('reportfiltertoolbar'),
            filters = this.down('#report_filters'),
            filters_height = filters.getHeight(),
            filter_footer = this.down('#report_filter_options_footer'),
            filter_offset = filter_header_height + filters_height;
        
        if (filter_formula) {
            filter_offset += filter_formula.getHeight(); 
        } else if (filter_toolbar) {
            filter_offset += filter_toolbar.getHeight();
        }
        
        filter_footer.setPosition(0, filter_offset);
    }
});