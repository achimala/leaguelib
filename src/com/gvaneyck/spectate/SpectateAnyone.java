package com.gvaneyck.spectate;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.gvaneyck.rtmp.Callback;
import com.gvaneyck.rtmp.LoLRTMPSClient;
import com.gvaneyck.rtmp.TypedObject;
import com.gvaneyck.util.ConsoleWindow;


/**
 * A simple program that allows a player to spectate anyone playing a game
 * (rather than just people on your friends list) 
 * 
 * @author Gabriel Van Eyck
 */
public class SpectateAnyone
{
	public static final boolean debug = false;
	
	public static final JFrame f = new JFrame("Spectate Anyone!");
	
	public static final Label lblName = new Label("Name:");
	public static final JTextField txtName = new JTextField();
	public static final JButton btnName = new JButton();

	public static final JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);

	public static final Label lblFile = new Label("Load List:");
	public static final JTextField txtFile = new JTextField();
	public static final JButton btnFile = new JButton("Check");

	public static final GameListModel lstModel = new GameListModel();
	public static final JList lstInGame = new JList(lstModel);
	public static final JScrollPane lstScroll = new JScrollPane(lstInGame);

	public static final JFileChooser fc = new JFileChooser(new File("."));
	
	public static int width = 350;
	public static int height = 250;
	
	public static LoLRTMPSClient client;
	public static Map<String, String> params;
	
	public static Map<String, String> regionMap;

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
		lblName.setAlignment(Label.RIGHT);
		lblFile.setAlignment(Label.RIGHT);
		lstInGame.setLayoutOrientation(JList.VERTICAL_WRAP);
		lstInGame.setVisibleRowCount(-1);
		lstInGame.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Initially grey out buttons
		btnName.setText("Connecting...");
		btnName.setEnabled(false);
		btnFile.setEnabled(false);
		
		// Add the items
		Container pane = f.getContentPane();
		pane.setLayout(null);

		pane.add(lblName);
		pane.add(txtName);
		pane.add(btnName);
		
		pane.add(sep);
		
		pane.add(lblFile);
		pane.add(txtFile);
		pane.add(btnFile);
		
		pane.add(lstScroll);

		// Layout everything
		doLayout();
		
		// Listeners
		txtName.addKeyListener(new KeyListener()
				{
					public void keyTyped(KeyEvent e) { }
					public void keyPressed(KeyEvent e) { }
	
					public void keyReleased(KeyEvent e)
					{
						if (e.getKeyCode() == KeyEvent.VK_ENTER && btnName.isEnabled())
							handleSpectate();
					}
				});
		
		btnName.addMouseListener(new MouseListener()
				{
					public void mousePressed(MouseEvent e) { }
					public void mouseReleased(MouseEvent e) { }
					public void mouseEntered(MouseEvent e) { }
					public void mouseExited(MouseEvent e) { }
					
					public void mouseClicked(MouseEvent e)
					{
						handleSpectate();
					}
				});
		
		btnName.addKeyListener(new KeyListener()
				{
					public void keyTyped(KeyEvent e) { }
					public void keyPressed(KeyEvent e) { }
		
					public void keyReleased(KeyEvent e)
					{
						if (e.getKeyCode() == KeyEvent.VK_SPACE)
							handleSpectate();
					}
				});
		
		txtFile.addFocusListener(new FocusListener()
				{
					public void focusLost(FocusEvent e) { }

					public void focusGained(FocusEvent e)
					{
						setFile();
					}
				});

		btnFile.addMouseListener(new MouseListener()
				{
					public void mousePressed(MouseEvent e) { }
					public void mouseReleased(MouseEvent e) { }
					public void mouseEntered(MouseEvent e) { }
					public void mouseExited(MouseEvent e) { }
					
					public void mouseClicked(MouseEvent e)
					{
						loadFile();
					}
				});
		
		btnFile.addKeyListener(new KeyListener()
				{
					public void keyTyped(KeyEvent e) { }
					public void keyPressed(KeyEvent e) { }
		
					public void keyReleased(KeyEvent e)
					{
						if (e.getKeyCode() == KeyEvent.VK_SPACE)
							loadFile();
					}
		});

		lstInGame.addMouseListener(new MouseListener()
				{
					public void mousePressed(MouseEvent e) { }
					public void mouseReleased(MouseEvent e) { }
					public void mouseEntered(MouseEvent e) { }
					public void mouseExited(MouseEvent e) { }
		
					public void mouseClicked(MouseEvent e)
					{
						if (e.getClickCount() == 2)
							handleSpectate();
					}
				});

		lstInGame.addKeyListener(new KeyListener()
				{
					public void keyTyped(KeyEvent e) { }
					public void keyPressed(KeyEvent e) { }
		
					public void keyReleased(KeyEvent e)
					{
						if (e.getKeyCode() == KeyEvent.VK_ENTER)
							handleSpectate();
					}
				});
		
		lstInGame.addListSelectionListener(new ListSelectionListener()
				{
					public void valueChanged(ListSelectionEvent e)
					{
						GameInfo game = (GameInfo)lstInGame.getSelectedValue();
						if (game == null)
							return;
						
						txtName.setText(game.targetSummoner);
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
		if (!params.containsKey("lollocation"))
		{
			String res = (String)JOptionPane.showInputDialog(
					f,
                    "Enter your LoL installation location\nE.g. C:\\Riot Games\\League of Legends\\",
                    "Login Information",
                    JOptionPane.QUESTION_MESSAGE);
			
			// Fix location if necessary
			res.replace("/", File.separator);
			res.replace("\\", File.separator);
			if (!res.endsWith(File.separator))
				res = res + File.separator;

			params.put("lollocation", res);
			newinfo = true;
		}

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

		if (!params.containsKey("locale"))
		{
			// Let the user select their language
			Set<String> langCodes = findValidLangs(new File(params.get("lollocation")));
			List<String> langs = new ArrayList<String>();
			for (String lang : langCodes)
			{
				String[] parts = lang.split("_");
				Locale l = new Locale(parts[0], parts[1]);
				langs.add(lang + " (" + l.getDisplayLanguage() + ")");
			}
			Collections.sort(langs);
			String[] langNames = new String[langs.size()];
			for (int i = 0; i < langs.size(); i++)
				langNames[i] = langs.get(i);
			
			String res = (String)JOptionPane.showInputDialog(
					f,
	                "Select a language",
	                "Login Information",
	                JOptionPane.QUESTION_MESSAGE,
	                null,
	                langNames,
	                null);
			
			params.put("locale", res.substring(0, 5));
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
				out.write("lollocation=" + params.get("lollocation") + "\r\n");
				out.write("user=" + params.get("user") + "\r\n");
				//out.write("pass=" + params.get("pass") + "\r\n"); // Don't save password by default
				out.write("version=" + params.get("version") + "\r\n");
				out.write("region=" + params.get("region") + "\r\n");
				out.write("locale=" + params.get("locale") + "\r\n");
				out.close();
			}
			catch (IOException e)
			{
				System.out.println("Encountered an error when creating config.txt:");
				e.printStackTrace();
			}
		}
		
		// Set the region used for the game executable
		params.put("region", regionMap.get(params.get("region").toUpperCase()));
		String region = params.get("region");
		if (region.equals("NA"))
			params.put("region2", "NA1");
		else if (region.equals("EUW"))
			params.put("region2", "EUW1");
		else if (region.equals("EUN"))
			params.put("region2", "EUN1");
		else if (region.equals("KR"))
			params.put("region2", "KR");
		else if (region.equals("BR"))
			params.put("region2", "BR1");
		else if (region.equals("PBE"))
			params.put("region2", "PBE1");
		
		// Figure out the path to executables
		// I'm not sure what the difference is, but using the executable in lol_game_client (rather than lol_game_client_sln)
		// gives the missing language file error
		System.out.println("Finding most recent installation...");
		File base = new File(params.get("lollocation"));
		File lol = findMostRecent(base, "League of Legends.exe", "lol_game_client");
		File air = findMostRecent(base, "LolClient.exe");
		
		if (lol == null || air == null)
		{
			JOptionPane.showMessageDialog(
					f,
					"The installation location does not appear to be valid, make sure lollocation is correct in config.txt.",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		// Save path information
		String lolPath = lol.getAbsolutePath();
		params.put("gameDir", lolPath.substring(0, lolPath.lastIndexOf(File.separator)));
		params.put("airExe", air.getAbsolutePath());		
		
		// Connect
		client = new LoLRTMPSClient(params.get("region"), params.get("version"), params.get("user"), params.get("pass"));
		client.setLocale(params.get("locale"));
		client.debug = debug;
		client.reconnect();
		
		// Enable the buttons
		btnName.setText("Spectate!");
		btnName.setEnabled(true);
		btnFile.setEnabled(true);
	}
	
	public static Set<String> findValidLangs(File base)
	{
		Set<String> result = new HashSet<String>();
		if (base.isFile())
		{
			String name = base.getName();
			if (name.startsWith("fontconfig_"))
				result.add(name.substring(11, 16));
			return result;
		}
		
		File[] contents = base.listFiles();
		for (File f : contents)
			result.addAll(findValidLangs(f));
		
		return result;
	}
	
	public static File findMostRecent(File base, String name)
	{
		return findMostRecent(base, name, null);
	}
	
	public static File findMostRecent(File base, String name, String ignore)
	{
		if (base.isFile())
		{
			if (base.getName().matches(name))
				return base;
			else
				return null;
		}
		
		if (base.getName().equals(ignore))
			return null;
		
		File[] contents = base.listFiles();
		File mostRecent = null;
		for (File f : contents)
		{
			File result = findMostRecent(f, name, ignore);
			if (result == null)
				continue;
			
			if (mostRecent == null)
				mostRecent = result;
			else if (mostRecent.lastModified() < result.lastModified())
				mostRecent = result;
		}
		
		return mostRecent;
	}

	public static void doLayout()
	{
		Insets i = f.getInsets();
		int twidth = width - i.left - i.right;
		int theight = height - i.top - i.bottom;

		int lblWidth = 60;
		int btnWidth = 125;
		int txtWidth = twidth - lblWidth - btnWidth - 10;
		
		lblName.setBounds(5, 5, lblWidth, 25);
		txtName.setBounds(5 + lblWidth, 5, txtWidth, 25);
		btnName.setBounds(twidth - btnWidth - 5, 5, btnWidth, 24);
		
		sep.setBounds(5, 35, twidth - 10, 5);
		
		lblFile.setBounds(5, 42, lblWidth, 25);
		txtFile.setBounds(5 + lblWidth, 42, txtWidth, 25);
		btnFile.setBounds(twidth - btnWidth - 5, 42, btnWidth, 24);
		
		lstScroll.setBounds(5, 72, twidth - 9, theight - 77);
	}
	
	public static synchronized void handleSpectate()
	{
		final String toSpec = txtName.getText();
		if (toSpec.length() == 0)
			return;
		
		try
		{
			// Get spectator info
			int id = client.invoke("gameService", "retrieveInProgressSpectatorGameInfo", new Object[] { toSpec });
			TypedObject result = client.getResult(id);
			TypedObject data = result.getTO("data");

			// Handle errors
			if (result.get("result").equals("_error"))
			{
				TypedObject rootCause = data.getTO("rootCause");
				if (rootCause.type.equals("com.riotgames.platform.messaging.UnexpectedServiceException"))
				{
					JOptionPane.showMessageDialog(
							f,
							"No summoner found for " + toSpec + ".\nOr, did you not set up a summoner name for this account?",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				}
				else if (rootCause.type.equals("com.riotgames.platform.game.GameNotFoundException"))
				{
					JOptionPane.showMessageDialog(
							f,
							toSpec + " is not currently in a game.",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				}
				else if (rootCause.get("localizedMessage").equals("An Authentication object was not found in the SecurityContext"))
				{
					JOptionPane.showMessageDialog(
							f,
							"This account has been logged in somewhere else.\nRestart this program to log in again.",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					JOptionPane.showMessageDialog(
							f,
							"Encountered an error when retrieving spectator information for " + toSpec + ":\n" + rootCause.get("localizedMessage"),
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				// Extract needed info
				TypedObject cred = data.getTO("body").getTO("playerCredentials");
				final String ip = cred.getString("observerServerIp");
				final int port = cred.getInt("observerServerPort");
				final String key = cred.getString("observerEncryptionKey");
				final int gameID = cred.getInt("gameId");
				
				// Check delay time first
				final int delay = data.getTO("body").getInt("reconnectDelay") - 5;
				
				// If there's more than a 5s wait, create a thread to automatically launch
				if (delay > 0)
				{
					Thread t = new Thread()
							{
								public void run()
								{
									// Delay until 5s left or text changes
									int wait = delay;
									lblName.requestFocusInWindow(); // Force the focus away so txtFile isn't accidentally selected
									btnName.setEnabled(false);
									while (toSpec.equals(txtName.getText()) && wait > 0)
									{
										btnName.setText("Delaying (" + wait + "s)");
										for (int i = 0; i < 10; i++)
										{
											if (!toSpec.equals(txtName.getText()))
												break;
											
											try { Thread.sleep(100); } catch (InterruptedException e) { }
										}
										wait--;
									}
									
									// Reset button state
									btnName.setEnabled(true);
									btnName.setText("Spectate!");
									
									// Spectate if text didn't change
									if (toSpec.equals(txtName.getText()))
										doSpectate(ip, port, key, gameID);
								}
							};
					t.setName("SpectateAnyone (SpectateDelay)");
					t.start();
				}
				// Otherwise, just spectate
				else
					doSpectate(ip, port, key, gameID);
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(
					f,
					"Encountered an error when trying to retrieve spectate information for " + toSpec + " (check console)\n" + e.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public static void doSpectate(String ip, int port, String key, int gameID) 
	{
		// Set up the command line
		File dir = new File(params.get("gameDir"));
		String[] cmd = new String[] {
				params.get("gameDir") + File.separator + "League of Legends.exe",
				"8394",
				"LoLLauncher.exe",
				params.get("airExe"),
				"spectator " + ip + ":" + port + " " + key + " " + gameID + " " + params.get("region2")
			};
		
		try
		{
			// Run (and make sure to consume output)
			Process game = Runtime.getRuntime().exec(cmd, null, dir);
			new StreamGobbler(game.getInputStream());
			new StreamGobbler(game.getErrorStream());
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(
					f,
					"Encountered an error when trying to launch spectator mode\n" + e.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);

			System.err.println("LoL location = " + params.get("lollocation"));
			for (String s : cmd)
				System.out.println("Args = " + s);
			e.printStackTrace();
		}
	}
	
	public static void setFile()
	{
		// Change focus first so no infinite loop
		btnFile.requestFocusInWindow();
		
		int returnVal = fc.showOpenDialog(f);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;
		
		File f = fc.getSelectedFile();

		txtFile.setText(f.getAbsolutePath());
	}
	
	public static synchronized void loadFile()
	{
		if (!client.isLoggedIn())
			return;
		
		String filename = txtFile.getText();
		if (filename == null)
			return;
		
		File file = new File(filename);
		if (!file.exists() || !file.isFile())
			return;

		// Clear old list
		lstModel.clear();
		
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null)
			{
				line = line.trim();
				line = line.replace("\"", "");
				
				if (line.length() == 0)
					continue;
				
				if (line.startsWith("#"))
					continue;
				
				// Handle notepad saving extra bytes for UTF8
				if (line.charAt(0) == 65279)
					line = line.substring(1);
				
				// Invoke asynchronously
				final String name = line;
				client.invokeWithCallback("gameService", "retrieveInProgressSpectatorGameInfo", new Object[] { name },
						new Callback()
						{
							public void callback(TypedObject result)
							{
								if (result.get("result").equals("_result"))
									lstModel.add(new GameInfo(name, result));
							}
						});
			}
			in.close();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(
					f,
					"Error reading from " + filename + ":\n" + e.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		
		// Wait for all requests to finish;
		client.join();
		
		// Update the list
		lstModel.update();
	}
}
