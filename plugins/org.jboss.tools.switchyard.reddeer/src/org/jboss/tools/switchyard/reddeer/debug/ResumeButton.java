package org.jboss.tools.switchyard.reddeer.debug;

import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.jboss.reddeer.common.matcher.RegexMatcher;
import org.jboss.reddeer.core.matcher.WithTooltipTextMatcher;

public class ResumeButton extends DefaultToolItem {

	public ResumeButton() {
		super(new WorkbenchShell(), new WithTooltipTextMatcher(new RegexMatcher("Resume.*")));
	}

	@Override
	public void click() {
		if (!isEnabled()) {
			throw new RuntimeException("Cannot click on 'Resume' button, it is NOT enabled!");
		}
		super.click();
	}

}
