package santander.exercise.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import santander.exercise.model.Price;
import santander.exercise.service.PriceService;

import java.util.List;

@RestController
public class PriceController {
    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    private final PriceService priceService;

    @GetMapping("/latestprices")
    public List<Price> getLatestPrices(){
        return priceService.getLatestPrices();
    }
}
