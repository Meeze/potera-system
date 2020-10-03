package de.potera.teamhardcore.db;

import java.util.ArrayList;
import java.util.List;

public class CompletionStateImpl implements CompletionState {

    private final List<Runnable> readyExecutors = new ArrayList<>();
    private boolean ready;

    public List<Runnable> getReadyExecutors() {
        return this.readyExecutors;
    }

    public void addReadyExecutor(Runnable exec) {
        if (this.ready) {
            exec.run();
            return;
        }
        this.readyExecutors.add(exec);
    }

    public boolean isReady() {
        return this.ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;

        if (ready) {
            for (Runnable exec : this.readyExecutors) {
                exec.run();
            }
            this.readyExecutors.clear();
        }
    }

}
