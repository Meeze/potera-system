package de.potera.teamhardcore.db;

public abstract class AsyncTimedUpdate implements Handler {
    private boolean update;
    private boolean forceUpdate;
    private final long updateDelay;
    private final String handlerName;
    private HandlerGroup handlerGroup;

    public AsyncTimedUpdate(String handlerName, long updateDelay, boolean update) {
        if (update) {
            if (!HandlerGroups.containsGroup(handlerName))
                HandlerGroups.addGroup(handlerName, updateDelay);
            this.handlerGroup = HandlerGroups.getHandlerGroup(handlerName);
            this.handlerGroup.addHandler(this);
        }
        this.updateDelay = updateDelay;
        this.handlerName = handlerName;
        this.update = false;
        this.forceUpdate = false;
    }

    public AsyncTimedUpdate(String handlerName, boolean update) {
        this(handlerName, 10000L, update);
    }

    public AsyncTimedUpdate(String handlerName) {
        this(handlerName, 10000L, true);
    }

    public HandlerGroup getHandlerGroup() {
        return this.handlerGroup;
    }

    public void setHandlerGroup(HandlerGroup handlerGroup) {
        this.handlerGroup = handlerGroup;
    }

    public boolean isUpdate() {
        return this.update;
    }

    public void setUpdate(boolean state) {
        this.update = state;
    }

    public boolean isForceUpdate() {
        return this.forceUpdate;
    }

    public void setForceUpdate(boolean state) {
        this.forceUpdate = state;
    }

    public long getUpdateDelay() {
        return this.updateDelay;
    }

    public String getHandlerName() {
        return this.handlerName;
    }
}
