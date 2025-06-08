/*
 * Project: Dynamic Flight Pricing Engine
 * Modules:
 *  - core.engine
 *  - core.db
 *  - plugins.api
 *
 * Folder structure:
 *   src/
 *     core/engine/
 *       Main.java
 *       PricingService.java
 *       PluginManager.java
 *       PricingRuleExecutor.java
 *     core/db/
 *       DataSourceFactory.java
 *       FlightRepository.java
 *     plugins/api/
 *       PricingStrategy.java
 *       PricingRule.java
 */

// core/engine/Main.java
package core.engine;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Main {
    public static void main(String[] args) throws Exception {
        PluginManager pluginManager = new PluginManager("plugins");
        pluginManager.loadPlugins();

        PricingService pricingService = new PricingService(pluginManager);
        // Example hello-world: list all loaded strategies
        System.out.println("Loaded pricing strategies:");
        pluginManager.getStrategies().forEach(s ->
                System.out.println(" - " + s.getClass().getSimpleName())
        );
    }
}

// core/engine/PluginManager.java
package core.engine;

import plugins.api.PricingStrategy;
import java.nio.file.Path;
import java.util.List;

public class PluginManager {
    private final Path pluginDir;
    private List<PricingStrategy> strategies;

    public PluginManager(String pluginsDirectory) {
        this.pluginDir = Path.of(pluginsDirectory);
    }

    public void loadPlugins() {
        // TODO: implement watch & URLClassLoader loading jars
    }

    public List<PricingStrategy> getStrategies() {
        return strategies;
    }
}

// core/engine/PricingService.java
package core.engine;

import plugins.api.PricingStrategy;
import java.util.List;

public class PricingService {
    private final List<PricingStrategy> strategies;

    public PricingService(PluginManager manager) {
        this.strategies = manager.getStrategies();
    }

    public double calculatePrice(Flight flight, PricingContext context) {
        double price = flight.getBasePrice();
        for (PricingStrategy strat : strategies) {
            price = strat.apply(flight, context);
        }
        return price;
    }
}

// core/db/DataSourceFactory.java
package core.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DataSourceFactory {
    public static DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/flights");
        config.setUsername("user");
        config.setPassword("pass");
        return new HikariDataSource(config);
    }
}

// plugins/api/PricingStrategy.java
package plugins.api;

import core.db.Flight;
import core.engine.PricingContext;

public interface PricingStrategy {
    /**
     * Modify the price based on flight and context
     */
    double apply(Flight flight, PricingContext context);
}

// plugins/api/PricingRule.java
package plugins.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PricingRule {
    int priority() default 0;
}
