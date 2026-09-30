package com.proteahealth.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val PrimaryTeal = Color(0xFF00796B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacistHomeScreen() {
    // Rich Sample Orders so you can preview the layout immediately
    var ordersList by remember {
        mutableStateOf(
            listOf(
                PatientOrder(
                    id = "ORD-101",
                    patientName = "Mahek",
                    patientSurname = "Lala",
                    items = listOf(
                        MedicationItem("Amlodipine", "5mg - 30 Tablets", 120.00),
                        MedicationItem("Salbutamol Inhaler", "100mcg", 85.50)
                    ),
                    collectionTime = "14:30 Today",
                    status = OrderStatus.RECEIVED
                ),
                PatientOrder(
                    id = "ORD-102",
                    patientName = "Thabo",
                    patientSurname = "Mokoena",
                    items = listOf(
                        MedicationItem("Metformin", "500mg - 60 Tablets", 145.00),
                        MedicationItem("Vitamin C", "1000mg Effervescent", 65.00)
                    ),
                    collectionTime = "15:15 Today",
                    status = OrderStatus.RECEIVED
                ),
                PatientOrder(
                    id = "ORD-103",
                    patientName = "Sarah",
                    patientSurname = "Jenkins",
                    items = listOf(
                        MedicationItem("Atorvastatin", "20mg - 30 Tablets", 210.00)
                    ),
                    collectionTime = "16:00 Today",
                    status = OrderStatus.PACKED
                )
            )
        )
    }

    var selectedFilter by remember { mutableStateOf<OrderStatus?>(null) }

    val activeOrders = remember(ordersList, selectedFilter) {
        ordersList.filter { order ->
            order.status != OrderStatus.COLLECTED &&
                    (selectedFilter == null || order.status == selectedFilter)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Pharmacy Fulfillment", fontWeight = FontWeight.Bold)
                        Text(
                            "${activeOrders.size} active orders pending",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Filter Pills Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All Active (${ordersList.count { it.status != OrderStatus.COLLECTED }})") },
                    shape = RoundedCornerShape(20.dp)
                )
                FilterChip(
                    selected = selectedFilter == OrderStatus.RECEIVED,
                    onClick = { selectedFilter = OrderStatus.RECEIVED },
                    label = { Text("Received") },
                    shape = RoundedCornerShape(20.dp)
                )
                FilterChip(
                    selected = selectedFilter == OrderStatus.PACKED,
                    onClick = { selectedFilter = OrderStatus.PACKED },
                    label = { Text("Packed") },
                    shape = RoundedCornerShape(20.dp)
                )
            }

            if (activeOrders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryTeal,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "All caught up!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "No pending orders in this queue.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(activeOrders, key = { it.id }) { order ->
                        OrderItemCard(
                            order = order,
                            onStatusChange = { newStatus ->
                                ordersList = ordersList.map {
                                    if (it.id == order.id) it.copy(status = newStatus) else it
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(
    order: PatientOrder,
    onStatusChange: (OrderStatus) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Patient Name & Collection Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PrimaryTeal.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${order.patientName.take(1)}${order.patientSurname.take(1)}",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTeal
                        )
                    }
                    Column {
                        Text(
                            text = "${order.patientName} ${order.patientSurname}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = order.id,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = PrimaryTeal
                        )
                        Text(
                            text = order.collectionTime,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Ordered Items
            Text(
                text = "Medications Ordered:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Medication,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = PrimaryTeal
                            )
                            Text(
                                text = "${item.name} (${item.dosage})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "R${String.format("%.2f", item.price)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Total Price Calculation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total Price:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "R${String.format("%.2f", order.totalPrice)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryTeal
                )
            }

            // Action Buttons Workflow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (order.status) {
                    OrderStatus.RECEIVED -> {
                        Button(
                            onClick = { onStatusChange(OrderStatus.PACKED) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                        ) {
                            Icon(Icons.Default.Inventory, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mark as Packed")
                        }
                    }

                    OrderStatus.PACKED -> {
                        OutlinedButton(
                            onClick = { onStatusChange(OrderStatus.RECEIVED) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Unpack")
                        }
                        Button(
                            onClick = { onStatusChange(OrderStatus.COLLECTED) },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark Collected")
                        }
                    }

                    OrderStatus.COLLECTED -> {}
                }
            }
        }
    }
}