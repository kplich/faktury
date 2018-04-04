package framework;

import controller.*;

public interface Controller {
    void initialize(Main main, Database database);

    void open();

    void close();

    void setToDefault();
}
