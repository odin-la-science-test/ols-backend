package com.odinlascience.backend.modules.history.context;

/**
 * ThreadLocal pour indiquer que la requete courante est un undo/redo
 * et que l'enregistrement dans l'historique doit etre ignore.
 * Utilise par HistorySkipFilter et HistoryRecordingListener.
 */
public final class HistoryContext {

    private static final ThreadLocal<Boolean> SKIP = ThreadLocal.withInitial(() -> false);

    private HistoryContext() {}

    public static void setSkip(boolean skip) {
        SKIP.set(skip);
    }

    public static boolean shouldSkip() {
        return SKIP.get();
    }

    public static void clear() {
        SKIP.remove();
    }
}
