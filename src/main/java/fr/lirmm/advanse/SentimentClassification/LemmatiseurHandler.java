package fr.lirmm.advanse.SentimentClassification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;

/**
 *
 * @author Nargisse
 */
public class LemmatiseurHandler{
   
    private TreeTaggerWrapper<String> tt;   
    private ArrayList<String> termes;
    private ArrayList<String> termesLem;
    private ArrayList<String> termesPos;
    
    public LemmatiseurHandler(String ch){
        termes = new ArrayList();
        termesLem = new ArrayList();
        termesPos = new ArrayList();
        tt = new TreeTaggerWrapper<String>();
        // Lemmatiseur
        System.setProperty("treetagger.home", ch);
        try {
                tt.setModel("french.par:UTF8");
                tt.setHandler(new TokenHandler<String>() {

                    @Override
                    public void token(String token, String pos, String lemma) {
                        //System.out.println(token + "\t" + pos + "\t" + lemma);
                        termesLem.add(lemma);
                        termesPos.add(pos);
                    }
            });
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        
    }
    
    public void process() throws IOException, TreeTaggerException {
        // Lemmatisation
        tt.process(termes);
    }
    
    public void clear(){
        termes.clear();
        termesLem.clear();
        termesPos.clear();
    }
    
     public void destroy() throws IOException, TreeTaggerException {
                // Lemmatisation
                tt.destroy();
    }
    
    public ArrayList<String> getListTermeLem(){
        return this.termesLem;
    }
    
     public ArrayList<String> getListPOS(){
        return this.termesPos;
    }
    
    public void setTermes(String s){
        StringTokenizer st = new StringTokenizer(s, " \r \t\n.,;:\'\"\\()?!-1234567890");
        while (st.hasMoreTokens()) termes.add(st.nextToken());
    }
        
}             