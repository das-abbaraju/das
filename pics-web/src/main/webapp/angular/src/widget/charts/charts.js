angular.module('PICS.charts', [])

.factory('AnimatedArcChart', function() {
    var chart = function (data) {
        this.data = data;
    };

    chart.prototype.getData = function () {
        return this.data;
    };

    chart.prototype.drawChart = function (element, width, height) {
        var arcStartAngle = -0.5 * Math.PI,
            arcEndAngle = 0.5 * Math.PI;

        var createArc = d3.svg.arc()
            .outerRadius(height)
            .innerRadius(height - 80);

        var pie = d3.layout.pie()
            .sort(null)
            .startAngle(arcStartAngle)
            .endAngle(arcEndAngle)
            .value(function(d) { return d.amount; });


        var chart = d3.select(element)
                        .append('svg')
                        .attr('height', height)
                        .attr('width', width)
                        .append('g')
                        .attr('transform', 'translate(' + width/2 + ',' + height + ')');

        chart.selectAll('.arc')
            .data(pie(this.data))
            .enter()
            .append('g')
            .append('path')
            .attr('fill', function(d, i) { return d.data.color; })
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