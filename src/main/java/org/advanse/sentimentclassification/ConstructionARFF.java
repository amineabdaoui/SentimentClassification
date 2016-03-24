package org.advanse.sentimentclassification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import weka.core.Attribute;
import weka.core.Instances;


/**
 *
 * @author amin.abdaoui
 */
public class ConstructionARFF {
    
    private final ArrayList<String> POS = new ArrayList<>();
    protected static final int nbClassesFEEL=7;
    protected static final int nbClassesAffects=45;
    protected static final int nbClassesDiko=1198;
    private CalculAttributs ca;
    private String propPath;
    
    public ConstructionARFF(String prop) throws IOException{
        this.propPath=prop;
        ca = new CalculAttributs(propPath);
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("ressources//POS.txt")));
        String line;
        while ((line=r.readLine())!=null) POS.add(line);
        r.close();
    }
    
    public Instances ConstructionInstances(Instances data) throws Exception{
        String tweet, tweetLem;
        Instances newData = new Instances(data);
        Properties prop = new Properties();
	InputStream input = new FileInputStream(this.propPath);
        prop.load(input);
        if (prop.getProperty("SyntacticFeatures.countElongatedWords").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_numberElongatedWords"), newData.numAttributes());
        if (prop.getProperty("SyntacticFeatures.presencePunctuation").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_punctuation"), newData.numAttributes());
        if (prop.getProperty("SyntacticFeatures.countCapitalizations").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_allCaps"), newData.numAttributes());
        if (prop.getProperty("SyntacticFeatures.presenceSmileys").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_emoticonePos"), newData.numAttributes());
        if (prop.getProperty("SyntacticFeatures.presenceSmileys").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_emoticoneNeg"), newData.numAttributes());
        if (prop.getProperty("SyntacticFeatures.countHashtags").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_hashtags"), newData.numAttributes());
        if (prop.getProperty("SyntacticFeatures.countNegators").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countNegators"), newData.numAttributes());
        if (prop.getProperty("SyntacticFeatures.presencePartOfSpeechTags").equalsIgnoreCase("yes")) for (int j=0; j<POS.size(); j++) newData.insertAttributeAt(new Attribute("__"+POS.get(j)), newData.numAttributes());
        // Lexicons
        if (prop.getProperty("Lexicons.feelPol").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countPosFEEL"), newData.numAttributes());
        if (prop.getProperty("Lexicons.feelPol").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countNegFEEL"), newData.numAttributes());
        if (prop.getProperty("Lexicons.polarimotsPol").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countPosPolarimots"), newData.numAttributes());
        if (prop.getProperty("Lexicons.polarimotsPol").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countNegPolarimots"), newData.numAttributes());
        if (prop.getProperty("Lexicons.polarimotsPol").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countNeuPolarimots"), newData.numAttributes());
        if (prop.getProperty("Lexicons.affectsPol").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countPosAffects"), newData.numAttributes());
        if (prop.getProperty("Lexicons.affectsPol").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countNegAffects"), newData.numAttributes());
        if (prop.getProperty("Lexicons.affectsPol").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countNeuAffects"), newData.numAttributes());
        if (prop.getProperty("Lexicons.dikoPol").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countPosDiko"), newData.numAttributes());
        if (prop.getProperty("Lexicons.dikoPol").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countNegDiko"), newData.numAttributes());
        if (prop.getProperty("Lexicons.dikoPol").equalsIgnoreCase("yes")) newData.insertAttributeAt(new Attribute("_countNeuDiko"), newData.numAttributes());
        if (prop.getProperty("Lexicons.feelEmo").equalsIgnoreCase("yes")) for (int i=0; i<nbClassesFEEL; i++) newData.insertAttributeAt(new Attribute("_countEmotionFEEL"+(i+1)), newData.numAttributes());
        if (prop.getProperty("Lexicons.affectsEmo").equalsIgnoreCase("yes")) for (int i=0; i<nbClassesAffects; i++) newData.insertAttributeAt(new Attribute("_countEmotionAffects"+(i+1)), newData.numAttributes());
        if (prop.getProperty("Lexicons.dikoEmo").equalsIgnoreCase("yes")) for (int i=0; i<nbClassesDiko; i++) newData.insertAttributeAt(new Attribute("_countEmotionDiko"+(i+1)), newData.numAttributes());
        // Data
        for (int i=0; i<newData.numInstances(); i++){
            tweet = newData.instance(i).stringValue(data.attribute("_text"));
            // Preprocessings1
            if (prop.getProperty("Preprocessings.normalizeHyperlinks").equalsIgnoreCase("yes")) tweet = Pretraitements.ReplaceLink(tweet);
            if (prop.getProperty("Preprocessings.normalizeEmails").equalsIgnoreCase("yes")) tweet = Pretraitements.ReplaceMail(tweet);
            if (prop.getProperty("Preprocessings.replacePseudonyms").equalsIgnoreCase("yes")) tweet = Pretraitements.ReplaceUserTag(tweet);
            if (prop.getProperty("Preprocessings.normalizeSlang").equalsIgnoreCase("yes")) tweet = Pretraitements.ReplaceArgots(tweet);
            // Syntactic features
            if (prop.getProperty("SyntacticFeatures.countElongatedWords").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_numberElongatedWords"), ca.ElongatedWords(tweet));
            if (prop.getProperty("SyntacticFeatures.presencePunctuation").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_punctuation"), (tweet.contains("!") || tweet.contains("?"))? 1 : 0);
            if (prop.getProperty("SyntacticFeatures.countCapitalizations").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_allCaps"), ca.AllCaps(tweet));
            if (prop.getProperty("SyntacticFeatures.presenceSmileys").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_emoticonePos"), ((ca.EmoticonesPos(tweet))? 1 : 0));
            if (prop.getProperty("SyntacticFeatures.presenceSmileys").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_emoticoneNeg"), ((ca.EmoticonesNeg(tweet))? 1 : 0));
            if (prop.getProperty("SyntacticFeatures.countHashtags").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_hashtags"), ca.CountHashtag(tweet));
            if (prop.getProperty("SyntacticFeatures.countNegators").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countNegators"), (ca.CountNegation(tweet)));
            if (prop.getProperty("SyntacticFeatures.presencePartOfSpeechTags").equalsIgnoreCase("yes")) for (int j=0; j<POS.size(); j++) newData.instance(i).setValue(newData.attribute("__"+POS.get(j)), ca.POS(tweet,POS.get(j)));
            // Preprocessings2
            if (prop.getProperty("Preprocessings.lowercase").equalsIgnoreCase("yes")) tweet = tweet.toLowerCase();
            if (prop.getProperty("Preprocessings.lemmatize").equalsIgnoreCase("yes")) tweet = ca.Lemmatiser(tweet);
            newData.instance(i).setValue(newData.attribute("_text"), tweet);
            // Lexicons
            tweetLem = ca.Lemmatiser(tweet);
            tweetLem = tweetLem.toLowerCase();
            if (prop.getProperty("Lexicons.feelPol").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countPosFEEL"), ca.ComputePosFEEL(tweetLem));
            if (prop.getProperty("Lexicons.feelPol").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countNegFEEL"), ca.ComputeNegFEEL(tweetLem));
            if (prop.getProperty("Lexicons.polarimotsPol").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countPosPolarimots"), ca.ComputePosPolarimots(tweetLem));
            if (prop.getProperty("Lexicons.polarimotsPol").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countNegPolarimots"), ca.ComputeNegPolarimots(tweetLem));
            if (prop.getProperty("Lexicons.polarimotsPol").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countNeuPolarimots"), ca.ComputeNeuPolarimots(tweetLem));
            if (prop.getProperty("Lexicons.affectsPol").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countPosAffects"), ca.ComputePosAffects(tweetLem));
            if (prop.getProperty("Lexicons.affectsPol").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countNegAffects"), ca.ComputeNegAffects(tweetLem));
            if (prop.getProperty("Lexicons.affectsPol").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countNeuAffects"), ca.ComputeNeuAffects(tweetLem));
            if (prop.getProperty("Lexicons.dikoPol").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countPosDiko"), ca.ComputePosDiko(tweetLem));
            if (prop.getProperty("Lexicons.dikoPol").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countNegDiko"), ca.ComputeNegDiko(tweetLem));
            if (prop.getProperty("Lexicons.dikoPol").equalsIgnoreCase("yes")) newData.instance(i).setValue(newData.attribute("_countNeuDiko"), ca.ComputeNeuDiko(tweetLem));
            if (prop.getProperty("Lexicons.feelEmo").equalsIgnoreCase("yes")) for (int j=0; j<nbClassesFEEL; j++) newData.instance(i).setValue(newData.attribute("_countEmotionFEEL"+(j+1)), ca.ComputeEmotionFEEL(tweetLem, j));
            if (prop.getProperty("Lexicons.affectsEmo").equalsIgnoreCase("yes")) for (int j=0; j<nbClassesAffects; j++) newData.instance(i).setValue(newData.attribute("_countEmotionAffects"+(j+1)), ca.ComputeEmotionAffects(tweetLem, j));
            if (prop.getProperty("Lexicons.dikoEmo").equalsIgnoreCase("yes")) for (int j=0; j<nbClassesDiko; j++) newData.instance(i).setValue(newData.attribute("_countEmotionDiko"+(j+1)), ca.ComputeEmotionDiko(tweetLem, j));
        }        
        return newData;
    }
    
}