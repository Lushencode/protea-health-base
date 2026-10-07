package com.proteahealth;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/** Local demo domain. A server must enforce the same rules on real orders. */
public class oldPharmacistState implements Serializable {
    private static final long serialVersionUID = 1L;
    public Profile profile = new Profile();
    public List<Medicine> medicines = new ArrayList<>();
    public List<Order> orders = new ArrayList<>();
    public List<String> events = new ArrayList<>();
    public boolean darkMode;
    public static class Profile implements Serializable {
        public String name = "Sample Protea Pharmacy", address = "Demo address — replace in Profile", phone = "", details = "";
        public String opens = "08:00", closes = "17:00";
        public boolean delivery = true;
        public long deliveryFee = 3500;
    }
    public static class Medicine implements Serializable {
        public String id, name, strength;
        public long price;
        public int stock;
        public Medicine(String id, String name, String strength, long price, int stock) {
            this.id=id; this.name=name; this.strength=strength; this.price=price; this.stock=stock;
        }
    }
    public static class Line implements Serializable {
        public String medicineId, description;
        public int quantity;
        public long unitPrice;
        public Line(String id, String description, int quantity, long unitPrice) {
            this.medicineId=id; this.description=description; this.quantity=quantity; this.unitPrice=unitPrice;
        }
    }
    public enum Status { RECEIVED, VERIFIED, PREPARING, READY, DISPATCHED, COMPLETED, REJECTED, CANCELLED }
    public static class Order implements Serializable {
        public String id, patient, phone, address, prescription, attachmentUri = "", reviewNote = "";
        public String readyMessage = "";
        public boolean delivery, refill, reserved;
        public long deliveryFee;
        public Status status = Status.RECEIVED;
        public List<Line> lines = new ArrayList<>();
        public long total() {
            long amount=delivery ? deliveryFee : 0;
            for(Line line:lines) amount=Math.addExact(amount,Math.multiplyExact(line.unitPrice,line.quantity));
            return amount;
        }
    }
    public static oldPharmacistState sample() {
        oldPharmacistState s=new oldPharmacistState();
        s.medicines.add(new Medicine("metformin", "Metformin", "500 mg · 60 tablets",14500,12));
        s.medicines.add(new Medicine("amlodipine", "Amlodipine", "5 mg · 30 tablets",12000,8));
        s.medicines.add(new Medicine("vitamin-d", "Vitamin D", "1000 IU · 30 tablets",7500,0));
        s.orders.add(sampleOrder("DEMO-101", "Sample Patient A", "amlodipine", false, false));
        s.orders.add(sampleOrder("DEMO-102", "Sample Patient B", "metformin", true, true));
        s.orders.add(sampleOrder("DEMO-103", "Sample Patient C", "vitamin-d", false, true));
        for(Order o:s.orders) {
            Medicine m=s.medicine(o.lines.get(0).medicineId);
            o.lines.get(0).description=m.name+" · "+m.strength;
            o.lines.get(0).unitPrice=m.price;
        }
        return s;
    }
    private static Order sampleOrder(String id,String patient,String medicine,boolean delivery,boolean refill) {
        Order o=new Order(); o.id=id; o.patient=patient; o.phone="Not provided (sample)";
        o.delivery=delivery; o.refill=refill; o.address=delivery ? "Sample delivery address — demonstration only" : "Collection at pharmacy";
        o.deliveryFee=delivery?3500:0;
        o.prescription="DEMO prescription reference RX-"+id+"\nPrescriber: Sample Doctor\nRequested item: "+medicine+"\nQuantity: 1 pack\nRefill request: "+refill+"\nFictional record for workflow testing. No uploaded image has been received.";
        o.lines.add(new Line(medicine,"",1,0)); return o;
    }
    public Medicine medicine(String id) {
        for(Medicine m:medicines) if(m.id.equals(id)) return m;
        throw new IllegalArgumentException("Medication not found");
    }
    public Order order(String id) {
        for(Order o:orders) if(o.id.equals(id)) return o;
        throw new IllegalArgumentException("Order not found");
    }
    public static long cents(String input) {
        try {
            long result=new BigDecimal(input.trim()).movePointRight(2).longValueExact();
            if(result<0 || result>100000000) throw new IllegalArgumentException();
            return result;
        } catch(Exception e) { throw new IllegalArgumentException("Enter a non-negative amount with at most 2 decimal places (no R symbol)"); }
    }
    public static String money(long cents) { return "R"+BigDecimal.valueOf(cents,2).toPlainString(); }
    public static void hours(String opens,String closes) {
        if(!opens.matches("([01]\\d|2[0-3]):[0-5]\\d") || !closes.matches("([01]\\d|2[0-3]):[0-5]\\d"))
            throw new IllegalArgumentException("Use 24-hour HH:mm for opening and closing times");
        if(opens.equals(closes)) throw new IllegalArgumentException("Opening and closing times must differ");
    }
    public void verify(String id,String note,boolean checked) {
        Order o=order(id); require(o.status==Status.RECEIVED,"Only received requests can be verified");
        require(checked,"Confirm the prescription checks first");
        require(!o.prescription.trim().isEmpty() || !o.attachmentUri.isEmpty(),"A prescription is required");
        require(!note.trim().isEmpty(),"Enter your verification notes");
        o.reviewNote=note.trim(); o.status=Status.VERIFIED; event(o,"Prescription verified manually");
    }
    public void reject(String id,String reason) {
        Order o=order(id); require(o.status==Status.RECEIVED || o.status==Status.VERIFIED,"Cannot reject after preparation starts");
        require(!reason.trim().isEmpty(),"Enter a rejection reason");
        o.reviewNote=reason.trim(); o.status=Status.REJECTED; event(o,"Rejected: "+reason.trim());
    }
    public void prepare(String id) {
        Order o=order(id); require(o.status==Status.VERIFIED,"Verify the prescription before preparation");
        Map<String,Integer> quantities=new HashMap<>();
        for(Line l:o.lines) { require(l.quantity>0,"Invalid item quantity"); quantities.merge(l.medicineId,l.quantity,Math::addExact); }
        require(!quantities.isEmpty(),"Order has no items");
        for(Map.Entry<String,Integer> e:quantities.entrySet()) require(medicine(e.getKey()).stock>=e.getValue(),"Insufficient stock: "+medicine(e.getKey()).name);
        for(Map.Entry<String,Integer> e:quantities.entrySet()) medicine(e.getKey()).stock-=e.getValue();
        o.reserved=true; o.status=Status.PREPARING; event(o,"Stock reserved; preparing order");
    }
    public void advance(String id) {
        Order o=order(id);
        if(o.status==Status.PREPARING) o.status=Status.READY;
        else if(o.status==Status.READY) o.status=o.delivery?Status.DISPATCHED:Status.COMPLETED;
        else if(o.status==Status.DISPATCHED && o.delivery) o.status=Status.COMPLETED;
        else throw new IllegalArgumentException("Order cannot advance from this status");
        event(o,"Status: "+o.status);
    }
    public void cancel(String id,String reason) {
        Order o=order(id);
        require(o.status!=Status.COMPLETED && o.status!=Status.DISPATCHED && o.status!=Status.REJECTED && o.status!=Status.CANCELLED,"This order can no longer be cancelled");
        require(!reason.trim().isEmpty(),"Enter a cancellation reason");
        if(o.reserved) { for(Line l:o.lines) medicine(l.medicineId).stock+=l.quantity; o.reserved=false; }
        o.status=Status.CANCELLED; event(o,"Cancelled: "+reason.trim());
    }
    public void readyNotification(String id) {
        Order o=order(id); require(o.status==Status.READY,"Only ready orders can receive a readiness notification");
        require(o.readyMessage.isEmpty(),"A local readiness notification already exists");
        o.readyMessage="Order "+id+(o.delivery?" is ready for delivery.":" is ready for collection.");
        event(o,"Notification added to LOCAL outbox; not sent to patient");
    }
    private void event(Order o,String action) { events.add(new Date()+" · "+o.id+" · "+action); }
    private static void require(boolean value,String message) { if(!value) throw new IllegalArgumentException(message); }
    public oldPharmacistState copy() {
        try {
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();
            try(ObjectOutputStream output=new ObjectOutputStream(bytes)) { output.writeObject(this); }
            try(ObjectInputStream input=new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) { return (oldPharmacistState)input.readObject(); }
        } catch(Exception e) { throw new IllegalStateException("Unable to copy local data",e); }
    }
}
