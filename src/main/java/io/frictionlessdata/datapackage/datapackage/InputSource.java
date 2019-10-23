package io.frictionlessdata.datapackage.datapackage;

public interface InputSource<T> {

    T getInput();

    void setInput(T input);
}
