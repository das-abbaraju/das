$(document).ready(function(){
      $("#add-plus-block a,#schedule-message").on("click",function(){

        if($(".animated-area").is(":hidden"))
             $( ".animated-area" ).slideDown( "slow" );
        else
            $( ".animated-area" ).slideUp( "slow" );

    })

    $("#select-message-type div").on("click",function(){
        var clickedOn = $("this").attr("id");
        $("#select-message-type div").removeClass("on");
        $(this).addClass("on");



    })



 /************* end of document ready***/
})