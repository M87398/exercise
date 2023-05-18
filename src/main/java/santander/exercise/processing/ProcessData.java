package santander.exercise.processing;

import lombok.extern.slf4j.Slf4j;
import santander.exercise.model.Price;
import santander.exercise.service.PriceService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ProcessData implements DataMessages {
    private final BigDecimal commisionRate = new BigDecimal("0.001");
    private final PriceService priceService;

    public ProcessData(PriceService priceService) {
        this.priceService = priceService;
    }

    @Override
    public void onMessage(String data) {

        data.lines().forEach(s -> {
            Optional<Price> parsedPrice = parsePrice(s);
            parsedPrice.ifPresent(price -> {
                addProvisions(price);
                priceService.save(price);
            });
        });
    }

    public List<Price> getLatestPrices() {
        return priceService.getLatestPrices();
    }

    private void addProvisions(Price price) {
        int bidScale=price.getBid().scale();
        int askScale=price.getAsk().scale();
        price.setBid(price.getBid().subtract(price.getBid().multiply(commisionRate)).setScale(bidScale));
        price.setAsk(price.getAsk().add(price.getAsk().multiply(commisionRate)).setScale(askScale));
    }

    private Optional<Price> parsePrice(String data) {
        String[] dataArray = data.split(",");
        if (dataArray.length != 5) {
            log.error("incorrect data " + data);
            return Optional.empty();
        }
        Price price = new Price();
        try {
            price.setId(Long.parseLong(dataArray[0]));
            price.setInstrumentName(dataArray[1].trim());
            price.setBid(new BigDecimal(dataArray[2].trim()));
            price.setAsk(new BigDecimal(dataArray[3].trim()));

            LocalDateTime localDateTime = stringToLocalDateTime(dataArray[4]);
            price.setTimestamp(localDateTime);
            return Optional.of(price);
        } catch (NumberFormatException | IncorrectDataFormatException exception) {
            log.error("incorrect data " + data);
            return Optional.empty();
        }
    }

    private LocalDateTime stringToLocalDateTime(String dt) {
        String[] dateTimeArr = dt.split(" ");
        if (dateTimeArr.length != 2) {
            throw new IncorrectDataFormatException("Incorrect data time format");
        }
        String[] dataArr = dateTimeArr[0].split("-");
        if (dataArr.length != 3) {
            throw new IncorrectDataFormatException("Incorrect data format");
        }
        String[] timeArr = dateTimeArr[1].split(":");
        if (timeArr.length != 4) {
            throw new IncorrectDataFormatException("Incorrect time format");
        }
        return LocalDateTime.of(Integer.parseInt(dataArr[2]),
                Integer.parseInt(dataArr[1]),
                Integer.parseInt(dataArr[0]),
                Integer.parseInt(timeArr[0]),
                Integer.parseInt(timeArr[1]),
                Integer.parseInt(timeArr[2]),
                1000000 * Integer.parseInt(timeArr[3]));
    }
}
