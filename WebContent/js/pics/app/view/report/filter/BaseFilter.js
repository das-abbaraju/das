Ext.define('PICS.view.report.filter.BaseFilter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.basefilter'],

    border: false,
    defaults: {
      border: false
    },
    items: [{
        xtype: 'panel',
        bodyStyle: 'background:transparent;',         
        html: "Field Name"
    }],
    record: null,    
    applyFilter: function() {
        //console.log("base applyFilter");
    },
    constructor: function (config) {
        if (config) {
            if (config.displayMode === 'docked') {
                this.width = 425;
                this.layout = 'hbox',
                this.bodyStyle = 'background:transparent;border:0px;';
                this.displayMode = 'docked'; 
            } else {
                this.bbar = [{
                    xtype: 'button',
                    action: 'apply',
                    cls: 'x-btn-default-small',
                    itemId: 'apply',
                    align: 'right',                
                    listeners: {
                        click: function (target) {
                            target.up('basefilter').applyFilter();
                        }
                    },
                    text: 'Apply'
                },{
                    xtype: 'checkbox',
                    align: 'right',
                    boxLabel: 'Dock',
                    name: 'dockFilter',
                    inputValue: '1'
                }];
            }
        }
        this.callParent(arguments);
    },
    setRecord: function (record) {
        this.record = record;
    },
    setTitle: function () {
        this.items.items[0].html = "<h1>" + this.record.data.field.data.text + "</h1>";
    }
});