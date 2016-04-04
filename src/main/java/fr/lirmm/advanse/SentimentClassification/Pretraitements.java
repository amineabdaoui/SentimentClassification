package fr.lirmm.advanse.SentimentClassification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author amin.abdaoui
 */
public class Pretraitements {
  
    private static String tweet = "non je n'aime pas voilà mdr";
    private static final int nbSlangTermes=311;
    
    public static void main(String args[]) throws IOException {
        System.out.println(ReplaceArgots(tweet));
    }
    
    private static final String[] sep = {" ", "\\?", "!", ",", ";", ":", "\\.", "\\(","\\)","\\{","\\}","\\+","=","'","\"","0","1","2","3","4","5","6","7","8","9"};
    
    /**
     * Remplace les mots d'argots par le texte correspondant
     * 
     * @param tweet
     * @param path
     * @return
     * @throws IOException 
     */
    public static String ReplaceArgots(String tweet) throws IOException{
        String res;
        //Etape1 : Lire les argots
        String Argot[][] = new String[nbSlangTermes][2];
        BufferedReader r = null;
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        r = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream("argot.txt")));

        String line;
        int i=0;
        while ((line=r.readLine())!=null) {
            Argot[i][0]=line.substring(0, line.indexOf(':')-1);
            Argot[i][1]=line.substring(line.indexOf(':')+1);
            i++;
        }
        //Remplacer les argots
        for (int j=0; j<sep.length; j++) tweet=tweet.replaceAll(sep[j], " ");
        res=ReplaceElongatedCharacters(tweet);
        for (i=0; i<Argot.length; i++){
            if (res.equals(Argot[i][0])) res=res.replaceAll(Argot[i][0], Argot[i][1]);
            if (res.contains(" "+Argot[i][0]+" ")) res=res.replaceAll(" "+Argot[i][0]+" ", " "+Argot[i][1]+" ");
            if (res.startsWith(Argot[i][0]+" ")) res=res.replaceAll(Argot[i][0]+" ", Argot[i][1]+" ");
            if (res.endsWith(" "+Argot[i][0])) res=res.replaceAll(" "+Argot[i][0], " "+Argot[i][1]);
        }
        return res;
    }
    
    /**
     * Remplace les séparateurs par un espace
     * 
     * @param tweet
     * @return 
     */
    public static String ReplaceSep(String tweet){
        String newTweet="";
        StringTokenizer st = new StringTokenizer(tweet, " \n         .,;:'‘’\"()?[]!-_\\/“<>$&®´…«»1234567890", false);
        while (st.hasMoreElements()) newTweet+=st.nextToken()+" ";
        return newTweet;
    }
    
    /**
     * Remplace les caractères allongés (si se répéte plus de 2 fois)
     * 
     * @param tweet
     * @return 
     */
    public static String ReplaceElongatedCharacters(String tweet){
        String result="";
        char c1=' ';
        char c2=' ';
        tweet = tweet.toLowerCase();
        for (int i=0; i<tweet.length(); i++){
            if (tweet.charAt(i)!=c1 || tweet.charAt(i)!=c2) result+=tweet.charAt(i);
            c1=c2;
            c2=tweet.charAt(i);
        }
        return result;
    }
    
    public static String ReplaceMail(String tweet) throws IOException{
        return tweet.replaceAll("[a-z0-9_-]*@[a-z0-9_-]*\\.[a-z0-9_-]*", "mail");
    }
    
    public static String ReplaceLink(String tweet) throws IOException{
        return tweet.replaceAll("http://t.co/[a-zA-Z0-9_-]*", "lienHTTP").replaceAll("https://t.co/[a-zA-Z0-9_-]*", "lienHTTP");
    }
    
    public static String ReplaceUserTag(String tweet) throws IOException{
        return tweet.replaceAll("@[a-zA-Z0-9_-]*", "@tag");
    }
    
    public static String Negate(String tweet) throws FileNotFoundException, IOException{
        boolean Sepa=false, Nega=false;
        String s, tok;
        ArrayList<String> Neg = new ArrayList<>();
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        BufferedReader r = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream("Negations.txt")));
        
        while ((s=r.readLine())!=null) Neg.add(s);
        r.close();
        StringTokenizer st = new StringTokenizer(tweet, " \n         .,;:'‘’\"?[]!-_\\/“<>$&®´…«»1234567890", true);
        boolean start=false, end=false;
        s="";
        while (st.hasMoreElements()){
            Nega=false;
            Sepa=false;
            tok=st.nextElement().toString();
            if (s.length()>=1 && !s.endsWith(" ")) s+=" ";
            for (String n:Neg) if (tok.equals(n)){
                start=true;
                Nega=true;
            }
            if (tok.equals(".") || tok.equals(",") || tok.equals(";") || tok.equals("?") || tok.equals("!") || tok.equals(":") || tok.equals("(") || tok.equals(")")){
                end=true;
                Sepa=true;
            }
            for (String se:sep) if(se.equals(tok)) Sepa=true;
            if (!Sepa) {
                s+=tok;
                if (start && !end && !Nega) s+="_neg";
            }
        }
        return s;
    }
    
    
    public static String NegateNegator(String tweet) throws FileNotFoundException, IOException{
        boolean Sepa=false, Nega=false;
        String s, tok, pNeg="neg";
        ArrayList<String> Neg = new ArrayList<>();
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        BufferedReader r = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream("Negations.txt")));
        
        while ((s=r.readLine())!=null) Neg.add(s);
        r.close();
        StringTokenizer st = new StringTokenizer(tweet, " \n         .,;:'‘’\"()?[]!-_\\/“<>$&®´…«»1234567890", true);
        boolean start=false, end=false;
        s="";
        while (st.hasMoreElements()){
            Nega=false;
            Sepa=false;
            tok=st.nextElement().toString();
            if (s.length()>=1 && !s.endsWith(" ")) s+=" ";
            for (String n:Neg) if (tok.equals(n)){
                start=true;
                Nega=true;
                pNeg=n;
            }
            if (tok.equals(".") || tok.equals(",") || tok.equals(";") || tok.equals("?") || tok.equals("!") || tok.equals(":")){
                end=true;
                Sepa=true;
            }
            for (String se:sep) if(se.equals(tok)) Sepa=true;
            if (!Sepa) {
                s+=tok;
                if (start && !end && !Nega) s+="_"+pNeg;
            }
        }
        return s;
    }
    
    public static String NegateAdd(String tweet) throws FileNotFoundException, IOException{
        boolean Sepa=false, Nega=false;
        String s, sn, tok, pNeg="neg";
        ArrayList<String> Neg = new ArrayList<>();
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        BufferedReader r = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream("Negations.txt")));
        
        while ((s=r.readLine())!=null) Neg.add(s);
        r.close();
        StringTokenizer st = new StringTokenizer(tweet, " \n         .,;:'‘’\"()?[]!-_\\/“<>$&®´…«»1234567890", true);
        boolean start=false, end=false;
        s=""; sn="";
        while (st.hasMoreElements()){
            Nega=false;
            Sepa=false;
            tok=st.nextElement().toString();
            if (s.length()>=1 && !s.endsWith(" ")) s+=" ";
            for (String n:Neg) if (tok.equals(n)){
                start=true;
                Nega=true;
                pNeg=n;
            }
            if (tok.equals(".") || tok.equals(",") || tok.equals(";") || tok.equals("?") || tok.equals("!") || tok.equals(":")){
                end=true;
                Sepa=true;
            }
            for (String se:sep) if(se.equals(tok)) Sepa=true;
            if (!Sepa) {
                s+=tok;
                if (start && !end && !Nega) sn+=" "+tok+"_"+pNeg;
            }
        }
        return s+sn;
    }
    
}
