describe('A Project List', function() {
    var scope, mockResource, httpMock;

    var projects_url = '/angular/json/operator/project_list_corp.json';

    var corporate_projects = [{
            "id": 4,
            "siteName": "BASF",
            "name": "Install new oven",
            "location": "Bob's Burgers",
            "startDate": "2012-05-01",
            "endDate": "2012-06-23"
        },{
            "id": 7,
            "siteName": "BASF",
            "name": "Wine Tasting",
            "location": "Paris, France",
            "startDate": "2014-01-07",
            "endDate": "2014-08-10"
        }];

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

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($httpBackend, ProjectList) {
        httpMock = $httpBackend;
        mockResource = ProjectList;
    }));

    describe('a corporate project list', function() {
        beforeEach(function () {
            httpMock.when('GET', projects_url).respond(corporate_projects);
        });
        it("should have a site", function() {
            var test = mockResource.query();
            httpMock.flush();

            expect(test[0].siteName).toBeDefined();
        });
    });

    describe('a operator project list', function() {
        beforeEach(function () {
            httpMock.when('GET', projects_url).respond(operator_projects);
        });

        it("should be an array", function() {
            var projects = mockResource.query();
            httpMock.flush();

            expect(angular.isArray(projects)).toBeTruthy();
        });

        it("should not have a site", function() {
            var test = mockResource.query();
            httpMock.flush();

            expect(test[1].siteName).not.toBeDefined();
        });
    });
});