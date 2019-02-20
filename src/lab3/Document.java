package lab3;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Document {
    private String _content;
    private String _title;
    private List<String> _terms;
    private List<Double> _tfIdfs;

    public Document(String content, String title) {
        this._content = content;
        this._title = title;
        preprocessDocument();
    }

    public String getContent() {
        return _content;
    }

    public String getTitle() {
        return _title;
    }

    public List<String> getTerms() {
        return _terms;
    }

    public List<Double> getTfIdfs() {
        return _tfIdfs;
    }

    public void preprocessDocument() {
        String normalized = normalizeText(_content);
        List<String> tokens = tokenizeDocument(normalized);
        _terms = stemTokens(tokens);
    }

    public void calculateRepresentations(Dictionary dictionary) {
        Map<String, Double> bagOfWords = calculateBagOfWords(_terms, dictionary);
        Map<String, Double> tfs = calculateTfs(bagOfWords);
        _tfIdfs = calculateTfIds(tfs, dictionary);
        int x = 0;
    }

    private List<String> stemTokens(List<String> tokens) {
        return tokens.stream()
                .map(Stemmer::stemToken)
                .collect(Collectors.toList());
    }

    private String normalizeText(String content) {
        return content.replaceAll("[^A-Za-z0-9 ]", "").toLowerCase();
    }

    private List<String> tokenizeDocument(String normalized) {
        return Stream.of(normalized.split("\\s+")).collect(Collectors.toList());
    }

    private Map<String, Double> calculateBagOfWords(List<String> terms, lab3.Dictionary dictionary) {
        Map<String, Double> bag = new HashMap<>();
        List<String> dictionaryTerms = dictionary.getTerms();
        /*dictionaryTerms.forEach(term -> {
            long occurrences = calculateOccurences(term, terms);
            if (occurrences > 0) {
                bag.put(term, (double) occurrences);
            }
        });*/
        dictionaryTerms.forEach(term -> bag.put(term, (double) calculateOccurences(term, terms)));
        return bag;
    }

    private long calculateOccurences(String term, List<String> allTerms) {
        return allTerms.stream()
                .filter(current -> current.equals(term))
                .count();
    }

    private Map<String, Double> calculateTfs(Map<String, Double> bagOfWords) {
        Double max = bagOfWords.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1);

        return bagOfWords.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> getTFSValue(max, entry)));
    }

    private double getTFSValue(Double max, Map.Entry<String, Double> entry) {
        return max ==0.0 ? 0.0 : entry.getValue() / max;
    }

    private List<Double> calculateTfIds(Map<String, Double> tfs, Dictionary dictionary) {
        Map<String, Double> idfs = dictionary.getIdfs();
        return dictionary.getTerms().stream()
                .map(term -> tfs.get(term) * idfs.get(term))
                .collect(Collectors.toList());
    }

    public double calculateSimilarity(Document query) {
        double scalarProduct = 0D;
        double normQuery = 0D;
        double normDocument = 0D;

        List<Double> queryData = query._tfIdfs;
        List<Double> docData = getTfIdfs();

        for (int i = 0; i < queryData.size(); i++) {
            scalarProduct += queryData.get(i) * docData.get(i);
            normDocument += Math.pow(docData.get(i), 2);
            normQuery += Math.pow(queryData.get(i), 2);
        }
        double denominator = Math.sqrt(normDocument) * Math.sqrt(normQuery);
        if(denominator!=0.0) {
            return scalarProduct / denominator;
        } else {
            return 0.0;
        }
    }
}
