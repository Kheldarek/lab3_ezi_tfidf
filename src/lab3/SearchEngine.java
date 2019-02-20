package lab3;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SearchEngine {
    private static String DOCUMENTS_PATH = "./resources/documents-lab3.txt";
    private static String KEYWORDS_PATH = "./resources/keywords-lab3.txt";
    private static int MAX_PRINT_RESULTS = 10;
    private List<Document> _documents;
    private lab3.Dictionary _dictionary;

    public static void main(String[] args) {
        SearchEngine engine = new SearchEngine();
        engine.run();
    }

    public void run() {
        loadDocuments();
        loadDictionary();

        _dictionary.calculateIdfs(_documents);
        _documents.forEach(item -> item.calculateRepresentations(_dictionary));

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String query = scanner.nextLine();
            if ("q".equals(query))
                break;
            Document queryDocument = new Document(query, "query");
            queryDocument.calculateRepresentations(_dictionary);
            Map<Document, Double> similarities = new HashMap<>();
            _documents.forEach(doc -> similarities.put(doc, doc.calculateSimilarity(queryDocument)));
            List<Map.Entry<Document, Double>> sortedSimilarities = similarities.entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(Collectors.toList());
            for (int i = 0; i < MAX_PRINT_RESULTS && i < sortedSimilarities.size(); i++) {
                Map.Entry<Document, Double> entry = sortedSimilarities.get(i);
                System.out.println(entry.getKey().getTitle() + " " + entry.getValue());
            }
        }
    }

    private void loadDictionary() {
        List<String> keywords = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(KEYWORDS_PATH);
            Scanner scanner = new Scanner(fileInputStream);
            while (scanner.hasNext())
                keywords.add(scanner.next().toLowerCase());
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        _dictionary = new Dictionary(keywords);
    }

    private void loadDocuments() {
        _documents = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(DOCUMENTS_PATH);
            Scanner scanner = new Scanner(fileInputStream);
            StringBuilder currentDocument = new StringBuilder();
            String currentTitle = null;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if ("".equals(line)) {
                    _documents.add(new Document(currentDocument.toString().trim(), currentTitle));
                    currentTitle = null;
                    currentDocument = new StringBuilder();
                } else {
                    if (currentTitle == null)
                        currentTitle = line;
                    currentDocument.append(" ").append(line);
                }
            }
            if (!"".equals(currentDocument.toString().trim()))
                _documents.add(new Document(currentDocument.toString().trim(), currentTitle));
            fileInputStream.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }
}
