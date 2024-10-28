package uz.pdp.fastfoodbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by: Mehrojbek
 * DateTime: 23/10/24 20:56
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Basket {

    private Map<Product, Integer> products;
    private Integer totalPrice;

}
