<script id="mobile_search_result_item" type="text/template">
{{#results}}
    <li>
        <a href="/Search.action?button=getResult&searchID={{result_id}}&searchType={{search_type}}" class="search-item account-{{account_status}}">
            <h1 class="name">{{result_name}}</h1>
            <p class="id">{{result_id}}</p>
            <p class="location">{{result_at}}</p>
            <p class="type">{{result_type}}</p>
        </a>
    </li>
{{/results}}
</script>

<script id="mobile_search_more_results_link" type="text/template">
<li>
    <a href="/SearchBox.action?button=search&searchTerm={{query}}" class="search-item">
        {{more_results_link_text}}
    </a>
</li>
</script>

<script id="mobile_search_more_results_msg" type="text/template">
<p class="total-results">
    {{total_results_message}}
</p>
</script>