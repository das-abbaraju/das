angular.module('PICS.charts')

.factory('AnimatedArcChart', function() {
    var chart = function (data) {
        this.data = data;
    };

    var DRAW_OPTION_DEFAULTS = {
        colors: ['#33b679', '#f4b400', '#e74c3c'],
        height: 200,
        width: 420
    };

    chart.prototype.getData = function () {
        return this.data;
    };

    chart.prototype.draw = function (options) {
        options = angular.extend({}, DRAW_OPTION_DEFAULTS, options);

        if (!options.element) {
            throw 'No element found';
        }

        var width = options.width,
            height = options.height,
            colors = options.colors,
            element = options.element;

        var arcStartAngle = -0.5 * Math.PI,
            arcEndAngle = 0.5 * Math.PI,
            data = this.getData();

        var createArc = d3.svg.arc()
            .outerRadius(height)
            .innerRadius(height - 80);

        var pie = d3.layout.pie()
            .sort(null)
            .startAngle(arcStartAngle)
            .endAngle(arcEndAngle)
            .value(function(d) { return d; });

        d3.select('chart').html('');

        var chart = d3.select(element)
                        .append('svg')
                        .attr('height', height)
                        .attr('width', width)
                        .append('g')
                        .attr('transform', 'translate(' + width/2 + ',' + height + ')');

        chart.selectAll('.arc')
            .data(pie(data))
            .enter()
            .append('g')
            .append('path')
            .attr('fill', function(d, i) { return colors[i]; })
            .transition()
            .duration(1500)
            .attrTween('d', animatePie);

        function animatePie(d) {
            var i = d3.interpolate({startAngle: arcStartAngle, endAngle: arcStartAngle}, d);
            return function(time) { return createArc(i(time)); };
        }
    };

    return chart;
});