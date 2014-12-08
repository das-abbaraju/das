var messageType="";
var messageSubject="";
var messageBody ="";

String.prototype.escapeSpecialChars = function() { return this.replace(/\\/g, "\\\\"). replace(/\n/g, "\\n"). replace(/\r/g, "\\r"). replace(/\t/g, "\\t"). replace(/\f/g, "\\f"). replace(/"/g,"\\\""). replace(/'/g,"\\\'"). replace(/\&/g, "\\&"); }

$(document).ready(function(){




    if($("select#recurrent option:selected").val()== "once"){
        $("#end-date-row").hide()
    }
    $("#deliver-options li.send-options").on("click",function(){

        $("#deliver-options ul li.send-options").removeClass("delivery-selected")
        $(this).addClass("delivery-selected");

        switch($(this).attr("id")){
            case "postmortem" :
                $("#start-date-row").hide();
                 $("#end-date-row").hide();
                $("select#recurrent").prop('selectedIndex',0);
                $("#date-selection-block").hide().css("left","70px").show()
                if($("select#recurrent").change(function(){
                    if($(this).val()!="once"){
                        $("#start-date-row").show()
                        $("#end-date-row").show()
                    }
                    else
                    {
                        $("#start-date-row").hide()
                        $("#end-date-row").hide()
                    }
                }))

            break;
            case "postmortem-date" :
                $("select#recurrent").prop('selectedIndex',0);
                 $("#start-date-row").show()
                 $("#date-selection-block").hide().css("left","200px").show()
                if($("select#recurrent").change(function(){
                    if($(this).val()!="once"){
                        $("#start-date-row").show()
                        $("#end-date-row").show()
                    }
                    else
                    {
                        $("#start-date-row").show()
                        $("#end-date-row").hide()
                    }
                }))
            break;
            case "prior-date" :
                $("select#recurrent").prop('selectedIndex',0);
                 $("#start-date-row").show()
                $("#date-selection-block").hide().css("left","380px").show()
                if($("select#recurrent").change(function(){
                    if($(this).val()!="once"){
                        $("#start-date-row").show()
                        $("#end-date-row").show()
                    }
                    else
                    {
                        $("#start-date-row").show()
                        $("#end-date-row").hide()
                    }
                }))
            break;
        }

        $("#methodType").val($(this).attr("id"))
        $("#date-selection-block").show();
    })

    $("#end-date-condition").change(function(){
        switch($(this).val()){
            case "for":
              $("#end-occurance").show();
              $(".end-occurrance-span").removeClass("displayNone")

            break
            case "by":
                 $("#end-occurance,#end-occurrance-span").hide();
                 $("#end-date").show();
                 $(".end-occurrance-span").addClass("displayNone")
            break;
            default:
                 $("#end-occurance,#end-date").hide();
                 $(".end-occurrance-span").addClass("displayNone")
        }
    })




    $("#message-form").validate({

                    errorPlacement          : function( label, element ) {
                            element.addClass('error');
                            label.addClass('inputValidationError');
                            label.insertAfter(element);
                        },
                    wrapper: 'div',
                    focusInvalid: false,
                    rules: {

                            subject: {
                                required: true,
                                notEqual: "Enter a subject"
                                },
                            emailbody: {
                                 required: true
                            }
                    },
                    messages: {

                        subject: {
                                required: "Subject is required!",
                                notEqual: "Subject is required!"

                                },
                        emailbody: {
                                required: "Email body is required!"

                                }

                    },
                    submitHandler:function(form) {

                        var typeOfMessage = $("#message-type").val();
                        switch(typeOfMessage){
                            case "text" :
                                var subject = $("#compose-"+typeOfMessage+" #subject").val();
                                    subject = subject.escapeSpecialChars();
                                var body = $("#compose-"+typeOfMessage+" #emailbody").val();
                                    body = body.escapeSpecialChars();
                                var data = '{ "userMessage": {"dateOfDelivery": null,"subject": "'+subject+'", "messageBody": "'+body+'"}}';
                                var url = '/api/usermsg/add';

                                LoadSpinner("data-loader-window","Saving Message...");
                                jQuery.ajax({
                                                        method:     'Post',
                                                        url:        url,
                                                        data:       data,
                                                        headers: {
                                                            "Accept" : "application/json; charset=utf-8",
                                                            "Content-Type": "application/json; charset=utf-8"
                                                        },
                                                        beforeSend: function() {
                                                            //attach loader
                                                           $('#data-loader-window').modal();

                                                        },
                                                        success:    function(resp) {
                                                            insertHandlebarsToDom( resp, "add-beneficiary-form", "#add-beneficiary-container", "my-account" )
                                                            $("#message-form").hide();
                                                            $("#add-beneficiary-form").show();
                                                            $.modal.close();

                                                        },
                                                        error:      function() {
                                                            alert("error")
                                                        }
                                                    });
                                /*$("#message-preview-block p#subject-preview").html("Re: "+subject);
                                $("#message-preview-block p#body-preview").html(body);
                                $("#message-form").hide();
                                $("#add-beneficiary-form").show();*/
                            break;
                        }
                    }
                });

      $("#login-link").on("click", function(){
          $("#login-div").modal();
      })
     $("#signup-link").on("click", function(){
          $("#sign-up-div").modal();
      })

     // validate sigup form
    jQuery.validator.addMethod(
            'notEqual'
        ,   function( value, element, param )
            {
                return this.optional(element) || value !== param;
            }
        ,   'This is not correct'
        );

        jQuery.validator.addMethod(
            'lettersdashonly'
        ,   function( value, element )
            {
                return this.optional(element) || /^[a-z-'\"\s]+$/i.test( value );
            }
        ,   'Letters and dashes only please'
        );

        jQuery.validator.addMethod(
            'phonenumber'
            ,   function( value, element )
                {
                    value = value.replace(/[-' '()]/g,'');

                    return this.optional(element) || !isNaN(value);
                }
            ,   'Please enter a valid phone number'
        );

        jQuery.validator.addMethod(
            'phonenumberlength'
            ,   function( value, element )
                {
                    value = value.replace(/[-' '()]/g,'');

                    console.log( value[0] );

                    return this.optional(element) || ( !isNaN(value) && ( value.length == 10 || ( value.length == 11 && value[0] == 1 ) ) );
                }
            ,   'Please enter a 10 digit phone number'
        );

     $("#sign-up-form").validate({


            errorPlacement          : function( label, element ) {
                    element.addClass('error');
                    label.addClass('inputValidationError');
                    label.insertAfter(element);
                },
            wrapper: 'div',
            focusInvalid: false,
            rules: {
                    fn: {
                        required: true,
                        notEqual: "First Name",
                        lettersdashonly: true
                        },
                    ln: {
                        required: true,
                        notEqual: "Last Name",
                        lettersdashonly: true
                        },
                    email: {
                        required: true,
                        notEqual: "Email Address",
                        email: true
                        },
                    password: {
                         required: true,
                         notEqual: "Password",
                         minlength: 6
                    },
                    zipcode: {
                         required: true,
                         notEqual: "Zipcode"
                    }
            },
            messages: {
            fn: {
                required: "First Name is required",
                notEqual:"Valid First Name is required",
                lettersdashonly:"First Name is invalid"
            },
            ln: {
                required: "Last Name is required",
                notEqual:"Valid Last Name is required",
                lettersdashonly:"Last Name is invalid"
            },
            email: {
                    required: "Email is required",
                    notEqual:"Valid Email is required",
                    email: "Email is invalid"
                    },
            password: {
                    required: "password is required",
                    notEqual:"Valid Password is required",
                    rangelength: jQuery.format("Enter at least {0} characters")
                    },
            zipcode: {
                    required: "zipcode is required",
                    notEqual:"Valid zipcode is required"

                    }

            },
            submitHandler:function(form) {
                         var queryData = '{"firstName":"'+$("#fn").val()+'","lastName":"'+$("#ln").val()+'","email":"'+$("#email").val()+'","password":"'+$("#password").val()+'","birthDate":null,"gender":null,"zipCode":"'+$("#zipcode").val()+'","created":null}';
                         var url = '/api/user/reg';
                         jQuery.ajax({
                                method:     'POST',
                                url:        url,
                                data:       queryData,
                                headers: {
                                    "Accept" : "application/json; charset=utf-8",
                                    "Content-Type": "application/json; charset=utf-8"
                                },
                                beforeSend: function() {
                                    //attach loader
                                },
                                success:    function(resp) {
                                  if(resp.statusCode == '200' || resp.statusCode == 200)
                                      window.location.href="myaccount.view?uid="+resp.data.id;
                                  else
                                  {
                                      if(resp.statusCode == 400)
                                        var err= '<div class="inputValidationError" style="margin-top:10px"><label for="username" class="error" style="display: inline;color:#ff0000;">Registration failed.This user already exists!</label></div>';
                                      else
                                      var err= '<div class="inputValidationError" style="margin-top:10px"><label for="username" class="error" style="display: inline;color:#ff0000;">Registration failed.Please try again!</label></div>';
                                         $(err).insertAfter("#zipcode");
                                         $("#sign-up-div .input-holder input").addClass("error")


                                  }

                                },
                                error:      function() {

                                }
                            });
            }
        });

     $("#login-form").validate({

                    errorPlacement          : function( label, element ) {
                            element.addClass('error');
                            label.addClass('inputValidationError');
                            label.insertAfter(element);
                        },
                    wrapper: 'div',
                    focusInvalid: false,
                    rules: {

                            username: {
                                required: true,
                                notEqual: "Email",
                                email: true
                                },
                            loginpassword: {
                                 required: true,
                                 notEqual: "Password",
                                 minlength: 6
                            }
                    },
                    messages: {

                        username: {
                                required: "Email is required",
                                notEqual:"Valid Email is required",
                                email: "Email is invalid"
                                },
                        loginpassword: {
                                required: "password is required",
                                notEqual:"Valid Password is required",
                                rangelength: jQuery.format("Enter at least {0} characters")
                                }

                    },
                    submitHandler:function(form) {
                         var queryData = '{"email":"'+$("#username").val()+'","password":"'+$("#loginpassword").val()+'"}';
                         var url = '/api/user/auth';
                         jQuery.ajax({
                                method:     'POST',
                                url:        url,
                                data:       queryData,
                                headers: {
                                    "Accept" : "application/json; charset=utf-8",
                                    "Content-Type": "application/json; charset=utf-8"
                                },
                                beforeSend: function() {
                                    //attach loader
                                },
                                success:    function(resp) {

                                   if(resp.message != "false"){
                                       window.location.href="myaccount.view?uid="+resp.data.id
                                   }
                                    else {
                                         var err= '<div class="inputValidationError"><label for="username" class="error" style="display: inline;">Invalid Login attempt!</label></div>';
                                         $(err).insertAfter("#username");
                                         $("#username,#loginpassword").addClass("error")
                                   }

                                },
                                error:      function(e) {
                                    alert(e.toLocaleString())

                                }
                            });


                    }
                });




 })

function changeInputVal(objElem, strText )
    {
        if ( objElem.value == strText ) {
            objElem.value = '';
            objElem.style.color = '#333';

        } else if ( objElem.value === '' ) {
            objElem.style.color = '#999';
            objElem.value = strText;
        }


    }

function insertHandlebarsToDom( objData, strTemplateToUse, strClassOrID, strVerticalDir, strInsertType )
    {
        // Set the Variables
        var
            // Class or ID to load the HTML into in the DOM
            $classIDToInsertInto      = $( strClassOrID )
            // The HTML Template
        ,   templateHTML              = this.compileHandlebarsTemplate( objData, strTemplateToUse, strVerticalDir )
        ;

        if ( typeof strClassOrID === 'object' ) {
            $classIDToInsertInto = strClassOrID;
        }



        if ( strInsertType === 'html' ) {
            // Load the compiled HTML into the Dom
            $classIDToInsertInto.html( templateHTML );
        } else if ( strInsertType === 'prepend' ) {
            // Load the compiled HTML into the Dom
            $classIDToInsertInto.prepend( templateHTML );
        } else if ( strInsertType === 'before' ) {
            // Load the compiled HTML before the Dom element
            $classIDToInsertInto.before( templateHTML );
        } else if ( strInsertType === 'after' ) {
            // Load the compiled HTML after the Dom element
            $classIDToInsertInto.after( templateHTML );
        } else if ( strInsertType === 'replaceWith' ) {
            // Replace the Dom element with the compiled HTML
            $classIDToInsertInto.replaceWith( templateHTML );
        } else {
            // Load the compiled HTML into the Dom
            $classIDToInsertInto.append( templateHTML );
        }

        //$.Topic( strTemplateToUse + '.Loaded' ).publish();


    }

function compileHandlebarsTemplate( objData, strTemplateToUse, strVerticalDir )
    {
        // Set the Variables
        var
        // The Compiled Handlebars Template
                compiledTemplate          = getHandlebarsTemplate( strTemplateToUse, strVerticalDir )
        // The HTML Template
            ,   templateHTML              = compiledTemplate( objData )
        ;

        return templateHTML;
    }
function getHandlebarsTemplate( strTemplateName, strVerticalDir )
    {
        // Check to see if strVerticalDir is provided, if not set it to 'global'
        if ( strVerticalDir === undefined || strVerticalDir === '' ) {
            strVerticalDir = 'global';
        }

        var
            strCallUrl      = '/site/static/' + strVerticalDir + '/templates/' + strTemplateName + '.handlebars'
        ;

        // Check to see if pre-compiled templates, or the specified template even exist
        if (
                // If there are no pre-compiled templates
                Handlebars.templates === undefined
                ||
                // Or the provided template to use is not pre-compiled
                Handlebars.templates[ strTemplateName ] === undefined
            )
        {

            // Check to see if there are already pre-compiled templates
            if ( Handlebars.templates === undefined ) {
                // If not create the object for later use
                Handlebars.templates = {};
            }

            $.when(
                $.ajax(
                    {
                                method:     'GET',
                                url:        strCallUrl,
                                dataType:   'html',
                                async:   false
                    }
                )
            )
            .done( function ( objAjaxReturn )             {
                // Create the specified template by compiling it on the spot using the returned HTML
                Handlebars.templates[ strTemplateName ] = Handlebars.compile( objAjaxReturn );
            });
            // End $jq.when
        }

        // Check to see if the template compiled correctly and exists
        if ( Handlebars.templates[ strTemplateName ] !== undefined ) {
            // Return the Handlebars Template
            return Handlebars.templates[ strTemplateName ];
        } else {
            return Handlebars.compile('strErrorHtml');
        }
    }

 function LoadSpinner(parentElement,displayText){
    var opts = {
                  lines: 13, // The number of lines to draw
                  length: 20, // The length of each line
                  width: 10, // The line thickness
                  radius: 30, // The radius of the inner circle
                  corners: 1, // Corner roundness (0..1)
                  rotate: 0, // The rotation offset
                  direction: 1, // 1: clockwise, -1: counterclockwise
                  color: '#fff', // #rgb or #rrggbb or array of colors
                  speed: 1, // Rounds per second
                  trail: 60, // Afterglow percentage
                  shadow: false, // Whether to render a shadow
                  hwaccel: false, // Whether to use hardware acceleration
                  className: 'spinner', // The CSS class to assign to the spinner
                  zIndex: 2e9, // The z-index (defaults to 2000000000)
                  top: '50%', // Top position relative to parent in px
                  left: '50%' // Left position relative to parent in px
                };
var target = document.getElementById(parentElement);
var spinner = new Spinner(opts).spin(target);
$("#"+parentElement+" .loader").html(displayText)


 }

 function LoadSmallSpinner(parentElement,displayText){
    var opts = {
                  lines: 13, // The number of lines to draw
                  length: 10, // The length of each line
                  width: 5, // The line thickness
                  radius: 15, // The radius of the inner circle
                  corners: 1, // Corner roundness (0..1)
                  rotate: 0, // The rotation offset
                  direction: 1, // 1: clockwise, -1: counterclockwise
                  color: '#333', // #rgb or #rrggbb or array of colors
                  speed: 1, // Rounds per second
                  trail: 60, // Afterglow percentage
                  shadow: false, // Whether to render a shadow
                  hwaccel: false, // Whether to use hardware acceleration
                  className: 'spinner', // The CSS class to assign to the spinner
                  zIndex: 2e9, // The z-index (defaults to 2000000000)
                  top: '50%', // Top position relative to parent in px
                  left: '50%' // Left position relative to parent in px
                };
var target = document.getElementById(parentElement);
var spinner = new Spinner(opts).spin(target);
$("#"+parentElement+" .loader").html(displayText)


 }

