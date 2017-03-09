package SimpleGraphLanguage;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.gervarro.democles.common.DataFrame;
import org.gervarro.democles.common.runtime.ListOperationBuilder;
import org.gervarro.democles.common.runtime.SearchPlanOperation;
import org.gervarro.democles.common.runtime.SpecificationExtendedVariableRuntime;
import org.gervarro.democles.constraint.CoreConstraintModule;
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
import org.gervarro.democles.plan.common.RelationalSearchPlanOperationBuilder;
import org.gervarro.democles.plan.common.RelationalWeightedOperationBuilder;
import org.gervarro.democles.plan.common.SearchPlanOperationBuilder;
import org.gervarro.democles.plan.emf.EMFSearchPlanOperationBuilder;
import org.gervarro.democles.plan.emf.EMFWeightedOperationBuilder;
import org.gervarro.democles.runtime.BatchInterpreterSearchPlanOperation;
import org.gervarro.democles.runtime.DepthFirstTraversalStrategy;
import org.gervarro.democles.runtime.GenericOperationBuilder;
import org.gervarro.democles.runtime.InterpretedDataFrame;
import org.gervarro.democles.specification.emf.Constraint;
import org.gervarro.democles.specification.emf.ConstraintParameter;
import org.gervarro.democles.specification.emf.EMFDemoclesPatternMetamodelPlugin;
import org.gervarro.democles.specification.emf.EMFPatternBuilder;
import org.gervarro.democles.specification.emf.Pattern;
import org.gervarro.democles.specification.emf.PatternBody;
import org.gervarro.democles.specification.emf.SpecificationFactory;
import org.gervarro.democles.specification.emf.SpecificationPackage;
import org.gervarro.democles.specification.emf.constraint.EMFTypeModule;
import org.gervarro.democles.specification.emf.constraint.PatternInvocationTypeModule;
import org.gervarro.democles.specification.emf.constraint.RelationalTypeModule;
import org.gervarro.democles.specification.emf.constraint.emf.emf.Attribute;
import org.gervarro.democles.specification.emf.constraint.emf.emf.EMFTypeFactory;
import org.gervarro.democles.specification.emf.constraint.emf.emf.EMFTypePackage;
import org.gervarro.democles.specification.emf.constraint.emf.emf.EMFVariable;
import org.gervarro.democles.specification.emf.constraint.emf.emf.Reference;
import org.gervarro.democles.specification.emf.constraint.relational.RelationalConstraint;
import org.gervarro.democles.specification.emf.constraint.relational.RelationalConstraintFactory;
import org.gervarro.democles.specification.emf.constraint.relational.Unequal;
import org.gervarro.democles.specification.impl.DefaultPattern;
import org.gervarro.democles.specification.impl.DefaultPatternBody;
import org.gervarro.democles.specification.impl.DefaultPatternFactory;
import org.gervarro.democles.specification.impl.PatternInvocationConstraintModule;

public class SimpleGraphInterpreterTest
{
   private ResourceSet set;

   private SimpleGraphInterpreterTest() throws Exception
   {
      set = EMFDemoclesPatternMetamodelPlugin.createDefaultResourceSet();
      EMFDemoclesPatternMetamodelPlugin.configureDemoclesDefaults(set);
   }

