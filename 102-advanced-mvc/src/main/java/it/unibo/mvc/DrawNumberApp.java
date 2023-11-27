package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    private static final int MIN = 0;
    private static final int MAX = 100;
    private static final int ATTEMPTS = 10;

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     * @throws IOException
     * @throws FileNotFoundException
     */
    public DrawNumberApp(final DrawNumberView... views) throws FileNotFoundException, IOException {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        this.model = new DrawNumberImpl(MIN, MAX, ATTEMPTS);

        final Configuration c = new Configuration.Builder().build();
        this.getSettingsFromFile();
    }

    private void getSettingsFromFile() throws FileNotFoundException, IOException {
        try (final BufferedReader r = new BufferedReader(
                new InputStreamReader(new FileInputStream("../resources/config.yml")))) {
            String n;
            StringTokenizer st;
            while ((n = r.readLine()) != null) {
                st = new StringTokenizer(n, ": ");
                while (st.hasMoreTokens()) {
                    switch(st.nextToken()) {
                        //case "minimum" -> new Configuration.Builder().setMin(st.nextToken());
                    }
                }
            }
        }
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(new DrawNumberViewImpl());
    }

}
