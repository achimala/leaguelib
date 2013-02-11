package com.gvaneyck.spectate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * A list model for LoL game info
 * 
 * @author Gvaneyck
 */
public class GameListModel implements ListModel
{
	List<ListDataListener> listeners = new ArrayList<ListDataListener>();
	List<GameInfo> data = new ArrayList<GameInfo>();
	
	public GameListModel()
	{
		Thread t = new Thread()
				{
					public void run()
					{
						while (true)
						{
							try { Thread.sleep(1000); } catch (InterruptedException e) { }
	
							synchronized (data)
							{
								for (GameInfo gi : data)
									if (gi.delay > 0)
										gi.delay--;
								
								update();
							}
						}
					}
				};
		t.setName("GameListModelUpdater");
		t.setDaemon(true);
		t.start();
	}
	
	public void clear()
	{
		synchronized (data)
		{
			data.clear();
		}
	}
	
	public void add(GameInfo gi)
	{
		synchronized (data)
		{
			data.add(gi);
			Collections.sort(data);
		}
	}
	
	public void update()
	{
		ListDataEvent lde = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, data.size());
		for (ListDataListener l : listeners)
			l.contentsChanged(lde);
	}
	
	public void addListDataListener(ListDataListener l)
	{
		listeners.add(l);
	}

	public Object getElementAt(int index)
	{
		return data.get(index);
	}

	public int getSize()
	{
		return data.size();
	}

	public void removeListDataListener(ListDataListener l)
	{
		listeners.remove(l);
	}
}
