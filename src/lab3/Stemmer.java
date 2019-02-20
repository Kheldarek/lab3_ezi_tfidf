package lab3;

public class Stemmer {
    public static String stemToken(String token) {
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.add(token.toCharArray(),token.length());
        stemmer.stem();
        return stemmer.toString();
    }
}
