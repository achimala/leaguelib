package com.gvaneyck.runesorter;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.gvaneyck.rtmp.LoLRTMPSClient;
import com.gvaneyck.rtmp.TypedObject;
import com.gvaneyck.util.ConsoleWindow;


public class RunePageSorter {

	public static final JFrame f = new JFrame("Rune/Mastery Page Sorter");
	
	public static final JButton btnSort = new JButton("Sort Alphabetically");
	public static final JButton btnMoveUp = new JButton("Move Up");
	public static final JButton btnMoveDown = new JButton("Move Down");

	public static final JLabel lblRunePages = new JLabel("Rune Pages");
	public static final RunePageListModel lstModel = new RunePageListModel();
	public static final JList lstRunePages = new JList(lstModel);
	public static final JScrollPane lstScroll = new JScrollPane(lstRunePages);
	
	public static final JLabel lblMasteryPages = new JLabel("Mastery Pages");
	public static final MasteryPageListModel lstModel2 = new MasteryPageListModel();
	public static final JList lstMasteryPages = new JList(lstModel2);
	public static final JScrollPane lstScroll2 = new JScrollPane(lstMasteryPages);
	
	public static final JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);

	public static int width = 250;
	public static int height = 320;
	
	public static LoLRTMPSClient client;
	public static Map<String, String> params;
	
	public static Map<String, String> regionMap;
	
	public static List<RunePage> pages = new ArrayList<RunePage>();
	public static int lastSelectedPage = -1;
	public static List<MasteryPage> masteries = new ArrayList<MasteryPage>();
	public static int lastSelectedMasteries = -1;
	public static boolean changingSelection = false;
	
	public static int acctId = 0;
	public static int summId = 0;

	public static void main(String[] args)
	{
		new ConsoleWindow(width, 0);
		
		initRegionMap();
		setupFrame();

		setupClient();
	}
	
	public static void initRegionMap()
	{
		regionMap = new HashMap<String, String>();
		regionMap.put("NORTH AMERICA", "NA");
		regionMap.put("EUROPE WEST", "EUW");
		regionMap.put("EUROPE NORDIC & EAST", "EUN");
		regionMap.put("KOREA", "KR");
		regionMap.put("BRAZIL", "BR");
		regionMap.put("TURKEY", "TR");
		regionMap.put("PUBLIC BETA ENVIRONMENT", "PBE");
		regionMap.put("SINGAPORE/MALAYSIA", "SG");
		regionMap.put("TAIWAN", "TW");
		regionMap.put("THAILAND", "TH");
		regionMap.put("PHILLIPINES", "PH");
		regionMap.put("VIETNAM", "VN");
	}
	
	public static void setupFrame()
	{
		// GUI settings
		lstRunePages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Initially grey out buttons
		btnSort.setEnabled(false);
		btnMoveUp.setEnabled(false);
		btnMoveDown.setEnabled(false);
		
		// Add the items
		Container pane = f.getContentPane();
		pane.setLayout(null);

		pane.add(btnSort);
		pane.add(btnMoveUp);
		pane.add(btnMoveDown);
		
		pane.add(sep);
		
		pane.add(lblRunePages);
		pane.add(lstScroll);
		pane.add(lblMasteryPages);
		pane.add(lstScroll2);

		// Layout everything
		doLayout();
		
		// Listeners
		btnSort.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (lastSelectedPage != -1)
						{
							List<Integer> pageIds = new ArrayList<Integer>();
							for (RunePage page : pages)
								pageIds.add(page.pageId);
							
							Collections.sort(pages, new Comparator<RunePage>()
									{
										public int compare(RunePage page1, RunePage page2) {
											return page1.name.compareTo(page2.name);
										}
									});
							
							for (int i = 0; i < pages.size(); i++)
								pages.get(i).pageId = pageIds.get(i);
							
							savePages();
						}
						if (lastSelectedMasteries != -1)
						{
							List<Integer> pageIds = new ArrayList<Integer>();
							for (MasteryPage page : masteries)
								pageIds.add(page.pageId);
							
							Collections.sort(masteries, new Comparator<MasteryPage>()
									{
										public int compare(MasteryPage page1, MasteryPage page2) {
											return page1.name.compareTo(page2.name);
										}
									});
							
							for (int i = 0; i < masteries.size(); i++)
								masteries.get(i).pageId = pageIds.get(i);
							
							saveMasteries();
						}
					}
				});

		btnMoveUp.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (lastSelectedPage != -1 && lastSelectedPage != 0)
						{
							RunePage current = pages.get(lastSelectedPage); 
							current.swap(pages.get(lastSelectedPage - 1));
	
							lastSelectedPage--;
							lstRunePages.setSelectedIndex(lastSelectedPage);
	
							savePages();
						}
						if (lastSelectedMasteries != -1 && lastSelectedMasteries != 0)
						{
							MasteryPage current = masteries.get(lastSelectedMasteries); 
							current.swap(masteries.get(lastSelectedMasteries - 1));
	
							lastSelectedMasteries--;
							lstMasteryPages.setSelectedIndex(lastSelectedMasteries);
	
							saveMasteries();
						}
					}
				});

		btnMoveDown.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (lastSelectedPage != -1 && lastSelectedPage != pages.size() - 1)
						{
							RunePage current = pages.get(lastSelectedPage); 
							current.swap(pages.get(lastSelectedPage + 1));
							
							lastSelectedPage++;
							lstRunePages.setSelectedIndex(lastSelectedPage);
	
							savePages();
						}
						if (lastSelectedMasteries != -1 && lastSelectedMasteries != masteries.size() - 1)
						{
							MasteryPage current = masteries.get(lastSelectedMasteries); 
							current.swap(masteries.get(lastSelectedMasteries + 1));
	
							lastSelectedMasteries++;
							lstMasteryPages.setSelectedIndex(lastSelectedMasteries);
	
							saveMasteries();
						}
					}
				});
		
		lstRunePages.addListSelectionListener(new ListSelectionListener()
				{
					public void valueChanged(ListSelectionEvent e)
					{
						if (changingSelection)
							return;
						
						changingSelection = true;
						lastSelectedPage = lstRunePages.getSelectedIndex();
						lstMasteryPages.clearSelection();
						lastSelectedMasteries = -1;
						changingSelection = false;
					}
				});
		
		lstMasteryPages.addListSelectionListener(new ListSelectionListener()
				{
					public void valueChanged(ListSelectionEvent e)
					{
						if (changingSelection)
							return;
						
						changingSelection = true;
						lastSelectedMasteries = lstMasteryPages.getSelectedIndex();
						lastSelectedPage = -1;
						lstRunePages.clearSelection();
						changingSelection = false;
					}
				});

		pane.addHierarchyBoundsListener(new HierarchyBoundsListener()
				{
					public void ancestorMoved(HierarchyEvent e) { }
		
					public void ancestorResized(HierarchyEvent e)
					{
						Dimension d = f.getSize();
						width = d.width;
						height = d.height;
						doLayout();
					}
				});

		f.addWindowListener(new WindowListener()
				{
					public void windowOpened(WindowEvent e) { }
					public void windowClosing(WindowEvent e) { }
					public void windowIconified(WindowEvent e) { }
					public void windowDeiconified(WindowEvent e) { }
					public void windowActivated(WindowEvent e) { }
					public void windowDeactivated(WindowEvent e) { }

					public void windowClosed(WindowEvent e)
					{
						client.close();
					}
				});
		
		// Window settings
		f.setSize(width, height);
		f.setMinimumSize(new Dimension(width, height));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public static void setupClient()
	{
		// Read in the config
		File conf = new File("config.txt");
		params = new HashMap<String, String>();
		
		// Parse if exists
		if (conf.exists())
		{
			try
			{
				String line;
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(conf), "UTF-8"));
				while ((line = in.readLine()) != null)
				{
					String[] parts = line.split("=");
					if (parts.length != 2)
						continue;

					// Handle notepad saving extra bytes for UTF8
					if (parts[0].charAt(0) == 65279)
						parts[0] = parts[0].substring(1);
					
					params.put(parts[0].trim(), parts[1].trim());
				}
				in.close();
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(
						f,
						"Encountered an error when parsing config.txt",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			}
		}

		// Get missing information
		boolean newinfo = false;

		if (!params.containsKey("region") || !regionMap.containsKey(params.get("region").toUpperCase()))
		{
			String res = (String)JOptionPane.showInputDialog(
					f,
                    "Select a region",
                    "Login Information",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[] {
							"North America",
							"Europe West",
							"Europe Nordic & East",
							"Korea",
							"Brazil",
							"Turkey",
							"Public Beta Environment",
							"Singapore/Malaysia",
							"Taiwan",
							"Thailand",
							"Phillipines",
							"Vietnam" },
                    "North America");
			
			params.put("region", res);
			newinfo = true;
		}
		
		if (!params.containsKey("version"))
		{
			String res = (String)JOptionPane.showInputDialog(
					f,
                    "Enter the Client Version for " + params.get("region") + "\nClient version can be found at the top left of the real client",
                    "Login Information",
                    JOptionPane.QUESTION_MESSAGE);

			params.put("version", res);
			newinfo = true;
		}

		if (!params.containsKey("user"))
		{
			String res = (String)JOptionPane.showInputDialog(
					f,
                    "Enter your login name for " + params.get("region"),
                    "Login Information",
                    JOptionPane.QUESTION_MESSAGE);

			params.put("user", res);
			newinfo = true;
		}

		if (!params.containsKey("pass"))
		{
			String res = (String)JOptionPane.showInputDialog(
					f,
                    "Enter the password for '" + params.get("user") + "'",
                    "Login Information",
                    JOptionPane.QUESTION_MESSAGE);

			params.put("pass", res);
		}
		
		// Set up config.txt if needed
		if (newinfo)
		{
			try
			{
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(conf), "UTF-8"));
				out.write("user=" + params.get("user") + "\r\n");
				//out.write("pass=" + params.get("pass") + "\r\n"); // Don't save password by default
				out.write("version=" + params.get("version") + "\r\n");
				out.write("region=" + params.get("region") + "\r\n");
				out.close();
			}
			catch (IOException e)
			{
				System.out.println("Encountered an error when creating config.txt:");
				e.printStackTrace();
			}
		}
		
		// Set the region code used by LoLRTMPSClient
		params.put("region", regionMap.get(params.get("region").toUpperCase()));
		
		// Connect
		client = new LoLRTMPSClient(params.get("region"), params.get("version"), params.get("user"), params.get("pass"));
		client.setLocale(params.get("locale"));
		client.reconnect();
		
		initPages();
	}
	
	public static void initPages()
	{
		try
		{
			// Get the account ID
			int id = client.invoke("clientFacadeService", "getLoginDataPacketForUser", new Object[] {});
			TypedObject summoner = client.getResult(id).getTO("data").getTO("body").getTO("allSummonerData").getTO("summoner");
			acctId = summoner.getInt("acctId");
			summId = summoner.getInt("sumId");
			
			// Get our pages
			id = client.invoke("summonerService", "getAllSummonerDataByAccount", new Object[] { acctId });
			TypedObject body = client.getResult(id).getTO("data").getTO("body");
			
			Object[] spellBookPages = body.getTO("spellBook").getArray("bookPages");
			for (Object o : spellBookPages)
				pages.add(new RunePage((TypedObject)o));
			Collections.sort(pages);
			updatePages();
			
			Object[] masteryPages = body.getTO("masteryBook").getArray("bookPages");
			for (Object o : masteryPages)
				masteries.add(new MasteryPage((TypedObject)o));
			Collections.sort(masteries);
			updateMasteries();

			// Enable the buttons
			btnSort.setEnabled(true);
			btnMoveUp.setEnabled(true);
			btnMoveDown.setEnabled(true);
			
		}
		catch (IOException e)
		{
			client.close();
			
			System.out.println("Failed to get account information:");
			e.printStackTrace();
			System.out.println();
			System.out.println("Restart the program to try again.");
		}
	}
	
	public static void updatePages()
	{
		lstModel.clear();
		for (RunePage page : pages)
			lstModel.add(page);
		lstModel.update();
	}
	
	public static void savePages()
	{
		try
		{
			TypedObject[] pages2 = new TypedObject[pages.size()];
			TypedObject currentPage = null;
			for (int i = 0; i < pages.size(); i++)
			{
				pages2[i] = pages.get(i).getSavePage(summId);
				if (pages.get(i).current)
					currentPage = pages2[i];
			}
			
			TypedObject args = new TypedObject("com.riotgames.platform.summoner.spellbook.SpellBookDTO");
			args.put("bookPages", TypedObject.makeArrayCollection(pages2));
			
			if (currentPage != null)
				args.put("defaultPage", currentPage);
			
			TypedObject sort = new TypedObject();
			sort.put("unique", false);
			sort.put("compareFunction", null);
			TypedObject fields = new TypedObject();
			fields.put("caseInsensitive", false);
			fields.put("name", "pageId");
			fields.put("numeric", null);
			fields.put("compareFunction", null);
			fields.put("descending", false);
			sort.put("fields", new Object[] { fields });
			args.put("sortByPageId", sort);
			
			args.put("summonerId", summId);
			args.put("dateString", null);
			args.put("futureData", null);
			args.put("dataVersion", null);
			
			int id = client.invoke("spellBookService", "saveSpellBook", new Object[] { args });
			TypedObject result = client.getResult(id);
			if (result.get("result").equals("_error"))
				System.out.println("Error changing rune page order");
			
			updatePages();
		}
		catch (IOException e)
		{
			client.close();
			
			System.out.println("Failed to update Rune Pages:");
			e.printStackTrace();
			System.out.println();
			System.out.println("Restart the program to try again.");
		}
	}
	
	public static void updateMasteries()
	{
		lstModel2.clear();
		for (MasteryPage page : masteries)
			lstModel2.add(page);
		lstModel2.update();
	}
	
	public static void saveMasteries()
	{
		try
		{
			TypedObject[] masteries2 = new TypedObject[pages.size()];
			for (int i = 0; i < masteries.size(); i++)
				masteries2[i] = masteries.get(i).getSavePage(summId);
			
			TypedObject args = new TypedObject("com.riotgames.platform.summoner.masterybook.MasteryBookDTO");
			args.put("bookPages", TypedObject.makeArrayCollection(masteries2));
			
			TypedObject sort = new TypedObject();
			sort.put("unique", false);
			sort.put("compareFunction", null);
			TypedObject fields = new TypedObject();
			fields.put("caseInsensitive", false);
			fields.put("name", "pageId");
			fields.put("numeric", null);
			fields.put("compareFunction", null);
			fields.put("descending", false);
			sort.put("fields", new Object[] { fields });
			args.put("sortByPageId", sort);
			
			args.put("summonerId", summId);
			args.put("dateString", null);
			args.put("futureData", null);
			args.put("dataVersion", null);
			
			int id = client.invoke("masteryBookService", "saveMasteryBook", new Object[] { args });
			TypedObject result = client.getResult(id);
			if (result.get("result").equals("_error"))
				System.out.println(result + "\nError changing mastery page order");
			
			updateMasteries();
		}
		catch (IOException e)
		{
			client.close();
			
			System.out.println("Failed to update Mastery Pages:");
			e.printStackTrace();
			System.out.println();
			System.out.println("Restart the program to try again.");
		}
	}
	
	public static void doLayout()
	{
		Insets i = f.getInsets();
		int twidth = width - i.left - i.right;
		int theight = height - i.top - i.bottom;
		
		btnSort.setBounds(5, 5, twidth - 10, 24);
		btnMoveUp.setBounds(5, 34, twidth / 2 - 10, 24);
		btnMoveDown.setBounds(5 + twidth / 2, 34, twidth / 2 - 10, 24);
		
		sep.setBounds(5, 63, twidth - 10, 5);
		
		int scrollHeight = theight / 2 - 50;
		lblRunePages.setBounds(5, 65, twidth - 9, 15);
		lstScroll.setBounds(5, 80, twidth - 9, scrollHeight);
		lblMasteryPages.setBounds(5, 80 + scrollHeight, twidth - 9, 15);
		lstScroll2.setBounds(5, 95 + scrollHeight, twidth - 9, scrollHeight);
	}
}
