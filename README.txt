Index the documents:
search-cmd solr menu_index docs/item1.xml
search-cmd solr menu_index docs/item2.xml
search-cmd solr menu_index docs/item3.xml

Run queries:
search-cmd search menu_index "topping_text:\"strawberry\""
** This returns 2 documents, which is correct

search-cmd search menu_index "topping_text:\"strawberry\" and base_text:\"pastry\""
** This returns 2 documents, I think it should only return menu_index/1

search-cmd search menu_index "topping_text:\"strawberry\" and base_text:\"waffles\""
** This returns 3 documents, it shouldn't return any


Lucene:
I double checked this against my understanding of how lucene parses these queries.
The "lucene" subdirectory contains a maven project that creates a similar 
index in lucene and runs the same queries. This is the output:

Query: "topping:strawberry" returned 2 results
Query: "topping:strawberry AND base:pastry" returned 1 results
Query: "topping:strawberry AND base:waffle" returned 0 results