package org.advanse.deft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.StringTokenizer;
import weka.core.Instances;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author amin.abdaoui
 */
public class Zscore {
    
    private final int nbDocuments;
    private double threshold;
    private ArrayList<String> termes;
    private ArrayList<String> classes;
    private ArrayList<Document> documents;
    private LemmatiseurHandler lm;
    
    public Zscore(Instances data) throws Exception{
        String ch="/TreeTagger"; // sur le serveur: ch="/data/TreeTagger"
        lm = new LemmatiseurHandler(ch);
        nbDocuments = data.numInstances();
        documents = new ArrayList<Document>();
        classes = new ArrayList<String>();
        String text;
        for (int i=0; i<nbDocuments; i++){
            text = data.instance(i).stringValue(data.attribute("_text"));
            text = text.toLowerCase();
            text = Lemmatiser(text);
            documents.add(new Document(text,data.instance(i).stringValue(data.attribute("_class"))));
        }
        for (int i=0; i<data.attribute("_class").numValues(); i++) classes.add(data.attribute("_class").value(i));
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
    
    public void BuildFile() throws IOException{
        String terme;
        termes = new ArrayList<String>();
        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter("ressources//ZscoresAVoirALire.txt")));
        for (Document doc:documents){
            StringTokenizer st = new StringTokenizer(doc.getDocument(), " ");
            while (st.hasMoreTokens()){
                terme=st.nextToken();
                if (terme.length()>2)
                if (!termes.contains(terme)){
                    termes.add(terme);
                    w.print(terme);
                    for (String c:classes) w.print(";"+ComputeZscore(documents,terme,c));
                    w.println();
                    w.flush();
                }
            }
        }
        w.close();
    }
    
    public double ComputeZscore(ArrayList<Document> Doc, String terme, String classe){
        double zscore;
        double a = 0 , b = 0 , c = 0 , d = 0, n;
        
        for (Document doc : Doc){
            StringTokenizer st = new StringTokenizer(doc.getDocument(), " ");
            while (st.hasMoreTokens()){
                if (st.nextToken().equalsIgnoreCase(terme))
                    if (doc.getClasse().equalsIgnoreCase(classe))
                        a++ ;
                    else
                        b++;
                else
                    if (doc.getClasse().equalsIgnoreCase(classe))
                        c++;
                    else 
                        d++;
            }
        }
        
        n = a+b+c+d;
        zscore = (a - ((a+c)*(a+b)/n))/sqrt(((a+c)*(a+b)/n)*(1-((a+b)/n))) ; 
               
        return zscore ;
    }
    
    public String Lemmatiser(String tweet) throws Exception {
        String tweet_lem="";
        lm.clear();
        lm.setTermes(tweet);
        lm.process();
        for (String t:lm.getListTermeLem()){
            if (tweet_lem.length()>1) tweet_lem+=" ";
            if (t.contains("|") && t.length()>1){
                try{
                    t=t.split("\\|")[0];
                } catch(Exception e){
                }
            }
            tweet_lem+=t;
        }
        return tweet_lem;
    }
    
      public static void main(String[] args)
    throws Exception
  {
    BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\amin.abdaoui\\Documents\\NetBeansProjects\\DEFT\\AVoirALireDEFT07\\train.arff"), "UTF-8"));
    Instances train = new Instances(r);
    Zscore z = new Zscore(train);
    z.BuildFile();
    
  }
    
}
