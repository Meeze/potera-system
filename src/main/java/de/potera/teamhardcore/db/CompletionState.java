package de.potera.teamhardcore.db;

import java.util.List;

public interface CompletionState {

    List<Runnable> getReadyExecutors();

    void addReadyExecutor(Runnable paramRunnable);

    boolean isReady();

    void setReady(boolean paramBoolean);

}
