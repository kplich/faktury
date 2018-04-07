package framework;

import controller.*;

public interface Controller {
	/**
	 * Method used to initialize a controller (sort of a constructor, only called ONCE).
	 * Sets up all the mechanisms of each scene.
	 * @param main a reference to the main application class
	 * @param database a reference to the database
	 */
    void initialize(Main main, Database database);

	/**
	 * Method called EVERY TIME the scene is opened. Here the fields are being cleared, panels are disabled,
	 * and the viewed lists are updated. Uses a helper method {@code setToDefault()}.
	 */
	void open();

	/**
	 * A helper method for {@code open()} method.
	 */
	void setToDefault();

	/**
	 * Method called EVERY TIME the scene is closed. Used mostly for saving the database to the file.
	 */
    void close();
}
