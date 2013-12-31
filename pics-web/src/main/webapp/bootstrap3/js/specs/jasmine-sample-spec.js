describe("Hello world", function() {
    it("says hello", function() {
        expect(PICS.getClass('sample.Test').helloWorld()).toEqual("Hello world!");
    });
});