package csp.constraints;

import java.util.Arrays;
import java.util.List;

import org.moflon.tgg.language.csp.Variable;
import org.moflon.tgg.language.csp.impl.TGGConstraintImpl;

public class IndexToLevel extends TGGConstraintImpl {
	
	private List<String> levels = Arrays.asList(new String[] {"master",
			"advanced", "beginner" });
	
	public void solve(Variable var_0, Variable var_1){
    	
		
		int index = ((Number) var_0.getValue()).intValue();
		String level = (String) var_1.getValue();  

		String bindingStates = getBindingStates(var_0, var_1);
    	
    	switch(bindingStates){
    	case "BB":
    		 if (index < 0) {
    		       index = 0;
    		      } else if (index > 2) {
    		       index = 2;
    		      } 
    		      
    		      setSatisfied(levels.get(index).equals(level));
    		      break;
    		
    	case "BF":
    		if (index < 0) {
    			var_1.setValue(levels.get(0));
    		} else if (index > 2) {
    			var_1.setValue(levels.get(2));
    		} else {
    			var_1.setValue(levels.get(index));
    		}
    		
    		var_1.setBound(true);
    		setSatisfied(true);
    		break;
    		
    	case "FB":
    		index = levels.indexOf(level);
    		if (index == -1) {
    			setSatisfied(false);
    		} else {
    			var_0.setValue(index);
    			var_0.setBound(true);
    			setSatisfied(true);
    		}
    		break;
    	}
    	
  	}	  
}