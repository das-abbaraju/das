describe('translate directive', function () {
    var $compile, $rootScope, $interpolate, translationsService;

    beforeEach(angular.mock.module('PICS.translations'));

    beforeEach(inject(function (_$compile_, _$rootScope_, _$interpolate_, _translationsService_) {
            $compile = _$compile_;
            $rootScope = _$rootScope_;
            $interpolate = _$interpolate_;
            translationsService = _translationsService_;
    }));

    it('should replace the interpolation expression with the proper translation value', function () {
        var linkingFn, element, parsedExpression, interpolatedExpression;

        translationsService.setTranslations({
            'my.first.translation.key': 'My {1} value for translation #{2}'
        });

        linkingFn = $compile([
            "<p translate>",
                "{{ text['my.first.translation.key'] | translationValues:['translation', '1'] }}",
            "</p>"
        ].join(''));

        element = linkingFn($rootScope);
        parsedExpression = $interpolate(element.html());
        interpolatedExpression = $rootScope.$eval(parsedExpression);

        expect(interpolatedExpression).toEqual('My translation value for translation #1');
    });
});