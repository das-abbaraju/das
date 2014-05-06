describe('translationsService', function() {
    var translationsService, keys, $rootScope, $timeout, $httpBackend;

    var requestParamsMock = {
        translationKeys: [
            'my.first.translation.key',
            'my.second.translation.key'
        ]
    };

    var responseMock = {
        translationsMap: {
            'my.first.translation.key': 'fake translation',
            'my.second.translation.key': 'fake translation 2' 
        }
    };

    beforeEach(angular.mock.module('PICS.translations'));

    beforeEach(inject(function(_translationsService_, _$rootScope_, _$timeout_, _$httpBackend_) {
            translationsService = _translationsService_;
            $rootScope = _$rootScope_;
            $timeout = _$timeout_;
            $httpBackend = _$httpBackend_;
            keys = ['my.first.translation.key', 'my.second.translation.key'];
    }));

    it('should fetch translations from the server', function() {
        $httpBackend.when('POST', /\w*/).respond(responseMock);

        translationsService.fetchTranslations(requestParamsMock).then(function (response) {
            expect(response.data).toEqual(responseMock);
        });

        $httpBackend.flush();
    });

    describe('createRouteParamsFromKeys', function () {
        it('should create the correct route params from the given translation keys', function() {
            var routeParams = translationsService.createRouteParamsFromKeys(keys);

            expect(routeParams).toEqual({
                translationKeys: [
                    'my.first.translation.key',
                    'my.second.translation.key'
                ]
            });
        });
    });

    describe('getRoutePathToTranslationKeys', function () {
        it('should return a mapping of route paths to translation keys', function () {
            var routePathToTranslationKeys = translationsService.getRoutePathToTranslationKeys();

            angular.forEach(routePathToTranslationKeys, function (value, prop) {
                var routePath = prop,
                    translationKeys = routePathToTranslationKeys[prop],
                    routePathRegEx = /\/+/;

                expect(routePathRegEx.test(routePath)).toBeTruthy();
                expect(angular.isArray(translationKeys)).toBeTruthy();

                angular.forEach(translationKeys, function (value, index) {
                    expect(angular.isString(value)).toBeTruthy();
                });
            });
        });
    });

    describe('setTranslations', function () {
        var translationsMock = {
            'my.first.translation': 'mock translation value 1',
            'my.second.translation': 'mock translation value 2'
        };

        it('should bind the value of translations to the text property of rootScope', function () {
            translationsService.setTranslations(translationsMock);

            expect($rootScope.text).toEqual(translationsMock);
        });

        it('should resolve the promise returned by getTranslations', function () {
            var mySpy = {
                callback: function () {}
            };

            spyOn(mySpy, 'callback');

            translationsService.getTranslations().then(mySpy.callback);

            $httpBackend.when('POST', /\w*/).respond(responseMock);

            translationsService.fetchTranslations(requestParamsMock).then(function (data) {
                translationsService.setTranslations(data.translationsMap);
            });

            $httpBackend.flush();

            expect(mySpy.callback).toHaveBeenCalled();
        });
    });
});