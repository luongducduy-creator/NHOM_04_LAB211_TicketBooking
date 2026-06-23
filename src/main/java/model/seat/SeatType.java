package model.seat;

public enum SeatType {
     VIP,
     NORMAL;

     public static SeatType fromString(String value) {
        return SeatType.valueOf(value.toUpperCase());
     }
}
