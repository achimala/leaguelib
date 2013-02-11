package com.gvaneyck.runesorter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * A list model for LoL mastery pages
 * 
 * @author Gvaneyck
 */
public class MasteryPageListModel implements ListModel
{
	List<ListDataListener> listeners = new ArrayList<ListDataListener>();
	List<MasteryPage> data = new ArrayList<MasteryPage>();
	
	public MasteryPageListModel()
	{
	}
	
	public void clear()
	{
		data.clear();
	}
	
	public void add(MasteryPage page)
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
