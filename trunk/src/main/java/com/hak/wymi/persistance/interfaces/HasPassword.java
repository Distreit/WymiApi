package com.hak.wymi.persistance.interfaces;

@FunctionalInterface
public interface HasPassword {
    boolean passwordsMatch();
}
