package com.assessgo.backend.dto;

/**
 * This class defines he model attributes for the graph (GoJS) operations
 */
public abstract class GraphModel {

    public static final int NEW = 1;
    public static final int UPDATED = 2;
    public static final int DELETED = -1;
    public static final int NONE = 0;

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
