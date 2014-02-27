<script id="mobile_search_result_item" type="text/template">
{{#results}}
    <li>
        <a href="/Search.action?button=getResult&searchID={{result_id}}&searchType={{search_type}}" class="search-item">
            <h1>{{result_name}}</h1>
            <p>{{result_id}}</p>
            <p>{{result_at}}</p>
            <p>{{result_type}}</p>
        </a>
    </li>
{{/results}}
</script>

<script id="mobile_search_more_results" type="text/template">
<li>
    <a href="/SearchBox.action?button=search&searchTerm={{query}}" class="search-item">
        {{more_results_link_text}}
    </a>
</li>
<p class="total-results">
    {{total_results_message}}
</p>
</script>