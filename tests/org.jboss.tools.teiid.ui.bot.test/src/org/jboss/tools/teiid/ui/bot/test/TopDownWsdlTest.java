package org.jboss.tools.teiid.ui.bot.test;

import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.eclipse.wst.server.ui.view.ServersView;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.server.ServerReqState;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.tools.teiid.reddeer.ModelProject;
import org.jboss.tools.teiid.reddeer.editor.ModelEditor;
import org.jboss.tools.teiid.reddeer.editor.SQLScrapbookEditor;
import org.jboss.tools.teiid.reddeer.editor.VDBEditor;
import org.jboss.tools.teiid.reddeer.manager.ConnectionProfileManager;
import org.jboss.tools.teiid.reddeer.manager.ModelExplorerManager;
import org.jboss.tools.teiid.reddeer.requirement.TeiidServerRequirement.TeiidServer;
import org.jboss.tools.teiid.reddeer.view.DataSourceExplorer;
import org.jboss.tools.teiid.reddeer.view.ModelExplorer;
import org.jboss.tools.teiid.reddeer.view.SQLResult;
import org.jboss.tools.teiid.reddeer.view.SQLResultView;
import org.jboss.tools.teiid.reddeer.view.ServersViewExt;
import org.jboss.tools.teiid.reddeer.wizard.CreateVDB;
import org.jboss.tools.teiid.reddeer.wizard.ImportFileWizard;
import org.jboss.tools.teiid.reddeer.wizard.ImportJDBCDatabaseWizard;
import org.jboss.tools.teiid.reddeer.wizard.WsdlWebImportWizard;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author apodhrad
 */
@RunWith(RedDeerSuite.class)
@TeiidServer(state = ServerReqState.RUNNING)
public class TopDownWsdlTest {

	public static final String BUNDLE = "org.teiid.designer.ui.bot.test";
	public static final String PROJECT_NAME = "TopDownWsdlTest";
	public static final String WS_NAME = "ChkOrdSvc";
	public static final String CONNECTION_PROFILE = "TopDownWsdl SQL Profile";
	public static final String VDB_NAME = "testVDB";

	private TeiidBot teiidBot;

	public TopDownWsdlTest() {
		teiidBot = new TeiidBot();
	}

