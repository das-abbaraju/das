Ext.define('PICS.view.report.settings.FavoriteToggle', {
    extend: 'Ext.form.field.Hidden',
    alias: 'widget.reportfavoritetoggle',
    
    beforeBodyEl: 'Report <strong class="favorite-text">is not</strong> a Favorite',
    fieldLabel: '<i class="favorite icon-star"></i>',
    hideLabel: false,
    labelAlign: 'right',
    labelSeparator: '',
    name: 'is_favorite',
    value: false,
    
    listeners: {
        afterrender: function(cmp, eOpts) {
            this.mon(this.getEl(), 'click', this.toggleFavoriteStatus, this, {
                delegate: '.icon-star'
            });
        }
    },
    
    getFavoriteStatus: function () {
        var element = this.getEl(),
            icon = element.down('.icon-star');
        
        return icon.hasCls('selected');
    },
    
    toggleFavoriteStatus: function () {
        var is_favorite = this.getFavoriteStatus();
        
        if (is_favorite) {
            this.toggleUnfavorite();
        } else {
            this.toggleFavorite();
        }
    },
    
    toggleFavorite: function () {
        var element = this.getEl(),
            icon = element.down('.icon-star'),
            text = element.down('.favorite-text');
        
        icon.addCls('selected');
        text.setHTML('is');
        
        this.setValue(true);
        
        this.fireEvent('favorite', this);
    },
    
    toggleUnfavorite: function () {
        var element = this.getEl(),
            icon = element.down('.icon-star'),
            text = element.down('.favorite-text');
        
        icon.removeCls('selected');
        text.setHTML('is not');
        
        this.setValue(false);
        
        this.fireEvent('unfavorite', this);
    }
});