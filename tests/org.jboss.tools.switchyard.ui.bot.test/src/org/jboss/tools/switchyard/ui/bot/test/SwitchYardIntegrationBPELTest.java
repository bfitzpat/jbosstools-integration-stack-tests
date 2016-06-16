package org.jboss.tools.switchyard.ui.bot.test;

import static org.jboss.tools.switchyard.ui.bot.test.util.TemplateHandler.javaSource;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.eclipse.condition.ConsoleHasText;
import org.jboss.reddeer.eclipse.core.resources.ProjectItem;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.bpel.reddeer.activity.Assign;
import org.jboss.tools.bpel.reddeer.activity.Pick;
import org.jboss.tools.bpel.reddeer.activity.Receive;
import org.jboss.tools.bpel.reddeer.activity.Reply;
import org.jboss.tools.bpel.reddeer.activity.Sequence;
import org.jboss.tools.bpel.reddeer.editor.BpelEditor;
import org.jboss.tools.switchyard.reddeer.component.Service;
import org.jboss.tools.switchyard.reddeer.component.SwitchYardComponent;
import org.jboss.tools.switchyard.reddeer.editor.SwitchYardEditor;
import org.jboss.tools.switchyard.reddeer.project.ProjectItemExt;
import org.jboss.tools.switchyard.reddeer.project.SwitchYardProject;
import org.jboss.tools.switchyard.reddeer.requirement.SwitchYardRequirement;
import org.jboss.tools.switchyard.reddeer.requirement.SwitchYardRequirement.SwitchYard;
import org.jboss.tools.switchyard.reddeer.view.JUnitView;
import org.jboss.tools.switchyard.reddeer.wizard.BPELServiceWizard;
import org.jboss.tools.switchyard.reddeer.wizard.ImportFileWizard;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for BPEL integration
 * 
 * @author apodhrad
 * 
 */
@SwitchYard
@RunWith(RedDeerSuite.class)
public class SwitchYardIntegrationBPELTest {

	public static final String PROJECT = "bpel-integration";
	public static final String PACKAGE = "com.example.switchyard.bpel_integration";

	@InjectRequirement
	private SwitchYardRequirement switchyardRequirement;

	@Before
	@After
	public void closeSwitchyardFile() {
		try {
			new SwitchYardEditor().saveAndClose();
		} catch (Exception ex) {
			// it is ok, we just try to close switchyard.xml if it is open
		}
	}

	@Test
	public void switchYardIntegrationBPELTest() throws Exception {
		switchyardRequirement.project(PROJECT).impl("BPEL").create();
		new SwitchYardProject(PROJECT).getProjectItem("src/main/resources").select();
		new ImportFileWizard().importFile("resources/bpel-integration", "deploy.xml", "Hello.wsdl");
		new SwitchYardProject(PROJECT).getProjectItem("src/test/resources").select();
		new ImportFileWizard().importFile("resources/bpel-integration", "bpel.properties", "request.xml",
				"response.xml");

		BPELServiceWizard bpelWizard = new SwitchYardEditor().addBPELImplementation().activate();
		bpelWizard.selectWSDLInterface("Hello.wsdl");
		bpelWizard.setFileName("Hello.bpel");
		bpelWizard.setServiceName("HelloService");
		bpelWizard.next();
		bpelWizard.setNamespace("http://www.jboss.org/bpel/examples");
		bpelWizard.finish();

		new Service("HelloService").openNewServiceTestClass().setPackage(PACKAGE).finish();
		TextEditor textEditor = new TextEditor("HelloServiceTest.java");
		Map<String, Object> testProperties = new HashMap<String, Object>();
		testProperties.put("package", PACKAGE);
		testProperties.put("service", "HelloService");
		testProperties.put("operation", "sayHello");
		textEditor.setText(javaSource("HelloServiceTest.java", testProperties));
		textEditor.close(true);

		new SwitchYardComponent("Hello").doubleClick();
		BpelEditor bpelEditor = new BpelEditor("Hello.bpel");
		bpelEditor.getVariable("sayHelloRequest").setType("Messages", "sayHello");
		bpelEditor.getVariable("sayHelloResponse").setType("Messages", "sayHelloResponse");

		new Pick("SwitchInvokedOperation").delete();

		Receive receive = new Sequence("MainSequence").addReceive("Receive");
		receive.pickOperation("sayHello");
		receive.checkCreateInstance();

		Assign assign = new Sequence("MainSequence").addAssign("Assign");
		assign.addExpToVar("concat('Hello ', $sayHelloRequest.parameters/ns:string)", new String[] {
				"sayHelloResponse : sayHelloResponse", "parameters : sayHelloResponse", "string : string" });

		Reply reply = new Sequence("MainSequence").addReply("Reply");
		reply.pickOperation("sayHello");

		bpelEditor.save();
		System.out.println();
		
		ProjectExplorer projectExplorer = new ProjectExplorer();
		projectExplorer.open();
		ProjectItem item = projectExplorer.getProject(PROJECT).getProjectItem("pom.xml");
		new ProjectItemExt(item).runAs("Maven test");
		new WaitWhile(new ConsoleHasText("BUILD SUCCESS"));
	}
}
