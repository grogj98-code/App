
package com.carter.lifeos

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

private val Black = Color(0xFF080808)
private val Panel = Color(0xFF151515)
private val Red = Color(0xFFE50935)
private val White = Color(0xFFF5F5F5)
private val Muted = Color(0xFF929292)

data class LifeItem(
    val title: String,
    val category: String,
    val details: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeOS(this)
        }
    }
}

@Composable
fun LifeOS(context: Context) {
    var page by remember { mutableStateOf("Home") }
    var items by remember { mutableStateOf(loadItems(context)) }
    var showDialog by remember { mutableStateOf(false) }
    var chat by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(listOf("CART3R AI: System online. How can I help?"))
    }

    fun addItem(item: LifeItem) {
        items = items + item
        saveItems(context, items)
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Black,
            surface = Panel,
            primary = Red,
            onBackground = White,
            onSurface = White
        )
    ) {
        Scaffold(
            containerColor = Black,
            bottomBar = {
                NavigationBar(
                    containerColor = Panel,
                    contentColor = White
                ) {
                    val pages = listOf(
                        "Home", "Schedule", "School",
                        "Work", "Projects", "AI"
                    )

                    pages.forEach { destination ->
                        NavigationBarItem(
                            selected = page == destination,
                            onClick = { page = destination },
                            icon = {
                                Icon(
                                    imageVector = when (destination) {
                                        "Home" -> Icons.Default.Home
                                        "Schedule" -> Icons.Default.CalendarMonth
                                        "School" -> Icons.Default.School
                                        "Work" -> Icons.Default.Work
                                        "Projects" -> Icons.Default.Folder
                                        else -> Icons.Default.SmartToy
                                    },
                                    contentDescription = destination
                                )
                            },
                            label = {
                                Text(destination, fontSize = 9.sp)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Red,
                                selectedTextColor = Red,
                                indicatorColor = Color(0xFF35101A),
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            },
            floatingActionButton = {
                if (page != "AI") {
                    FloatingActionButton(
                        onClick = { showDialog = true },
                        containerColor = Red,
                        contentColor = White
                    ) {
                        Icon(Icons.Default.Add, "Add item")
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Black)
                    .padding(padding)
                    .padding(18.dp)
            ) {
                Header()
                Spacer(Modifier.height(24.dp))

                when (page) {
                    "Home" -> HomePage(items, onNavigate = { page = it })
                    "AI" -> AIPage(
                        chat = chat,
                        onChatChange = { chat = it },
                        messages = messages,
                        onSend = {
                            if (chat.isNotBlank()) {
                                messages = messages + listOf(
                                    "YOU: $chat",
                                    "CART3R AI: I received your message. AI integration is coming next."
                                )
                                chat = ""
                            }
                        }
                    )
                    else -> CategoryPage(
                        page = page,
                        items = items.filter { it.category == page }
                    )
                }
            }
        }

        if (showDialog) {
            AddItemDialog(
                onDismiss = { showDialog = false },
                onAdd = {
                    addItem(it)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "CART3R",
                color = Red,
                fontSize = 25.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "LIFE OPERATING SYSTEM",
                color = Muted,
                fontSize = 9.sp,
                letterSpacing = 2.sp
            )
        }

        Surface(
            color = Color(0xFF35101A),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                "SYSTEM ONLINE",
                color = Red,
                modifier = Modifier.padding(10.dp),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HomePage(
    items: List<LifeItem>,
    onNavigate: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text(
                "COMMAND CENTER",
                color = White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
            Text("Your life. Organised.", color = Muted)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("SCHEDULE", items.count { it.category == "Schedule" }.toString(), Modifier.weight(1f))
                StatCard("SCHOOL", items.count { it.category == "School" }.toString(), Modifier.weight(1f))
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("WORK", items.count { it.category == "Work" }.toString(), Modifier.weight(1f))
                StatCard("PROJECTS", items.count { it.category == "Projects" }.toString(), Modifier.weight(1f))
            }
        }

        item {
            Text(
                "QUICK ACCESS",
                color = Red,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        items(listOf("Schedule", "School", "Work", "Projects", "AI")) { section ->
            Surface(
                color = Panel,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(section) }
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(section, color = White)
                    Icon(Icons.Default.ChevronRight, null, tint = Red)
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = Panel,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = Muted, fontSize = 10.sp)
            Spacer(Modifier.height(8.dp))
            Text(value, color = Red, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CategoryPage(page: String, items: List<LifeItem>) {
    Column {
        Text(
            page.uppercase(),
            color = White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Manage your $page.", color = Muted)
        Spacer(Modifier.height(18.dp))

        if (items.isEmpty()) {
            Text("No entries yet. Press + to add one.", color = Muted)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items) { item ->
                    Surface(
                        color = Panel,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(item.title, color = White, fontWeight = FontWeight.Bold)
                            if (item.details.isNotBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text(item.details, color = Muted)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onAdd: (LifeItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Schedule") }
    val categories = listOf("Schedule", "School", "Work", "Projects")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Panel,
        title = { Text("NEW ENTRY", color = Red) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Details") }
                )
                categories.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = category == it,
                            onClick = { category = it }
                        )
                        Text(it, color = White)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(LifeItem(title, category, details))
                    }
                }
            ) {
                Text("SAVE", color = Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Muted)
            }
        }
    )
}

@Composable
fun AIPage(
    chat: String,
    onChatChange: (String) -> Unit,
    messages: List<String>,
    onSend: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Text("CART3R AI", color = Red, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("PERSONAL ASSISTANT", color = Muted, fontSize = 10.sp)

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) {
                Surface(color = Panel, shape = RoundedCornerShape(10.dp)) {
                    Text(it, color = White, modifier = Modifier.padding(14.dp))
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = chat,
                onValueChange = onChatChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask CART3R...") }
            )
            IconButton(onClick = onSend) {
                Icon(Icons.Default.Send, null, tint = Red)
            }
        }
    }
}

private fun saveItems(context: Context, items: List<LifeItem>) {
    val array = JSONArray()
    items.forEach {
        array.put(
            JSONObject().apply {
                put("title", it.title)
                put("category", it.category)
                put("details", it.details)
            }
        )
    }

    context.getSharedPreferences("life_os", Context.MODE_PRIVATE)
        .edit()
        .putString("items", array.toString())
        .apply()
}

private fun loadItems(context: Context): List<LifeItem> {
    return try {
        val raw = context.getSharedPreferences("life_os", Context.MODE_PRIVATE)
            .getString("items", "[]") ?: "[]"
        val array = JSONArray(raw)

        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            LifeItem(
                item.getString("title"),
                item.getString("category"),
                item.optString("details")
            )
        }
    } catch (_: Exception) {
        emptyList()
    }
}
