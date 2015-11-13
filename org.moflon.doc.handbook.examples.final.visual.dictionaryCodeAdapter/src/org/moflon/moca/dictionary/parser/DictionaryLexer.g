lexer grammar DictionaryLexer;

@members {
  public Collection<Problem> problems = new ArrayList<Problem>();

      public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
          Problem problem = ProcessingFactory.eINSTANCE.createProblem();
          int line  = e.line;
          int charPos = e.charPositionInLine;
          int tokenLenght = 1;
          Token token = e.token;
          if(token != null) 
            tokenLenght = token.getText().length();
                  
          
          problem.setType(ProblemType.ERROR);
          problem.setLine(line); 
          problem.setCharacterPositionStart(charPos); 
          problem.setCharacterPositionEnd(charPos+tokenLenght);
          problem.setMessage("Lexer: " + getErrorMessage(e, tokenNames)); 
          
          problems.add(problem);
          super.displayRecognitionError(tokenNames, e);
    }
}

@header {
package org.moflon.moca.dictionary.parser;
import org.moflon.moca.MocaUtil;
import java.util.Collection;
import org.moflon.core.moca.processing.ProcessingFactory;
import org.moflon.core.moca.processing.Problem;
import org.moflon.core.moca.processing.ProblemType;
}

WS: (' ' | '\t' | '\n' | '\r')+ {skip(); };

TITLE: 'title:';

EMAIL: 'email:';

QUOTED_STRING: '"' .* '"'{ MocaUtil.trim(this, 1, 1); };

BEGINNER: 'beginner';

ADVANCED: 'advanced';

MASTER: 'master';

OPEN: '{';

CLOSE: '}';

SEPARATOR: ',';

DICTIONARY: 'DICTIONARY';

ENTRY: 'ENTRY';
