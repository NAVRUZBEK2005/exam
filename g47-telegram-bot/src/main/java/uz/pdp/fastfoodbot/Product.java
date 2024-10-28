package uz.pdp.fastfoodbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Mehrojbek
 * DateTime: 23/10/24 20:54
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {

    private Integer id;
    private String name;
    private String description;
    private Integer price;

}
