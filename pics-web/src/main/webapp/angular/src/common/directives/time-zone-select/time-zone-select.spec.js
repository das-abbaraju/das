describe('Time zone select directive', function () {
    var $compile, $rootScope, $httpBackend;

    beforeEach(angular.mock.module('PICSApp'));

    beforeEach(inject(function (_$compile_, _$rootScope_, _$httpBackend_) {
            $compile = _$compile_;
            $rootScope = _$rootScope_;
            $httpBackend = _$httpBackend_;
    }));

    beforeEach(function () {
        var linkingFn,
            timeZoneSelect = [
                '<input ',
                    'tabindex="tabindex"' ,
                    'ng-model="registrationForm.timezoneId"' ,
                    'time-zone-select>',
                '</input>'
            ].join('');

        linkingFn = $compile(timeZoneSelect);

        template = linkingFn($rootScope);

        $rootScope.timeZones = {
            "result":[
                {
                    "id":"America\/Los_Angeles",
                    "time":"8:00 AM",
                    "offset":"-7:00",
                    "date":"Fri, May 30"
                }
            ]};

        $rootScope.$digest();
    });

    it('should create a select2 select', function () {
       expect(template.siblings().hasClass('select2-container')).toBe(true);
    });
});