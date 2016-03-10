package org.advanse.deft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.annolab.tt4j.TreeTaggerException;
import weka.core.Attribute;
import weka.core.Instances;


/**
 *
 * @author amin.abdaoui
 */
public class ConstructionARFF {
    
    private final ArrayList<String> POS = new ArrayList<>();
    private CalculAttributs ca;
    
    public ConstructionARFF() throws IOException{
        ca = new CalculAttributs();
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("ressources//POS.txt")));
        String line;
        while ((line=r.readLine())!=null) POS.add(line);
        r.close();
    }
    
    public Instances ConstructionInstances(Instances data/*, ArrayList<String> alChar*/) throws Exception{
        String tweet, tweetLem;
        Instances newData = new Instances(data);
        // Entete
        //newData.insertAttributeAt(new Attribute("_numberElongatedWords"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_punctuation"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_allCaps"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_emoticonePos"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_emoticoneNeg"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_hashtags"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_countNegators"), newData.numAttributes());
        // Attributs des Lexiques
        //newData.insertAttributeAt(new Attribute("_countPosFEEL"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_countNegFEEL"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_countPosPolarimots"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_countNegPolarimots"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_countNeuPolarimots"), newData.numAttributes());
        newData.insertAttributeAt(new Attribute("_countPosAffects"), newData.numAttributes());
        newData.insertAttributeAt(new Attribute("_countNegAffects"), newData.numAttributes());
        newData.insertAttributeAt(new Attribute("_countNeuAffects"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_countPosDiko"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_countNegDiko"), newData.numAttributes());
        //newData.insertAttributeAt(new Attribute("_countNeuDiko"), newData.numAttributes());
        //for (int i=0; i<7; i++) newData.insertAttributeAt(new Attribute("_countEmotion"+(i+1)), newData.numAttributes());
        for (int j=0; j<POS.size(); j++) newData.insertAttributeAt(new Attribute("__"+POS.get(j)), newData.numAttributes());
        //for (int i=0; i<alChar.size(); i++) newData.insertAttributeAt(new Attribute("__"+alChar.get(i)), newData.numAttributes());
        // Data
        for (int i=0; i<newData.numInstances(); i++){
            tweet = newData.instance(i).stringValue(data.attribute("_text"));
            // Appliquer les prétraitements
            //tweet = Pretraitements.ReplaceLink(tweet);
            //tweet = Pretraitements.ReplaceMail(tweet);
            //tweet = Pretraitements.ReplaceUserTag(tweet);
            tweet = Pretraitements.ReplaceArgots(tweet);
            // Attributs
            //newData.instance(i).setValue(newData.attribute("_numberElongatedWords"), ca.ElongatedWords(tweet));
            //newData.instance(i).setValue(newData.attribute("_punctuation"), (tweet.contains("!") || tweet.contains("?"))? 1 : 0);
            //newData.instance(i).setValue(newData.attribute("_allCaps"), ca.AllCaps(tweet));
            //newData.instance(i).setValue(newData.attribute("_emoticonePos"), ((ca.EmoticonesPos(tweet))? 1 : 0));
            //newData.instance(i).setValue(newData.attribute("_emoticoneNeg"), ((ca.EmoticonesNeg(tweet))? 1 : 0));
            //newData.instance(i).setValue(newData.attribute("_hashtags"), ca.CountHashtag(tweet));
            //newData.instance(i).setValue(newData.attribute("_countNegators"), (ca.CountNegation(tweet)));
            // Lémmatisation
            tweet = tweet.toLowerCase();
            tweet = ca.Lemmatiser(tweet);
            newData.instance(i).setValue(newData.attribute("_text"), tweet);
            // Lexiques
            tweetLem = ca.Lemmatiser(tweet);
            tweetLem = tweetLem.toLowerCase();
            //newData.instance(i).setValue(newData.attribute("_countPosFEEL"), ca.ComputePosFEEL(tweetLem));
            //newData.instance(i).setValue(newData.attribute("_countNegFEEL"), ca.ComputeNegFEEL(tweetLem));
            //newData.instance(i).setValue(newData.attribute("_countPosPolarimots"), ca.ComputePosPolarimots(tweetLem));
            //newData.instance(i).setValue(newData.attribute("_countNegPolarimots"), ca.ComputeNegPolarimots(tweetLem));
            //newData.instance(i).setValue(newData.attribute("_countNeuPolarimots"), ca.ComputeNeuPolarimots(tweetLem));
            newData.instance(i).setValue(newData.attribute("_countPosAffects"), ca.ComputePosAffects(tweetLem));
            newData.instance(i).setValue(newData.attribute("_countNegAffects"), ca.ComputeNegAffects(tweetLem));
            newData.instance(i).setValue(newData.attribute("_countNeuAffects"), ca.ComputeNeuAffects(tweetLem));
            //newData.instance(i).setValue(newData.attribute("_countPosDiko"), ca.ComputePosDiko(tweetLem));
            //newData.instance(i).setValue(newData.attribute("_countNegDiko"), ca.ComputeNegDiko(tweetLem));
            //newData.instance(i).setValue(newData.attribute("_countNeuDiko"), ca.ComputeNeuDiko(tweetLem));
            //for (int j=0; j<7; j++) newData.instance(i).setValue(newData.attribute("_countEmotion"+(j+1)), ca.ComputeEmotion(tweetLem, j));
            for (int j=0; i<POS.size(); i++) newData.instance(i).setValue(newData.attribute("__"+POS.get(j)), ca.POS(tweet,POS.get(j)));
            //for (int j=0; j<alChar.size(); j++) newData.instance(i).setValue(newData.attribute("__"+alChar.get(j)), tweet.startsWith(alChar.get(j))? 1 : 0);
        }        
        return newData;
    }
    
}