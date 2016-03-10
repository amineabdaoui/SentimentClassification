package org.advanse.deft;

import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

/**
 *
 * @author amin.abdaoui
 */
public class SelectionAttributs {
    
    public static AttributeSelection InfoGainAttributeEval(Instances data) throws Exception{
        AttributeSelection filter = new AttributeSelection();
        InfoGainAttributeEval evaluator = new InfoGainAttributeEval();
        filter.setEvaluator(evaluator);
        Ranker search = new Ranker();
        search.setThreshold(0);
        filter.setSearch(search);
        filter.setInputFormat(data);
        
        return filter;
    }
    
    
    public static AttributeSelection InfoGainAttributeEval(Instances data, int n) throws Exception{
        AttributeSelection filter = new AttributeSelection();
        InfoGainAttributeEval evaluator = new InfoGainAttributeEval();
        filter.setEvaluator(evaluator);
        Ranker search = new Ranker();
        search.setNumToSelect(n);
        filter.setSearch(search);
        filter.setInputFormat(data);
        
        return filter;
    }
    
    public static AttributeSelection GainRatioAttributeEval(Instances data, int n) throws Exception{
        AttributeSelection filter = new AttributeSelection();
        GainRatioAttributeEval evaluator = new GainRatioAttributeEval();
        filter.setEvaluator(evaluator);
        Ranker search = new Ranker();
        search.setNumToSelect(n);
        filter.setSearch(search);
        filter.setInputFormat(data);
        
        return filter;
    }
}
