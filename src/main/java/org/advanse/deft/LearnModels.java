
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;


/**
 *
 * @author amin.abdaoui
 */
public class LearnModels {
    
     public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException, Exception{
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("Train//T1.arff"), "UTF-8"));
        BufferedReader rt = new BufferedReader(new InputStreamReader(new FileInputStream("Test//Test1.arff"), "UTF-8"));

        Instances train = new Instances(r);
        Instances test = new Instances(rt);
        
        SMO classifier = new SMO();
        classifier.setC(0.4D);

        train.setClassIndex(train.numAttributes() - 1);
        test.setClassIndex(test.numAttributes() - 1);
        
        System.out.println("Construction objet ConstructionARFF..");
        ConstructionARFF obj = new ConstructionARFF();
        System.out.println("Construction train ARFF..");
        train = obj.ConstructionInstances(train/*, new ArrayList()*/);
        System.out.println("Construction test ARFF..");
        test = obj.ConstructionInstances(test/*, new ArrayList()*/);
        System.out.println("  Train.numAttributes = " + train.numAttributes());
        System.out.println("  Test.numAttributes = " + test.numAttributes());

        StringToWordVector filter = Tokenisation.WordNgrams(1, 1);
        filter.setInputFormat(train);
        train = Filter.useFilter(train, filter);
        test = Filter.useFilter(test, filter);
        train.setClass(train.attribute("_class"));
        test.setClass(train.attribute("_class"));
        System.out.println("  numAttributes Après StringToWordVector = " + train.numAttributes());

        // Selection d'attributs
        AttributeSelection as = SelectionAttributs.InfoGainAttributeEval(train);
        as.setInputFormat(train);
        train = Filter.useFilter(train, as);
        test =  Filter.useFilter(test, as);

        // Contstruire le classifieur
        classifier.buildClassifier(train);

        // Serialisation
        weka.core.SerializationHelper.write("DEFT15_T1_SMO.model", classifier);
        weka.core.SerializationHelper.write("DEFT15_T1_STW.model", filter);
        weka.core.SerializationHelper.write("DEFT15_T1_IG.model", as);
     }
    
}
