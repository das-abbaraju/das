describe('translatedPage directive', function () {
    var $compile, $rootScope, $interpolate, $log, translationsService;

    beforeEach(angular.mock.module('PICS.translations'));

    beforeEach(inject(function (_$compile_, _$rootScope_, _$interpolate_, _$log_, _$http_, _translationsService_) {
            $compile = _$compile_;
            $rootScope = _$rootScope_;
            $log = _$log_;
            $http = _$http_;
            translationsService = _translationsService_;
    }));

    function setup(setDevelopmentMode) {
        var linkingFn, nextMock;

        translationsService.setDevelopmentMode(setDevelopmentMode);

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
        spyOn($http, 'post');

        element = linkingFn($rootScope);

        $rootScope.$broadcast('$viewContentLoaded');
    }

    it('should cause a mapping of the route to associated translation keys to be logged to the console once $viewContentLoaded is fired', function () {
        setup('on');
        expect($log.info).toHaveBeenCalledWith('"/fakeroute":["my.first.translation.key","my.second.translation.key"]');
    });

    it('should request an update to the translations keys file once $viewContentLoaded is fired', function () {
        setup('on');
        expect($http.post).toHaveBeenCalledWith(
            'http://localhost:8081',
            { '/fakeroute': [ 'my.first.translation.key', 'my.second.translation.key' ] }
        );
    });

    it('should not cause anything to be logged if setDevelopmentMode is passed the value of false', function () {
        setup('off');
        expect($log.info).not.toHaveBeenCalled();
    });

    it('should not request an update to the translations keys file once $viewContentLoaded is fired', function () {
        setup('off');
        expect($http.post).not.toHaveBeenCalled();
    });    
});