package com.gvaneyck.runesorter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * A list model for LoL rune pages
 * 
 * @author Gvaneyck
 */
public class RunePageListModel implements ListModel
{
	List<ListDataListener> listeners = new ArrayList<ListDataListener>();
	List<RunePage> data = new ArrayList<RunePage>();
	
	public RunePageListModel()
	{
	}
	
	public void clear()
	{
		data.clear();
	}
	
	public void add(RunePage page)
	{
		data.add(page);
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
