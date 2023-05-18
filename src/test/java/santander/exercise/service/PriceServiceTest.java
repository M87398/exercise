package santander.exercise.service;

import org.junit.jupiter.api.Test;
import santander.exercise.PriceDataGenerator;
import santander.exercise.model.Price;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriceServiceTest {
    @Test
    void shouldGetLatestPrices() {
        //given
        PriceService priceService = new PriceService();
        LocalDateTime localDateTime = LocalDateTime.of(2020, 6, 1, 12, 1, 1, 1000000);
        Price price = PriceDataGenerator.getPrice(101l, "EUR/USD", "1.1000", "1.2000", localDateTime);
        priceService.save(price);

        LocalDateTime localDateTime2 = LocalDateTime.of(2020, 6, 1, 12, 1, 1, 1000001);
        Price price2 = PriceDataGenerator.getPrice(102l, "EUR/USD", "1.1005", "1.2006", localDateTime2);
        priceService.save(price2);

        //when
        List<Price> latestPrices = priceService.getLatestPrices();

        //then
        assertEquals(1, latestPrices.size());
        assertEquals(price2, latestPrices.get(0));
    }

    @Test
    void shouldGetLatestPricesForManyCurrencies() {
        //given
        PriceService priceService = new PriceService();
        LocalDateTime localDateTime = LocalDateTime.of(2020, 6, 1, 12, 1, 1, 1000000);
        Price price = PriceDataGenerator.getPrice(101l, "EUR/JPY", "119.60", "119.90", localDateTime);
        priceService.save(price);

        LocalDateTime localDateTime2 = LocalDateTime.of(2020, 6, 1, 12, 1, 1, 1000001);
        Price price2 = PriceDataGenerator.getPrice(102l, "EUR/USD", "1.1005", "1.2006", localDateTime2);
        priceService.save(price2);

        //when
        List<Price> latestPrices = priceService.getLatestPrices();

        //then
        assertEquals(2, latestPrices.size());
        assertEquals(List.of(price, price2), latestPrices);
    }
}