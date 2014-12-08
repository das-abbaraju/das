
Handlebars.registerHelper("firstLetterUpperCase", function(str) {
     str = str.toLowerCase().replace(/\b[a-z]/g, function(letter) {
        return letter.toUpperCase();
    });
    return str;
});


Handlebars.registerHelper("isEqualTo", function() {
    var
                args           = Array.prototype.slice.call(arguments)
            ,   objOptions     = args.pop()
        ;

        if ( window[args[0]] !== undefined && args[0] !== 0 ) {
            args[0] = window[args[0]];
        }

        for ( var i = 0; i < args.length; ++i ) {
            if ( args[i] === args[0] && i !== 0 ) {
                return objOptions.fn(this);
            }
        }

        return objOptions.inverse(this);
});

Handlebars.registerHelper("gt", function ( intBase, intValue, objOptions )
    {
        if ( window[ intBase ] !== undefined && intBase !== 0 ) {
            intBase = window[ intBase ];
        }
        // If second param is var name, not number
        if ( typeof intValue !== 'number' && window[ intValue ] !== undefined && intValue !== 0 ) {
            intValue = window[ intValue ];
        }

        if ( typeof intBase === 'number' && typeof intValue === 'number' && intBase > intValue ) {
            return objOptions.fn(this);
        }

        return objOptions.inverse(this);
    }
);

Handlebars.registerHelper("add", function ( firstVal, secondVal )
    {
        var addedNumber = parseInt(firstVal)+parseInt(secondVal)

        return addedNumber;
    }
);

Handlebars.registerHelper("forEachWithBreak",function ( arrItems, intBreakIndex, objOptions )
    {
        var
                strRet          = ''
            ;

        for ( var i = 0, j = arrItems.length; i<j; i++ ) {
            strRet += objOptions.fn( arrItems[i] );
            if ( i === parseInt( intBreakIndex - 1 ) )
                break;
        }

        return strRet;
    }
 )

Handlebars.registerHelper("formatDate", function ( unformatteddate, format )
    {
        var date = new date(unformatteddate);
        var day = date.getDate();
        var month = date.getMonth();
        var year = date.getFullYear();

        var d = year + '-' + month + '-' + day;

        return d;
    }
);