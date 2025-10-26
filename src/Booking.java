public class Booking {
    private int id;
    private String customerName, roomType;
    private String checkIn, checkOut;
    private double pricePerNight, extraCharges, totalAmount;

    public Booking(String customerName, String roomType,
                   String checkIn, String checkOut,
                   double pricePerNight, double extraCharges) {
        this.customerName = customerName;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.pricePerNight = pricePerNight;
        this.extraCharges = extraCharges;
    }

    public double calculateTotal(int nights) {
        return (pricePerNight * nights) + extraCharges;
    }
}
