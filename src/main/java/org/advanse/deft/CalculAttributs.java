
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.annolab.tt4j.TreeTaggerException;


/**
 *
 * @author amin.abdaoui
 */
public class CalculAttributs {
    
    ArrayList<String> alPosFEEL = new ArrayList<>();
    ArrayList<String> alNegFEEL = new ArrayList<>();
    ArrayList<String> alPosPolarimots = new ArrayList<>();
    ArrayList<String> alNegPolarimots = new ArrayList<>();
    ArrayList<String> alNeuPolarimots = new ArrayList<>();
    ArrayList<String> alPosAffects = new ArrayList<>();
    ArrayList<String> alNegAffects = new ArrayList<>();
    ArrayList<String> alNeuAffects = new ArrayList<>();
    ArrayList<String> alPosDiko = new ArrayList<>();
    ArrayList<String> alNegDiko = new ArrayList<>();
    ArrayList<String> alNeuDiko = new ArrayList<>();
    ArrayList<String> al1 = new ArrayList<>();
    ArrayList<String> al2 = new ArrayList<>();
    ArrayList<String> al3 = new ArrayList<>();
    ArrayList<String> al4 = new ArrayList<>();
    ArrayList<String> al5 = new ArrayList<>();
    ArrayList<String> al6 = new ArrayList<>();
    ArrayList<String> al7 = new ArrayList<>();
    ArrayList<ArrayList<String>> al = new ArrayList<>();
    private final ArrayList<String> Neg = new ArrayList<>();
    private LemmatiseurHandler lm;
    
    
    public CalculAttributs() throws FileNotFoundException, IOException{
        String line;
        // FEEL
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("ressources//FEEL.txt")));
        al.add(al1);al.add(al2);al.add(al3);al.add(al4);al.add(al5);al.add(al6);al.add(al7);
        while ((line=r.readLine())!=null){
            // Polarité
            if (line.split(";")[2].equals("positive")) alPosFEEL.add(line.split(";")[1].toLowerCase());
            else if (line.split(";")[2].equals("negative")) alNegFEEL.add(line.split(";")[1].toLowerCase());
            // Emotion
            for (int i=0; i<7; i++) if (line.split(";")[i+3].equals("1")) al.get(i).add(line.split(";")[1].toLowerCase());            
        }
        r.close();
        // Polarimots
        /*r = new BufferedReader(new InputStreamReader(new FileInputStream("ressources//Polarimots.txt")));
        while ((line=r.readLine())!=null){
            if (line.split(";")[3].equals("positive")) alPosPolarimots.add(line.split(";")[1].toLowerCase());
            else if (line.split(";")[3].equals("negative")) alNegPolarimots.add(line.split(";")[1].toLowerCase());
            else if (line.split(";")[3].equals("neutre")) alNeuPolarimots.add(line.split(";")[1].toLowerCase());
        }
        r.close();*/
        // Affects
        r = new BufferedReader(new InputStreamReader(new FileInputStream("ressources//Augustin-pol.txt")));
        while ((line=r.readLine())!=null){
            if (line.split(";")[1].equals("positive")) alPosAffects.add(line.split(";")[0].toLowerCase());
            else if (line.split(";")[1].equals("negative")) alNegAffects.add(line.split(";")[0].toLowerCase());            
            else if (line.split(";")[1].equals("neutre")) alNeuAffects.add(line.split(";")[0].toLowerCase());
        }
        r.close();
        // Diko
        r = new BufferedReader(new InputStreamReader(new FileInputStream("ressources//Diko.txt")));
        while ((line=r.readLine())!=null){
            if (line.split(";")[2].equals("positive")) alPosDiko.add(line.split(";")[1].toLowerCase());
            else if (line.split(";")[2].equals("negative")) alNegDiko.add(line.split(";")[1].toLowerCase());
            else if (line.split(";")[2].equals("neutre")) alNeuDiko.add(line.split(";")[1].toLowerCase());
        }
        r.close();
        // Negateurs
        r = new BufferedReader(new InputStreamReader(new FileInputStream("ressources//Negations.txt")));
        while ((line=r.readLine())!=null) Neg.add(line);
        r.close();
        // tt4j
        String ch="/data/TreeTagger"; // sur le serveur: ch="/data/TreeTagger"
        lm = new LemmatiseurHandler(ch);
    }

    public int AllCaps(String tweet){
        int count=0;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()) if (isCap(st.nextToken())) count++;
        return count;
    }
    
    public boolean isCap(String word){
        boolean r = true;
        for (int i=0; i<word.length(); i++) if (Character.isUpperCase(word.charAt(i))==false) r=false;
        return r;
    }
    
    public int POS(String tweet, String pos) throws IOException, TreeTaggerException{
        int count=0;
        lm.clear();
        lm.setTermes(tweet);
        lm.process();
        for (String t:lm.getListPOS()){
            //t=t.split(":")[0];
            if (t.equals(pos)) count++;
        }
        return count;
    }
    
    public int ComputeEmotion(String tweet, int i) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (al.get(i).contains(lemme)) count++;
        }
        return count;
    }
    
    public int ComputePosFEEL(String tweet) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (alPosFEEL.contains(lemme)) count++;
        }
        return count;
    }
    
    public int ComputeNegFEEL(String tweet) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (alNegFEEL.contains(lemme)) count++;
        }
        return count;
    }
    
    public int ComputePosPolarimots(String tweet) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (alPosPolarimots.contains(lemme)) count++;
        }
        return count;
    }
    
    public int ComputeNegPolarimots(String tweet) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (alNegPolarimots.contains(lemme)) count++;
        }
        return count;
    } 
    
    public int ComputeNeuPolarimots(String tweet) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (alNeuPolarimots.contains(lemme)) count++;
        }
        return count;
    }
    
    public int ComputePosAffects(String tweet) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (alPosAffects.contains(lemme)) count++;
        }
        return count;
    }
    
    public int ComputeNegAffects(String tweet) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (alNegAffects.contains(lemme)) count++;
        }
        return count;
    } 
    
    public int ComputeNeuAffects(String tweet) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (alNeuAffects.contains(lemme)) count++;
        }
        return count;
    }
    
    public int ComputePosDiko(String tweet) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (alPosDiko.contains(lemme)) count++;
        }
        return count;
    }
    
    public int ComputeNegDiko(String tweet) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (alNegDiko.contains(lemme)) count++;
        }
        return count;
    } 
    
    public int ComputeNeuDiko(String tweet) throws FileNotFoundException, IOException, TreeTaggerException{
        int count=0;
        String lemme;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%1234567890", false);
        while (st.hasMoreElements()){
            lemme=st.nextToken();
            if(lemme.contains("|")) lemme=lemme.split("|")[0];
            if (alNeuDiko.contains(lemme)) count++;
        }
        return count;
    }
    
    public boolean LastTokenEmoticone(String tweet) throws IOException{
        Charset charset = Charset.forName("Windows-1252");
        String tok [] = tweet.split("\t ");
        String lasttoken = tok[tok.length-1] ;
        if (lasttoken.equals("lienHTTP")) lasttoken = tok[tok.length-2];
        InputStream ips = new FileInputStream("ressources//emoticone.txt");
        InputStreamReader ipsr = new InputStreamReader(ips,charset);
        BufferedReader br = new BufferedReader(ipsr);
        String ligne;
        while ((ligne=br.readLine())!=null){
                String [] tmp = ligne.split("\t");
                if(lasttoken.toUpperCase().indexOf(tmp[0].toUpperCase()) != -1){
                        br.close();
                        return true ;
                }
        }
        br.close();

        return false;
    }
    
    
    public boolean EmoticonesPos(String tweet) throws IOException{
        Charset charset = Charset.forName("Windows-1252");
        StringTokenizer st = new StringTokenizer(tweet, "\t ");
        while (st.hasMoreElements()) {
                String token = (String) st.nextElement() ;
                InputStream ips=new FileInputStream("ressources//emoticone.txt");
                InputStreamReader ipsr=new InputStreamReader(ips,charset);
                BufferedReader br=new BufferedReader(ipsr);
                String ligne;	
                while ((ligne=br.readLine())!=null){
                        String [] tmp = ligne.split("\t");
                        if(token.indexOf(tmp[0]) != -1 && Double.parseDouble(tmp[1])>0){
                                br.close();
                                return true ;
                        }
                }
                br.close();
        }
        return false;
    }
    
    public boolean EmoticonesNeg(String tweet) throws IOException{
        Charset charset = Charset.forName("Windows-1252");
        StringTokenizer st = new StringTokenizer(tweet, "\t ");
        while (st.hasMoreElements()) {
                String token = (String) st.nextElement() ;
                InputStream ips=new FileInputStream("ressources//emoticone.txt");
                InputStreamReader ipsr=new InputStreamReader(ips,charset);
                BufferedReader br=new BufferedReader(ipsr);
                String ligne;	
                while ((ligne=br.readLine())!=null){
                        String [] tmp = ligne.split("\t");
                        if(token.indexOf(tmp[0]) != -1 && Double.parseDouble(tmp[1])<0){
                                br.close();
                                return true ;
                        }
                }
                br.close();
        }
        return false;
    }
    
    public int ContiguousSequences(String tweet){
        int cpt = 0 ;
        int sqc = 0 ;
        int ltw = tweet.length() ;
        char tmp =' ';
        for(int i=0; i<ltw; i++){
                if( ((tweet.charAt(i) == '?') || (tweet.charAt(i) == '!')) && (cpt == 1) ){
                        sqc++ ;
                        cpt++ ;
                }else if( ((tweet.charAt(i) == '?') || (tweet.charAt(i) == '!')) && ((tmp!='?') && (tmp!='!')) ){
                        cpt = 1 ;
                }else{
                        cpt++;
                }
                tmp = tweet.charAt(i);
        }

        return sqc ;
    }
    
    public boolean Punctuation(String tweet){
        return tweet.contains("?") || tweet.contains("!");
    }
    
    public boolean LastTokenPonctuation(String tweet){
        StringTokenizer st = new StringTokenizer(tweet, ".,|;:'\"()-\t ");
        String lastToken = "", sauv="";
        while (st.hasMoreElements()) {
                sauv=lastToken;
                lastToken = (String) st.nextElement();
        }
        if (lastToken.equals("lienHTTP")) lastToken=sauv;
        for(int i=0 ; i<lastToken.length() ; i++){
                if((lastToken.charAt(i) == '?') || (lastToken.charAt(i) == '!')){
                        return true ;
                }
        }
        return false; 
    }
    
    public int CountHashtag (String tweet){
        int nbh = 0 ;
        for(int i=0 ; i<tweet.length(); i++){
                if(tweet.charAt(i) == '#')
                        nbh++ ;
        }
        return nbh;
    }

    public int ElongatedWords(String tweet){
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:'\"|()?!-1234567890");
        String lastToken = null ;
        int cpt = 0, elw = 0 ; 
        char tmp = ' ';
        while (st.hasMoreElements()) {
            lastToken = (String) st.nextElement() ;
            int ltw = lastToken.length();
            tmp = ' ';
            cpt = 0 ;
            for(int i=0; i<ltw; i++){
                if( lastToken.charAt(i) == tmp ){
                        cpt++ ;
                }else 
                        cpt = 0 ;

                if( cpt >= 2 ){
                        elw++ ;
                        break ;
                }
                tmp = lastToken.charAt(i);
            }
        }
        return elw ;
    }
        
    
    public boolean Negation(String tweet){
        boolean res=false;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:\'\"|()?!-1234567890", false);
        while (st.hasMoreElements()) if (Neg.contains(st.nextElement())) res=true;
        return res;
    }
    
    public int CountNegation(String tweet){
        int cpt=0;
        StringTokenizer st = new StringTokenizer(tweet, " 	.,;:\'\"|()?!-1234567890", false);
        while (st.hasMoreElements()) if (Neg.contains(st.nextElement())) cpt++;
        return cpt;
    }
    
    public int nbApp(String line, char c){
        int count=0;
        for (int i=0; i<line.length(); i++) if (line.charAt(i)==c) count++;
        return count;
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
        
}