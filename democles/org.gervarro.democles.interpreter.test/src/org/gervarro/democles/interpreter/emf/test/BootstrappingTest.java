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

import java.io.IOException;
import java.util.LinkedList;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.gervarro.democles.common.DataFrame;
import org.gervarro.democles.common.runtime.ListOperationBuilder;
import org.gervarro.democles.common.runtime.SearchPlanOperation;
import org.gervarro.democles.common.runtime.SpecificationExtendedVariableRuntime;
import org.gervarro.democles.constraint.emf.EMFConstraintModule;
import org.gervarro.democles.interpreter.InterpreterPatternMatcherModule;
import org.gervarro.democles.interpreter.InterpreterSearchPlanAlgorithm;
import org.gervarro.democles.interpreter.OperationCachingPattern;
import org.gervarro.democles.operation.RelationalOperationBuilder;
import org.gervarro.democles.operation.emf.DefaultEMFBatchAdornmentStrategy;
import org.gervarro.democles.operation.emf.EMFNativeOperationBuilder;
import org.gervarro.democles.plan.WeightedOperation;
import org.gervarro.democles.plan.combiner.InterpreterCombiner;
import org.gervarro.democles.plan.common.CombinedSearchPlanOperationBuilder;
import org.gervarro.democles.plan.common.DefaultAlgorithm;
import org.gervarro.democles.plan.common.SearchPlanOperationBuilder;
import org.gervarro.democles.plan.emf.EMFSearchPlanOperationBuilder;
import org.gervarro.democles.plan.emf.EMFWeightedOperationBuilder;
import org.gervarro.democles.runtime.DepthFirstTraversalStrategy;
import org.gervarro.democles.runtime.GenericOperationBuilder;
import org.gervarro.democles.runtime.InterpretableAdornedOperation;
import org.gervarro.democles.runtime.InterpretedDataFrame;
import org.gervarro.democles.runtime.QueryableRemappingOperation;
import org.gervarro.democles.specification.emf.EMFDemoclesPatternMetamodelPlugin;
import org.gervarro.democles.specification.emf.EMFPatternBuilder;
import org.gervarro.democles.specification.emf.Pattern;
import org.gervarro.democles.specification.emf.SpecificationPackage;
import org.gervarro.democles.specification.emf.constraint.EMFTypeModule;
import org.gervarro.democles.specification.emf.constraint.emf.emf.EMFTypePackage;
import org.gervarro.democles.specification.impl.DefaultPattern;
import org.gervarro.democles.specification.impl.DefaultPatternBody;
import org.gervarro.democles.specification.impl.DefaultPatternFactory;

public class BootstrappingTest {
	private ResourceSet set;

	private BootstrappingTest() throws Exception {
		set = EMFDemoclesPatternMetamodelPlugin.createDefaultResourceSet();
		EMFDemoclesPatternMetamodelPlugin.configureDemoclesDefaults(set);
	}

	private void run() throws IOException {
		// 1) Loads pattern specification from EMF file

		SpecificationPackage.eINSTANCE.getSpecificationFactory();
		EMFTypePackage.eINSTANCE.getEMFTypeFactory();

		final URI base = URI.createPlatformResourceURI(
				"/org.gervarro.democles.interpreter.test/model/", true);
		final URI uri = URI.createFileURI("meaningful.pattern");
		set.getURIConverter().getURIMap().put(uri, uri.resolve(base));
		Resource res = set.createResource(uri, ContentHandler.UNSPECIFIED_CONTENT_TYPE);
		res.load(null);
		final Pattern patternSpecification = (Pattern) res.getContents().get(0);

		// 2) Prepares an internal representation for the pattern
		EMFPatternBuilder<DefaultPattern, DefaultPatternBody> patternBuilder =
				new EMFPatternBuilder<DefaultPattern, DefaultPatternBody>(new DefaultPatternFactory());
		EMFConstraintModule emfTypeModule =
				new EMFConstraintModule(set);
		EMFTypeModule internalEMFTypeModule =
				new EMFTypeModule(emfTypeModule);
		patternBuilder.addVariableTypeSwitch(internalEMFTypeModule.getVariableTypeSwitch());
		patternBuilder.addConstraintTypeSwitch(internalEMFTypeModule.getConstraintTypeSwitch());

		final DefaultPattern internalPatternSpecification =
				patternBuilder.build(patternSpecification);

		// 3) Initializes the pattern matcher module
		final LinkedList<SearchPlanOperationBuilder<WeightedOperation<SearchPlanOperation<QueryableRemappingOperation>,Integer>,QueryableRemappingOperation>> builders =
				new LinkedList<SearchPlanOperationBuilder<WeightedOperation<SearchPlanOperation<QueryableRemappingOperation>,Integer>,QueryableRemappingOperation>>();
		builders.add(new CombinedSearchPlanOperationBuilder<WeightedOperation<SearchPlanOperation<QueryableRemappingOperation>,Integer>,SearchPlanOperation<QueryableRemappingOperation>,QueryableRemappingOperation>(
				new EMFSearchPlanOperationBuilder<QueryableRemappingOperation>(),
				new EMFWeightedOperationBuilder<SearchPlanOperation<QueryableRemappingOperation>,QueryableRemappingOperation>()));
		
		DefaultAlgorithm<InterpreterCombiner,SearchPlanOperation<QueryableRemappingOperation>,QueryableRemappingOperation> searchPlanAlgorithm = 
			new DefaultAlgorithm<InterpreterCombiner,SearchPlanOperation<QueryableRemappingOperation>,QueryableRemappingOperation>(builders);
		InterpreterPatternMatcherModule patternMatcherModule =
			new InterpreterPatternMatcherModule(DepthFirstTraversalStrategy.INSTANCE);
		patternMatcherModule.setSearchPlanAlgorithm(
				new InterpreterSearchPlanAlgorithm(searchPlanAlgorithm));
		
		GenericOperationBuilder<SpecificationExtendedVariableRuntime> emfReflectiveOperationBuilder =
			new GenericOperationBuilder<SpecificationExtendedVariableRuntime>(
					new EMFNativeOperationBuilder<SpecificationExtendedVariableRuntime>(emfTypeModule),
					DefaultEMFBatchAdornmentStrategy.INSTANCE);
		patternMatcherModule.addOperationBuilder(emfReflectiveOperationBuilder);
		patternMatcherModule.addOperationBuilder(
				new ListOperationBuilder<InterpretableAdornedOperation, SpecificationExtendedVariableRuntime>(
						new RelationalOperationBuilder<SpecificationExtendedVariableRuntime>()));

		// 4) Creates the interpretable pattern matcher
		OperationCachingPattern patternRuntime =
				patternMatcherModule.build(internalPatternSpecification);

		// 5) Invokes pattern matching (runtime step)
		final InterpretedDataFrame frame = patternRuntime.createDataFrame();
		frame.setValue(0, patternSpecification);
		System.out.println("Input: " + frame);
		int i = 0;
		for (final DataFrame matching : patternRuntime.matchAll(frame)) {
			matching.getValue(2);
			System.out.println(String.format("[Match all]: %2d %s", i++, matching));
		}
		final DataFrame singleMatch = patternRuntime.matchSingle(frame);
		singleMatch.getValue(2);
      System.out.println(String.format("[Match single]: %2d %s", i++, singleMatch));
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		final BootstrappingTest m = new BootstrappingTest();
		EMFDemoclesPatternMetamodelPlugin.setWorkspaceRootDirectory(m.set,
				args[0]);
		m.run();
		System.out.println("Done.");
	}
}
