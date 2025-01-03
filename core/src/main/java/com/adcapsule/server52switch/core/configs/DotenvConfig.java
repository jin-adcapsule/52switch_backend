package com.adcapsule.server52switch.core.configs;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvConfig {

    private static Dotenv dotenv;

    // Initialize dotenv in a static block
    static {
        dotenv = Dotenv.configure()
                .ignoreIfMissing() // Prevent exceptions if .env file is missing
                .load();
    }

    /**
     * Fetches the value of the given key from the .env file.
     *
     * @param key The name of the environment variable
     * @return The value of the variable, or null if not found
     */
    public static String get(String key) {
        return dotenv.get(key);
    }

    /**
     * Fetches the value of the given key from the .env file, with a default value.
     *
     * @param key The name of the environment variable
     * @param defaultValue The default value if the key is not found
     * @return The value of the variable, or the default value if not found
     */
    public static String get(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);
    }
}
