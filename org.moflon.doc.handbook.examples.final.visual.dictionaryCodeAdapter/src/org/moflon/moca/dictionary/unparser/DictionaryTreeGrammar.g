tree grammar DictionaryTreeGrammar;

options {
  ASTLabelType = CommonTree; 
  output       = template;
}

// Tokens used internally by Moca
// ID: ('a'..'z' | 'A'..'Z')+;
// STRING: ( ID | ('0'..'9') )+; 
// ATRIBUTE: Used as an imaginary token for coding attributes in XML files (ATTRIBUTE name=ID value=STRING)
tokens {
  ID;
  STRING;
  ATTRIBUTE; 
} 
  
@members {
	public Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow)	throws RecognitionException  {
		try {
        		return super.recoverFromMismatchedToken(input, ttype, follow);
            } catch(java.util.NoSuchElementException e){
                throw new IllegalArgumentException("Your tree does not comply with your tree grammar!\n"
                		+ " Problems encountered at: [" + "..." + getTreeNodeStream().LT(-1) + " " 
                		+ getTreeNodeStream().LT(1) + "..." + "] in tree.");
		}
    }
}  
  
@header {
package org.moflon.moca.dictionary.unparser;
}
// tree grammar rules:
main: ^('DICTIONARY' name=STRING author=STRING? entries+=entry+)
-> dictionary(name={$name}, author={$author}, entires={$entries});

entry: ^('ENTRY' entryLabel=STRING level=STRING)
-> entry(entry={$entryLabel}, level={$level});
