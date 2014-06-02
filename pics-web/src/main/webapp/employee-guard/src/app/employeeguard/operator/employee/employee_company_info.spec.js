describe('A Project List', function() {
    var scope, mockResource, httpMock, whoAmI;

    var employee_info_dev_url = '/angular/json/operator/employee_skills/employee_info.json';

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

    beforeEach(inject(function($httpBackend, EmployeeCompanyInfo) {
        httpMock = $httpBackend;
        httpMock.when('GET', employee_info_dev_url).respond(employee_info);

        httpMock.when('GET', /\employee-guard\/operators\/contractors\/employees\/[0-9]+/).respond(employee_info);

        EmployeeCompanyInfo.get({id: 24});

        httpMock.flush();

    }));

    describe('Employee Info', function() {
        it("should have an id", function() {
            expect(employee_info.id).toBeTruthy();
        });

        it("should have a status", function() {
            expect(employee_info.status).toBeTruthy();
        });

        it("should have a first name", function() {
            expect(employee_info.firstName).toBeTruthy();
        });

        it("should have a last name", function() {
            expect(employee_info.lastName).toBeTruthy();
        });

        it("should have a list of companies", function() {
            expect(angular.isArray(employee_info.companies)).toBeTruthy();
        });
    });

    describe('An Employee Company list', function() {
        it("should have an id", function() {
            expect(employee_info.companies[0].id).toBeTruthy();
        });

        it("should have a title", function() {
            expect(employee_info.companies[0].title).toBeTruthy();
        });

        it("should have a name", function() {
            expect(employee_info.companies[0].name).toBeTruthy();
        });
    });

});