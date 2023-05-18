package santander.exercise;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import santander.exercise.model.Price;
import santander.exercise.processing.ProcessData;
import santander.exercise.service.PriceService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessDataTest {
    private final BigDecimal commisionRate = new BigDecimal("0.001");
    @Mock
    PriceService priceService;

    @InjectMocks
    ProcessData processData;

    @Test
    void shouldSaveonMessage() {
        //given
        String inputData = "106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001";
        Price price = new Price();
        price.setId(106l);
        price.setInstrumentName("EUR/USD");
        price.setBid(new BigDecimal("1.1000"));
        price.setAsk(new BigDecimal("1.2000"));
        LocalDateTime localDateTime = LocalDateTime.of(2020, 6, 1, 12, 1, 1, 1000000);
        price.setTimestamp(localDateTime);

        //when
        processData.onMessage(inputData);

        //then
        ArgumentCaptor<Price> argument = ArgumentCaptor.forClass(Price.class);
        verify(priceService).save(argument.capture());
        assertEquals(106, argument.getValue().getId());
        assertEquals("EUR/USD", argument.getValue().getInstrumentName());
        assertEquals(new BigDecimal("1.0989"), argument.getValue().getBid());
        assertEquals(new BigDecimal("1.2012"), argument.getValue().getAsk());
        assertEquals(localDateTime, argument.getValue().getTimestamp());
    }

    private static Stream<Arguments> incorrectDatas() {
        return Stream.of(
                Arguments.of("106, EUR,USD, 1.1000,1.2000,01-06-2020 12:01:01:001"),
                Arguments.of("abc, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001"),
                Arguments.of("106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01001")
        );
    }

    @ParameterizedTest
    @MethodSource("incorrectDatas")
    void shouldNotParseMessage(String inputData) {
        //given
        //when
        processData.onMessage(inputData);

        //then
        verify(priceService, never()).save(any());
    }

    @Test
    void shouldGetLatestPrices() {
        //given
        List<Price> prices = new LinkedList<>();
        when(processData.getLatestPrices()).thenReturn(prices);

        //when
        List<Price> priceList = processData.getLatestPrices();

        //then
        assertEquals(prices, priceList);
    }

    private static Stream<Arguments> provisionDatas() {
        return Stream.of(
                Arguments.of("0.0000", "0.0000", "0.0000", "0.0000"),
                Arguments.of("1.1000", "1.2000", "1.0989", "1.2012")
        );
    }

    @ParameterizedTest
    @MethodSource("provisionDatas")
    void shouldAddProvisions(String bid, String ask, String bidExpected, String askExpected) {
        //given
        LocalDateTime localDateTime = LocalDateTime.of(2020, 6, 1, 12, 1, 1, 1000000);
        Price price = PriceDataGenerator.getPrice(101l, "EUR/USD", bid, ask, localDateTime);

        //when
        ReflectionTestUtils.invokeMethod(processData, "addProvisions", price);

        //then
        assertEquals(new BigDecimal(bidExpected), price.getBid());
        assertEquals(new BigDecimal(askExpected), price.getAsk());
    }
}