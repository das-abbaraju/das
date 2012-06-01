(function ($) {
    PICS.define('badge.Badge', {
        methods: {
            init: function () {
                if ($('#ContractorBadge-page').length) {
                    this.initializeCopyToClipboard();
                    $('#ContractorBadge-page a.toggleCode').click(function (event) {
                        var textarea = $(this).closest('.code').find('textarea');
                        textarea.toggle();
                    });
                }
            },

            initializeCopyToClipboard: function () {
                ZeroClipboard.setMoviePath( "js/zeroclipboard/ZeroClipboard.swf" );

                var clip_80 = new ZeroClipboard.Client();
                var clip_100 = new ZeroClipboard.Client();
                var clip_150 = new ZeroClipboard.Client();

                clip_80.setHandCursor(true);
                clip_100.setHandCursor(true);
                clip_150.setHandCursor(true);

                clip_80.addEventListener('mouseDown', function(client) {
                    var textarea = $('#clip_button_80').closest('.code').find('textarea');

                    clip_80.setText(textarea.text());
                });

                clip_100.addEventListener('mouseDown', function(client) {
                    var textarea = $('#clip_button_100').closest('.code').find('textarea');

                    clip_100.setText(textarea.text());
                });

                clip_150.addEventListener('mouseDown', function(client) {
                    var textarea = $('#clip_button_150').closest('.code').find('textarea');

                    clip_150.setText(textarea.text());
                });

                clip_80.glue('clip_button_80', 'clip_container_80');
                clip_100.glue('clip_button_100', 'clip_container_100');
                clip_150.glue('clip_button_150', 'clip_container_150');
            }
        }
    });
}(jQuery));