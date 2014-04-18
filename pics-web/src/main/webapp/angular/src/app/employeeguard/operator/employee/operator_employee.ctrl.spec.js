describe('An Operator Employee', function() {
    var scope, $httpBackend, routeParams;

    var result = {
        "required": {
            "skills": [
                {
                 "id":6,
                 "name":"Site Required Skill 1",
                 "status": "Expired"
                },
                {
                 "id":5,
                 "name":"Corp Required Skill 1",
                 "status": "Completed"
                },
                {
                 "id":5,
                 "name":"Corp Required Skill 2",
                 "status": "Expiring"
                }
            ]
        },
        projects:[
          {
            id:1,
            name:"Blue Buffalo",
            slug:"blue-buffalo",
            status:"Expired",
            "required": {
                "skills": [
                    {
                     "id":4,
                     "name":"ABC Project Required Learning the alphabet",
                     "status": "Expired"
                    },
                    {
                     "id":25,
                     "name":"ABC Project Required Skill 2",
                     "status": "Completed"
                    }
                ]
            },
            roles:[
                {
                    skills:[
                      {
                         id:21,
                         projects:[

                         ],
                         status:"Complete",
                         roles:[
                            1
                         ],
                         name:"Defensing Driving"
                      }
                    ],
                    id:1,
                    status:"Expired",
                    name:"Soccer Mom"
                }
            ]
          },
          {
            id:41,
            name:"Red Rocket",
            slug:"red-rocket",
            status:"Expiring",
            "required": {
                "skills": [
                    {
                     "id":3,
                     "name":"Red Rocket Required 1",
                     "status": "Expired"
                    },
                    {
                     "id":215,
                     "name":"Red Rocket Required 2",
                     "status": "Completed"
                    }
                ]
            },
            roles:[
                {
                    skills:[
                      {
                         id:21,
                         projects:[

                         ],
                         status:"Complete",
                         roles:[
                            1
                         ],
                         name:"Defensing Driving"
                      }
                    ],
                    id:1,
                    status:"Expired",
                    name:"Soccer Mom"
                }
            ]
          }
        ],
        roles:[
          {
             skills:[
                {
                   id:13,
                   projects:[
                      7
                   ],
                   status:"Complete",
                   roles:[
                      1
                   ],
                   name:"General Safety Training"
                }
             ],
             id:24,
             status:"Expiring",
             name:"Destructor The Great",
             slug:"destructor-the-great"
          },
          {
             skills:[
                {
                   id:13,
                   projects:[
                      7
                   ],
                   status:"Complete",
                   roles:[
                      1
                   ],
                   name:"General Safety Training"
                }
             ],
             id:24,
             status:"Completed",
             name:"Redemption",
             slug:"redemption"
          }
        ]
    };

    var employee_info = {
       "id":29,
       "image":"",
       "status":"Expired",
       "firstName":"Murphy",
       "lastName":"Hibbert",
       "companies":[
          {
             "id":54578,
             "title":"Demo Op",
             "name":"ACME Co."
          }
       ]
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $httpBackend, $routeParams, EmployeeCompanyInfo, SkillModel, SkillList) {
        $routeParams.id = Math.floor((Math.random()*1000)+1);

        routeParams = $routeParams;

        //This needs to come first!!
        $httpBackend.when('GET', /\/employee-guard\/operators\/skills\/employees\/[0-9]+/).respond(result);

        $httpBackend.when('GET', '/angular/json/operator/employee_skills/employee_info.json').respond(employee_info);
        $httpBackend.when('GET', '/angular/json/operator/employee_skills/skill_list.json').respond(result);



        scope = $rootScope.$new();
        $controller("operatorEmployeeCtrl", {
            $scope: scope
        });

        //This needs to come after controller is loaded
        $httpBackend.flush();

        scope.employeeStatusIcon = result.status;
    }));

    it("should set the view type to role", function() {
        routeParams.roleSlug = 'destructor';

        expect(scope.getSelectedView()).toEqual('role');
    });

    it("should set the view type to project", function() {
        routeParams.projectSlug = 'complete-annihilation-of-the-planet-krypton';

        expect(scope.getSelectedView()).toEqual('project');
    });

    it("should set the view type to all", function() {
        expect(scope.getSelectedView()).toEqual('all');
    });

    it("should load the menu items with projects and roles", function() {
        scope.loadMenuItems();

        expect(scope.projects).toEqual(result.projects);
        expect(scope.roles).toEqual(result.roles);
    });

    describe("Role model", function() {
        it('should have site and corp required skills', function() {
            routeParams.roleSlug = 'redemption';
            scope.selectViewModel();

            var requiredSkills = [
                {
                 "id":6,
                 "name":"Site Required Skill 1",
                 "status": "Expired"
                },
                {
                 "id":5,
                 "name":"Corp Required Skill 1",
                 "status": "Completed"
                },
                {
                 "id":5,
                 "name":"Corp Required Skill 2",
                 "status": "Expiring"
                }
            ];

            expect(scope.requiredSkills).toEqual(requiredSkills);
        });

        it('should have the skill list for a selected role', function() {
            routeParams.roleSlug = 'redemption';
            scope.selectViewModel();

            expect(scope.skillList).toEqual(result.roles[1]);
        });

        it('should set the selected menu item to role name', function() {
            routeParams.roleSlug = 'redemption';
            scope.selectViewModel();

            expect(scope.selectedMenuItem).toEqual('redemption');
        });

        it('should set the employee status icon to the role status', function() {
            routeParams.roleSlug = 'redemption';
            scope.selectViewModel();

            expect(scope.employeeStatusIcon).toEqual('Completed');
        });

        it('should set the view title to the role name', function() {
            routeParams.roleSlug = 'destructor-the-great';
            scope.selectViewModel();

            expect(scope.viewTitle).toEqual('Destructor The Great');
        });
    });

    describe("Project model", function() {
        it('should have project, site and corp required skills', function() {
            routeParams.projectSlug = 'blue-buffalo';
            scope.selectViewModel();

            var requiredSkills = [
                {
                 "id":6,
                 "name":"Site Required Skill 1",
                 "status": "Expired"
                },
                {
                 "id":5,
                 "name":"Corp Required Skill 1",
                 "status": "Completed"
                },
                {
                 "id":5,
                 "name":"Corp Required Skill 2",
                 "status": "Expiring"
                },
                {
                 "id":4,
                 "name":"ABC Project Required Learning the alphabet",
                 "status": "Expired"
                },
                {
                 "id":25,
                 "name":"ABC Project Required Skill 2",
                 "status": "Completed"
                }
            ];

            expect(scope.requiredSkills).toEqual(requiredSkills);
        });

        it('should have the skill list for a selected project', function() {
            routeParams.projectSlug = 'blue-buffalo';
            scope.selectViewModel();

            expect(scope.skillList).toEqual(result.projects[0]);
        });

        it('should have the selected menu item to project name', function() {
            routeParams.projectSlug = 'blue-buffalo';
            scope.selectViewModel();

            expect(scope.selectedMenuItem).toEqual('blue-buffalo');
        });


        it('should set the employee status icon to the project status', function() {
            routeParams.projectSlug = 'blue-buffalo';
            scope.selectViewModel();

            expect(scope.employeeStatusIcon).toEqual('Expired');
        });

        it('should set the view title to the project name', function() {
            routeParams.projectSlug = 'blue-buffalo';
            scope.selectViewModel();

            expect(scope.viewTitle).toEqual('Blue Buffalo');
        });
    });

    describe("Default model", function() {
        it('should have all project, site and corp required skills', function() {
            scope.selectViewModel();

            var requiredSkills = [
                {
                 "id":6,
                 "name":"Site Required Skill 1",
                 "status": "Expired"
                },
                {
                 "id":5,
                 "name":"Corp Required Skill 1",
                 "status": "Completed"
                },
                {
                 "id":5,
                 "name":"Corp Required Skill 2",
                 "status": "Expiring"
                },
                {
                 "id":4,
                 "name":"ABC Project Required Learning the alphabet",
                 "status": "Expired"
                },
                {
                 "id":25,
                 "name":"ABC Project Required Skill 2",
                 "status": "Completed"
                },
                {
                 "id":3,
                 "name":"Red Rocket Required 1",
                 "status": "Expired"
                },
                {
                 "id":215,
                 "name":"Red Rocket Required 2",
                 "status": "Completed"
                }
            ];

            expect(scope.requiredSkills).toEqual(requiredSkills);
        });

        it('should set the default selected menu item', function() {
            expect(scope.selectedMenuItem).toEqual('all');
        });
    });

});