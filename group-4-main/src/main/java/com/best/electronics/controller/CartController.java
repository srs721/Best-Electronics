package com.best.electronics.controller;

import com.best.electronics.cartandwishlist.GetTotalOfProduct;
import com.best.electronics.cartandwishlist.Invoker;
import com.best.electronics.model.CardDetails;
import com.best.electronics.model.Order;
import com.best.electronics.database.IDatabasePersistence;
import com.best.electronics.database.MySQLDatabasePersistence;
import com.best.electronics.model.CartItem;
import com.best.electronics.model.Product;
import com.best.electronics.repository.ProductRepository;
import com.best.electronics.repository.UserRepository;
import com.best.electronics.repository.CartRepository;
import com.best.electronics.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Controller
public class CartController {
    @PostMapping("/CartController/{product_id}")
    public String index(HttpServletRequest request, @PathVariable Integer product_id, Model model) throws Exception {
        Integer quantity = Integer.valueOf(request.getParameter("userQuantity"));

        HttpSession oldSession = request.getSession(false);
        if (oldSession == null) {
            return "products";
        }
        else {
            Integer id = (Integer) oldSession.getAttribute("id");
            IDatabasePersistence databasePersistence = new MySQLDatabasePersistence();
            UserRepository userRepository = new UserRepository(databasePersistence);
            Map<String, Object> userDetail = userRepository.getUserDetailsById(id);
            if (userDetail == null) {
                return "products";
            }
            else {
                CartItem cartItem = new CartItem(product_id, "Cart", quantity, id);
                Invoker invoker = new Invoker();
                invoker.setCommand(cartItem, null);
                invoker.Add();
            }
        }
        IDatabasePersistence db = new MySQLDatabasePersistence();
        ProductRepository productRepository = new ProductRepository(db);
        ArrayList<Map<String, Object>> productList = productRepository.getProductDetails();
        model.addAttribute("listProducts", productList);
        return "productList";
    }

    @GetMapping("/cart")
    public String displayWishlist(Model model, HttpServletRequest request) throws Exception {
        HttpSession oldSession = request.getSession(false);
        if (oldSession == null) {
            return "products";
        }
        else {
            Integer id = (Integer) oldSession.getAttribute("id");
            IDatabasePersistence databasePersistence = new MySQLDatabasePersistence();
            UserRepository userRepository = new UserRepository(databasePersistence);
            Map<String, Object> userDetail = userRepository.getUserDetailsById(id);
            if (userDetail == null) {
                return "products";
            }
            else {
                CartRepository cartRepository = new CartRepository(databasePersistence);
                ArrayList<Map<String, Object>> cartListResult = cartRepository.getCartListDetails(id);
                if (cartListResult == null) {
                    return "products";
                } else {
                    GetTotalOfProduct getTotalOfProduct = GetTotalOfProduct.getInstance();
                    double totalSum = getTotalOfProduct.calculateTotalOfProducts(cartListResult);
                    model.addAttribute("sumofcart", totalSum);
                    model.addAttribute("cart", new Product());
                    model.addAttribute("listCart", cartListResult);
                }
            }
            return "cart";
        }
    }

    @PostMapping("addCardDetails")
    public String addCardDetails(HttpServletRequest request){
        HttpSession oldSession = request.getSession(false);
        if (oldSession == null) {
            return "products";
        }
        else {
            Integer id = (Integer) oldSession.getAttribute("id");
            IDatabasePersistence databasePersistence = new MySQLDatabasePersistence();
            UserRepository userRepository = new UserRepository(databasePersistence);
            Map<String, Object> userDetail = userRepository.getUserDetailsById(id);
            if (userDetail == null) {
               return "products";
            }
            else {
                CardDetails cardDetails = new CardDetails();
                cardDetails.setCardName(request.getParameter("cardName"));
                cardDetails.setCardNumber(request.getParameter("cardNumber"));
                cardDetails.setCardType(request.getParameter("cardType"));
                cardDetails.setExpiryDate(request.getParameter("cardExpiry"));
                cardDetails.setSecurityCode(request.getParameter("securityCode"));
                cardDetails.setUserId(id);
                CartRepository cartRepository = new CartRepository(databasePersistence);
                cartRepository.saveCard(cardDetails);
            }
            return "redirect:/proceedToOrder";

        }
    }

