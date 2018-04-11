package framework;

import controller.*;

import java.util.*;

/**
 * Class created to hold all the scenes it the program.
 */
public class SceneManager {
	/**
	 * A map that holds all the scenes in the application. Each scene has its own identifying string.
	 */
	private HashMap<SceneID, SceneWrapper> scenes = new HashMap<>();

	/**
	 * Currently viewed scene.
	 */
	private SceneWrapper current;

	/**
	 * Method registering a new scene with a given ID.
	 * @param id ID of the scene
	 * @param scene scene to register
	 */
	public void registerScene(SceneID id, SceneWrapper scene) {
		assert scene != null : "Podana scena jest pusta.";
		scenes.put(id, scene);
	}

	public SceneWrapper getScene(SceneID id) {
		return scenes.get(id);
	}

	public SceneWrapper getCurrent() {
		return current;
	}

	/**
	 * If a scene with a given ID exists, it's being set as a current scene.
	 * @param id id of a scene the user wants to switch to
	 * @throws NoSuchElementException when scene with a given ID doesn't exist
	 */
	public void setCurrent(SceneID id) throws NoSuchElementException{
		if(scenes.get(id) != null) {
			current = scenes.get(id);
		}
		else {
			throw new NoSuchElementException("Nie istnieje scena o podanym ID");
		}
	}


}
