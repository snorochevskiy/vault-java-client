package me.snorochevskiy.vault.client.lib;

import java.util.function.Supplier;

public class Trial<T> {

    public static <T> TrialBuilder<T> exec(Supplier<T> call) {
        return new TrialBuilder<T>();
    }

    public static class TrialBuilder<T> {
        private Supplier<T> call;
        private Runnable restore;


    }
}
