package lab3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Dictionary {
    private Map<String, Double> _idfs;
    private List<String> _terms;

    public Dictionary(List<String> keywords) {
        _terms = keywords
                .stream()
                .map(Stemmer::stemToken)
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<String, Double> getIdfs() {
        return _idfs;
    }

    public List<String> getTerms() {
        return _terms;
    }

    public void calculateIdfs(List<Document> documents) {
        this._idfs = _terms.stream()
                .collect(Collectors.toMap(
                        term -> term,
                        term -> calculateIdfForTerm(term, documents)));
    }

    private double calculateIdfForTerm(String term, List<Document> documents) {
        long documentsWithTerm = documents.stream()
                .filter(document -> document.getContent().contains(term))
                .count();
        if (documentsWithTerm != 0) {
            return Math.log((double) documents.size() / documentsWithTerm);
        } else {
            return 0;
        }

    }

}
