describe('Profile Service', function() {
    var scope, ProfileService, $httpBackend;
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

    beforeEach(angular.mock.module('ProfileService'));

    beforeEach(inject(function(_$httpBackend_, _ProfileService_, $translate, ProfileResource) {
        ProfileService = _ProfileService_;
        $httpBackend = _$httpBackend_;
    }));


    it('should cache profile settings', function() {
        ProfileService.cacheProfileSettings(profile_settings);

        expect(ProfileService.getSettings()).toEqual(profile_settings);
    });


    describe("existing profile settings", function() {
        beforeEach(function () {
            ProfileService.cacheProfileSettings(profile_settings);
        });

        it('should NOT fetch settings if they exist', function() {
            spyOn(ProfileService, "fetchSettings");

            ProfileService.getSettings();

            expect(ProfileService.fetchSettings).not.toHaveBeenCalled();
        });

    });

    describe("Fetch profile settings", function() {
        beforeEach(function () {
            $httpBackend.when('GET', '/employee-guard/json/employee/settings/settings.json').respond(profile_settings);
        });

        it("should fetch settings", function() {
            var settings = ProfileService.fetchSettings();

            $httpBackend.flush();

            expect(settings.language).toEqual(profile_settings.language);
            expect(settings.dialect).toEqual(profile_settings.dialect);
        });

        it("should fetch settings and execute callback", function(done) {
            var foo = {
                callback: function(values) {
                    console.log(values);
                }
            };

            spyOn(foo, 'callback');

            ProfileService.fetchSettings(foo.callback);

            $httpBackend.flush();

            expect(foo.callback).toHaveBeenCalled();
        });
    });

    describe('Get profile Settings', function() {
        it('should fetch settings if they do not exist', function() {
            spyOn(ProfileService, "fetchSettings");
            ProfileService.getSettings();

            expect(ProfileService.fetchSettings).toHaveBeenCalled();
        });

        it("should force a fetch for profile settings", function() {
            spyOn(ProfileService, "fetchSettings");

            ProfileService.getSettings('', true);

            expect(ProfileService.fetchSettings).toHaveBeenCalled();
        });

        it("should return existing profile settings object", function() {
            ProfileService.cacheProfileSettings(profile_settings);

            var result = ProfileService.getSettings();

            expect(result).toEqual(profile_settings);
        });

        it('should return existing profile settings object and execute callback', function() {
            ProfileService.cacheProfileSettings(profile_settings);

            var foo = {
                bar: function(values) {
                    console.log(values);
                }
            };

            spyOn(foo, 'bar');

            ProfileService.getSettings(foo.bar);

            expect(foo.bar).toHaveBeenCalled();
        });
    });

    describe("Save settings", function() {
        it("should make a request to save profile settings", function() {
            $httpBackend.when('PUT', '/employee-guard/json/employee/settings/settings.json').respond();

            ProfileService.save(profile_settings);

            $httpBackend.flush();
        });

    });
});