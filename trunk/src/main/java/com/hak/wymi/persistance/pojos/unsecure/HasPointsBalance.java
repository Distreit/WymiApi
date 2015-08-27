package com.hak.wymi.persistance.pojos.unsecure;

public interface HasPointsBalance {
    boolean addPoints(Integer amount);

    boolean removePoints(Integer amount);
}