    @GetMapping("proceedToOrder")
    public String proceedToOrder(Model model, HttpServletRequest request) throws Exception {
        HttpSession oldSession = request.getSession(false);
        if (oldSession == null) {
            return "products";
        }
        else {
            Integer id = (Integer) oldSession.getAttribute("id");
            IDatabasePersistence databasePersistence = new MySQLDatabasePersistence();
            UserRepository userRepository = new UserRepository(databasePersistence);
            Map<String, Object> userDetail = userRepository.getUserDetailsById(id);
            if (userDetail == null) {
                return "products";
            }
            else {
                CartRepository cartRepository = new CartRepository(databasePersistence);
                ArrayList<Map<String, Object>> cartListResult = cartRepository.getCartListDetails(id);
                if (cartListResult == null) {
                    return "products";
                } else {
                    //getting total sum of the cart
                    GetTotalOfProduct getTotalOfProduct = GetTotalOfProduct.getInstance();
                    double totalsum = getTotalOfProduct.calculateTotalOfProducts(cartListResult);

                    //getting address details of user
                    model.addAttribute("address", userDetail.get("address"));

                    //getting payment method
                    ArrayList<Map<String, Object>> paymentListDetails = cartRepository.getCardDetails(id);

                    model.addAttribute("cardDetails",paymentListDetails);
                    model.addAttribute("sumofcart",totalsum);
                }
            }
            return "proceedToCheckout";

        }
    }
    @PostMapping("/placeOrder")
    public String placeOrder(HttpServletRequest request) throws Exception {
        Order order = new Order();
        order.setOrderAmount(0.0);
        order.setOrderStatus("Order placed");
        order.setPaymentMethod(request.getParameter("payment"));

        HttpSession oldSession = request.getSession(false);
        if (oldSession == null) {
            return "products";
        } else {
            Integer id = (Integer) oldSession.getAttribute("id");
            IDatabasePersistence databasePersistence = new MySQLDatabasePersistence();
            UserRepository userRepository = new UserRepository(databasePersistence);
            Map<String, Object> userDetail = userRepository.getUserDetailsById(id);
            order.setUserId(id);
            order.setOrderDate(String.valueOf(new Date()));
            if (userDetail == null) {
                return "products";
            }
            else {
                order.setAddress((String) userDetail.get("address"));
                CartRepository cartRepository = new CartRepository(databasePersistence);
                ArrayList<Map<String, Object>> cartListResult = cartRepository.getCartListDetails(id);
                if (cartListResult == null) {
                    return "products";
                } else {
                    //placing order here
                    OrderRepository orderRepository = new OrderRepository(databasePersistence);
                    orderRepository.placeOrder(order);
                    int latestOrderDetailsId = cartRepository.getOrderId(id);
                    orderRepository.saveOrderItems(cartListResult, latestOrderDetailsId);
                    cartRepository.removeFullCart(id);
                }
            }
            return "cart";
        }
    }

    @PostMapping("/RemoveFromCartlistController/{product_id}")
    public String removeFromCart(HttpServletRequest request) {
        int cardItemId = Integer.parseInt(request.getParameter("cartItemId"));

        HttpSession oldSession = request.getSession(false);
        if (oldSession == null) {
            return "products";
        }
        else {
            Integer id = (Integer) oldSession.getAttribute("id");
            IDatabasePersistence databasePersistence = new MySQLDatabasePersistence();
            UserRepository userRepository = new UserRepository(databasePersistence);
            Map<String, Object> userDetail = userRepository.getUserDetailsById(id);
            if (userDetail == null) {
                return "products";
            }
            else {
                CartItem cartItem = new CartItem(cardItemId,"Cart", id);
                Invoker invoker1 = new Invoker();
                invoker1.setCommand(cartItem, null);
                invoker1.Remove();
            }
            return "redirect:/cart";
        }
    }
}