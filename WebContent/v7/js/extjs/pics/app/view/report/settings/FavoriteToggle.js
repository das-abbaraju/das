Ext.define('PICS.view.report.settings.FavoriteToggle', {
    extend: 'Ext.form.field.Display',
    alias: ['widget.favoritetoggle'],
    
    fieldLabel: '<i class="favorite icon-star"></i>',
    labelAlign: 'right',
    labelSeparator: '',
    value: 'Report <strong class="favorite-text">is not</strong> a Favorite',
    
    listeners: {
        afterrender: function(cmp, eOpts) {
            this.mon(this.getEl(), 'click', this.toggleFavoriteStatus, this, {
                delegate: '.icon-star'
            });
        }
    },
    
    toggleFavoriteStatus: function () {
        var element = this.getEl(),
            icon = element.down('.icon-star'),
            is_favorite = icon.hasCls('selected');
        
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
        
        this.fireEvent('favorite', this);
    },
    
    toggleUnfavorite: function () {
        var element = this.getEl(),
            icon = element.down('.icon-star'),
            text = element.down('.favorite-text');
        
        icon.removeCls('selected');
        text.setHTML('is not');
        
        this.fireEvent('unfavorite', this);
    }
});