   /**
    * @throws IOException
    */
   private void run() throws IOException
   {
      // 1) Loads pattern specification from EMF file

      SpecificationPackage.eINSTANCE.getSpecificationFactory();
      EMFTypePackage.eINSTANCE.getEMFTypeFactory();

      final Pattern patternSpecification = createNodePairPattern();
      this.validatPatternSpecification(patternSpecification);

      // 2) Prepares an internal representation for the pattern
      EMFPatternBuilder<DefaultPattern, DefaultPatternBody> patternBuilder = new EMFPatternBuilder<DefaultPattern, DefaultPatternBody>(
            new DefaultPatternFactory());
      EMFConstraintModule emfTypeModule = new EMFConstraintModule(set);

      EMFTypeModule internalEMFTypeModule = new EMFTypeModule(emfTypeModule);
      patternBuilder.addVariableTypeSwitch(internalEMFTypeModule.getVariableTypeSwitch());
      patternBuilder.addConstraintTypeSwitch(internalEMFTypeModule.getConstraintTypeSwitch());

      RelationalTypeModule relationalTypeModule = new RelationalTypeModule(CoreConstraintModule.INSTANCE);
      patternBuilder.addVariableTypeSwitch(relationalTypeModule.getVariableTypeSwitch());
      patternBuilder.addConstraintTypeSwitch(relationalTypeModule.getConstraintTypeSwitch());

      final PatternInvocationConstraintModule<DefaultPattern, DefaultPatternBody> patternInvocationTypeModule = new PatternInvocationConstraintModule<>(
            patternBuilder);
      final PatternInvocationTypeModule<DefaultPattern, DefaultPatternBody> internalPatternInvocationTypeModule = new PatternInvocationTypeModule<>(
            patternInvocationTypeModule);
      patternBuilder.addVariableTypeSwitch(internalPatternInvocationTypeModule.getVariableTypeSwitch());
      patternBuilder.addConstraintTypeSwitch(internalPatternInvocationTypeModule.getConstraintTypeSwitch());

      final DefaultPattern internalPatternSpecification = patternBuilder.build(patternSpecification);

      // 3) Initializes the pattern matcher module
      final LinkedList<SearchPlanOperationBuilder<WeightedOperation<SearchPlanOperation<BatchInterpreterSearchPlanOperation>, Integer>, BatchInterpreterSearchPlanOperation>> builders = new LinkedList<>();
      builders.add(new CombinedSearchPlanOperationBuilder<>(new EMFSearchPlanOperationBuilder<>(), new EMFWeightedOperationBuilder<>()));
      builders.add(new CombinedSearchPlanOperationBuilder<>(new RelationalSearchPlanOperationBuilder<>(), new RelationalWeightedOperationBuilder<>()));

      DefaultAlgorithm<InterpreterCombiner, SearchPlanOperation<BatchInterpreterSearchPlanOperation>, BatchInterpreterSearchPlanOperation> searchPlanAlgorithm = new DefaultAlgorithm<>(
            builders);

      InterpreterPatternMatcherModule patternMatcherModule = new InterpreterPatternMatcherModule(DepthFirstTraversalStrategy.INSTANCE);
      patternMatcherModule.setSearchPlanAlgorithm(new InterpreterSearchPlanAlgorithm(searchPlanAlgorithm));

      GenericOperationBuilder<SpecificationExtendedVariableRuntime> emfReflectiveOperationBuilder = new GenericOperationBuilder<>(
            new EMFNativeOperationBuilder<>(emfTypeModule), DefaultEMFBatchAdornmentStrategy.INSTANCE);
      patternMatcherModule.addOperationBuilder(emfReflectiveOperationBuilder);
      patternMatcherModule.addOperationBuilder(new ListOperationBuilder<>(new RelationalOperationBuilder<>()));

      // 4) Creates the interpretable pattern matcher
      OperationCachingPattern patternRuntime = patternMatcherModule.build(internalPatternSpecification);

      // Create sample graph
      SimpleGraphLanguageFactory graphFactory = SimpleGraphLanguageFactory.eINSTANCE;

      SimpleGraph graph = graphFactory.createSimpleGraph();

      Node node1 = graphFactory.createNode();
      graph.getNodes().add(node1);
      node1.setId("node1");

      Node node2 = graphFactory.createNode();
      graph.getNodes().add(node2);
      node2.setId("node2+3");
      
      Node node3 = graphFactory.createNode();
      graph.getNodes().add(node3);
      node3.setId("node2+3");

      // 5) Invokes pattern matching (runtime step)
      final InterpretedDataFrame frame = patternRuntime.createDataFrame();
      frame.setValue(0, graph);
      System.out.println("Input: " + frame);
      int i = 0;
      for (final DataFrame matching : patternRuntime.matchAll(frame))
      {
         matching.getValue(2);
         System.out.println(String.format("[Match all]: %2d %s", i++, matching));
      }
      final DataFrame singleMatch = patternRuntime.matchSingle(frame);
      singleMatch.getValue(2);
      System.out.println(String.format("[Match single]: %2d %s", i++, singleMatch));
   }

   private String formatConstraintParameters(final Constraint constraint)
   {
      EList<ConstraintParameter> parameters = constraint.getParameters();
      return formatConstraintParameters(parameters);
   }

