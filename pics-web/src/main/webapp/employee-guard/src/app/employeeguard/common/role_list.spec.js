describe('A Role List', function() {
    var scope, mockResource, httpMock, whoAmI, roles;

    var roles_url = '/employee-guard/operators/role/list';
    var roles_url_dev = '/angular/json/operator/role_list.json';
    var whoami_url = '/employee-guard/who-am-i';

    var operator_roles = [{
            "id": 4,
            "name": "Safety Supervisor",
            "requiredSkills": [{
                    "id": 1,
                    "name": "Bob's Burgers"
                }
            ]
        },{
            "id": 28,
            "name": "Engineer",
            "requiredSkills": [{
                    "id": 24,
                    "name": "DB 101"
                }
            ]
        },{
            "id": 5,
            "name": "Leadman",
            "requiredSkills": [{
                    "id": 2,
                    "name": "Agile Practices"
                }, {
                    "id": 5,
                    "name": "How to NOT be annoying"
                }
            ]
    }];
    var operator_user = {
        "userId":116680,
        "accountId":55654,
        "name":"EmployeeGUARD User",
        "type":"OPERATOR"
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($httpBackend, RoleList, WhoAmI) {
        httpMock = $httpBackend;
        mockResource = RoleList;
        roles = mockResource.query();
        whoAmI = WhoAmI.get();

        httpMock.when('GET', roles_url).respond(operator_roles);
        httpMock.when('GET', roles_url_dev).respond(operator_roles);
        httpMock.when('GET', whoami_url).respond(operator_user);

        httpMock.flush();
    }));

    describe('a operator role list', function() {
        it("should contain an array of roles", function() {
            expect(angular.isArray(roles)).toBeTruthy();
        });
    });

    describe('a operator role', function() {
        it("should contain an array of skills", function() {
            expect(angular.isArray(roles[0].requiredSkills)).toBeTruthy();
        });

        it("should contain an id", function() {
            expect(roles[0].id).toBeDefined();
        });

        it("should contain a name", function() {
            expect(roles[0].name).toBeDefined();
        });
    });

    describe('a operator role skill', function() {
        it("should contain an id", function() {
            expect(roles[0].requiredSkills[0].id).toBeDefined();
        });

        it("should contain a name", function() {
            expect(roles[0].requiredSkills[0].name).toBeDefined();
        });
    });
});