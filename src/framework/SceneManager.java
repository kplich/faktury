package framework;

import java.util.*;

public class SceneManager {
	/**
	 * A map that holds all the scenes in the application. Each scene has its own identifying string.
	 */
	private HashMap<String, SceneWrapper> scenes = new HashMap<>();

	/**
	 * Currently viewed scene.
	 */
	private SceneWrapper current;

	/**
	 * Method registering a new scene with a given ID.
	 * @param id ID of the scene
	 * @param scene scene to register
	 */
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

	/**
	 * If a scene with a given ID exists, it's being set as a current scene.
	 * @param id
	 */
	public void setCurrent(String id) {
		if(scenes.get(id) != null) {
			current = scenes.get(id);
		}
		else {
			throw new NoSuchElementException("Nie istnieje scena o podanym ID");
		}
	}


}
