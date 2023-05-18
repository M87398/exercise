package santander.exercise;

import santander.exercise.model.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceDataGenerator {
    public static Price getPrice(long id, String instrument, String bid, String ask, LocalDateTime localDateTime) {
        Price price = new Price();
        price.setId(id);
        price.setInstrumentName(instrument);
        price.setBid(new BigDecimal(bid));
        price.setAsk(new BigDecimal(ask));
        price.setTimestamp(localDateTime);
        return price;
    }
}
