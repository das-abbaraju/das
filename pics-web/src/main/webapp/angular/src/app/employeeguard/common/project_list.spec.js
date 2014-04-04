describe('A Project List', function() {
    var scope, mockResource, httpMock, whoAmI;

    var projects_url = '/employee-guard/operators/projects/list';
    var whoami_url = '/employee-guard/operators/who-am-i';

    var corporate_projects = [{
            "id": 4,
            "site": "BASF",
            "name": "Install new oven",
            "location": "Bob's Burgers",
            "startDate": "2012-05-01",
            "endDate": "2012-06-23"
        },{
            "id": 7,
            "site": "BASF",
            "name": "Wine Tasting",
            "location": "Paris, France",
            "startDate": "2014-01-07",
            "endDate": "2014-08-10"
        }];

    var corporate_user = {
       "userId":116679,
       "accountId":55653,
       "name":"Lydia Rodarte-Quayle",
       "type":"CORPORATE"
    };

    var operator_projects = [{
            "id": 4,
            "name": "Install new oven",
            "location": "Bob's Burgers",
            "startDate": "2012-05-01",
            "endDate": "2012-06-23"
        },{
            "id": 10,
            "name": "Highland Games",
            "location": "Braemar, Scotland",
            "startDate": "2015-02-01",
            "endDate": "2015-12-02"
        }];

    var operator_user = {
        "userId":116680,
        "accountId":55654,
        "name":"EmployeeGUARD User",
        "type":"OPERATOR"
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($httpBackend, ProjectList, WhoAmI) {
        httpMock = $httpBackend;
        mockResource = ProjectList;
        whoAmI = WhoAmI.get();
    }));

    describe('a corporate project list', function() {
        beforeEach(function () {
            httpMock.when('GET', projects_url).respond(corporate_projects);
            httpMock.when('GET', whoami_url).respond(corporate_user);
        });
        it("should be an array", function() {
            var projects = mockResource.query();
            httpMock.flush();

            expect(angular.isArray(projects)).toBeTruthy();
        });
    });

    describe('a operator project list', function() {
        beforeEach(function () {
            httpMock.when('GET', projects_url).respond(operator_projects);
            httpMock.when('GET', whoami_url).respond(operator_user);
        });

        it("should be an array", function() {
            var projects = mockResource.query();
            httpMock.flush();

            expect(angular.isArray(projects)).toBeTruthy();
        });
    });
});