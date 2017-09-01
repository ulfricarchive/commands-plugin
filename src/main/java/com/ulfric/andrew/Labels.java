package com.ulfric.andrew;

import com.ulfric.commons.value.Bean;

import java.util.Map;

public class Labels extends Bean {

	private String root;
	private Map<Class<? extends Command>, String> labels;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public Map<Class<? extends Command>, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<Class<? extends Command>, String> labels) {
		this.labels = labels;
	}

}
