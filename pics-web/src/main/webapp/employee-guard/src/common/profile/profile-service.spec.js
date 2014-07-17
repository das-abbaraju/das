describe('Profile Service', function() {
    var scope, Profile, $httpBackend;
    var settings_url = {
        live: '/employee-guard/api/settings',
        dev: '/employee-guard/json/employee/settings/settings.json'
    };

    beforeEach(angular.mock.module('ProfileService'));

    beforeEach(inject(function(_$httpBackend_, _Profile_, $translate) {
        Profile = _Profile_;
        $httpBackend = _$httpBackend_;
    }));

    it("should return profile settings", function() {
        var profile_settings = {
            "language": {
              "id":"de",
              "name":"Deutsch"
           },
            "dialect": {
              "id":"CH",
              "name":"Schweiz"
           }
        };

        $httpBackend.when('GET', settings_url.live).respond(profile_settings);

        Profile.get().then(function(profile) {
            expect(profile.language).toEqual(profile_settings.language);
            expect(profile.dialect).toEqual(profile_settings.dialect);
        });

        $httpBackend.flush();
    });
});