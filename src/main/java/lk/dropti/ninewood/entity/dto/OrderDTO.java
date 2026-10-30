package lk.dropti.ninewood.entity.dto;


import java.sql.Timestamp;
import java.time.LocalDateTime;

public class OrderDTO {

    private int id;
    private int statusId;
    private int userId;
    private String userName;
//    private DateTime createdAt;

//    private LocalDateTime updatedAt;
    private Double price;
    private int orderItemsCount;
    private String StatusName;
//

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }

//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }

//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }

    public int getOrderItemsCount() {
        return orderItemsCount;
    }

    public void setOrderItemsCount(int orderItemsCount) {
        this.orderItemsCount = orderItemsCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}




//
//        private int id;
//        private int statusId;
//        private int userId;
//        private int deliveryTypeId;
//
//
////        public OrderDTO(Long id, Long statusId, Long userId,
////                        Long deliveryTypeId) {
////            this.id = id;
////            this.statusId = statusId;
////            this.userId = userId;
////            this.deliveryTypeId = deliveryTypeId;
////        }
//
//    public OrderDTO() {
//
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getStatusId() {
//        return statusId;
//    }
//
//    public void setStatusId(int statusId) {
//        this.statusId = statusId;
//    }
//
//    public int getUserId() {
//        return userId;
//    }
//
//    public void setUserId(int userId) {
//        this.userId = userId;
//    }
//
//    public int getDeliveryTypeId() {
//        return deliveryTypeId;
//    }
//
//    public void setDeliveryTypeId(int deliveryTypeId) {
//        this.deliveryTypeId = deliveryTypeId;
//    }
//
//}
