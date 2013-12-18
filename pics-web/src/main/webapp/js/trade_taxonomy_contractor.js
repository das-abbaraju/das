PICS.define('contractor.TradeTaxonomyController', {
    methods: (function () {
        function init() {
            $('#trade-view').on('click', 'input[type="submit"]', onSubmitButtonClick);

            $('#trade-view').on('submit', '#trade-form', onTradeFormSubmit);

            $('body').on('click', '.safety-sensitive-confirm-modal .confirm', onSafetySensitiveModalConfirmClick);
        }

        function onSubmitButtonClick(event) {
            $form = $(event.target).closest('form');

            $form.data('clicked-button-id', this.id);
        }

        function onTradeFormSubmit(event) {
            var $form = $(event.currentTarget),
                $button = $form.find('#addButton');

            if ($button.data('affects-safety-sensitive-status') &&
                !$form.data('is-confirmed')) {
                event.preventDefault();

                PICS.modal(getModalConfig());
            } else if ($form.data('clicked-button-id') == 'addButton') {
                event.preventDefault();

                requestAddTrade();
            }
        }

        function onSafetySensitiveModalConfirmClick(event) {
            var $trade_form = $('#trade-form'),
                $modal = $('.safety-sensitive-confirm-modal');

            $trade_form.data('is-confirmed', true);

            requestAddTrade();

            $modal.modal('hide');
        }

        function getModalConfig() {
            return {
                modal_class: 'safety-sensitive-confirm-modal',
                show: true,
                width: 560,
                title: 'Safety Sensitive Trade',
                content: getModalContent(),
                buttons: [{
                    html: '<button href="#" class="btn primary confirm">Confirm</button>'
                }, {
                    html: '<a href="javascript:;" class="btn" data-dismiss="modal">cancel</a>'
                }]
            };
        }

        function getModalContent() {
            return [
                '<p>',
                    'You have selected a trade that is considered a Safety Sensitive trade. ',
                    'Adding this trade will change your account status to Safety Sensitive.',
                '</p>',
                '<p>',
                    'For assistance selecting applicable trades, please contact a PICS representative.',
                '</p>'
            ].join('');
        }

        function requestAddTrade() {
            PICS.ajax({
                url: 'ContractorTrades!saveTradeAjax.action',
                data: $('#trade-form').serializeArray(),
                success: onTradeFormAjaxSubmitSuccess
            });
        }

        function onTradeFormAjaxSubmitSuccess(data) {
            $('#trade-view').html(data);

            loadTradeCallback();
        }

        return {
            init: init
        };
    }())
});

var search_tree, browse_tree;
var ajaxUrl = 'ContractorTrades!tradeAjax.action?contractor='+conID+'&trade.trade=';

function loadTradeCallback() {
    if (($('input.product').length > 0) || ($('input.service').length == 2 && $('input.service:checked').length == 0)) {
        $("#addButton").attr("disabled", "disabled");
    }

    $('#trade_children').hide();

    if ($('#nonSelectable').length > 0) {
        $("#addButton").attr("disabled", "disabled");
        $('#activityPercent').addClass('hide');
        $('#tradeOptions').addClass('hide');
    }

    setupCluetip();
}

function setupCluetip() {
    $('a.CTInstructions').cluetip( {
        local: true,
        hideLocal: true,
        showTitle: false,
        hoverClass : 'cluetip',
        cluetipClass : 'jtip',
        arrows: true
    });
}

$(function() {
    $('a.tradeInfo').live('click',function(e) {
        e.preventDefault();

        $('#trade_children').toggle();
    });

    $('body').delegate('.jstree a', 'click', function(e) {
        e.preventDefault();
        var data = { contractor: conID, "trade.trade": $(this).parent().attr('id') };
        $('#trade-view').load('ContractorTrades!tradeAjax.action', data, loadTradeCallback);
    });

    $('#trade-view').delegate('#trade-form .save', 'click', function(e) {
        e.preventDefault();
        $('#trade-view').load('ContractorTrades!saveTradeAjax.action', $('#trade-form').serializeArray(), loadTradeCallback)
    }).delegate('#trade-form .remove', 'click', function(e) {
        e.preventDefault();
        if (confirm(translate("JS.TradeTaxonomy.RemoveTrade"))) {
            $('#trade-view').load('ContractorTrades!removeTradeAjax.action', $('#trade-form').serializeArray());
        }
    });

    $('input.service').live("change", function() {
        if ($('input.service').length == 2 && $('input.service:checked').length == 0)
            $("#addButton").attr("disabled", "disabled");
        else
            $("#addButton").removeAttr("disabled");
    });

    $('input.product').live('change', function() {
        if ($(this).is(':checked'))
            $("#addButton").removeAttr("disabled");
        else
            $("#addButton").attr("disabled", "disabled");
    });

    setupCluetip();

    if (!$('#trade-view-single').length > 0) {
        var maxArray = [];
        $('a.trade').each(function(){
            var classArray = $.trim($(this).attr('class')).split(' ');
            for (var cls in classArray) {
                var str = classArray[cls];
                if(str.indexOf('trade-cloud-') > -1){
                    maxArray.push(Number((str.slice(str.lastIndexOf('-')+1, str.length))));
                    break;
                }
            }
        });
        if(maxArray.length > 0){
            var max = Math.max.apply(Math, maxArray);
            $('a.trade-cloud-'+max).first().click();
        }
    }
});