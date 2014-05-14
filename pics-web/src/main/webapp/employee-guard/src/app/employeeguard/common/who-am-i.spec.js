describe('Who Am I service', function() {
    var scope, mockResource, httpMock, whoAmI, roles;
    var whoami_url = '/employee-guard/operators/who-am-i';

    var operator_user = {
        "userId":116680,
        "accountId":55654,
        "name":"EmployeeGUARD User",
        "type":"OPERATOR"
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($httpBackend, WhoAmI) {
        httpMock = $httpBackend;
        whoAmI = WhoAmI.get();

        httpMock.when('GET', whoami_url).respond(operator_user);

        httpMock.flush();
    }));

    it("should contain a user id", function() {
        expect(whoAmI.userId).toBeDefined();
    });

    it("should contain an account id", function() {
        expect(whoAmI.accountId).toBeDefined();
    });

    it("should contain a name", function() {
        expect(whoAmI.name).toBeDefined();
    });

    it("should contain a type", function() {
        expect(whoAmI.type).toBeDefined();
    });
});