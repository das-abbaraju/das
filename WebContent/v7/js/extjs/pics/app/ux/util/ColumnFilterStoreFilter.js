Ext.define('PICS.ux.util.ColumnFilterStoreFilter', {
    extend: 'Ext.util.Filter',
    
    anyMatch: true,
    property: [
        'category',
        'description'
    ],
    root: 'data',
    
    createFilterFn: function () {
        var me = this,
            matcher = me.createValueMatcher(),
            property = !Ext.isArray(me.property) ? me.property.split(',') : me.property;

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