package org.jboss.tools.teiid.ui.bot.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.requirements.server.ServerReqState;
import org.jboss.reddeer.swt.api.TableItem;
import org.jboss.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.tab.DefaultTabItem;
import org.jboss.reddeer.swt.impl.table.DefaultTable;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.tools.teiid.reddeer.connection.TeiidJDBCHelper;
import org.jboss.tools.teiid.reddeer.manager.ConnectionProfilesConstants;
import org.jboss.tools.teiid.reddeer.manager.ImportManager;
import org.jboss.tools.teiid.reddeer.manager.ImportMetadataManager;
import org.jboss.tools.teiid.reddeer.manager.ModelExplorerManager;
import org.jboss.tools.teiid.reddeer.manager.VDBManager;
import org.jboss.tools.teiid.reddeer.perspective.TeiidPerspective;
import org.jboss.tools.teiid.reddeer.requirement.TeiidServerRequirement;
import org.jboss.tools.teiid.reddeer.requirement.TeiidServerRequirement.TeiidServer;
import org.jboss.tools.teiid.reddeer.view.ModelExplorer;
import org.jboss.tools.teiid.reddeer.wizard.DDLExportWizard;
import org.jboss.tools.teiid.reddeer.wizard.DDLImportWizard;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author mkralik
 */

@RunWith(RedDeerSuite.class)
@OpenPerspective(TeiidPerspective.class)

@TeiidServer(state = ServerReqState.RUNNING, connectionProfiles={
		ConnectionProfilesConstants.ORACLE_11G_PARTS_SUPPLIER,
})

public class ImportDDLtest {
	@InjectRequirement
	private static TeiidServerRequirement teiidServer;

	
	private static TeiidBot teiidBot = new TeiidBot();

	private static final String PROJECT_NAME = "DDLimport";
	private static final String PROJECT_LOCATION = "resources/projects/"+ PROJECT_NAME;
	private static final String NAME_SOURCE_MODEL = "sourceModel";
	private static final String NAME_VIEW_ORIGINAL_MODEL = "viewModelOriginal";
	private static final String NAME_VIEW_MODEL = "viewModel";
	private static final String NAME_VDB = "ddlVDB";
	private static final String PATH_TO_DDL = teiidBot.toAbsolutePath("resources/ddl/viewModel.ddl");
	private static final String PATH_TO_ORIGINAL_DDL = teiidBot.toAbsolutePath("resources/ddl/viewModelOriginal.ddl");

	
	@BeforeClass
	public static void openPerspective() {
		new ImportManager().importProject(PROJECT_LOCATION);
    	new ModelExplorerManager().changeConnectionProfile(ConnectionProfilesConstants.ORACLE_11G_PARTS_SUPPLIER, PROJECT_NAME, NAME_SOURCE_MODEL);
	}	

	@Test
	public void importDDL(){
		Properties props = new Properties();
		props.setProperty("modelType", DDLImportWizard.View_Type);
		new ImportMetadataManager().importFromDDL(PROJECT_NAME, NAME_VIEW_MODEL, PATH_TO_DDL ,DDLImportWizard.TEIID_WIZARD, props);
				
		new ModelExplorer().getModelProject(PROJECT_NAME).open();
		new DefaultTreeItem(PROJECT_NAME,NAME_VIEW_MODEL + ".xmi","tempTable").doubleClick();
		AbstractWait.sleep(TimePeriod.SHORT);
		new DefaultCTabItem("Table Editor").activate();
		new DefaultTabItem("Base Tables").activate();
		List<TableItem> items = new DefaultTable().getItems();
		//if table was set to temp
		assertTrue("true".equals(items.get(0).getText(10)));
			
		VDBManager vdb = new VDBManager();
		vdb.createVDB(PROJECT_NAME, NAME_VDB);
		vdb.addModelsToVDB(PROJECT_NAME, NAME_VDB, new String[]{NAME_SOURCE_MODEL + ".xmi", NAME_VIEW_MODEL + ".xmi"});
		vdb.deployVDB(new String[]{PROJECT_NAME, NAME_VDB});
		
		TeiidJDBCHelper jdbchelper = new TeiidJDBCHelper(teiidServer, NAME_VDB);
		try {
			List<ResultSet> resultSets = jdbchelper.executeMultiQuery(
					"select * from \"viewModel\".\"tempTable\";",
					"INSERT INTO \"viewModel\".\"tempTable\" VALUES ('testID1',10,true);",
					"INSERT INTO \"viewModel\".\"tempTable\" VALUES ('testID2','10',false);",
					"select * from \"viewModel\".\"tempTable\";",
					"select * from ( exec \"viewModel\".\"getProduct\"('PRD01095') ) AS X_X ;",
					"select * from ( exec \"viewModel\".\"selfProc\"('hello') ) AS X_X ;"
					);
			//first select is empty
			assertFalse(resultSets.get(0).isBeforeFirst()); 
			//test global temp table
			ResultSet rs = resultSets.get(1);
 			rs.next();
 			assertEquals("testID1", rs.getString(1));
 			assertEquals("true", rs.getString(3));
 			rs.next();
 			assertEquals("testID2", rs.getString(1));
 			assertEquals("false", rs.getString(3));
 			//test procedures
 			rs = resultSets.get(2);
 			rs.next();
 			assertEquals("Intel Corporation3", rs.getString(1));
 			rs = resultSets.get(3);
 			rs.next();
 			assertEquals("hello", rs.getString(1));
 			
 		} catch (SQLException e) {
 			fail(e.getMessage());
 		}
		jdbchelper.closeConnection();
	}
	
	@Test
	public void exportOriginalDDL(){
		//TEIIDDES-2827
		new ModelExplorer().getModelProject(PROJECT_NAME).open();
		new DefaultTreeItem(PROJECT_NAME,NAME_VIEW_ORIGINAL_MODEL + ".xmi").select();
		
		DDLExportWizard export = new DDLExportWizard(DDLExportWizard.TEIID_WIZARD);		
		export.setPathToModel(PROJECT_NAME,NAME_VIEW_ORIGINAL_MODEL + ".xmi");
		export.setExportName("originalDDL");
		export.setExportLocation(PROJECT_NAME);
		export.execute();
		
		new ModelExplorer().getModelProject(PROJECT_NAME).open();
		new DefaultTreeItem(PROJECT_NAME,"originalDDL").doubleClick();
		
		String generetedText = new DefaultStyledText().getText();
		generetedText=generetedText.replaceAll("\r|\n|\t", "");
		
		String expectedText = null;
		try {
			expectedText = convertFileToString(new File(PATH_TO_ORIGINAL_DDL));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		expectedText=expectedText.replaceAll("\r|\n|\t", "");
		assertTrue(expectedText.equals(generetedText));
	}
	
	private String convertFileToString(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append('\n');
		}
		reader.close();
		return stringBuilder.toString();
	}
}
