package org.jboss.tools.esb.reddeer.wizard;

import org.jboss.reddeer.jface.wizard.NewWizardDialog;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.tools.esb.reddeer.widget.LabeledTextExt;

/**
 * Wizard for creating an ESB file.
 * 
 * @author apodhrad
 * 
 */
public class ESBFileWizard extends NewWizardDialog {

	public static final String DIALOG_TITLE = "New ESB File";
	public static final String FOLDER = "Folder:*";
	public static final String NAME = "Name:*";
	public static final String VERSION = "Version:*";

	public ESBFileWizard() {
		super("ESB", "ESB File");
	}

	public ESBFileWizard activate() {
		new DefaultShell(DIALOG_TITLE);
		return this;
	}

	public ESBFileWizard openWizard() {
		open();
		activate();
		return this;
	}

	public ESBFileWizard setName(String name) {
		new LabeledTextExt(NAME).setFocus();
		new LabeledTextExt(NAME).setText(name);
		return this;
	}

	public String getName() {
		return new LabeledTextExt(NAME).getText();
	}

}
