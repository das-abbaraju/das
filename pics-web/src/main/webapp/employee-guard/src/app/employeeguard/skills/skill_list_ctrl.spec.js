describe("Profile Settings Controller", function() {
    var scope, $httpBackend;

    var urls = {
        settings: {
            live: '/employee-guard/api/settings',
            dev: '/employee-guard/json/employee/settings/settings.json'
        },
        language: {
            live: '/employee-guard/api/languages',
            dev: '/employee-guard/json/employee/settings/languages.json'
        },
        dialect: {
            live: '/employee-guard/api/dialects/de',
            dev: '/employee-guard/json/employee/settings/dialects_de.json'
        }
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($injector, $rootScope, $controller) {
            scope = $rootScope.$new();
            $controller("skillListCtrl", {
                $scope: scope
            });
    }));

    describe("Prefill Selected Skills", function() {
        it("should return an array skill ids", function() {
            var requiredSkillsList = [
                {
                    id: '3',
                    name:'Civil Engineering Degree'
                },
                {
                    id: '26',
                    name:'Emergency Planning'
                }
            ],
            expected = ['3', '26'];

            scope.prefillSelectedSkills(requiredSkillsList);

            expect(scope.selected_skills).toEqual(expected);
        });
    });

    describe("Updated Required Skills", function() {
        var selected;

        beforeEach(function() {
            scope.skills = [{
                'id': 3,
                'name': 'Operator Permit'
            }, {
                'id': 41,
                'name': 'Defensive Driving'
            }];

            selected = ['41'];

            scope.showEditForm = !scope.showEditForm;
            scope.updateRequiredSkills(selected);

        });

        it("should update the requiredSkillsList with new values", function() {
            var expected = [{
                'id': 41,
                'name': 'Defensive Driving'
            }];

            expect(scope.requiredSkillsList).toEqual(expected);
        });

        it("should toggle the form visibility", function() {
            expect(scope.showEditForm).toBeFalsy();
        });
    });
});
