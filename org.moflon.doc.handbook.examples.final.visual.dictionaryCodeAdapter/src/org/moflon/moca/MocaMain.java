package org.moflon.moca;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.moflon.core.moca.processing.CodeAdapter;
import org.moflon.core.moca.processing.ProcessingFactory;
import org.moflon.core.utilities.eMoflonEMFUtil;
import org.moflon.moca.dictionary.parser.DictionaryParserAdapter;
import org.moflon.moca.dictionary.unparser.DictionaryUnparserAdapter;

import MocaTree.Folder;

public class MocaMain 
{

  private static CodeAdapter codeAdapter;

  public static void main(String[] args) 
  {
    BasicConfigurator.configure();

    // Perform text-to-tree
    Folder tree = getCodeAdapter().parse(new File("instances/in/"));

    // Save tree to file
    ResourceSet set = eMoflonEMFUtil.createDefaultResourceSet();
    Resource resource = set.createResource(eMoflonEMFUtil.createFileURI("instances/tree.xmi", false));
    resource.getContents().add(tree);
    try {
		resource.save(null);
	} catch (IOException e) {
		System.err.println("Error while saving model: " + e.getMessage());
	}

    // Perform tree-to-model
    //TODO
    
    // Save model to file
    //TODO
    
    // Perform model-to-tree
    //TODO
    
    // Perform tree-to-text (using initial tree)
    getCodeAdapter().unparse("instances/out", tree);    
  }
  
  public static CodeAdapter getCodeAdapter(){
    if(codeAdapter == null){
	    // Register parsers and unparsers
		codeAdapter = ProcessingFactory.eINSTANCE.createCodeAdapter();
        codeAdapter.getParser().add(new DictionaryParserAdapter());
        codeAdapter.getUnparser().add(new DictionaryUnparserAdapter());	  
     }
	  
	 return codeAdapter;
  }
}