$(document).ready(function(){
     $( "#start-date,#end-date" ).datepicker();
     $("#side-nav-links ul li").on("click", function(){
         if(!$(this).hasClass("selected"))
         {
             $(this).parent().find("li").removeClass("selected").prev().find("div:nth-child(2)").css("border-bottom","1px solid #797979");
             $(this).addClass("selected").prev().find("div:nth-child(2)").css("border-bottom","0px");

         }
     }) ;

     $("#side-nav-links ul li").on("click", function(){
          var sectionSelected = $(this).find("div:first").attr("class");
          switch(sectionSelected){
              case "messages":
                    $("#teaser-section,#home-section,#benef-section").hide();
                    $("#messages-section").show();
                 break;

              case "home":
                 $("#teaser-section,#home-section").show();
                 $("#messages-section,#benef-section").hide();
              break;

              case "beneficiaries":
                  $("#teaser-section,#home-section,#messages-section").hide();
                  $("#benef-section").show()

           }
     })

    $("#pre-added-bene ul li").on("click", function(){
        if($(this).hasClass("ben-selected"))
           $(this).removeClass("ben-selected")
        else
        $(this).addClass("ben-selected");

        var selectedUser = $(this).attr("benefId");

    })

})
function PopulateUserInfo(resp){
   if(resp.message == "success"){
        var userId = resp.data.user.id;
       insertHandlebarsToDom( resp, "dashboard-heading", "#home-section .header-line", "my-account" )

   }
}
function PopulateCounts(resp){
     insertHandlebarsToDom( resp, "counts", "#home-section #counts", "my-account" )
     insertHandlebarsToDom( resp, "outgoing-message-summary", "#sent-messages-block #outgoing", "my-account" )
     insertHandlebarsToDom( resp, "incoming-message-summary", "#sent-messages-block #incoming", "my-account" )
     insertHandlebarsToDom( resp, "beneficiary-summary", "#sent-messages-block #bene", "my-account" )
     insertHandlebarsToDom( resp, "messages", "#messages-section", "my-account" )
    $.modal.close()
}