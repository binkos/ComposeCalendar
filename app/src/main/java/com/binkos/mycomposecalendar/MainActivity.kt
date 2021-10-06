package com.binkos.mycomposecalendar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.binkos.mycomposecalendar.ui.theme.MyComposeCalendarTheme
import com.google.accompanist.flowlayout.FlowRow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeCalendarTheme {
                Calendar()

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        CalendarHeader(dateState = mutableStateOf(Date()))
                        Spacer(modifier = Modifier.height(4.dp))
                        CalendarMonths()
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(dateState: State<Date>) {
    Row(
        modifier = Modifier.height(36.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_arrow), contentDescription = "arrow left")
        Spacer(modifier = Modifier.weight(1f))
        Text(text = dateState.value.toString())
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            modifier = Modifier.rotate(180f),
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = "arrow right"
        )
    }
}

data class Month(
    val id: Int,
    val name: String,
    val isSelected: Boolean
)

fun List<Month>.updateMonthById(id: Int) = map {
    return@map when (it.id == id) {
        true -> it.copy(isSelected = true)
        false ->
            if (it.isSelected) it.copy(isSelected = false)
            else it
    }
}

@Composable
fun CalendarMonths() {
    var months by remember {
        mutableStateOf(
            listOf(
                Month(0, "Mon", false),
                Month(1, "Tue", false),
                Month(2, "Wed", false),
                Month(3, "Thu", false),
                Month(4, "Fri", false),
                Month(5, "Sut", false),
                Month(6, "Sun", false),
            )
        )
    }

    val lambda: (Int) -> Unit = {
        months.updateMonthById(it).also { updated -> months = updated }
    }
    Row {
        val modifier = Modifier.weight(1f)
        Month(modifier, months[0], lambda)
        Month(modifier, months[1], lambda)
        Month(modifier, months[2], lambda)
        Month(modifier, months[3], lambda)
        Month(modifier, months[4], lambda)
        Month(modifier, months[5], lambda)
        Month(modifier, months[6], lambda)
    }
}

@Composable
fun Month(
    modifier: Modifier,
    model: Month,
    onClicked: (Int) -> Unit
) {
    Text(
        modifier = modifier.clickable { onClicked(model.id) },
        text = model.name,
        textAlign = TextAlign.Center,
        textDecoration = if (model.isSelected) TextDecoration.Underline else null
    )
}

@Composable
fun Calendar() {
    val cal = Calendar.getInstance()
    val nextCal = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.MONTH, cal[Calendar.MONTH] + 1)
    }
    val prevCal = Calendar.getInstance().apply {
        set(Calendar.MONTH, cal[Calendar.MONTH] - 1)
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    }
    val firstDay = cal.minimalDaysInFirstWeek
    val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val context = LocalContext.current

    Toast.makeText(context, prevCal.time.toString(), Toast.LENGTH_SHORT).show()
    Toast.makeText(context, cal.time.toString(), Toast.LENGTH_SHORT).show()
    Toast.makeText(context, nextCal.minimalDaysInFirstWeek.toString(), Toast.LENGTH_SHORT).show()

    SimpleDateFormat("EEEE")
        .format(nextCal.time)
        .also {
            Log.d("Day", it)
        }
//    Toast.makeText(context, nextCal.getd, Toast.LENGTH_SHORT).show()

    Log.d("PREV", prevCal.time.toString())
    Log.d("CURRENT", cal.time.toString())
    Log.d("NEXT", nextCal.time.toString())
}

@Composable
fun LineDays(cal: Calendar) {
    FlowRow() {

    }
}