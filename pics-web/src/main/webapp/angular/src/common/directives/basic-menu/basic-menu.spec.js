describe('Basic menu directive', function () {
    var $compile, $rootScope, $interpolate, $httpBackend, template;

    beforeEach(angular.mock.module('PICSApp'));

    beforeEach(inject(function (_$compile_, _$rootScope_, _$interpolate_, _$httpBackend_) {
            $compile = _$compile_;
            $rootScope = _$rootScope_;
            $interpolate = _$interpolate_;
            $httpBackend = _$httpBackend_;
    }));

    beforeEach(function () {
        var linkingFn;

        $httpBackend.when('GET', '/sales-phone/US.action').respond({
            "id":"US",
            "name":"1-877-725-3022"
        });

        $httpBackend.whenGET('/sales-phone/GB.action').respond({
            "id":"GB","name":"+44 (0) 1628 450400"
        });

        $httpBackend.whenGET('/angular/src/common/directives/basic-menu/basic-menu.tpl.html').respond(
            '<span class="phone pics_phone_number" title="{{ phoneCountry }}">{{ phoneNumber }}</span>'
        );

        $rootScope.countryISOCode = 'US';
        $rootScope.language = 'en';

        linkingFn = $compile("<basic-menu country-id='countryISOCode' language-id='language'></basic-menu>");

        template = linkingFn($rootScope);

        $httpBackend.flush();
    });

    it('should display the phone number associated with the selected country', function () {
        var parsedExpression = $interpolate(template.html());

        interpolatedExpression = $rootScope.$eval(parsedExpression);

        expect(interpolatedExpression).toEqual('1-877-725-3022');
    });

    it('should display the phone number country on phone number tooltip', function () {
        expect(template.attr('title')).toEqual('US');
    });
});