import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.trm.alarmist.core.ui.DayOfWeekEllipsizedContent
import epicarchitect.calendar.compose.basis.BasisDayOfWeekContent
import epicarchitect.calendar.compose.basis.config.DefaultBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.daysOfWeekSortedBy
import epicarchitect.calendar.compose.basis.firstDayOfWeek
import kotlinx.datetime.DayOfWeek

@Composable
fun DaysOfWeekRow(
  modifier: Modifier = Modifier,
  onDayOfWeekClick: ((DayOfWeek) -> Unit)? = null,
  dayOfWeekContent: BasisDayOfWeekContent = DayOfWeekEllipsizedContent,
) {
  Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
    daysOfWeekSortedBy(firstDayOfWeek()).forEach { dayOfWeek ->
      Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
        Box(
          modifier =
            Modifier.clip(DefaultBasisEpicCalendarConfig.dayOfWeekShape)
              .height(DefaultBasisEpicCalendarConfig.dayOfWeekViewHeight)
              .width(DefaultBasisEpicCalendarConfig.columnWidth)
              .let {
                if (onDayOfWeekClick == null) it else it.clickable { onDayOfWeekClick(dayOfWeek) }
              },
          contentAlignment = Alignment.Center,
        ) {
          dayOfWeekContent(dayOfWeek)
        }
      }
    }
  }
}
