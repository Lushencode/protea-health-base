package com.proteahealth.data;

import com.proteahealth.model.Order;
import com.proteahealth.model.Prescription;

import java.util.ArrayList;
import java.util.List;


public class MockDataProvider {


    // =========================================================
    // MOCK PRESCRIPTIONS
    // =========================================================

    public static List<Prescription> getPrescriptions() {

        List<Prescription> prescriptions =
                new ArrayList<>();


        prescriptions.add(
                new Prescription(
                        "presc-001",
                        "Metformin 500mg",
                        60,
                        "Dr. Naidoo",
                        "031 555 0192",
                        "500mg",
                        "Small round white pill",
                        "For blood sugar control",
                        "Morning",
                        60,
                        22,
                        false
                )
        );


        prescriptions.add(
                new Prescription(
                        "presc-002",
                        "Amlodipine 5mg",
                        30,
                        "Dr. Naidoo",
                        "031 555 0192",
                        "5mg",
                        "Small oval blue pill",
                        "For blood pressure",
                        "Morning",
                        30,
                        6,
                        false
                )
        );


        prescriptions.add(
                new Prescription(
                        "presc-003",
                        "Insulin Glargine",
                        1,
                        "Dr. Pillay",
                        "031 555 0455",
                        "10 units",
                        "Clear liquid, pre-filled pen",
                        "For blood sugar control",
                        "Bedtime",
                        30,
                        30,
                        false
                )
        );


        return prescriptions;
    }


    // =========================================================
    // MOCK ORDER HISTORY
    // =========================================================

    public static List<Order> getOrderHistory() {

        List<Order> orders =
                new ArrayList<>();


        orders.add(
                new Order(
                        "ord-001",
                        "Clicks Pharmacy - Musgrave",
                        "Metformin 500mg",
                        60,
                        "1.2km",
                        "In Stock",
                        185.50,
                        "2026-08-02",
                        "Collected",
                        "Card",
                        "RCT-1001",
                        true
                )
        );


        orders.add(
                new Order(
                        "ord-002",
                        "Dis-Chem Pharmacy - Gateway",
                        "Amlodipine 5mg",
                        30,
                        "3km",
                        "In Stock",
                        96.00,
                        "2026-09-10",
                        "Pending",
                        "Medical Aid",
                        "RCT-1002",
                        false
                )
        );


        orders.add(
                new Order(
                        "ord-003",
                        "Alpha Pharm - Umhlanga",
                        "Insulin Glargine",
                        1,
                        "800m",
                        "Cash Only",
                        412.75,
                        "2026-09-15",
                        "Ready for Pickup",
                        "Cash",
                        "RCT-1003",
                        false
                )
        );


        return orders;
    }
}
