package net.vijedi;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.lucene.document.Field.Index.ANALYZED;
import static org.apache.lucene.document.Field.Index.NOT_ANALYZED;
import static org.apache.lucene.document.Field.Store.YES;

/**
 * Author: Tejus Parikh
 * Date: 5/14/11 11:32 AM
 */
public class Main {

    private Version version = Version.LUCENE_CURRENT;
    private Analyzer analyzer = new StandardAnalyzer(version);

    public static void main(String[] args) throws IOException, ParseException {
        Main main = new Main();
        main.go();
    }

    private void go() throws IOException, ParseException {
        List<Item> items = new ArrayList<Item>();
        items.add(new Item(1, "strawberry", "pastry"));
        items.add(new Item(2, "blueberry", "waffle"));
        items.add(new Item(2, "strawberry", "pancake"));
        IndexSearcher searcher = createSearcher(items);
        doSearch(searcher, "topping:strawberry");
        doSearch(searcher, "topping:strawberry AND base:pastry");
        doSearch(searcher, "topping:strawberry AND base:waffle");
    }

    private void doSearch(IndexSearcher searcher, String query) throws ParseException, IOException {
        QueryParser qp = new QueryParser(version, "id", analyzer);
        Query q = qp.parse(query);
        TopDocs docs = searcher.search(q, 100);
        System.out.println("Query: \"" + query + "\" returned " + docs.totalHits + " results");
    }

    private IndexSearcher createSearcher(List<Item> items) {
        IndexSearcher searcher;
        try {
            searcher = new IndexSearcher(createDirectory(items), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return searcher;
    }

    private Directory createDirectory(List<Item> items) {
        RAMDirectory directory = new RAMDirectory();

        try {
            IndexWriter indexWriter = new IndexWriter(directory, analyzer,
                    IndexWriter.MaxFieldLength.LIMITED);

            for(Item item: items) {
                indexWriter.addDocument(item.luceneDoc());
            }
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return directory;
    }

    private class Item {
        int id;
        String topping;
        String base;

        private Item(int id, String topping, String base) {
            this.id = id;
            this.topping = topping;
            this.base = base;
        }

        Document luceneDoc() {
            Document doc = new Document();
            doc.add(new Field( "id", "" + id, YES, NOT_ANALYZED));
            doc.add(new Field( "topping", topping, YES, ANALYZED));
            doc.add(new Field( "base", base, YES, ANALYZED));
            return doc;
        }
    }
}
