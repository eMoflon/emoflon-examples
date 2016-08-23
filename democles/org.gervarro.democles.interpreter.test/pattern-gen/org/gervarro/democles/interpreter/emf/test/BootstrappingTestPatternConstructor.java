/*
 * Democles, Declarative Model Query Framework for Monitoring Heterogeneous Embedded Systems
 * Copyright (C) 2010  Gergely Varro
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Contributors:
 * 		Gergely Varro <gervarro@cs.bme.hu> - initial API and implementation and/or initial documentation
 */
package org.gervarro.democles.interpreter.emf.test;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.gervarro.democles.constraint.emf.EMFConstraintModule;
import org.gervarro.democles.specification.emf.Constraint;
import org.gervarro.democles.specification.emf.ConstraintParameter;
import org.gervarro.democles.specification.emf.EMFDemoclesPatternMetamodelPlugin;
import org.gervarro.democles.specification.emf.Pattern;
import org.gervarro.democles.specification.emf.PatternBody;
import org.gervarro.democles.specification.emf.Variable;
import org.gervarro.democles.specification.emf.SpecificationFactory;
import org.gervarro.democles.specification.emf.SpecificationPackage;
import org.gervarro.democles.specification.emf.constraint.emf.emf.EMFTypeFactory;
import org.gervarro.democles.specification.emf.constraint.emf.emf.EMFTypePackage;
import org.gervarro.democles.specification.emf.constraint.emf.emf.EMFVariable;
import org.gervarro.democles.specification.emf.constraint.emf.emf.Reference;

public class BootstrappingTestPatternConstructor {

	private static void createSamplePattern(Resource resource, EMFConstraintModule emfBuilder) {
		EMFTypePackage emfTypeModulePackage = EMFTypePackage.eINSTANCE;
		EMFTypeFactory emfTypeModuleFactory = emfTypeModulePackage.getEMFTypeFactory();
		// Real specification
		SpecificationPackage specificationPackage = SpecificationPackage.eINSTANCE;
		SpecificationFactory specificationFactory = new SpecificationFactory();
		
		ConstraintParameter parameter;
		Pattern pattern = specificationFactory.createPattern();
		resource.getContents().add(pattern);
		pattern.setName("pattern");
		
		List<Variable> list = pattern.getSymbolicParameters();
		EMFVariable p = emfTypeModuleFactory.createEMFVariable();
		list.add(p);
		p.setName("P");
		p.setEClassifier(specificationPackage.getPattern());
		EMFVariable pb = emfTypeModuleFactory.createEMFVariable();
		list.add(pb);
		pb.setName("PB");
		pb.setEClassifier(specificationPackage.getPatternBody());
		EMFVariable c = emfTypeModuleFactory.createEMFVariable();
		list.add(c);
		c.setName("C");
		c.setEClassifier(specificationPackage.getConstraint());
		EMFVariable v = emfTypeModuleFactory.createEMFVariable();
		list.add(v);
		v.setName("V");
		v.setEClassifier(specificationPackage.getVariable());
		
		PatternBody body = specificationFactory.createPatternBody();
		pattern.getBodies().add(body);
		List<Constraint> constraints = body.getConstraints();
		Reference c1 = emfTypeModuleFactory.createReference();
		parameter = specificationFactory.createConstraintParameter();
		c1.getParameters().add(parameter);
		parameter.setReference(c);
		parameter = specificationFactory.createConstraintParameter();
		c1.getParameters().add(parameter);
		parameter.setReference(v);
		c1.setEModelElement(specificationPackage.getConstraint_Parameters());
		constraints.add(c1);
		
		Reference c2 = emfTypeModuleFactory.createReference();
		parameter = specificationFactory.createConstraintParameter();
		c2.getParameters().add(parameter);
		parameter.setReference(pb);
		parameter = specificationFactory.createConstraintParameter();
		c2.getParameters().add(parameter);
		parameter.setReference(c);
		c2.setEModelElement(specificationPackage.getPatternBody_Constraints());
		constraints.add(c2);
		
		Reference c3 = emfTypeModuleFactory.createReference();
		parameter = specificationFactory.createConstraintParameter();
		c3.getParameters().add(parameter);
		parameter.setReference(pb);
		parameter = specificationFactory.createConstraintParameter();
		c3.getParameters().add(parameter);
		parameter.setReference(v);
		c3.setEModelElement(specificationPackage.getPatternBody_LocalVariables());
		constraints.add(c3);
		
		Reference c4 = emfTypeModuleFactory.createReference();
		parameter = specificationFactory.createConstraintParameter();
		c4.getParameters().add(parameter);
		parameter.setReference(pb);
		parameter = specificationFactory.createConstraintParameter();
		c4.getParameters().add(parameter);
		parameter.setReference(pb);
		c4.setEModelElement(specificationPackage.getPattern_Bodies());
		constraints.add(c4);
	}
	
