package com.proteahealth.data;
import com.proteahealth.model.Medication;
import com.proteahealth.model.Order;
import com.proteahealth.model.Pharmacy;
import com.proteahealth.model.Prescription;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockDataProvider {

    public static List<Prescription> getPrescriptions() {
        List<Prescription> prescriptions = new ArrayList<>();
        prescriptions.add(new Prescription("presc-001", "Metformin 500mg", 60, "Dr. Naidoo",
                "031 555 0192", "500mg", "Small round white pill", "For blood sugar control",
                "Morning", 60, 22, false));
        prescriptions.add(new Prescription("presc-002", "Amlodipine 5mg", 30, "Dr. Naidoo",
                "031 555 0192", "5mg", "Small oval blue pill", "For blood pressure",
                "Morning", 30, 6, false));
        prescriptions.add(new Prescription("presc-003", "Insulin Glargine", 1, "Dr. Pillay",
                "031 555 0455", "10 units", "Clear liquid, pre-filled pen", "For blood sugar control",
                "Bedtime", 30, 30, false));
        return prescriptions;
    }
    public static List<Order> getOrderHistory() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("ord-001", "Clicks Pharmacy - Musgrave", "Metformin 500mg", 60,
                "1.2km", "In Stock", 185.50, "2026-08-02", "Collected", "Card", "RCT-1001", true));
        orders.add(new Order("ord-002", "Dis-Chem Pharmacy - Gateway", "Amlodipine 5mg", 30,
                "3km", "In Stock", 96.00, "2026-09-10", "Pending", "Medical Aid", "RCT-1002", false));
        orders.add(new Order("ord-003", "Alpha Pharm - Umhlanga", "Insulin Glargine", 1,
                "800m", "Cash Only", 412.75, "2026-09-15", "Ready for Pickup", "Cash", "RCT-1003", false));
        return orders;
    }
    public static List<Pharmacy> getPharmacies() {
        List<Pharmacy> pharmacies = new ArrayList<>();

        pharmacies.add(new Pharmacy(
                "ph-001",
                "Clicks Pharmacy - Musgrave",
                "Musgrave Centre, Berea, Durban",
                "1.2km",
                "Mon-Fri 08:00-19:00, Sat 08:00-16:00",
                Arrays.asList(
                        new Medication("Metformin 500mg", 185.50, "In Stock"),
                        new Medication("Amlodipine 5mg", 102.00, "In Stock"),
                        new Medication("Insulin Glargine", 430.00, "Low Stock")
                )
        ));

        pharmacies.add(new Pharmacy(
                "ph-002",
                "Dis-Chem Pharmacy - Gateway",
                "Gateway Theatre of Shopping, Umhlanga",
                "3km",
                "Mon-Sun 08:00-20:00",
                Arrays.asList(
                        new Medication("Metformin 500mg", 179.99, "In Stock"),
                        new Medication("Amlodipine 5mg", 96.00, "In Stock"),
                        new Medication("Insulin Glargine", 425.50, "In Stock")
                )
        ));

        pharmacies.add(new Pharmacy(
                "ph-003",
                "Alpha Pharm - Umhlanga",
                "Chartwell Drive, Umhlanga",
                "800m",
                "Mon-Fri 08:00-18:00, Sat 08:00-13:00",
                Arrays.asList(
                        new Medication("Metformin 500mg", 192.00, "In Stock"),
                        new Medication("Amlodipine 5mg", 99.50, "Cash Only"),
                        new Medication("Insulin Glargine", 412.75, "Out of Stock")
                )
        ));

        return pharmacies;
    }

}