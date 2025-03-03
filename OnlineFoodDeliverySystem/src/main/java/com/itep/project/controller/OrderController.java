package com.itep.project.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itep.project.dto.OrderDto;
import com.itep.project.model.Order;
import com.itep.project.model.OrderItem;
import com.itep.project.model.User;
import com.itep.project.response.ApiResponse;
import com.itep.project.service.email.EmailService;
import com.itep.project.service.order.OrderService;
import com.itep.project.service.user.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import com.razorpay.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/onlinefoodDelivery/orders")
public class OrderController {
	
private final OrderService serv;

private final UserService userServ;

private final EmailService email;
    
    @GetMapping("/myorders")
    public String showMyOrders(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/onlinefoodDelivery/users/login";
        }
        // Add user and cart to model for the navbar
        model.addAttribute("user", user);
        model.addAttribute("cart", user.getCart());
        
        List<OrderDto> orders = serv.getUserOrder(user.getId());
        List<OrderDto> currentOrders = orders.stream()
            .filter(order -> order.getOrderStatus().equalsIgnoreCase("PENDING")
                    || order.getOrderStatus().equalsIgnoreCase("PROCESSING")
                    || order.getOrderStatus().equalsIgnoreCase("OUTFORDELIVERY"))
            .collect(Collectors.toList());
        List<OrderDto> pastOrders = orders.stream()
            .filter(order -> order.getOrderStatus().equalsIgnoreCase("DELIVERED")
                    || order.getOrderStatus().equalsIgnoreCase("CANCELLED"))
            .collect(Collectors.toList());
        model.addAttribute("currentOrders", currentOrders);
        model.addAttribute("pastOrders", pastOrders);
        return "myorders"; // returns myorders.html
    }
    
    
    @GetMapping("/trackOrders")
    public String showTrackOrdersPage(Model model) {
        // Optionally, load initial orders data:
        List<Order> orders = serv.getAllOrdersAsEntities();
        model.addAttribute("orders", orders);
        return "trackOrders"; // resolves to trackOrders.html
    }
    
    @GetMapping("/search")
    @ResponseBody
    public ApiResponse searchOrders(@RequestParam(required = false) String search) {
        try {
            List<Order> orders;
            if (search == null || search.isBlank()) {
                orders = serv.getAllOrdersAsEntities();
            } else {
                orders = serv.searchOrdersByUserGeneric(search);
            }
            if (orders.isEmpty()) {
                return new ApiResponse("No orders found", null);
            }
            return new ApiResponse("Found orders", orders);
        } catch(Exception e) {
            return new ApiResponse(e.getMessage(), null);
        }
    }
    
    @PutMapping("/updateStatus/{orderId}")
    @ResponseBody
    public ApiResponse updateOrderStatus(@PathVariable Long orderId, @RequestParam String orderStatus) {
        try {
            Order order = serv.updateOrderStatus(orderId, orderStatus);
            OrderDto updatedDto = serv.convertToDto(order);
            User user = order.getUser();
            email.sendEmail(user.getEmail(),"Order Status Updated : "+order.getOrderStatus()+" !!!!","Dear "+user.getFirstName()+",\n Your Order is now : "+order.getOrderStatus()+" !\n Order Id : "+order.getOrderId()+"\n Order Date : "+order.getOrderDate()+"\n Total Amount :  "+order.getTotalAmount()+" \n Payment Staus : "+order.getPaymentStatus()+" \n Thanks and regards,\nOnlineFoodDelivery Team");
            return new ApiResponse("Order status updated successfully!",updatedDto);
        } catch(Exception e) {
            return new ApiResponse(e.getMessage(), null);
        }
    }
     
    @PostMapping("/createOrder")
    @ResponseBody
    public String createOrder(@RequestBody Map<String,Object> data) throws Exception {
    	System.out.println(data);
    	
    	int amt = Integer.parseInt(data.get("amount").toString());
    	
    	var client = new RazorpayClient("rzp_test_kDmY5OZwR2wFim","L9KYv1Qlh1fM9obnOh06NWgr");
    	
    	JSONObject options = new JSONObject();
    	options.put("amount", amt*100);
    	options.put("currency", "INR");
    	options.put("receipt", "txn_123456");
    	com.razorpay.Order order = client.Orders.create(options);
    	System.out.println(order);
    	
    	return order.toString();
    }
    
