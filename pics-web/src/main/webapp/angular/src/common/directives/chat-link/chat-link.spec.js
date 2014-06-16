describe('Chat link directive', function () {
    var $compile, $rootScope, $httpBackend;

    var chatLink, template;

    beforeEach(angular.mock.module('PICSApp'));

    beforeEach(inject(function (_$compile_, _$rootScope_, _$interpolate_, _$httpBackend_) {
            $compile = _$compile_;
            $rootScope = _$rootScope_;
            $httpBackend = _$httpBackend_;
    }));

    beforeEach(function () {
        var linkingFn;

        $httpBackend.when('GET', '/mibew-base-url/en.action').respond({
            "id":"en",
            "name":"https://chat.picsorganizer.com/client.php?locale\u003den\u0026style\u003dPICS\u0026name\u003dTrevor+Allred\u0026accountName\u003dPICS+Auditing%2C+LLC\u0026accountId\u003d1100\u0026userId\u003d941\u0026email\u003dtester%40picsauditing.com"
        });

        $httpBackend.whenGET('/angular/src/common/directives/chat-link/chat-link.tpl.html').respond(
            '<a href="{{ href }}" ng-click="openMibew()" target="_blank">Chat</a>'
        );

        chatLink = angular.element('<chatlink language-id="languageId"></chatlink>');

        $rootScope.languageId = 'en';

        linkingFn = $compile(chatLink);

        template = linkingFn($rootScope);

        $httpBackend.flush();
    });

    it('should provide the mibew link for the selected language', function () {
        expect(template.attr('href')).toMatch(/^https:\/\/chat.picsorganizer.com/);
        expect(template.attr('href')).toContain('&url=');
        expect(template.attr('href')).toContain('&referrer=');
    });

    it('should define a click handler for opening in a separate window', function () {
        template.click();

        expect(chatLink.isolateScope().newWindow.closed).toBe(false);
    });
});