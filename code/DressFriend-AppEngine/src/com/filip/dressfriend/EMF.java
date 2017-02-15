package com.filip.dressfriend;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EMF {
	private static EntityManagerFactory emfInstance;

	private EMF() {
	}

	public static EntityManagerFactory get() {

		if (emfInstance == null) {
			
			emfInstance = Persistence.createEntityManagerFactory("df-persistence-unit");

		}

		return emfInstance;
	}
}