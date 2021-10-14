package com.binkos.mycomposecalendar

import android.os.Bundle
import android.text.format.DateFormat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.binkos.mycomposecalendar.ui.theme.MyComposeCalendarTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeCalendarTheme {
                var calendar by remember { mutableStateOf(Calendar.getInstance()) }
                var displayedCalendar by remember { mutableStateOf(Calendar.getInstance()) }
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        CalendarHeader(
                            dateState = displayedCalendar,
                            {
                                val updCal = calendar.previousMonth()
                                calendar = updCal
                                displayedCalendar = updCal
                            },
                            {
                                val updCal = calendar.nextMonth()
                                calendar = updCal
                                displayedCalendar = updCal
                            })
                        Spacer(modifier = Modifier.height(4.dp))
                        CalendarMonths()
                        Spacer(modifier = Modifier.height(4.dp))
                        DaysView(calendar) { displayedCalendar = calendarWithDate(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(
    dateState: Calendar,
    onPrevMonthClicked: (Calendar) -> Unit,
    onNextMonthClicked: (Calendar) -> Unit
) {
    Row(
        modifier = Modifier.height(36.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.clickable { onPrevMonthClicked(dateState) },
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = "arrow left"
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = DateFormat.format("yyyy-MM-d", dateState.time).toString())
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            modifier = Modifier
                .clickable { onNextMonthClicked(dateState) }
                .rotate(180f),
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = "arrow right"
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarMonths() {
    val months =
        listOf(
            Month(0, "Mon", false),
            Month(1, "Tue", false),
            Month(2, "Wed", false),
            Month(3, "Thu", false),
            Month(4, "Fri", false),
            Month(5, "Sut", false),
            Month(6, "Sun", false)
        )

    Row {
        val modifier = Modifier.weight(1f)
        months.forEach { Month(modifier, it) }
    }
}

@Composable
fun Month(modifier: Modifier, model: Month) {
    Text(
        modifier = modifier,
        text = model.name,
        textAlign = TextAlign.Center,
        textDecoration = if (model.isSelected) TextDecoration.Underline else null
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DaysView(
    calendar: Calendar,
    onItemClicked: (Date) -> Unit
) {
    val dates = remember(calendar) { calendar.getDates() }

    LazyVerticalGrid(cells = GridCells.Fixed(7)) {
        items(dates) {
            Text(
                modifier = Modifier.clickable { onItemClicked(it) },
                text = DateFormat.format("d", it).toString(),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun Calendar.nextMonth(): Calendar {
    val newCalendar = Calendar.getInstance().apply {
        time = this@nextMonth.time
    }
    val currentMonth = get(Calendar.MONTH)
    val currentYear = get(Calendar.YEAR)
    val currentDay = get(Calendar.DAY_OF_MONTH)

    val nextMonth = when (currentMonth == Calendar.DECEMBER) {
        true -> {
            newCalendar.set(Calendar.YEAR, currentYear + 1)
            Calendar.JANUARY
        }
        false -> currentMonth + 1
    }
    newCalendar.set(Calendar.DAY_OF_MONTH, currentDay)
    newCalendar.set(Calendar.MONTH, nextMonth)

    return newCalendar
}

private fun Calendar.previousMonth(): Calendar {
    val newCalendar = Calendar.getInstance().apply {
        time = this@previousMonth.time
    }
    val currentMonth = get(Calendar.MONTH)
    val currentYear = get(Calendar.YEAR)
    val currentDay = get(Calendar.DAY_OF_MONTH)

    val prevMonth = when (currentMonth == Calendar.JANUARY) {
        true -> {
            newCalendar.set(Calendar.YEAR, currentYear - 1)
            Calendar.DECEMBER
        }
        false -> {
            currentMonth - 1
        }
    }
    newCalendar.set(Calendar.DAY_OF_MONTH, currentDay)
    newCalendar.set(Calendar.MONTH, prevMonth)

    return newCalendar
}

private fun calendarWithDate(date: Date): Calendar = Calendar.getInstance().apply { time = date }

private fun Calendar.getDates(): List<Date> {
    set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfMonthInWeek = get(Calendar.DAY_OF_WEEK)
    val dates = mutableListOf<Date>()

    val allDays = getActualMaximum(Calendar.DAY_OF_MONTH)

    if (firstDayOfMonthInWeek != Calendar.MONDAY) {
        val prevMonth = previousMonth()
        val prevMonthDays = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        val visibleDaysInCalendar = firstDayOfMonthInWeek - 2
        val startVisibleDate = prevMonthDays - visibleDaysInCalendar
        for (i in startVisibleDate + 1..prevMonthDays) {
            prevMonth.set(Calendar.DAY_OF_MONTH, i)
            dates.add(prevMonth.time)
        }
    }

    for (i in 1..allDays) {
        set(Calendar.DAY_OF_MONTH, i)
        dates.add(time)
    }

    set(Calendar.DAY_OF_MONTH, allDays)
    val newDayOfWeek = get(Calendar.DAY_OF_WEEK)
    if (newDayOfWeek != Calendar.SUNDAY) {
        val nextMonth = nextMonth()
        for (i in 1..(7 - (newDayOfWeek - 1))) {
            nextMonth.set(Calendar.DAY_OF_MONTH, i)
            dates.add(nextMonth.time)
        }
    }

    return dates
}