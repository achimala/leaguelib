package com.gvaneyck.util;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ConsoleWindow
{	
	StringBuffer data = new StringBuffer();
	StringOutputStream sos = new StringOutputStream(data);
	
	private int width = 480;
	private int height = 320;
	
	private static final JFrame f = new JFrame("Console");
	private static final JTextArea txtConsole = new JTextArea();
	private static final JScrollPane scrollArea = new JScrollPane(txtConsole);
	
	public static void main(String[] args)
	{
		new ConsoleWindow();
	}

	public ConsoleWindow()
	{
		this(0, 0);
	}
	
	public ConsoleWindow(int xpos, int ypos)
	{
		// Redirect output
		System.setOut(new PrintStream(sos));
		System.setErr(new PrintStream(sos));
		
		// Start up the thread
		Thread t = new Thread()
				{
					String lastData = "";
					public void run()
					{
						while (true)
						{
							try { Thread.sleep(100); } catch (InterruptedException e) { }
							String tempData = data.toString();
							if (!lastData.equals(tempData))
							{
								lastData = tempData;
								txtConsole.setText(lastData);
							}
						}
					}
				};
		t.setDaemon(true);
		t.setName("ConsoleWindow");
		t.start();
		
		// Set up the window
		txtConsole.setEditable(false);
		scrollArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		Container pane = f.getContentPane();
		pane.setLayout(null);
		pane.add(scrollArea);
		
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
		
		// Window settings
		f.setSize(width, height);
		f.setLocation(xpos, ypos);
		f.setMinimumSize(new Dimension(width, height));
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.setVisible(true);
	}
	
	public void doLayout()
	{
		Insets i = f.getInsets();
		int twidth = width - i.left - i.right;
		int theight = height - i.top - i.bottom;

		scrollArea.setBounds(5, 5, twidth - 10, theight - 10);
	}
}
