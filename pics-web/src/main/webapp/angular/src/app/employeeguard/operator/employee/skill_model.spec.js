describe('An Operator Skill Data Model', function() {
    var skillModel;

    var skillListData = {
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
                    }
                ]
            },
           "projects": [
            {
              "id":4,
              "name":"Bob Super Fun Time",
              "status": "Expiring",
              "roles":[
                 {
                    "id":3,
                    "name":"Becoming Bob",
                    "skills":[
                       {
                          "id":13,
                          "name":"Learn Bob's Mannerisms",
                          "status": "Completed"
                       }
                    ]
                 }
              ],
              "required": {
                "skills": [
                    {
                     "id":4,
                     "name":"Bob Super Fun Time Required Skill 1",
                     "status": "Expired"
                    }
                ]
              }
            }
           ],
           "roles":[
                {
                "id":24,
                "skills":[
                    {
                       "id":13,
                       "name":"General Safety Training",
                       "status":"Completed"
                    }
                 ],
                 "status":"Completed",
                 "name":"Dead by Dawn",
                 "slug":"DeadbyDawn"
               },
              {
                 "id":6,
                 "name":"Demolition Operative",
                 "skills":[
                    {
                       "id":12,
                       "name":"Fire Prevention Training",
                       "status": "Completed"

                    }
                 ],
                 "status": "Completed"
              }
           ]
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function(SkillModel) {
        skillModel = new SkillModel(skillListData);
    }));

    describe('contains data that', function() {
        it('should have the skill data', function() {
            expect(skillModel.getData()).toEqual(skillListData);
        });

        it('should have all role data', function() {
            expect(skillModel.getRoles()).toEqual(skillListData.roles);
        });

        it('should have all project data', function() {
            expect(skillModel.getProjects()).toEqual(skillListData.projects);
        });
    });

    describe('required skills', function() {
        it('should get all project, corp, and site requiredskills', function() {
            var result = [
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
                         "id":4,
                         "name":"Bob Super Fun Time Required Skill 1",
                         "status": "Expired"
                        }
            ];
            expect(skillModel.getAllRequiredSkills()).toEqual(result);
        });

        it('should get all project required skills', function() {
            var projectSkills = [
                {
                 "id":4,
                 "name":"Bob Super Fun Time Required Skill 1",
                 "status": "Expired"
                }
            ];

            expect(skillModel.getAllProjectRequiredSkills()).toEqual(projectSkills);
        });

        it('should get all project required skills by slug', function() {
            var projectSkills = [
                    {
                     "id":4,
                     "name":"Bob Super Fun Time Required Skill 1",
                     "status": "Expired"
                    }
            ];

            expect(skillModel.getProjectRequiredSkillsBySlug('bob-super-fun-time')).toEqual(projectSkills);
        });

        it('should get project required skills AND site required by slug', function() {
            var projectSkills = [
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
                     "id":4,
                     "name":"Bob Super Fun Time Required Skill 1",
                     "status": "Expired"
                    }
            ];

            expect(skillModel.getProjectAndSiteRequiredSkillsBySlug('bob-super-fun-time')).toEqual(projectSkills);
        });

        it('should get all site and corp required skills', function() {
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
                    }
            ];

            expect(skillModel.getSiteAndCorpRequiredSkills()).toEqual(requiredSkills);
        });
    });

    describe('getting individual roles/projects', function() {
        it('should return only the selected role by slug', function() {
            var selected_role = {
                    "id":24,
                    "skills":[
                        {
                           "id":13,
                           "name":"General Safety Training",
                           "status":"Completed"
                        }
                     ],
                     "status":"Completed",
                     "name":"Dead by Dawn",
                     "slug":"dead-by-dawn"
                   };
            expect(skillModel.getRoleBySlug('dead-by-dawn')).toEqual(selected_role);
        });

        it('should return the selected role name by slug', function() {
            expect(skillModel.getRoleNameBySlug('dead-by-dawn')).toEqual('Dead by Dawn');
        });

        it('should return only the selected project by slug', function() {
            var selected_project = {
              "id":4,
              "name":"Bob Super Fun Time",
              "slug":"bob-super-fun-time",
              "status": "Expiring",
              "roles":[
                 {
                    "id":3,
                    "name":"Becoming Bob",
                    "skills":[
                       {
                          "id":13,
                          "name":"Learn Bob's Mannerisms",
                          "status": "Completed"
                       }
                    ]
                 }
              ],
              "required": {
                "skills": [
                    {
                     "id":4,
                     "name":"Bob Super Fun Time Required Skill 1",
                     "status": "Expired"
                    }
                ]
              }
            };

            expect(skillModel.getProjectBySlug('bob-super-fun-time')).toEqual(selected_project);
        });

        it('should return only the selected project name by slug', function() {
            expect(skillModel.getProjectNameBySlug('bob-super-fun-time')).toEqual('Bob Super Fun Time');
        });
    });
});