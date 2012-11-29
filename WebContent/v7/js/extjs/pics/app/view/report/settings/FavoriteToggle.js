Ext.define('PICS.view.report.settings.FavoriteToggle', {
    extend: 'Ext.form.field.Display',
    alias: ['widget.favoritetoggle'],
    
    fieldLabel: '<i class="favorite icon-star"></i>',
    labelAlign: 'right',
    labelSeparator: '',
    value: 'Report <strong class="favorite-text">is not</strong> a Favorite',

    initComponent: function () {
        this.callParent(arguments);
    },

    listeners: {
        afterrender: function(cmp, eOpts) {
            var config = PICS.app.configuration;

            if (config.isFavorite()) {
                this.toggleFavoriteStatus();
            }
            
            this.mon(this.el,'click', this.toggleFavoriteStatus, this, {
                delegate: '.icon-star'
            });            
        }
    },

    getFavoriteElements: function () {
        var element = this.getEl();

        return {
            icon: element.down('.icon-star'),
            text: element.down('.favorite-text')
        };
    },

    favoriteSelected: function (favorite_elements) {
        var favorite_elements = favorite_elements || this.getFavoriteElements();

        return favorite_elements.icon.hasCls('selected');
    },

    saveFavoriteStatus: function (setting) {
        var event_name = setting ? 'favorite' : 'unfavorite',
            config = PICS.app.configuration;

        this.fireEvent(event_name);
        config.setIsFavorite(setting);
    },

    selectFavorite: function (favorite_elements) {
        var favorite_elements = favorite_elements || this.getFavoriteElements();

        favorite_elements.icon.addCls('selected');
        favorite_elements.text.setHTML('is');
    },
    
    selectNotFavorite: function (favorite_elements) {
        var favorite_elements = favorite_elements || this.getFavoriteElements();

        favorite_elements.icon.removeCls('selected');
        favorite_elements.text.setHTML('is not');
    },
    
    toggleFavoriteStatus: function () {
        var favorite_elements = favorite_elements || this.getFavoriteElements(),
            favorite_selected = this.favoriteSelected(favorite_elements),
            is_editable = PICS.app.configuration.isEditable(),
            duplicating = this.up('panel').$className == 'PICS.view.report.settings.CopySettings';

        favorite_selected ? this.selectNotFavorite(favorite_elements) : this.selectFavorite(favorite_elements);

        if (!is_editable && !duplicating) {
            this.saveFavoriteStatus(!favorite_selected);
        }
    }
});