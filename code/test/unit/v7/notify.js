describe("notify service", function() {
    var rootScope;
    var notifyService;

    // Force our application model to be loaded. And, use the Angular
    // '$provide' service to mock out the '$timeout' service. We don't want
    // the timeout fire and clear the message before we can test it.
    beforeEach(module('peopleApp', function($provide) {
        var mockTimeout = function(callback, timeout) { };
        $provide.value('$timeout', mockTimeout);
    }));

    // Before each test, make sure we have access to both the root scope and
    // the notify service.
    beforeEach(inject(function($rootScope, picsnotify) {
        rootScope = $rootScope;
        notifyService = picsnotify;
    }));

    it("should set warning variable when showWarning() is called", function() {
        notifyService.showWarning("warning message");
        expect(rootScope.warning).toBe("warning message");
    });

    it("should set error variable when showError() is called", function() {
        notifyService.showError("message");
        expect(rootScope.error).toBe("message");
    });
});