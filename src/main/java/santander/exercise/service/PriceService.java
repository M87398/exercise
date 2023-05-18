package santander.exercise.service;

import org.springframework.stereotype.Service;
import santander.exercise.model.Price;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceService {
    private final Map<String, Price> prices;

    public PriceService() {
        prices = new HashMap<>();
    }

    public void save(Price price) {
        Price oldPrice = prices.get(price.getInstrumentName());
        if (oldPrice == null || price.getTimestamp().compareTo(oldPrice.getTimestamp()) > 0) {
            prices.put(price.getInstrumentName(), price);
        }
    }

    public List<Price> getLatestPrices() {
        return prices.values().stream().toList();
    }
}
