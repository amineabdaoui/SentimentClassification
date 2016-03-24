package org.advanse.sentimentclassification;

/**
 *
 * @author amin.abdaoui
 */
public class Document {
    
    private String document;
    private String classe;

    public Document(String d, String c) {
        document=d;
        classe=c;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getClasse() {
        return classe;
    }

    public String getDocument() {
        return document;
    }  
    
    
}
