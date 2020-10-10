package de.potera.teamhardcore.db;

import java.util.concurrent.CopyOnWriteArrayList;

public class HandlerGroup {

    private final String name;
    private final long executeDelay;
    private final CopyOnWriteArrayList<Handler> handlerList;
    private Thread thread;
    private boolean active;

    public HandlerGroup(String name, long executeDelay) {
        this.name = name;
        this.executeDelay = executeDelay;
        this.handlerList = new CopyOnWriteArrayList<>();
        startThread();
    }

    public void startThread() {
        if (this.thread != null && this.thread.isAlive())
            return;
        this.active = true;

        this.thread = new Thread(() -> {
            while (this.active) {
                try {
                    Thread.sleep(this.executeDelay);
                } catch (InterruptedException e) {
                    return;
                }

                if (!this.handlerList.isEmpty()) {
                    executeAll();
                }
            }
        }, "HandlerGroup Thread - " + this.name);


        this.thread.start();
    }

    public void stopThread() {
        if (this.thread == null || !this.thread.isAlive())
            return;
        this.active = false;
        this.thread.interrupt();
        this.thread = null;
        executeAll();
    }

    public boolean isRunning() {
        return (this.thread != null && this.thread.isAlive());
    }

    public void addHandler(Handler handler) {
        if (this.handlerList.contains(handler))
            return;
        this.handlerList.add(handler);
        if (this.thread == null || !this.thread.isAlive())
            startThread();
    }

    public void removeHandler(Handler handler) {
        if (!this.handlerList.contains(handler))
            return;
        this.handlerList.remove(handler);
    }

    private void executeAll() {
        for (Handler handler : this.handlerList) {
            handler.execute();
        }
    }

    public String getName() {
        return this.name;
    }

    public CopyOnWriteArrayList<Handler> getHandlerList() {
        return this.handlerList;
    }

}
