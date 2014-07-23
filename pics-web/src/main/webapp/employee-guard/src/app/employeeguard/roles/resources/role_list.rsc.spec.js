describe('A Role List', function() {
    var scope, mockResource, httpMock, whoAmI, role_list;

    var roles_url = '/employee-guard/roles';
    var roles_url_dev = '/employee-guard/json/roles/role_list.json';
    var whoami_url = '/employee-guard/who-am-i';

    var roles = [{
            "id": 4,
            "name": "Safety Supervisor",
            "skills": [{
                    "id": 1,
                    "name": "Bob's Burgers"
                }
            ],
            'employees': [],
            'projects': []
        },{
            "id": 28,
            "name": "Engineer",
            "skills": [{
                    "id": 24,
                    "name": "DB 101"
                }
            ],
            'employees': [],
            'projects': []
        },{
            "id": 5,
            "name": "Leadman",
            "skills": [{
                    "id": 2,
                    "name": "Agile Practices"
                }, {
                    "id": 5,
                    "name": "How to NOT be annoying"
                }
            ],
            'employees': [],
            'projects': []
    }];
    var operator_user = {
        "userId":116680,
        "accountId":55654,
        "name":"EmployeeGUARD User",
        "type":"OPERATOR"
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($httpBackend, RoleListResource, WhoAmI) {
        httpMock = $httpBackend;
        mockResource = RoleListResource;
        role_list = mockResource.query();
        whoAmI = WhoAmI.get();

        httpMock.when('GET', roles_url).respond(roles);
        httpMock.when('GET', roles_url_dev).respond(roles);
        httpMock.when('GET', whoami_url).respond(operator_user);

        httpMock.flush();
    }));

    describe('a role list', function() {
        it("should contain an array of roles", function() {
            expect(angular.isArray(roles)).toBeTruthy();
        });
    });

});