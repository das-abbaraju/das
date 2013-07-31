(function ($) {
    PICS.define('widget.chart.custom-color-chart.FlagsChart', {
        extend: 'widget.chart.CustomColorChart',

        methods: {
            init: function () {
                this.style_type = 'Flags';
                this.type_color_associations = {
                    'red-flag': '#ac030c',
                    'amber-flag': '#e67e1c',
                    'green-flag': '#229322'
                };

                this.loadCharts();
            },

            getStyleType: function () {
                return this.style_type;
            },

            getTypeColorAssociations: function () {
                return this.type_color_associations;
            },

            getChartOptions: function (data_table) {
                var options = {};

                options.colors = this.getColors(data_table, this.getTypeColorAssociations());

                return options;
            }
        }
    });
}(jQuery));