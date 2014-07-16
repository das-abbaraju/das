// describe('Profile Service', function() {
//     var scope, Profile, $httpBackend;
//     var profile_settings = {
//         "language": {
//           "id":"de",
//           "name":"Deutsch"
//        },
//         "dialect": {
//           "id":"CH",
//           "name":"Schweiz"
//        }
//     };
//     var settings_url = {
//         live: '/employee-guard/api/settings',
//         dev: '/employee-guard/json/employee/settings/settings.json'
//     };

//     beforeEach(angular.mock.module('Profile'));

//     beforeEach(inject(function(_$httpBackend_, _Profile_, $translate, ProfileResource) {
//         Profile = _Profile_;
//         $httpBackend = _$httpBackend_;
//     }));


//     it('should cache profile settings', function() {
//         Profile.cacheProfileSettings(profile_settings);

//         expect(Profile.getSettings()).toEqual(profile_settings);
//     });


//     describe("existing profile settings", function() {
//         beforeEach(function () {
//             Profile.cacheProfileSettings(profile_settings);
//         });

//         it('should NOT fetch settings if they exist', function() {
//             spyOn(Profile, "fetchSettings");

//             Profile.getSettings();

//             expect(Profile.fetchSettings).not.toHaveBeenCalled();
//         });

//     });

//     describe("Fetch profile settings", function() {
//         beforeEach(function () {
//             $httpBackend.when('GET', settings_url.live).respond(profile_settings);
//         });

//         it("should fetch settings", function() {
//             var settings = Profile.fetchSettings();

//             $httpBackend.flush();

//             expect(settings.language).toEqual(profile_settings.language);
//             expect(settings.dialect).toEqual(profile_settings.dialect);
//         });

//         it("should fetch settings and execute callback", function(done) {
//             var foo = {
//                 callback: function(values) {
//                     console.log(values);
//                 }
//             };

//             spyOn(foo, 'callback');

//             Profile.fetchSettings(foo.callback);

//             $httpBackend.flush();

//             expect(foo.callback).toHaveBeenCalled();
//         });
//     });

//     describe('Get profile Settings', function() {
//         it('should fetch settings if they do not exist', function() {
//             spyOn(Profile, "fetchSettings");
//             Profile.getSettings();

//             expect(Profile.fetchSettings).toHaveBeenCalled();
//         });

//         it("should force a fetch for profile settings", function() {
//             spyOn(Profile, "fetchSettings");

//             Profile.getSettings('', true);

//             expect(Profile.fetchSettings).toHaveBeenCalled();
//         });

//         it("should return existing profile settings object", function() {
//             Profile.cacheProfileSettings(profile_settings);

//             var result = Profile.getSettings();

//             expect(result).toEqual(profile_settings);
//         });

//         it('should return existing profile settings object and execute callback', function() {
//             Profile.cacheProfileSettings(profile_settings);

//             var foo = {
//                 bar: function(values) {
//                     console.log(values);
//                 }
//             };

//             spyOn(foo, 'bar');

//             Profile.getSettings(foo.bar);

//             expect(foo.bar).toHaveBeenCalled();
//         });
//     });

//     describe("Save settings", function() {
//         it("should make a request to save profile settings", function() {
//             $httpBackend.when('PUT', settings_url.live).respond();

//             Profile.save(profile_settings);

//             $httpBackend.flush();
//         });

//     });
// });