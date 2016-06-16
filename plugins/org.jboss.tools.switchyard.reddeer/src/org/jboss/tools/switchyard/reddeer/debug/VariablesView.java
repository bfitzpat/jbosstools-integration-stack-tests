package org.jboss.tools.switchyard.reddeer.debug;

import org.jboss.reddeer.common.condition.AbstractWaitCondition;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.impl.view.WorkbenchView;

public class VariablesView extends WorkbenchView {

	public VariablesView() {
		super("Variables");
	}

	public String getValue(final String... variablePath) {
		open();
		new DefaultTreeItem(variablePath).select();
		new WaitUntil(new AbstractWaitCondition() {

			@Override
			public boolean test() {
				return new DefaultTreeItem(variablePath).isSelected();
			}

			@Override
			public String description() {
				return "Variable is not selected";
			}
		});
		return new DefaultStyledText().getText();
	}
}
