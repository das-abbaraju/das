<div class="text-center">
    <button type="button" class="btn btn-default" data-container="body" data-toggle="popover" data-placement="left" data-title="Popover Title" data-content="Sed posuere consectetur est at lobortis. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum.">
        Popover on left
    </button>

    <button type="button" class="btn btn-default" data-container="body" data-toggle="popover" data-placement="top" data-title="Popover Title" data-content="Sed posuere consectetur est at lobortis. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum.">
        Popover on top
    </button>

    <br><br>

    <button type="button" class="btn btn-default" data-container="body" data-toggle="popover" data-placement="bottom" data-title="Popover Title" data-content="Sed posuere consectetur est at lobortis. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum.">
        Popover on bottom
    </button>

    <button type="button" class="btn btn-default" data-container="body" data-toggle="popover" data-placement="right" data-title="Popover Title" data-content="Sed posuere consectetur est at lobortis. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum.">
        Popover on right
    </button>

    <script>
        setTimeout(function() {
            $('button[data-toggle="popover"]').popover();
        }, 1000);
    </script>
</div>