//    @PostMapping("/placeorder")
//    @ResponseBody
//    public ResponseEntity<ApiResponse> placeOrder(@RequestParam Long userId) {
//        try {
//            // Attempt to place the order in DB
//            serv.placeOrder(userId);
//            
//           // System.out.println(data);
//            // Return JSON with success message
//            return ResponseEntity.ok(new ApiResponse("Order placed successfully!", null));
//        } catch (Exception e) {
//            // Return JSON with error message
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                                 .body(new ApiResponse(e.getMessage(), null));
//        }
//    }
    @PostMapping("/placeorder")
    @ResponseBody
    public ResponseEntity<ApiResponse> placeOrder(@RequestParam Long userId, @RequestParam String paymentMethod,HttpSession session) {
        try {
            // In your service layer, use the paymentMethod to set paymentStatus accordingly.
            // For example, if paymentMethod equals "CASH" then set paymentStatus to PaymentStatus.CASH,
            // or if "ONLINE" then PaymentStatus.ONLINE.
            Order order = serv.placeOrder(userId, paymentMethod);
            User updatedUser = userServ.getUserById(userId);
            session.setAttribute("user", updatedUser);
            StringBuilder itemsBuilder = new StringBuilder();
            itemsBuilder.append("Order Items:\n");
            int index = 1;
            for (OrderItem item : order.getOrderItems()) {
                itemsBuilder.append(index++)
                            .append(". ")
                            .append(item.getFoodItem().getName())
                            .append(" (Qty: ")
                            .append(item.getQuantity())
                            .append(")  Price: ")
                            .append(item.getPrice())
                            .append("\n");
            }
            
            String emailBody = "Dear " + updatedUser.getFirstName() + ",\n"
            	    + "Your Order is placed Successfully!\n"
            	    + "Order Id: " + order.getOrderId() + "\n"
            	    + "Order Date: " + order.getOrderDate() + "\n"
            	    + itemsBuilder.toString() + "\n"
            	    + "Total Amount: " + order.getTotalAmount() + "\n"
            	    + "Order Status: " + order.getOrderStatus() + "\n"
            	    + "Payment Status: " + order.getPaymentStatus() + "\n"
            	    + "Thanks and regards,\nOnlineFoodDelivery Team";

            	// Send the email:
            	email.sendEmail(updatedUser.getEmail(), "Order Placed Successfully !!!!", emailBody);
            
           // email.sendEmail(updatedUser.getEmail(),"Order Placed Successfully !!!!","Dear "+updatedUser.getFirstName()+",\n Your Order is placed Successfully !\n Order Id : "+order.getOrderId()+"\n Order Date : "+order.getOrderDate()+"\n Order Items : "+order.getOrderItems()+" \n Total Amount :  "+order.getTotalAmount()+" \n Order Status : "+order.getOrderStatus()+"\n Payment Staus : "+order.getPaymentStatus()+" \n Thanks and regards,\nOnlineFoodDelivery Team");
            return ResponseEntity.ok(new ApiResponse("Order placed successfully!", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ApiResponse(e.getMessage(), null));
        }
    }
    
//    @PostMapping("/placeorder")
//    @ResponseBody
//    public ResponseEntity<ApiResponse> placeOrder(@RequestParam Long userId) {
//        try {
//            // Attempt to place the order in DB
//            serv.placeOrder(userId);
//            // Return JSON with success message
//            return ResponseEntity.ok(new ApiResponse("Order placed successfully!", null));
//        } catch (Exception e) {
//            // Return JSON with error message
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                                 .body(new ApiResponse(e.getMessage(), null));
//        }
//    }
    
//    @GetMapping("/get/order/{orderId}")
//    public ResponseEntity<ApiResponse> getOrderById(Long orderId) {
//        try {
//            OrderDto order = serv.getOrder(orderId);
//            return ResponseEntity.ok(new ApiResponse("Found !!!!", order));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
//        }
//    }
    
    @GetMapping("/get/order/{orderId}")
    @ResponseBody
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto order = serv.getOrder(orderId);
            return ResponseEntity.ok(new ApiResponse("Found !!!!", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(new ApiResponse(e.getMessage(), null));
        }
    }
    
    @GetMapping("/get/ordersby/user")
    @ResponseBody
    public ApiResponse getOrdersByUser(@RequestParam Long userId) {
        try {
            List<OrderDto> orders = serv.getUserOrder(userId);
            return new ApiResponse("Found orders", orders);
        } catch(Exception e) {
            return new ApiResponse(e.getMessage(), null);
        }
    }
	
	
//	
//	private final OrderService serv;
//	
//	@GetMapping("/myorders")
//    public String showMyOrders(Model model, HttpSession session) {
//        User user = (User) session.getAttribute("user");
//        if (user == null) {
//            return "redirect:/onlinefoodDelivery/users/login";
//        }
//        List<OrderDto> orders = serv.getUserOrder(user.getId());
//        List<OrderDto> currentOrders = orders.stream()
//            .filter(order -> order.getStatus().equalsIgnoreCase("PENDING")
//                    || order.getStatus().equalsIgnoreCase("PROCESSING")
//                    || order.getStatus().equalsIgnoreCase("OUTFORDELIVERY"))
//            .collect(Collectors.toList());
//        List<OrderDto> pastOrders = orders.stream()
//            .filter(order -> order.getStatus().equalsIgnoreCase("DELIVERED")
//                    || order.getStatus().equalsIgnoreCase("CANCELLED"))
//            .collect(Collectors.toList());
//        model.addAttribute("currentOrders", currentOrders);
//        model.addAttribute("pastOrders", pastOrders);
//        return "myorders"; // returns myorders.html
//    }
//	
//	@GetMapping("/trackOrders")
//	public String showTrackOrdersPage(Model model) {
//	    // Optionally, load initial orders data:
//	    List<Order> orders = serv.getAllOrdersAsEntities();
//	    model.addAttribute("orders", orders);
//	    
//	    return "trackOrders"; // This will resolve to trackOrders.html in your templates folder.
//	}
//	
////	@GetMapping("/search")
////    @ResponseBody
////    public ApiResponse searchOrders(@RequestParam(required = false) String search) {
////        try {
////            List<Order> orders;
////           // List<Order> orderAsEntities;
////            if (search == null || search.isBlank()) {
////                // If no search param => return all
////            	orders = serv.getAllOrdersAsEntities();
////            } else {
////                // Attempt to find orders by user info
////                // You can parse 'search' to see if it's numeric for userId or mobile, or
////                // if it contains '@' for email, etc. Or have a separate approach:
////                orders = serv.searchOrdersByUserGeneric(search);
////            }
////            if (orders.isEmpty()) {
////                return new ApiResponse("No orders found", null);
////            }
////           
////            return new ApiResponse("Found orders",orders);
////        } catch(Exception e) {
////            return new ApiResponse(e.getMessage(), null);
////        }
////    }
//	
//	@GetMapping("/search")
//    @ResponseBody
//    public ApiResponse searchOrders(@RequestParam(required = false) String search) {
//        try {
//            List<Order> orders;
//            if(search == null || search.isBlank()){
//                orders = serv.getAllOrdersAsEntities();
//            } else {
//                orders = serv.searchOrdersByUserGeneric(search);
//            }
//            if(orders.isEmpty()){
//                return new ApiResponse("No orders found", null);
//            }
//            return new ApiResponse("Found orders", orders);
//        } catch(Exception e) {
//            return new ApiResponse(e.getMessage(), null);
//        }
//    }
//	
//	 @PutMapping("/updateStatus/{orderId}")
//	    @ResponseBody
//	    public ApiResponse updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
//	        try {
//	            Order updated = serv.updateOrderStatus(orderId, status);
//	            return new ApiResponse("Order status updated successfully!", updated);
//	        } catch(Exception e) {
//	            return new ApiResponse(e.getMessage(), null);
//	        }
//	    }
//	 
//	 @PostMapping("/placeorder")
//	 public String placeOrder(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
//		 try {
//			 Order order = serv.placeOrder(userId);
//	         redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
//	         return "redirect:/onlinefoodDelivery/orders/myorders";
//	     } catch (Exception e) {
//	    	 redirectAttributes.addFlashAttribute("error", e.getMessage());
//	         return "redirect:/onlinefoodDelivery/orders/myorders";
//	     }
//	 }
//	
////	@PostMapping("/placeorder")
////	public ResponseEntity<ApiResponse> createOrder(@RequestParam Long userId){
////		try {
////			Order order = serv.placeOrder(userId);
////			return ResponseEntity.ok(new ApiResponse("Order Successfully placed !!!!", order));
////		}catch (Exception e) {
////			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
////		}
////	}
//	
//	@GetMapping("/get/order/{orderId}")
//	public ResponseEntity<ApiResponse> getOrderById(Long orderId){
//		try {
//			OrderDto order = serv.getOrder(orderId);
//			return ResponseEntity.ok(new ApiResponse("Found !!!!", order));
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
//		}
//	}
//	
////	@GetMapping("/get/ordersby/user/{userId}")
////	public ResponseEntity<ApiResponse> getUserOrders(Long userId){
////		try {
////			List<OrderDto> orders = serv.getUserOrder(userId);
////			return ResponseEntity.ok(new ApiResponse("Found !!!!", orders));
////		}catch (Exception e) {
////			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
////		}
////	}
//	
//	@GetMapping("/get/ordersby/user")
//    @ResponseBody
//    public ApiResponse getOrdersByUser(@RequestParam Long userId) {
//        try {
//            List<OrderDto> orders = serv.getUserOrder(userId);
//            return new ApiResponse("Found orders", orders);
//        } catch(Exception e) {
//            return new ApiResponse(e.getMessage(), null);
//        }
//    }
	
}