	@Test
	public void topDownWsdlTestScript() throws Exception {

		new ModelExplorerManager().createProject(PROJECT_NAME);
		new ImportFileWizard().importFile("resources/wsdl", "wsdl");
		new WsdlWebImportWizard().importWsdl(WS_NAME, PROJECT_NAME, "TpcrOrderChecking.wsdl");

		/* Create DB connection profile */
		new ConnectionProfileManager().createCPWithDriverDefinition(CONNECTION_PROFILE, "resources/db/sqlserver_tpcr.properties");

		/* Import a Relational Source */
		String fileName = "TPCR_S2k.xmi";
		ImportJDBCDatabaseWizard wizard = new ImportJDBCDatabaseWizard();
		wizard.setConnectionProfile(CONNECTION_PROFILE);
		wizard.setProjectName(PROJECT_NAME);
		wizard.setModelName(fileName);
		wizard.execute();

		ModelProject modelproject = teiidBot.modelExplorer().getModelProject(PROJECT_NAME);
		Assert.assertTrue(fileName + " not created!", modelproject.containsItem(fileName));

		open(WS_NAME + "Responses.xmi", "Service1Soap_CheckOrder_OCout", "Mapping Diagram");

		ModelEditor modelEditor = new ModelEditor(WS_NAME + "Responses.xmi");

		modelEditor.show();
		modelEditor.showMappingTransformation("CONTAINER");

		/* Map XML view to Relational Sources */
		String sql = "SELECT convert(O_ORDERKEY, string) AS ORDER_KEY, convert(O_ORDERDATE, date) AS ORDER_DATE, C_NAME AS CUSTOMER, convert(P_PARTKEY, string) AS PART_KEY, P_NAME AS PART_NAME, convert(L_SHIPDATE, date) AS SHIP_DATE, convert(O_ORDERSTATUS, string) AS ORDER_STATUS, P_COMMENT AS PART_COMMENT, C_COMMENT AS CUSTOMER_COMMENT "
				+ "FROM TPCR_S2k.ORDERS, TPCR_S2k.PART, TPCR_S2k.LINEITEM, TPCR_S2k.CUSTOMER "
				+ "WHERE (O_ORDERDATE = {ts'1993-03-31 00:00:00.0'}) AND (O_ORDERKEY = L_ORDERKEY) AND (O_CUSTKEY = C_CUSTKEY) AND (L_PARTKEY = P_PARTKEY) AND (L_SHIPDATE BETWEEN {ts'1993-04-01 00:00:00.0'} AND {ts'1993-04-15 00:00:00.0'})";

		modelEditor.setTransformation(sql);
		modelEditor.saveAndValidateSql(); // !!!NEEDS RECONCILING!!!
		modelEditor.save();

		/* Build the WS Operation's transformation */
		String procedureSql = "CREATE VIRTUAL PROCEDURE\n"
				+ "BEGIN\n"
				+ "\tDECLARE string VARIABLES.IN_ShipDateHigh;\n"
				+ "\tVARIABLES.IN_ShipDateHigh = xpathValue(ChkOrdSvc.Service1Soap.CheckOrder.OCin, '/*:OC_Input/*:ShipDateHigh');\n"
				+ "\tDECLARE string VARIABLES.IN_ShipDateLow;\n"
				+ "\tVARIABLES.IN_ShipDateLow = xpathValue(ChkOrdSvc.Service1Soap.CheckOrder.OCin, '/*:OC_Input/*:ShipDateLow');\n"
				+ "\tDECLARE string VARIABLES.IN_OrderDate;\n"
				+ "\tVARIABLES.IN_OrderDate = xpathValue(ChkOrdSvc.Service1Soap.CheckOrder.OCin, '/*:OC_Input/*:OrderDate');\n";
		String selectSql = "SELECT * FROM ChkOrdSvcResponses.Service1Soap_CheckOrder_OCout WHERE (ChkOrdSvcResponses.Service1Soap_CheckOrder_OCout.OC_Output.CONTAINER.ORDER_DATE = parseDate(VARIABLES.IN_ORDERDATE, 'yyyy-MM-dd')) AND ((ChkOrdSvcResponses.Service1Soap_CheckOrder_OCout.OC_Output.CONTAINER.SHIP_DATE >= parseDate(VARIABLES.IN_SHIPDATELOW, 'yyyy-MM-dd')) AND (ChkOrdSvcResponses.Service1Soap_CheckOrder_OCout.OC_Output.CONTAINER.SHIP_DATE <= parseDate(VARIABLES.IN_SHIPDATEHIGH, 'yyyy-MM-dd')));";

		open(WS_NAME + ".xmi", "Service1Soap", "CheckOrder", "Transformation Diagram");

		modelEditor = new ModelEditor(WS_NAME + ".xmi");
		modelEditor.show();
		modelEditor.showTransformation();
		modelEditor.setTransformation(procedureSql + selectSql + "\nEND");
		modelEditor.saveAndValidateSql();
		modelEditor.save();

		/* Create a VDB */
		CreateVDB createVDB = new CreateVDB();
		createVDB.setFolder(PROJECT_NAME);
		createVDB.setName(VDB_NAME);
		createVDB.execute();

		VDBEditor editor = new VDBEditor("testVDB.vdb");
		editor.show();
		editor.addModel(PROJECT_NAME, WS_NAME + ".xmi");
		editor.save();
		
		new ServersView().open();
		new ServersViewExt().refreshServer(new ServersView().getServers().get(0).getLabel().getName());
		new ModelExplorer().executeVDB(PROJECT_NAME, VDB_NAME + ".vdb");
		
		String testSql = "SELECT * FROM ChkOrdSvcResponses.Service1Soap_CheckOrder_OCout";
		Assert.assertEquals(SQLResult.STATUS_SUCCEEDED, executeSQL(VDB_NAME, testSql).getStatus());

		testSql = "EXEC ChkOrdSvc.Service1Soap.CheckOrder('<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<OC_Input xmlns=\"http://com.metamatrix/TPCRwsdl_VDB\">"
				+ "<OrderDate>1993-03-31</OrderDate>"
				+ "<ShipDateLow>1993-04-01</ShipDateLow>"
				+ "<ShipDateHigh>1993-04-02</ShipDateHigh>" + "</OC_Input>')";

		Assert.assertEquals(SQLResult.STATUS_SUCCEEDED, executeSQL(VDB_NAME, testSql).getStatus());
	}

	private static void open(String... path) {
		
		new DefaultShell().setFocus();
		new ModelExplorer().getModelProject(PROJECT_NAME).open(path);
	}
	
	private static SQLResult executeSQL(String datasource, String sql) {

		new DefaultShell();
		new DataSourceExplorer().openSQLScrapbook(datasource);
		SQLScrapbookEditor sqlEditor = new SQLScrapbookEditor();
		sqlEditor.show();
		sqlEditor.setText(sql);
		sqlEditor.executeAll();
		new WaitWhile(new ShellWithTextIsAvailable("SQL Statement Execution"), TimePeriod.LONG);
		SQLResultView resView = new SQLResultView();
		resView.open();
		SQLResult result = resView.getByOperation(sql);
		sqlEditor.close();

		return result;
	}
}