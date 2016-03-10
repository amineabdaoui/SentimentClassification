
import java.io.File;
import weka.core.Instances;
//import weka.core.stemmers.LovinsStemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author amin.abdaoui
 */
public class Tokenisation {
    public static StringToWordVector WordNgrams(int min, int max) throws Exception{
        final StringToWordVector filter = new StringToWordVector();
        // Paramèttres
        filter.setAttributeIndices("first-last");
        filter.setOutputWordCounts(false);
        filter.setTFTransform(false);
        filter.setIDFTransform(false);
        filter.setStopwords(new File("ressources//MotsVides.txt"));
        filter.setWordsToKeep(10000);
        filter.setMinTermFreq(1);
        NGramTokenizer tok = new NGramTokenizer();
        tok.setDelimiters(" \n 	.,;:'\"()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%");
        tok.setNGramMinSize(min);
        tok.setNGramMaxSize(max);
        filter.setTokenizer(tok);
        
        return filter;
    }
    
}
