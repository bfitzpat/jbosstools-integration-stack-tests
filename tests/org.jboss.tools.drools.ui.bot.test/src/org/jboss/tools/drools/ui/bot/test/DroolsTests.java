package org.jboss.tools.drools.ui.bot.test;

import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.drools.ui.bot.test.functional.DroolsRuntimeManagementTest;
import org.jboss.tools.drools.ui.bot.test.functional.DslEditorTest;
import org.jboss.tools.drools.ui.bot.test.functional.DslrEditorTest;
import org.jboss.tools.drools.ui.bot.test.functional.MavenProjectTest;
import org.jboss.tools.drools.ui.bot.test.functional.RulesManagementTest;
import org.jboss.tools.drools.ui.bot.test.functional.drleditor.ConditionsCompletionTest;
import org.jboss.tools.drools.ui.bot.test.functional.drleditor.ConsequencesCompletionTest;
import org.jboss.tools.drools.ui.bot.test.functional.drleditor.DeclareCompletionTest;
import org.jboss.tools.drools.ui.bot.test.functional.drleditor.MetadataCompletionTest;
import org.jboss.tools.drools.ui.bot.test.functional.view.AgendaViewTest;
import org.jboss.tools.drools.ui.bot.test.functional.view.AuditLogTest;
import org.jboss.tools.drools.ui.bot.test.functional.view.GlobalDataViewTest;
import org.jboss.tools.drools.ui.bot.test.functional.view.WorkingMemoryViewTest;
import org.jboss.tools.drools.ui.bot.test.kienavigator.CloneRepoTest;
import org.jboss.tools.drools.ui.bot.test.kienavigator.CreateItemsRestTest;
import org.jboss.tools.drools.ui.bot.test.kienavigator.CreateItemsTest;
import org.jboss.tools.drools.ui.bot.test.kienavigator.OpenKieNavigatorTest;
import org.jboss.tools.drools.ui.bot.test.kienavigator.RepositoryManipulationTest;
import org.jboss.tools.drools.ui.bot.test.kienavigator.ServerConnectionTest;
import org.jboss.tools.drools.ui.bot.test.smoke.ConvertProjectTest;
import org.jboss.tools.drools.ui.bot.test.smoke.DroolsProjectTest;
import org.jboss.tools.drools.ui.bot.test.smoke.KieNavigatorTest;
import org.jboss.tools.drools.ui.bot.test.smoke.NewResourcesTest;
import org.jboss.tools.drools.ui.bot.test.smoke.PerspectiveTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(RedDeerSuite.class)
@SuiteClasses({
	// smoke tests
	PerspectiveTest.class,
	KieNavigatorTest.class,
	DroolsProjectTest.class,
	NewResourcesTest.class,
	ConvertProjectTest.class,
	// basic tests
	MavenProjectTest.class,
	DroolsRuntimeManagementTest.class,
	RulesManagementTest.class,
	// DRL editor code completion
	MetadataCompletionTest.class,
	DeclareCompletionTest.class,
	ConditionsCompletionTest.class,
	ConsequencesCompletionTest.class,
	// DSL editor
	DslEditorTest.class,
	// DSLR editor
	DslrEditorTest.class,
	// TODO debugging tests
	// views test
	AgendaViewTest.class,
	GlobalDataViewTest.class,
	WorkingMemoryViewTest.class,
	AuditLogTest.class,
	// Kie Navigator tests
	OpenKieNavigatorTest.class,
	CloneRepoTest.class,
	CreateItemsRestTest.class,
	CreateItemsTest.class,
	RepositoryManipulationTest.class,
	ServerConnectionTest.class
})
public class DroolsTests {

}
