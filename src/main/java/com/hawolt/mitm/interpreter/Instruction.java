package com.hawolt.mitm.interpreter;

public interface Instruction {
    String getName();

    String manipulate(String[] args) throws Exception;
}
