package de.potera.klysma;

import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

public class ScanEnvironment {

    private static ConfigurationBuilder builder = null;

    /**
     * Reflections Configuration Builder to add new Urls
     * @return Configuration Builder
     */
    public static ConfigurationBuilder getBuilder() {
        if(builder == null)
            builder = new ConfigurationBuilder()
                    .setScanners(new TypeAnnotationsScanner(), new MethodAnnotationsScanner(), new SubTypesScanner());
        return builder;
    }
}
