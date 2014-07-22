describe('Profile Service', function() {
    var scope, Profile, $httpBackend, $translate;
    var settings_url = {
        live: '/employee-guard/api/settings',
        dev: '/employee-guard/json/employee/settings/settings.json'
    };

    beforeEach(angular.mock.module('ProfileService'));

    beforeEach(inject(function(_$httpBackend_, _Profile_, _$translate_) {
        Profile = _Profile_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
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

    describe("The translated language", function() {
        it("should be set to language and dialect", function() {
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

            Profile.setTranslatedLanguage(profile_settings.language, profile_settings.dialect);
            expect($translate.use()).toEqual('de_CH');
        });

        it("should be set to language only if dialect", function() {
            var profile_settings = {
                "language": {
                  "id":"de",
                  "name":"Deutsch"
               }
            };

            Profile.setTranslatedLanguage(profile_settings.language);
            expect($translate.use()).toEqual('de');
        });

        it("should be set to the default language if no dialect or language", function() {
            Profile.setTranslatedLanguage();
            expect($translate.use()).toEqual('en_US');
        });
    });
});