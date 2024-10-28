package uz.pdp.fastfoodbot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Mehrojbek
 * DateTime: 23/10/24 20:55
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    private Long chatId;
    private Basket basket;
    private String phoneNumber;

    public User(Long chatId, Basket basket) {
        this.chatId = chatId;
        this.basket = basket;
    }
}
