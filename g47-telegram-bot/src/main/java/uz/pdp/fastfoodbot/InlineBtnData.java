package uz.pdp.fastfoodbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Mehrojbek
 * DateTime: 25/10/24 19:23
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InlineBtnData {

    private String text;
    private String callback;
    private int rowNumber;
    private int columnNumber;

}
