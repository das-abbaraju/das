(function ($) {
    PICS.define('badge.Badge', {
        methods: {
            init: function () {
                var me = this;

                if ($('#ContractorBadge-page').length) {

                    ZeroClipboard.setMoviePath( "js/zeroclipboard/ZeroClipboard.swf" );

                    //default icon
                    me.copyToClipboard('100');

                    $('.badges').delegate('#badgeSize', 'change', function (event) {
                        me.changeBadgeSize.apply(me, [event]);
                    });

                    $('#ContractorBadge-page').delegate('a.toggleCode', 'click', function () {
                        var code_toggle = $(this),
                            textarea = code_toggle.closest('.badgeIcon').find('textarea');

                        textarea.toggle();
                    });
                }
            },

            changeBadgeSize: function (event) {
                var size  = $("#badgeSize"),
                    icons = $(".badgeIcon"),
                    that = this;

                //hide all badges
                icons.hide();

                //show selected badge
                switch (size.val()) {
                    case "small":
                        $("#badge_80").show();
                        that.copyToClipboard('80');
                        break;
                    case "medium":
                        $("#badge_100").show();
                        that.copyToClipboard('100');
                        break;
                    case "large":
                        $("#badge_150").show();
                        that.copyToClipboard('150');
                        break;
                    default: break;
                }
            },

            copyToClipboard: function (badge_size) {
                if (!$('#badge_' + badge_size + ' embed').length) {
                    this.createZeroClipboardClient(badge_size);
                }
            },

            createZeroClipboardClient: function (size) {
                var client = new ZeroClipboard.Client();

                client.setHandCursor(true);

                client.addEventListener('mouseDown', function(client) {
                    var textarea = $('#badge_' + size).find('textarea');

                    client.setText(textarea.text());
                });

                client.glue('clip_button_' + size, 'clip_container_' + size);
            }
        }
    });
}(jQuery));