   private String formatConstraintParameters(List<ConstraintParameter> parameters)
   {
      return "[" + parameters.stream().map(parameter -> parameter.getReference() != null ? parameter.getReference() : "null").map(Object::toString)
            .collect(Collectors.joining(", ")) + "]";
   }

   private Pattern createNodePairPattern()
   {
      final SpecificationFactory specFac = SpecificationFactory.eINSTANCE;
      final EMFTypeFactory typeFactory = EMFTypeFactory.eINSTANCE;
      final RelationalConstraintFactory relationalFactory = RelationalConstraintFactory.eINSTANCE;
      final SimpleGraphLanguagePackage simplePackage = SimpleGraphLanguagePackage.eINSTANCE;

      final Pattern trianglePattern = specFac.createPattern();
      final PatternBody patternBody = specFac.createPatternBody();
      trianglePattern.getBodies().add(patternBody);
      // -
      EMFVariable graphv1 = typeFactory.createEMFVariable();
      trianglePattern.getSymbolicParameters().add(graphv1);
      graphv1.setEClassifier(simplePackage.getSimpleGraph());
      graphv1.setName("graphv1");
      // -
      EMFVariable nv1 = typeFactory.createEMFVariable();
      trianglePattern.getSymbolicParameters().add(nv1);
      nv1.setEClassifier(simplePackage.getNode());
      nv1.setName("nv1");
      // -
      EMFVariable nv2 = typeFactory.createEMFVariable();
      trianglePattern.getSymbolicParameters().add(nv2);
      nv2.setEClassifier(simplePackage.getNode());
      nv2.setName("nv2");
      //-
      EMFVariable nv1Id = typeFactory.createEMFVariable();
      patternBody.getLocalVariables().add(nv1Id);
      //-
      EMFVariable nv2Id = typeFactory.createEMFVariable();
      patternBody.getLocalVariables().add(nv2Id);

      // -
      Reference graphv1ToNv1 = typeFactory.createReference();
      patternBody.getConstraints().add(graphv1ToNv1);
      graphv1ToNv1.setEModelElement(simplePackage.getSimpleGraph_Nodes());

      ConstraintParameter graphv1ToNv1_param_graphv1 = specFac.createConstraintParameter();
      graphv1ToNv1.getParameters().add(graphv1ToNv1_param_graphv1);
      graphv1ToNv1_param_graphv1.setReference(graphv1);

      ConstraintParameter graphv1ToNv1_param_nv1 = specFac.createConstraintParameter();
      graphv1ToNv1.getParameters().add(graphv1ToNv1_param_nv1);
      graphv1ToNv1_param_nv1.setReference(nv1);

      // -
      Reference graphv1ToNv2 = typeFactory.createReference();
      patternBody.getConstraints().add(graphv1ToNv2);
      graphv1ToNv2.setEModelElement(simplePackage.getSimpleGraph_Nodes());

      ConstraintParameter graphv1ToNv2_param_graphv1 = specFac.createConstraintParameter();
      graphv1ToNv2.getParameters().add(graphv1ToNv2_param_graphv1);
      graphv1ToNv2_param_graphv1.setReference(graphv1);

      ConstraintParameter graphv1ToNv2_param_nv2 = specFac.createConstraintParameter();
      graphv1ToNv2.getParameters().add(graphv1ToNv2_param_nv2);
      graphv1ToNv2_param_nv2.setReference(nv2);

      // - Access nv1.id
      Attribute nv1IdAttribute = typeFactory.createAttribute();
      patternBody.getConstraints().add(nv1IdAttribute);
      nv1IdAttribute.setEModelElement(simplePackage.getNode_Id());

      ConstraintParameter nv1Id_param_nv1 = specFac.createConstraintParameter();
      nv1Id_param_nv1.setReference(nv1);
      nv1IdAttribute.getParameters().add(nv1Id_param_nv1);

      ConstraintParameter nv1Id_param_nv1Id = specFac.createConstraintParameter();
      nv1Id_param_nv1Id.setReference(nv1Id);
      nv1IdAttribute.getParameters().add(nv1Id_param_nv1Id);

      // -  Access nv2.id  
      Attribute nv2IdAttribute = typeFactory.createAttribute();
      patternBody.getConstraints().add(nv2IdAttribute);
      nv2IdAttribute.setEModelElement(simplePackage.getNode_Id());

      ConstraintParameter nv2Id_param_nv2 = specFac.createConstraintParameter();
      nv2Id_param_nv2.setReference(nv2);
      nv2IdAttribute.getParameters().add(nv2Id_param_nv2);

      ConstraintParameter nv2Id_param_nvId2 = specFac.createConstraintParameter();
      nv2Id_param_nvId2.setReference(nv2Id);
      nv2IdAttribute.getParameters().add(nv2Id_param_nvId2);

      // - nv1 != nv2
      Unequal unequal = relationalFactory.createUnequal();
      patternBody.getConstraints().add(unequal);
      ConstraintParameter unequal_param_nv1 = specFac.createConstraintParameter();
      unequal_param_nv1.setReference(nv1);
      ConstraintParameter unequal_param_nv2 = specFac.createConstraintParameter();
      unequal_param_nv2.setReference(nv2);
      unequal.getParameters().addAll(Arrays.asList(unequal_param_nv1, unequal_param_nv2));

      // - nv1.id != nv2.id
      Unequal unequalId = relationalFactory.createUnequal();
      patternBody.getConstraints().add(unequalId);
      ConstraintParameter unequalId_param_nv1Id = specFac.createConstraintParameter();
      unequalId_param_nv1Id.setReference(nv1Id);
      ConstraintParameter unequalId_param_nv2Id = specFac.createConstraintParameter();
      unequalId_param_nv2Id.setReference(nv2Id);
      unequalId.getParameters().addAll(Arrays.asList(unequalId_param_nv1Id, unequalId_param_nv2Id));

      return trianglePattern;
   }

