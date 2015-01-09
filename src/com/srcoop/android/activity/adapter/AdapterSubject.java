package com.srcoop.android.activity.adapter;

import com.srcoop.android.activity.fragment.FragmentObserver;

public interface AdapterSubject {
	public void registerObserver(FragmentObserver observer);

	public void removeObserver(FragmentObserver observer);

	public void notifyObserver();
}
