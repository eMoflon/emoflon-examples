package org.moflon.moca.dictionary.unparser;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileReader;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import org.moflon.core.moca.processing.unparser.impl.TemplateUnparserImpl;

public class DictionaryUnparserAdapter extends TemplateUnparserImpl 
{
  
  @Override
  public boolean canUnparseFile(String fileName) 
  {
    return fileName.endsWith(".dictionary");
  }

  @Override
  protected String callMainRule(CommonTreeNodeStream tree, StringTemplateGroup templates) throws RecognitionException 
  {
    DictionaryTreeGrammar dictionaryTreeGrammar = new DictionaryTreeGrammar(tree);
    dictionaryTreeGrammar.setTemplateLib(templates);  
    StringTemplate st = dictionaryTreeGrammar.main().st;
    if (st==null) {
      return "";
    }
    else {
      return st.toString();
    }  
  }

  @Override
  protected StringTemplateGroup getStringTemplateGroup() throws FileNotFoundException 
  {
    //TODO provide StringTemplateGroup ...
    // ... from folder "dictionary" containing .st files
    // return new StringTemplateGroup("dictionary", "templates/dictionary");
    // ... from group file Dictionary.stg
    return new StringTemplateGroup(new FileReader(new File("./templates/Dictionary.stg")));
  }

  @Override
  protected String[] getTokenNames() 
  {
    return DictionaryTreeGrammar.tokenNames;
  }
}