   private void validatPatternSpecification(Pattern patternSpecification)
   {
      patternSpecification.getSymbolicParameters().forEach(parameter -> {
         if (parameter.getName() == null)
            throw new IllegalArgumentException("Symbolic parameter has no name: " + parameter);
         if (parameter instanceof EMFVariable)
         {
            final EMFVariable emfParameter = EMFVariable.class.cast(parameter);
            if (emfParameter.getEClassifier() == null)
               throw new IllegalArgumentException("Symbolic parameter has no type: " + parameter);
         }
      });

      // Reverse engineered from org.gervarro.democles.operation.emf.EMFNativeOperationBuilder.getConstraintOperation(ConstraintType, List<? extends VR>)
      patternSpecification.getBodies().forEach(b -> b.getConstraints().forEach(constraint -> {
         List<ConstraintParameter> parametersWithNullReference = constraint.getParameters().stream()
               .filter(parameter -> (parameter == null || parameter.getReference() == null)).collect(Collectors.toList());
         if (!parametersWithNullReference.isEmpty())
            throw new IllegalArgumentException(
                  String.format("Attribute constraint '%s' has parameters with 'null' reference: '%s'", constraint, formatConstraintParameters(constraint)));

         if (constraint instanceof Attribute)
         {
            if (constraint.getParameters().size() != 2)
            {
               throw new IllegalArgumentException(String.format("Attribute constraint '%s' must have 2 parameters. Actual parameters: '%s'", constraint,
                     formatConstraintParameters(constraint)));
            }
         } else if (constraint instanceof Reference)
         {
            final Reference refConstraint = Reference.class.cast(constraint);
            if (refConstraint.getEModelElement() == null)
            {
               throw new IllegalArgumentException(String.format("Reference constraint '%s' must have an associated EModelElement.", constraint));
            }
            if (constraint.getParameters().size() != 2)
            {
               throw new IllegalArgumentException(String.format("Reference constraint '%s' must have 2 parameters. Actual parameters: '%s'", constraint,
                     formatConstraintParameters(constraint)));
            }
         } else if (constraint instanceof RelationalConstraint)
         {
            if (constraint.getParameters().size() != 2)
            {
               throw new IllegalArgumentException(String.format("%s constraint '%s' must have 2 parameters. Actual parameters: '%s'",
                     constraint.getClass().getSimpleName(), constraint, formatConstraintParameters(constraint)));
            }
         }
      }));

   }

   /**
    * @param args
    */
   public static void main(final String[] args) throws Exception
   {
      final SimpleGraphInterpreterTest m = new SimpleGraphInterpreterTest();
      //EMFDemoclesPatternMetamodelPlugin.setWorkspaceRootDirectory(m.set, args[0]);
      m.run();
      System.out.println("Done.");
   }
}
