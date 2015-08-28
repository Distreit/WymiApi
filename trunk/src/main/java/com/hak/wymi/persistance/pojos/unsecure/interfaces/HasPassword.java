package com.hak.wymi.persistance.pojos.unsecure.interfaces;

@FunctionalInterface
public interface HasPassword {
    boolean passwordsMatch();
}
