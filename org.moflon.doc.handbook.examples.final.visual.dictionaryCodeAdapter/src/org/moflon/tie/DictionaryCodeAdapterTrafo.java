package org.moflon.tie;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.emf.ecore.EObject;
import org.moflon.core.utilities.eMoflonEMFUtil;
import org.moflon.moca.MocaMain;
import org.moflon.tgg.algorithm.synchronization.SynchronizationHelper;

import DictionaryCodeAdapter.DictionaryCodeAdapterPackage;
import MocaTree.Folder;


public class DictionaryCodeAdapterTrafo extends SynchronizationHelper{

   public DictionaryCodeAdapterTrafo()
   {
      super(DictionaryCodeAdapterPackage.eINSTANCE, ".");
   }

	public static void main(String[] args) throws IOException {
		// Set up logging
        BasicConfigurator.configure();
        
        // Text to tree
        Folder folder = MocaMain.getCodeAdapter().parse(new File("instances/in/myLibrary"));
        eMoflonEMFUtil.saveModel(eMoflonEMFUtil.createDefaultResourceSet(), folder, "instances/fwd.src.xmi");

		// Forward Transformation
        DictionaryCodeAdapterTrafo helper = new DictionaryCodeAdapterTrafo();
		helper.performForward("instances/fwd.src.xmi");
		
		// Backward Transformation
		helper = new DictionaryCodeAdapterTrafo();
		helper.performBackward("instances/bwd.src.xmi");
		
		// Tree to text
		MocaMain.getCodeAdapter().unparse("instances/out", (Folder) helper.getSrc());
	}

	public void performForward() {
		integrateForward();

		saveTrg("instances/fwd.trg.xmi");
		saveCorr("instances/fwd.corr.xmi");
		saveSynchronizationProtocol("instances/fwd.protocol.xmi");

		System.out.println("Completed forward transformation!");
	}

	public void performForward(EObject srcModel) {
		setSrc(srcModel);
		performForward();
	}

	public void performForward(String source) {
		try {
			loadSrc(source);
			performForward();
		} catch (IllegalArgumentException iae) {
			System.err.println("Unable to load " + source + ", "
					+ iae.getMessage());
			return;
		}
	}

	public void performBackward() {
		integrateBackward();

		saveSrc("instances/bwd.trg.xmi");
		saveCorr("instances/bwd.corr.xmi");
		saveSynchronizationProtocol("instances/bwd.protocol.xmi");

		System.out.println("Completed backward transformation!");
	}

	public void performBackward(EObject targetModel) {
		setTrg(targetModel);
		performBackward();
	}

	public void performBackward(String target) {
		try {
			loadTrg(target);
			performBackward();
		} catch (IllegalArgumentException iae) {
			System.err.println("Unable to load " + target + ", "
					+ iae.getMessage());
			return;
		}
	}
}