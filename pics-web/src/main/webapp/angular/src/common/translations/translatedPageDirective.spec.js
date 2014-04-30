describe('translatedPage directive', function () {
    var $compile, $rootScope, $interpolate, $log, translationsService;

    beforeEach(angular.mock.module('PICS.translations'));

    beforeEach(inject(function (_$compile_, _$rootScope_, _$interpolate_, _$log_, _translationsService_) {
            $compile = _$compile_;
            $rootScope = _$rootScope_;
            $log = _$log_;
            translationsService = _translationsService_;
    }));

    it('should cause a mapping of the route to associated translation keys to be logged to the console once $includedContentLoaded is fired', function () {
        var linkingFn, nextMock;

        translationsService.setLogKeysToConsole(true);

        translationsService.setTranslations({
            'my.first.translation.key': 'My {1} value for translation #{2}'
        });

        nextMock = {
            $$route: {
                originalPath: '/fakeroute'
            }
        };

        $rootScope.$broadcast('$routeChangeStart', nextMock);

        linkingFn = $compile([
            "<div translated-page>",
                "<p translate>",
                    "{{ text['my.first.translation.key'] }}",
                "</p>",
                "<p translate>",
                    "{{ text['my.second.translation.key'] }}",
                "</p>",
            "</div>"
        ].join(''));

        spyOn($log, 'info');

        element = linkingFn($rootScope);

        $rootScope.$broadcast('$includeContentLoaded');

        expect($log.info).toHaveBeenCalledWith('"/fakeroute":["my.first.translation.key","my.second.translation.key"]');
    });
});