package com.example.schwartz.myapplication;

/**
 * Imports
 */
import java.util.List;

/**
 * Interface that listens for the direction finders
 */
public interface DirectionFinderListener {

    /**
     * WHAT DOES THIS DO? ***********************************************************************
     */
    void onDirectionFinderStart();

    /**
     * WHAT DOES THIS DO? ***********************************************************************
     * @param route
     */
    void onDirectionFinderSuccess(List<Route> route);
}
