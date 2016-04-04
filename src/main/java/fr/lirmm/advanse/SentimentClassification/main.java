package fr.lirmm.advanse.SentimentClassification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class main
{
    private static int nbFolds=0;
    private static ArrayList<Object> MiP=new ArrayList<>();
    private static ArrayList<Object> MiR=new ArrayList<>();
    private static ArrayList<Object> MiF=new ArrayList<>();
    private static ArrayList<Object> MaP=new ArrayList<>();
    private static ArrayList<Object> MaR=new ArrayList<>();
    private static ArrayList<Object> MaF=new ArrayList<>();
  
  public static void main(String[] args)
    throws Exception
  {     
        String propPath="src\\main\\java\\properties\\config.properties";
        //if (args[0]!=null) propPath=args[0];
        Properties prop = new Properties();
	InputStream input = new FileInputStream(propPath);
        prop.load(input);
        
        BufferedReader r, rt;
        Instances train, test = null;
        
        r = new BufferedReader(new InputStreamReader(new FileInputStream(prop.getProperty("Data.trainPath")), "UTF-8"));
        train = new Instances(r);
        train.setClassIndex(train.numAttributes() - 1);
        ConstructionARFF obj = new ConstructionARFF(propPath);
        train = obj.ConstructionInstances(train);
        
        if (prop.getProperty("Data.testPath").length()>0){
            rt = new BufferedReader(new InputStreamReader(new FileInputStream(prop.getProperty("Data.testPath")), "UTF-8"));
            test = new Instances(rt);
            test.setClassIndex(test.numAttributes() - 1);
            test = obj.ConstructionInstances(test);
        }
        else nbFolds=Integer.parseInt(prop.getProperty("Data.nbFolds"));
        System.out.println("  Number of selected attributes = " + train.numAttributes());
        
        if (nbFolds==0) Run(train,test,propPath);
        else{
            Instances data=train;
            Random rand = new Random();   // create seeded number generator
            data.randomize(rand);        // randomize data with number generator
            data.setClass(data.attribute("_class"));
            data.stratify(nbFolds);
            for (int f=0; f<nbFolds; f++){
                System.out.println();
                System.out.println("######## Fold"+(f+1)+" ########");
                train = data.trainCV(nbFolds,f);
                test = data.testCV(nbFolds,f);
                Run(train,test,propPath);
            }
            System.out.println();
            System.out.println("######## Global results ########");
            double mip=0, mir=0, mif=0, map=0, mar=0, maf=0;
            for (int i=0;i<nbFolds;i++){
                mip+=(Double) MiP.get(i);
                mir+=(Double) MiR.get(i);
                mif+=(Double) MiF.get(i);
                map+=(Double) MaP.get(i);
                mar+=(Double) MaR.get(i);
                maf+=(Double) MaF.get(i);
            }
            System.out.println("    miP="+roundTwoDecimals(mip/nbFolds));
            System.out.println("    miR="+roundTwoDecimals(mir/nbFolds));
            System.out.println("    miF="+roundTwoDecimals(mif/nbFolds));
            System.out.println("    maP="+roundTwoDecimals(map/nbFolds));
            System.out.println("    maR="+roundTwoDecimals(mar/nbFolds));
            System.out.println("    maF="+roundTwoDecimals(maf/nbFolds));
        }
  }
  
  public static void Run(Instances train, Instances test,String propPath) throws Exception{
        Properties prop = new Properties();
	InputStream input = new FileInputStream(propPath);
        prop.load(input);
        StringToWordVector filter = Tokenisation.WordNgrams(propPath);
        filter.setInputFormat(train);
        train = Filter.useFilter(train, filter);
        test = Filter.useFilter(test, filter);
        train.setClass(train.attribute("_class"));
        test.setClass(train.attribute("_class"));
        System.out.println("  Number of attributes after Tokenization = " + train.numAttributes());
        
        double macroPrecision, macroRappel, macroFmesure;
        
        AttributeSelection f;
        if (prop.getProperty("FeatureSelection.percentageAttributes").equalsIgnoreCase("ig")){
            f = SelectionAttributs.InfoGainAttributeEval(train);
            f.setInputFormat(train);
            train = Filter.useFilter(train, f);
            test =  Filter.useFilter(test, f);
        }
        else if (Double.parseDouble(prop.getProperty("FeatureSelection.percentageAttributes"))<100){
            double numberOfAtt=100;
            numberOfAtt = (Double.parseDouble(prop.getProperty("FeatureSelection.percentageAttributes"))*train.numAttributes())/numberOfAtt;
            f = SelectionAttributs.InfoGainAttributeEval(train,(int) Math.round(numberOfAtt));
            f.setInputFormat(train);
            train = Filter.useFilter(train, f);
            test =  Filter.useFilter(test, f);
        }
        System.out.println("  Number of attributes after Feature Selection = " + train.numAttributes());
        
        SMO classifier = new SMO();
        double c = Double.parseDouble(prop.getProperty("SVM.CompexityParameter"));
        classifier.setC(c);
        classifier.buildClassifier(train);
        
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
        System.out.println("    miP="+roundTwoDecimals(eTest.weightedPrecision()));
        MiP.add(eTest.weightedPrecision());
        System.out.println("    miR="+roundTwoDecimals(eTest.weightedRecall()));
        MiR.add(eTest.weightedRecall());
        System.out.println("    miF="+roundTwoDecimals(eTest.weightedFMeasure()));
        MiF.add(eTest.weightedFMeasure());
        System.out.println("    maP="+roundTwoDecimals(macroPrecision));
        MaP.add(macroPrecision);
        System.out.println("    maR="+roundTwoDecimals(macroRappel));
        MaR.add(macroRappel);
        System.out.println("    maF="+roundTwoDecimals(macroFmesure));
        MaF.add(macroFmesure);
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