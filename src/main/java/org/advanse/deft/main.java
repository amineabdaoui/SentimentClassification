package org.advanse.deft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class main
{
  private static int folds = 10;
  //private static int nch=6;
  
  public static void main(String[] args)
    throws Exception
  {      
    String benchmark="AVoirALire";
    int fold=Integer.parseInt(args[0]);
    BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("data//"+benchmark+"//train"+fold+".arff"), "UTF-8"));
    BufferedReader rt = new BufferedReader(new InputStreamReader(new FileInputStream("data//"+benchmark+"//test"+fold+".arff"), "UTF-8"));
    
    Instances train = new Instances(r);
    Instances test = new Instances(rt);
    
    Zscore z = new Zscore(train);
    z.BuildFile();
    
    SMO classifier = new SMO();
    
    train.setClassIndex(train.numAttributes() - 1);
    test.setClassIndex(test.numAttributes() - 1);
    /*
    System.out.println("Générer les Nchars");
    LemmatiseurHandler lm = new LemmatiseurHandler("/data/TreeTagger");
    ArrayList<String> alChar = new ArrayList<String>();
    int min=5,max=5;
    String term, nchar, tweet;
    for (int i=0; i<train.numInstances(); i++){
        tweet=train.instance(i).stringValue(train.attribute("_tweet"));
        tweet=Pretraitements.ReplaceLink(tweet);
        tweet=Pretraitements.ReplaceUserTag(tweet);
        tweet=tweet.toLowerCase();
        tweet=Lemmatiser(tweet, lm);
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()) {
            term=st.nextToken();
            for (int lg=min; lg<=max; lg++){
                for (int index=lg; index<=term.length(); index++){
                    nchar = term.substring(index-lg, index);
                    if (!alChar.contains(nchar)) alChar.add(nchar);
                }
            }
        }
    }
    System.out.println("  alChar.size=" + alChar.size());
    */
    System.out.println("Construction objet ConstructionARFF..");
    ConstructionARFF obj = new ConstructionARFF();
    System.out.println("Construction train ARFF..");
    train = obj.ConstructionInstances(train/*,alChar*/);
    System.out.println("Construction test ARFF..");
    test = obj.ConstructionInstances(test/*,alChar*/);
    System.out.println("  Train.numAttributes = " + train.numAttributes());
    System.out.println("  Test.numAttributes = " + test.numAttributes());
    
    StringToWordVector filter = Tokenisation.WordNgrams(1,2);
    filter.setInputFormat(train);
    train = Filter.useFilter(train, filter);
    test = Filter.useFilter(test, filter);
    train.setClass(train.attribute("_class"));
    test.setClass(train.attribute("_class"));
    System.out.println("  numAttributes Après StringToWordVector = " + train.numAttributes());
    
    double macroPrecision, macroRappel, macroFmesure;
    PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter("results//"+benchmark+"//Best_"+(fold-1)+".txt")));
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    /*
    AttributeSelection f = SelectionAttributs.InfoGainAttributeEval(train);
    f.setInputFormat(train);
    train = Filter.useFilter(train, f);
    test =  Filter.useFilter(test, f);*/
    
    //classifier.setC(0.32);
    classifier.buildClassifier(train);
        
    // Calculer les micro et macro mesures    
    Evaluation eTest = new Evaluation(train);
    eTest.evaluateModel(classifier, test);
    macroPrecision=0; macroRappel=0; macroFmesure=0;
    for (int i=0; i<train.attribute("_class").numValues(); i++){
        macroPrecision+=eTest.precision(i);
        macroRappel+=eTest.recall(i);
        macroFmesure+=eTest.fMeasure(i);
    }
    macroPrecision=macroPrecision/train.attribute("_class").numValues();
    macroRappel=macroRappel/train.attribute("_class").numValues();
    macroFmesure=macroFmesure/train.attribute("_class").numValues();
    // Enregistrer les résultats
    w.println(roundTwoDecimals(eTest.weightedPrecision())+" & "+roundTwoDecimals(eTest.weightedRecall())+" & "+roundTwoDecimals(eTest.weightedFMeasure())+" & "+roundTwoDecimals(macroPrecision)+" & "+roundTwoDecimals(macroRappel)+" & "+roundTwoDecimals(macroFmesure));
    w.flush();
    
    /*
    Instances trainS=train;
    Instances testS=test;
    
    // Estimation du nombre d'attributs
    System.out.println("AttributeSelection ");
    // Selection d'attributs    
    for (int c=0; c<=trainS.numAttributes(); c=c+10){
        train = trainS;
        test = testS;
        AttributeSelection f = SelectionAttributs.InfoGainAttributeEval(train, c);
        f.setInputFormat(train);
        train = Filter.useFilter(train, f);
        test =  Filter.useFilter(test, f);
    
        classifier.buildClassifier(train);
        double macroPrecision, macroRappel, macroFmesure;
        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter("results//"+benchmark+"//NumAttributs"+c+"_"+(fold-1)+".txt")));
    
        // Calculer les micro et macro mesures    
        Evaluation eTest = new Evaluation(train);
        eTest.evaluateModel(classifier, test);
        macroPrecision=0; macroRappel=0; macroFmesure=0;
        for (int i=0; i<train.attribute("_class").numValues(); i++){
            macroPrecision+=eTest.precision(i);
            macroRappel+=eTest.recall(i);
            macroFmesure+=eTest.fMeasure(i);
        }
        macroPrecision=macroPrecision/train.attribute("_class").numValues();
        macroRappel=macroRappel/train.attribute("_class").numValues();
        macroFmesure=macroFmesure/train.attribute("_class").numValues();
        // Enregistrer les résultats
        w.println(c+" & "+roundTwoDecimals(eTest.weightedPrecision())+" & "+roundTwoDecimals(eTest.weightedRecall())+" & "+roundTwoDecimals(eTest.weightedFMeasure())+" & "+roundTwoDecimals(macroPrecision)+" & "+roundTwoDecimals(macroRappel)+" & "+roundTwoDecimals(macroFmesure));
        w.flush();
    }
    /*
    // Estimation du paramètre C
    for (double c=0; c<=2; c=c+0.001){
        // Contstruire le classifieur
        classifier.setC(c);
        classifier.buildClassifier(train);
        
        // Calculer les micro et macro mesures    
        Evaluation eTest = new Evaluation(train);
        eTest.evaluateModel(classifier, test);
        macroPrecision=0; macroRappel=0; macroFmesure=0;
        for (int i=0; i<train.attribute("_class").numValues(); i++){
            macroPrecision+=eTest.precision(i);
            macroRappel+=eTest.recall(i);
            macroFmesure+=eTest.fMeasure(i);
        }
        macroPrecision=macroPrecision/train.attribute("_class").numValues();
        macroRappel=macroRappel/train.attribute("_class").numValues();
        macroFmesure=macroFmesure/train.attribute("_class").numValues();
        // Enregistrer les résultats
        w.println(c+" & "+roundTwoDecimals(eTest.weightedPrecision())+" & "+roundTwoDecimals(eTest.weightedRecall())+" & "+roundTwoDecimals(eTest.weightedFMeasure())+" & "+roundTwoDecimals(macroPrecision)+" & "+roundTwoDecimals(macroRappel)+" & "+roundTwoDecimals(macroFmesure));
        w.flush();
    }
    */
  }
  
  
  public static String Lemmatiser(String tweet, LemmatiseurHandler lm)
    throws Exception
  {
    String tweet_lem = "";
    lm.clear();
    lm.setTermes(tweet);
    lm.process();
    for (String t : lm.getListTermeLem())
    {
      if (tweet_lem.length() > 1) {
        tweet_lem = tweet_lem + " ";
      }
      if ((t.contains("|")) && (t.length() > 1)) {
        try
        {
          t = t.split("\\|")[0];
        }
        catch (Exception e) {}
      }
      tweet_lem = tweet_lem + t;
    }
    return tweet_lem;
  }
  
  public static void Save(Instances data, String file) throws IOException{
      ArffSaver saver = new ArffSaver();
      saver.setInstances(data);
      saver.setFile(new File(file));
      saver.writeBatch();
  }
  
  
  public static String roundTwoDecimals(double d) {
    double r=d*100;
    DecimalFormat twoDForm = new DecimalFormat("#.#");
    return twoDForm.format(r).replaceAll(",", ".");
  }
  
}