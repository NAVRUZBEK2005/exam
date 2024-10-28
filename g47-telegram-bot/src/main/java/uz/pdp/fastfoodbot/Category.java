package uz.pdp.fastfoodbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by: Mehrojbek
 * DateTime: 23/10/24 20:54
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Category {

    private Integer id;
    private String name;
    private List<Product> products;

}
