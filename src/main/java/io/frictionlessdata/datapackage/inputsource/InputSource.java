package io.frictionlessdata.datapackage.inputsource;

public interface InputSource<T> {

    T getInput();

    void setInput(T input);
}
