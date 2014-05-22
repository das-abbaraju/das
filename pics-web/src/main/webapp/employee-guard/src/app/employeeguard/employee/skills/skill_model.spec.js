describe('An Employee Skill Data Model', function() {
    var skillModel;

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
                         "id":45,
                         "name":"BASF Site Skill 1",
                         "status": "Expired"
                        },
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
                         "id":45,
                         "name":"BASF Site Skill 1",
                         "status": "Expired"
                        },
                        {
                             "id":4,
                             "name":"Ninja Dojo Skill 4",
                             "status": "Expiring"
                        }
                    ]
                }
            ],
            "skills": [
                {
                 "id":45,
                 "name":"BASF Site Skill 1",
                 "status": "Expired"
                }
            ]
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
                             "id":6,
                             "name":"Spectre Site Required Skill 1",
                             "status": "Expired"
                        },
                        {
                         "id":4,
                         "name":"Volcano Base Skill",
                         "status": "Expired"
                        }
                    ]
                }
            ],
            "skills": [
                {
                     "id":6,
                     "name":"Spectre Site Required Skill 1",
                     "status": "Expired"
                }
            ]
        }
        ]
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    describe('contains data that', function() {
        beforeEach(inject(function(EmployeeSkillModel) {
            skillModel = new EmployeeSkillModel(skillListData);
        }));

        it('should have the skill data', function() {
            expect(skillModel.getData()).toEqual(skillListData);
        });

        it('should have all site data', function() {
            expect(skillModel.getSites()).toEqual(skillListData.sites);
        });

        it('should append a slug name to each site', function() {
            expect(skillModel.getSites()[0].slug).toBeDefined();
            expect(skillModel.getSites()[0].slug).toEqual('basf-houston-texas');
        });

        it('should append a slug name to each project', function() {
            expect(skillModel.getSites()[0].projects[0].slug).toBeDefined();
            expect(skillModel.getSites()[0].projects[0].slug).toEqual('dynamic-reporting');
        });
    });

    describe('a request via slugname', function() {
        beforeEach(inject(function(EmployeeSkillModel) {
            skillModel = new EmployeeSkillModel(skillListData);
        }));

        it('should get a site object', function() {
            expect(skillModel.getSiteBySlug('basf-houston-texas')).toEqual(skillListData.sites[0]);
        });

        it('should not get a site object if no site match', function() {
            expect(skillModel.getSiteBySlug('brown-ipswitch')).not.toBeDefined();
        });

        it('should get a site name', function() {
            expect(skillModel.getSiteNameBySlug('basf-houston-texas')).toEqual('BASF Houston Texas');
        });

        it('should not a site name if no site match', function() {
            expect(skillModel.getSiteNameBySlug('monolith')).not.toBeDefined();
        });

        it('should get a project object', function() {
            expect(skillModel.getProjectBySlug('ninja-dojo')).toEqual(skillListData.sites[0].projects[1]);
        });

        it('should not get a project object if no project match', function() {
            expect(skillModel.getProjectBySlug('soldierdojo')).not.toBeDefined();
        });

        it('should get a project name', function() {
            expect(skillModel.getProjectNameBySlug('volcano-base')).toEqual(skillListData.sites[1].projects[0].name);
        });

        it('should get a site name from a project slug', function() {
            expect(skillModel.getSiteNameByProjectSlug('volcano-base')).toEqual('Spectre');
        });
    });

    describe("the default view of employee skills", function() {
        beforeEach(inject(function(EmployeeSkillModel) {
            skillModel = new EmployeeSkillModel(skillListData);
        }));

        it("should return a list of all project and site skills", function() {
            var expected_result = [
                {
                 "id":45,
                 "name":"BASF Site Skill 1",
                 "status": "Expired"
                },
                {
                 "id":210,
                 "name":"Dynamic Reporting Skill",
                 "status": "Expiring"
                },
                {
                 "id":4,
                 "name":"Ninja Dojo Skill 4",
                 "status": "Expiring"
                }
            ];

            expect(skillModel.getAllSiteAndProjectSkills()[0].skills).toBeDefined();
            expect(skillModel.getAllSiteAndProjectSkills()[0].skills).toEqual(expected_result);
        });

        it("should only return project skills if there are no site skills", function() {
            delete skillModel.data.sites[0].skills;
            delete skillModel.data.sites[1].skills;

            expect(skillModel.getAllSiteAndProjectSkills()).toBeDefined();
        });
    });

    describe("the site view of skills", function() {
        beforeEach(inject(function(EmployeeSkillModel) {
            var skillModelData = {
                "status": "Expired",
                "sites": [{
                    "id": 2,
                    "name": "BASF Houston Texas",
                    "status": "Expired",
                    "projects": [{
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
                    }],
                    "skills": [{
                       "id":14,
                       "name":"Site Role Skill",
                       "status":"Completed"
                    }]
                }]
            };
            skillModel = new EmployeeSkillModel(skillModelData);
        }));

        it("should return ONLY project skills if no site skills", function() {
            delete skillModel.data.sites[0].skills;

            var actual = skillModel.getAllSiteAndProjectSkillsBySlug('basf-houston-texas');
            var expected = [{
                "id":4,
                "name":"Volcano Base Skill",
                "status": "Expired"
            }];

            expect(actual.skills).toEqual(expected);
        });

        it("should return site skills if matching site slug", function() {
            var actual = skillModel.getSiteBySlug('basf-houston-texas'),
                expected = [{
                   "id":14,
                   "name":"Site Role Skill",
                   "status":"Completed"
                }];

            expect(actual).toBeDefined();
            expect(actual.skills).toEqual(expected);
        });

        it("should not return site skills if there is no site slug match", function() {
            delete skillModel.data.sites;

            var actual = skillModel.getSiteBySlug('redtrinket');
            expect(actual).not.toBeDefined();
        });

        it("should not return a project if there is no project slug matches", function() {
            delete skillModel.data.sites[0].projects;

            var actual = skillModel.getProjectBySlug('volcano-base');
            expect(actual).not.toBeDefined();
        });

        it("should only return the selected project skills if there is no site  skills", function() {
            var actual = skillModel.getProjectBySlug('volcano-base'),
                expected = [{
                     "id":4,
                     "name":"Volcano Base Skill",
                     "status": "Expired"
                }];

            expect(actual.skills).toEqual(expected);
        });
    });
});