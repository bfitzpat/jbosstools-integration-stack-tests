package org.jboss.tools.teiid.reddeer.view;

import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.eclipse.wst.server.ui.view.Server;
import org.jboss.reddeer.eclipse.wst.server.ui.view.ServersView;
import org.jboss.reddeer.eclipse.wst.server.ui.view.ServersViewEnums.ServerState;
import org.jboss.reddeer.eclipse.wst.server.ui.view.ServersViewException;
import org.jboss.reddeer.swt.exception.SWTLayerException;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.impl.view.WorkbenchView;
import org.jboss.tools.teiid.reddeer.condition.ServerHasState;

/**
 * Represents a view where one can manage Teiid instances.
 * 
 * @author Lucia Jelinkova
 * 
 */
public class TeiidInstanceView extends WorkbenchView {// DEPRECATED - JBDS7 only Servers view
	// TODO - merge with serversViewExt
	public static final String TOOLBAR_CREATE_TEIID = "Create a new Teiid instance";
	public static final String TOOLBAR_RECONNECT_TEIID = "Reconnect to the selected Teiid Instance";

	/**
	 * Previous to Kepler
	 */
	/*
	 * @Deprecated public TeiidInstanceView() { super("Teiid Designer", "Teiid"); }
	 */

	public TeiidInstanceView(boolean isKelperOrMore) {// remove
		super("Servers");
	}

	/*
	 * public void reconnect(String teiidInstance) {//--> servermgr throw new UnsupportedOperationException(); //
	 * bot().tree().getTreeItem(teiidInstance).select(); // getToolbarButtonWitTooltip(TOOLBAR_RECONNECT_TEIID).click();
	 * }
	 */

	/*
	 * public void deleteDataSource(String teiidInstance, String dataSource) {// new DefaultTreeItem(teiidInstance,
	 * "Data Sources", dataSource).select(); new ContextMenu("Delete Data Source").select(); }
	 */

	/*
	 * public void undeployVDB(String teiidInstance, String vdb) {//--> VDB new DefaultTreeItem(teiidInstance, "VDBs",
	 * vdb).select(); new ContextMenu("Undeploy VDB").select(); }
	 * 
	 * public boolean containsDataSource(String teiidInstance, String datasource) { SWTBot bot = new SWTWorkbenchBot();
	 * try { SWTBotTreeItem item = bot.tree().expandNode(teiidInstance, "Data Sources"); item.getNode(datasource);
	 * return true; } catch (WidgetNotFoundException e) { return false; } }
	 */

	/**
	 * Previous to Kepler
	 * 
	 * @param teiidInstance
	 * @param vdb
	 * @return
	 */
	/*
	 * @Deprecated public boolean containsVDB(String teiidInstance, String vdb) { SWTBot bot = new SWTWorkbenchBot();
	 * try { SWTBotTreeItem item = bot.tree().expandNode(teiidInstance, "VDBs"); item.getNode(vdb); return true; //}
	 * catch (WidgetNotFoundException e) { } catch (SWTLayerException ex){ return false; } }
	 */

	/**
	 * 
	 * @param isKeplerOrMore
	 * @param pathToVDB
	 *            path to VDB (the last element is name of VDB)
	 * @return
	 */
	public boolean containsVDB(boolean isKeplerOrMore, String... pathToVDB) {// -->move to servermgr

		// general
		try {
			// SWTBotTreeItem item = bot.tree(TEIID_INSTANCE_TREE_INDEX).expandNode(pathToVDB);new
			// ContextMenu("Refresh");OK; new TeiidInstanceView(true);expand;getNode;
			new DefaultTreeItem(pathToVDB).select();
			return true;
		} catch (SWTLayerException ex) {

		} catch (Exception e) {

		}
		// add [default] (in case of more servers)
		try {
			String[] newPath = { pathToVDB[0], pathToVDB[1], pathToVDB[2] + "  [default]", pathToVDB[3], pathToVDB[4] };
			new DefaultTreeItem(newPath).select();
			return true;
		} catch (Exception ex) {

		}
		// remove [default] (in case of one server)
		try {
			String[] newPath = {
				pathToVDB[0],
				pathToVDB[1],
				pathToVDB[2].substring(0, pathToVDB[2].indexOf(" ")),
				pathToVDB[3],
				pathToVDB[4] };
			new DefaultTreeItem(newPath).select();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Previous to Kepler
	 * 
	 * @param name
	 * @return
	 */
	/*
	 * @Deprecated public boolean containsTeiidInstance(String name) { SWTBot bot = new SWTWorkbenchBot(); try {
	 * bot.tree().getTreeItem(name); return true; } catch (WidgetNotFoundException e) { return false; } }
	 */

	public void setDefaultTeiidInstance(String teiidInstanceUrl) {// cp to servermgr
		new GuidesView().chooseAction("Teiid", "Set the Default JBoss / Teiid Instance");

		new DefaultShell("Server Selection");
		new DefaultCombo().setSelection(teiidInstanceUrl);
		new PushButton("OK").click();

		try {
			new DefaultShell("Change of Teiid Version");
			new PushButton("Yes").click();// save all opened editors
		} catch (Exception ex) {
			// dialog doesn't apper if restart of server precedes setup of default teiid instance
			System.err.println(ex.getCause() + "," + ex.getMessage());
		}

		try {
			new DefaultShell("Default Server Changed");
			new PushButton("OK").click();// confirm change of default server
		} catch (Exception ex) {
			// not shown in some cases
			System.err.println(ex.getCause() + "," + ex.getMessage());
		}
	}

	public void startServer(String serverName) {// --copy to servermgr for servers view
		Server server = new ServersView().getServer(serverName);
		server.start();
		new WaitUntil(new ServerHasState(serverName), TimePeriod.LONG);
	}

	public void stopServer(String serverName) {// --> server mgr
		Server server = new ServersView().getServer(serverName);
		log.info("Stopping server " + server.getLabel().getName());
		ServerState state = server.getLabel().getState();
		if (!ServerState.STARTING.equals(state) && !state.isRunningState()) {
			throw new ServersViewException("Cannot stop server because it not running");
		}
		try {
			new DefaultTreeItem(serverName + "  [Started, Synchronized]").select();
		} catch (Exception ex) {
			new DefaultTreeItem(serverName + "  [Started]").select();
		}

		new ContextMenu("Stop").select();
		AbstractWait.sleep(TimePeriod.VERY_LONG);
		// new WaitUntil(new ServerHasState(serverName), TimePeriod.LONG);
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}

}
