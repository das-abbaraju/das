describe("A Employee Skill List", function() {
    var scope, $httpBackend, routeParams;

    var skill_list_dev_url = '/angular/json/employee/skills/skill_list.json';
    var skill_list_url = '/employee-guard/employee/profile/skills';

    var skillListData = {
        "status": "Expired",
        "sites": [
        {
            "id": 2,
            "name": "BASF Houston Texas",
            "status": "Expired",
            "projects": [
                {
                    "id": 3,
                    "name": "Dynamic Reporting",
                    "status": "Expired",
                    "skills": [
                        {
                             "id":210,
                             "name":"Dynamic Reporting Skill",
                             "status": "Expiring"
                        }
                    ]
                },
                {
                    "id": 3,
                    "name": "Ninja Dojo",
                    "status": "Expiring",
                    "skills": [
                        {
                             "id":4,
                             "name":"Ninja Dojo Skill 4",
                             "status": "Expiring"
                        }
                    ]
                }
            ],
            "required": {
                "skills": [
                    {
                     "id":45,
                     "name":"BASF Site Skill 1",
                     "status": "Expired"
                    }
                ]
            }
        },
        {
            "id": 8,
            "name": "Spectre",
            "status": "Completed",
            "projects": [
                {
                    "id": 32,
                    "name": "Volcano Base",
                    "status": "Completed",
                    "skills": [
                        {
                         "id":4,
                         "name":"Volcano Base Skill",
                         "status": "Expired"
                        }
                    ]
                }
            ],
            "required": {
                "skills": [
                    {
                         "id":6,
                         "name":"Spectre Site Required Skill 1",
                         "status": "Expired"
                    }
                ]
            }
        }
        ]
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $httpBackend, EmployeeSkillList, EmployeeSkillModel, $routeParams, $filter) {
        routeParams = $routeParams;

        $httpBackend.when('GET', /\angular\/json\/employee\/skills\/skill_list.json/).respond(skillListData);
        $httpBackend.when('GET', skill_list_url).respond(skillListData);

        scope = $rootScope.$new();
        $controller("employeeSkillListCtrl", {
            $scope: scope
        });

        $httpBackend.flush();
    }));

    describe("An Employee View Model", function() {
        it("should set the view type to site", function() {
            routeParams.siteSlug = 'sitename';

            expect(scope.getSelectedView()).toEqual('site');
        });

        it("should set the view type to project", function() {
            routeParams.siteSlug = 'sitename';
            routeParams.projectSlug = 'projectname';

            expect(scope.getSelectedView()).toEqual('project');
        });

        it("should set the view type to all", function() {
            expect(scope.getSelectedView()).toEqual('all');
        });

        it("should load the menu items with sites and projects", function() {
            scope.loadMenuItems();

            expect(scope.sites[0].id).toEqual(2);
            expect(scope.sites[0].name).toEqual('BASF Houston Texas');
            expect(scope.sites[0].projects[0].id).toEqual(3);
            expect(scope.sites[0].projects[0].name).toEqual('Dynamic Reporting');
        });
    });

    describe("Site Model", function() {
        beforeEach(function() {
            routeParams.siteSlug = 'spectre';
            scope.selectViewModel();
        });

        it("should populate the skill list with the selected site", function() {
            expect(scope.skillList).toBeDefined();
            expect(scope.skillList.name).toEqual('Spectre');
            expect(scope.skillList.status).toEqual('Completed');
        });

        it("should have all site and project skills for the selected site", function() {
            var expected_result = [
                {
                     "id":6,
                     "name":"Spectre Site Required Skill 1",
                     "status": "Expired"
                },
                {
                 "id":4,
                 "name":"Volcano Base Skill",
                 "status": "Expired"
                }
            ];

            expect(scope.skillList.skills).toEqual(expected_result);
        });

        it("should have the correct title", function() {
            expect(scope.viewTitle).toEqual('Spectre');
        });

        it("should have the correct menu item selected", function() {
            expect(scope.selectedMenuItem).toEqual('spectre');
        });
    });

    describe("Project Model", function() {
        beforeEach(function() {
            routeParams.siteSlug = 'basf-houston-texas';
            routeParams.projectSlug = 'ninja-dojo';
            scope.selectViewModel();
        });

        it("should populate the skill list with the selected project", function() {
            expect(scope.skillList).toBeDefined();
            expect(scope.skillList.name).toEqual('Ninja Dojo');
            expect(scope.skillList.status).toEqual('Expiring');
        });

        it("should have site required and project skills for the selected site", function() {
            var expected_result = [
                {
                     "id":45,
                     "name":"BASF Site Skill 1",
                     "status": "Expired"
                },
                {
                     "id":4,
                     "name":"Ninja Dojo Skill 4",
                     "status": "Expiring"
                }
            ];

            expect(scope.skillList.skills).toEqual(expected_result);
        });

        it("should have the correct title", function() {
            expect(scope.viewTitle).toEqual('BASF Houston Texas: Ninja Dojo');
        });

        it("should have the correct menu item selected", function() {
            expect(scope.selectedMenuItem).toEqual('basf-houston-texas-ninja-dojo');
        });
    });

    describe("Default Model", function() {
        beforeEach(function() {
            scope.selectViewModel();
        });

        it("should populate the skill list with all site and project skills", function() {
            var expected_result = [
                {
                     "id":6,
                     "name":"Spectre Site Required Skill 1",
                     "status": "Expired"
                },
                {
                 "id":4,
                 "name":"Volcano Base Skill",
                 "status": "Expired"
                }
            ];

            expect(scope.skillList).toBeDefined();

            expect(scope.skillList[0].status).toEqual('Expired');
            expect(scope.skillList[0].required.skills[0].name).toEqual('BASF Site Skill 1');
            expect(scope.skillList[1].name).toEqual('Spectre');
            expect(scope.skillList[1].projects[0].id).toEqual(32);
            expect(scope.skillList[1].skills).toEqual(expected_result);
        });

        it("should have the correct menu item selected", function() {
            expect(scope.selectedMenuItem).toEqual('all');
        });
    });
});