	private static void createMeaningfulPattern(Resource resource, EMFConstraintModule emfBuilder) {
		EMFTypePackage emfTypeModulePackage = EMFTypePackage.eINSTANCE;
		EMFTypeFactory emfTypeModuleFactory = emfTypeModulePackage.getEMFTypeFactory();
		// Real specification
		SpecificationPackage specificationPackage = SpecificationPackage.eINSTANCE;
		SpecificationFactory specificationFactory = specificationPackage.getSpecificationFactory();

		ConstraintParameter parameter;
		Pattern pattern = specificationFactory.createPattern();
		resource.getContents().add(pattern);
		pattern.setName("pattern");
		
		List<Variable> list = pattern.getSymbolicParameters();
		EMFVariable p = emfTypeModuleFactory.createEMFVariable();
		list.add(p);
		p.setName("P");
		p.setEClassifier(specificationPackage.getPattern());
		EMFVariable pb = emfTypeModuleFactory.createEMFVariable();
		list.add(pb);
		pb.setName("PB");
		pb.setEClassifier(specificationPackage.getPatternBody());
		EMFVariable c = emfTypeModuleFactory.createEMFVariable();
		list.add(c);
		c.setName("C");
		c.setEClassifier(specificationPackage.getConstraint());
		EMFVariable v = emfTypeModuleFactory.createEMFVariable();
		list.add(v);
		v.setName("V");
		v.setEClassifier(specificationPackage.getVariable());
		
		PatternBody body = specificationFactory.createPatternBody();
		pattern.getBodies().add(body);
		EMFVariable cp = emfTypeModuleFactory.createEMFVariable();
		body.getLocalVariables().add(cp);
		cp.setName("CP");
		cp.setEClassifier(specificationPackage.getConstraintParameter());
		List<Constraint> constraints = body.getConstraints();
		Reference c1 = emfTypeModuleFactory.createReference();
		constraints.add(c1);
		parameter = specificationFactory.createConstraintParameter();
		c1.getParameters().add(parameter);
		parameter.setReference(c);
		parameter = specificationFactory.createConstraintParameter();
		c1.getParameters().add(parameter);
		parameter.setReference(cp);
		c1.setEModelElement(specificationPackage.getConstraint_Parameters());
		
		Reference c2 = emfTypeModuleFactory.createReference();
		constraints.add(c2);
		parameter = specificationFactory.createConstraintParameter();
		c2.getParameters().add(parameter);
		parameter.setReference(pb);
		parameter = specificationFactory.createConstraintParameter();
		c2.getParameters().add(parameter);
		parameter.setReference(c);
		c2.setEModelElement(specificationPackage.getPatternBody_Constraints());
		
		Reference c3 = emfTypeModuleFactory.createReference();
		constraints.add(c3);
		parameter = specificationFactory.createConstraintParameter();
		c3.getParameters().add(parameter);
		parameter.setReference(p);
		parameter = specificationFactory.createConstraintParameter();
		c3.getParameters().add(parameter);
		parameter.setReference(v);
		c3.setEModelElement(specificationPackage.getPattern_SymbolicParameters());
		
		Reference c4 = emfTypeModuleFactory.createReference();
		constraints.add(c4);
		parameter = specificationFactory.createConstraintParameter();
		c4.getParameters().add(parameter);
		parameter.setReference(p);
		parameter = specificationFactory.createConstraintParameter();
		c4.getParameters().add(parameter);
		parameter.setReference(pb);
		c4.setEModelElement(specificationPackage.getPattern_Bodies());
		
		Reference c5 = emfTypeModuleFactory.createReference();
		constraints.add(c5);
		parameter = specificationFactory.createConstraintParameter();
		c5.getParameters().add(parameter);
		parameter.setReference(cp);
		parameter = specificationFactory.createConstraintParameter();
		c5.getParameters().add(parameter);
		parameter.setReference(v);
		c5.setEModelElement(specificationPackage.getConstraintParameter_Reference());
	}
	
	private static void createPattern(final ResourceSet set) throws Exception {
		EMFConstraintModule emfBuilder = new EMFConstraintModule(set);
		URI base = URI.createPlatformResourceURI("/org.gervarro.democles.standalone/model/", true);
		URI uri = URI.createFileURI("meaningful.pattern");
		set.getURIConverter().getURIMap().put(uri, uri.resolve(base));
		Resource res = set.createResource(uri);
		createMeaningfulPattern(res, emfBuilder);
		res.save(null);
		URI uri2 = URI.createFileURI("sample.pattern");
		set.getURIConverter().getURIMap().put(uri2, uri2.resolve(base));
		Resource res2 = set.createResource(uri2);
		createSamplePattern(res2, emfBuilder);
		res2.save(null);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ResourceSet set = EMFDemoclesPatternMetamodelPlugin.createDefaultResourceSet();
		EMFDemoclesPatternMetamodelPlugin.configureDemoclesDefaults(set);
		EMFDemoclesPatternMetamodelPlugin.setWorkspaceRootDirectory(set, args[0]);
		createPattern(set);
	}
}
