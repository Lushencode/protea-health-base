package com.proteahealth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proteahealth.ui.theme.PurpleGrey80

@Composable
fun ProfileContainerScreen() {
    var activeRole by remember { mutableStateOf(UserRole.PATIENT) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Modern Floating Pill Selector Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                UserRole.values().forEach { role ->
                    val selected = activeRole == role
                    FilterChip(
                        selected = selected,
                        onClick = { activeRole = role },
                        label = {
                            Text(
                                role.name.lowercase().capitalize(),
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            val icon = when (role) {
                                UserRole.PATIENT -> Icons.Default.Person
                                UserRole.DOCTOR -> Icons.Default.MedicalServices
                                UserRole.PHARMACIST -> Icons.Default.LocalPharmacy
                            }
                            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurpleGrey80,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (activeRole) {
                UserRole.PATIENT -> PatientProfileView()
                UserRole.DOCTOR -> DoctorProfileView(onSwitchToPatient = { activeRole = UserRole.PATIENT })
                UserRole.PHARMACIST -> PharmacistProfileView(onSwitchToPatient = { activeRole = UserRole.PATIENT })
            }
        }
    }
}

@Composable
fun PatientProfileView() {
    val patient = PatientProfile(
        name = "Mahek", surname = "Lala", idNumber = "030101XXXXXXX", age = 23,
        email = "mahek@example.com", phone = "+27 82 123 4567", homeAddress = "Johannesburg, South Africa",
        emergencyContacts = "+27 83 987 6543 (Father)",
        personalConditions = listOf("Hypertension", "Asthma"),
        prescriptions = listOf("Amlodipine 5mg - 1 Tablet Daily", "Salbutamol Inhaler - 2 Puffs as needed")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card Banner
        ModernProfileHeader(
            fullName = "${patient.name} ${patient.surname}",
            subtitle = "Patient Account",
            badge = "Out-of-Pocket / Private"
        )

        // Stat Row Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(modifier = Modifier.weight(1f), title = "Age", value = "${patient.age} yrs", icon = Icons.Default.Cake)
            StatCard(modifier = Modifier.weight(1f), title = "Prescriptions", value = "${patient.prescriptions.size} Active", icon = Icons.Default.Medication)
        }

        // Detailed Information Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Personal & Contact Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PurpleGrey80
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                InfoRow(icon = Icons.Default.Badge, label = "ID Number", value = patient.idNumber)
                InfoRow(icon = Icons.Default.Email, label = "Email Address", value = patient.email)
                InfoRow(icon = Icons.Default.Phone, label = "Phone Contact", value = patient.phone)
                InfoRow(icon = Icons.Default.Home, label = "Home Address", value = patient.homeAddress)
                InfoRow(icon = Icons.Default.ContactPhone, label = "Emergency Contact", value = patient.emergencyContacts)
            }
        }

        // Health Conditions
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Personal Conditions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PurpleGrey80
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    patient.personalConditions.forEach { condition ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(condition, fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.Healing, contentDescription = null, modifier = Modifier.size(16.dp), tint = PurpleGrey80) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Active Prescriptions Header
        Text(
            text = "Active Prescriptions (View-Only)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PurpleGrey80
        )

        patient.prescriptions.forEach { rx ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PurpleGrey80.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Medication, contentDescription = null, tint = PurpleGrey80)
                    }
                    Column {
                        Text(text = rx, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text(text = "Issued by Primary Doctor", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorProfileView(onSwitchToPatient: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ModernProfileHeader(
            fullName = "Dr. Sarah Govender",
            subtitle = "General Practitioner",
            badge = "Verified Practitioner"
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Practice Credentials", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PurpleGrey80)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                InfoRow(icon = Icons.Default.LocalHospital, label = "Clinic Name", value = "Johannesburg Central Health Centre")
                InfoRow(icon = Icons.Default.LocationOn, label = "Location", value = "Johannesburg, South Africa")
                InfoRow(icon = Icons.Default.Work, label = "Experience", value = "8 Years Professional Practice")
                InfoRow(icon = Icons.Default.Payments, label = "Consultation Rate", value = "R350.00 (Out-of-Pocket)")
                InfoRow(icon = Icons.Default.Verified, label = "HPCSA Registration", value = "MP-892134")
            }
        }

        Button(
            onClick = onSwitchToPatient,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleGrey80)
        ) {
            Icon(Icons.Default.SwitchAccount, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Switch View to Patient", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PharmacistProfileView(onSwitchToPatient: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ModernProfileHeader(
            fullName = "Sipho Nkosi",
            subtitle = "Licensed Retail Pharmacist",
            badge = "Active SAPC Member"
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Pharmacy & Branch Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PurpleGrey80)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                InfoRow(icon = Icons.Default.Email, label = "Email", value = "sipho@pharmacy.co.za")
                InfoRow(icon = Icons.Default.Phone, label = "Work Phone", value = "+27 11 987 6543")
                InfoRow(icon = Icons.Default.Store, label = "Pharmacy Branch", value = "Braamfontein Retail Pharmacy")
                InfoRow(icon = Icons.Default.WorkHistory, label = "Experience", value = "6 Years")
                InfoRow(icon = Icons.Default.Verified, label = "SAPC Registration", value = "YP-109238")
                InfoRow(icon = Icons.Default.Schedule, label = "Branch Hours", value = "Mon - Fri: 08:00 - 17:00")
            }
        }

        Button(
            onClick = onSwitchToPatient,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleGrey80)
        ) {
            Icon(Icons.Default.SwitchAccount, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Switch View to Patient", fontWeight = FontWeight.Bold)
        }
    }
}

// Reusable Fancy Components

@Composable
fun ModernProfileHeader(fullName: String, subtitle: String, badge: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gradient Avatar Box
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PurpleGrey80, PurpleGrey80.copy(alpha = 0.7f))
                        )
                    )
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fullName.take(1),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Surface(
                    color = PurpleGrey80.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badge,
                        color = PurpleGrey80,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = PurpleGrey80, modifier = Modifier.size(22.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PurpleGrey80.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PurpleGrey80, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

private fun String.capitalize(): String = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }