package controller;

import framework.*;

import java.util.*;

public class SceneManager {
	private HashMap<String, SceneWrapper> scenes = new HashMap<>();
	private SceneWrapper current;

	public void registerScene(String id, SceneWrapper scene) {
		Objects.requireNonNull(scene, "Podana scena jest pusta");
		scenes.put(id, scene);
	}

	public SceneWrapper getScene(String id) {
		return scenes.get(id);
	}

	public SceneWrapper getCurrent() {
		return current;
	}

	public void setCurrent(String id) {
		if(scenes.get(id) != null) current = scenes.get(id);
		else throw new NoSuchElementException("Nie istnieje scena o podanym ID");
	}


}
