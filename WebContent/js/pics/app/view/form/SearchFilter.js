Ext.define('PICS.view.form.SearchFilter', {
    extend: 'Ext.form.field.Text',    
    alias: ['widget.searchFilter'],
    
    enableKeyEvents: true,
    listeners: {
        keyup: function (target, event) {
            var store = Ext.StoreMgr.lookup(this.store),
            filterValue = Ext.getCmp('filterfield').getValue(),
            filter = new Ext.util.Filter({
                 property: this.fields,
                 value: filterValue,
                 anyMatch: true,
                 caseSensitive: false,
                 root: 'data'
            });
            
            store.clearFilter();
            store.filter(filter);            
        }
    },
    
    initComponent: function () {
        Ext.override(Ext.util.Filter, {
            createFilterFn: function() {
                var me = this,
                matcher  = me.createValueMatcher(),
                property = !Ext.isArray(me.property) ? me.property.split(',') : me.property
                
                return function(item) {
                    var hasmatch = false;
                    
                    for(var i = 0; i < property.length; i++) {
                        if(matcher.test(me.getRoot.call(me, item)[property[i]])) {
                            hasmatch = true;
                            break;
                        }
                    }
                    
                    return matcher === null ? value === null : hasmatch;
                };
            }
        });
        
        this.callParent();
    }
});