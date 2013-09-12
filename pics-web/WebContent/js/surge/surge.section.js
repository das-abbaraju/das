(function($) {

$.widget("surge.section", {
    options: { cornerz: { radius: 8 } },
    _create: function () {
        this.element.addClass("sfx-section").cornerz(this.options.cornerz);
        //this.element.find("> h3").cornerz(this.options.cornerz);
    }
});

})(jQuery);