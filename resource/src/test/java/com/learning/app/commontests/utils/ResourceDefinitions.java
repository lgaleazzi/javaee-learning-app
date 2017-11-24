package com.learning.app.commontests.utils;

import org.junit.Ignore;

/*
 * Enum to store name of folders where json files are stored
 */

@Ignore
public enum ResourceDefinitions {
	CATEGORY("category"),
	COURSE("course"),
	REVIEW("review"),
	USER("user");

	private String resourceName;

	private ResourceDefinitions(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceName() {
		return resourceName;
	}
}