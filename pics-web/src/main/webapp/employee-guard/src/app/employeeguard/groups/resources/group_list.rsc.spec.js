describe('A Group', function() {
    var scope, httpMock, whoAmI, group_list;

    var groups_url = '/employee-guard/groups';
    var groups_url_dev = '/employee-guard/src/app/employeeguard/groups/json/group_list.json';
    var whoami_url = '/employee-guard/who-am-i';

    var groups = [{
        "id": 4,
        "name": "Medical",
        "skills": [{
                "id": 22,
                "name": "Professional Safety Training"
            }
        ],
        "employees": [{
            "id": 3,
            "firstName": "Bob",
            "lastName": "Jones",
            "companies":[
              {
                 "id":54578,
                 "title":"Demo Op",
                 "name":"ACME Co."
              }
            ]
        }, {
            "id": 23,
            "firstName": "Jim",
            "lastName": "Johnson",
            "companies":[]
        }]
    }];

    var contractor_user = {
        "userId":116680,
        "accountId":55654,
        "name":"Lurleen Lumpkin",
        "type":"CONTRACTOR"
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($httpBackend, GroupListResource, WhoAmI) {
        httpMock = $httpBackend;
        group_list = GroupListResource.query();
        whoAmI = WhoAmI.get();

        httpMock.when('GET', groups_url).respond(groups);
        httpMock.when('GET', groups_url_dev).respond(groups);
        httpMock.when('GET', whoami_url).respond(contractor_user);

        httpMock.flush();
    }));

    describe('list', function() {
        it("should contain an array of groups", function() {
            expect(angular.isArray(groups)).toBeTruthy();
        });
    });

});