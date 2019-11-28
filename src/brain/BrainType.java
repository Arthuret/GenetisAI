package brain;

import menu.brain_editor.BrainConfigPanel;
import brain.neural_brain.NeuralBrainConfig;
//import brain.neural_brain.NeuralBrainTemplate;
//import brain.sequential_brain.SequentialBrainConfig;
//import brain.sequential_brain.SequentialBrainTemplate;

public enum BrainType {
	NEURAL_BRAIN(/*NeuralBrainTemplate.class,*/NeuralBrainConfig.class);
	//SEQUENTIAL_BRAIN(/*SequentialBrainTemplate.class,*/SequentialBrainConfig.class);
	
	//Add above the new brain types
	//Ajouter ci-dessus les nouveaux types de brains
	
	//private Class<? extends BrainTemplate> template;
	private Class<? extends BrainConfigPanel> editor;
	
	BrainType(/*Class<? extends BrainTemplate> template,*/ Class<? extends BrainConfigPanel> editor) {
		this.editor = editor;
		//this.template = template;
	}

	/*public Class<? extends BrainTemplate> getTemplate() {
		return template;
	}*/

	public Class<? extends BrainConfigPanel> getEditor() {
		return editor;
	}
}
