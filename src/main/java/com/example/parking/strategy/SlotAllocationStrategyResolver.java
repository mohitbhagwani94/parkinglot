package com.example.parking.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SlotAllocationStrategyResolver {

    private final Map<String, SlotAllocationStrategy> strategies;
    private final String configuredName;

    public SlotAllocationStrategyResolver(Map<String, SlotAllocationStrategy> strategies,
                                          @Value("${app.allocation.strategy:nearest}") String configuredName) {
        this.strategies = strategies;
        this.configuredName = configuredName;
    }

    /**
     * Return the configured strategy bean by name. If not found, fallback to 'nearest'.
     */
    public SlotAllocationStrategy getStrategy() {
        var s = strategies.get(configuredName);
        if (s != null) return s;
        s = strategies.get("nearest");
        if (s != null) return s;
        // last resort: return any one
        return strategies.values().stream().findFirst().orElseThrow();
    }
}
