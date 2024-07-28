package com.trm.alarmist.core.domain.di

import com.trm.alarmist.core.domain.usecase.AddAlarmUseCase
import com.trm.alarmist.core.domain.usecase.AddGroupUseCase
import com.trm.alarmist.core.domain.usecase.DeleteAlarmUseCase
import com.trm.alarmist.core.domain.usecase.DeleteGroupUseCase
import com.trm.alarmist.core.domain.usecase.EditAlarmUseCase
import com.trm.alarmist.core.domain.usecase.EditGroupUseCase
import com.trm.alarmist.core.domain.usecase.GetAlarmsInGroupFlowUseCase
import com.trm.alarmist.core.domain.usecase.GetAlarmsScheduledOnDateFlowUseCase
import com.trm.alarmist.core.domain.usecase.GetAndResetMissedAlarmsOnBootUseCase
import com.trm.alarmist.core.domain.usecase.GetGroupedAlarmsFlowUseCase
import com.trm.alarmist.core.domain.usecase.GetNextTodayAlarmUseCase
import com.trm.alarmist.core.domain.usecase.GetScheduledAlarmCountsForDateRangeUseCase
import com.trm.alarmist.core.domain.usecase.GetTodayAlarmsUseCase
import com.trm.alarmist.core.domain.usecase.IsAlarmScheduledToFireAtDateTimeUseCase
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffOnDateUseCase
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffUseCase
import com.trm.alarmist.core.domain.usecase.TurnAlarmOffOnDateUseCase
import com.trm.alarmist.core.domain.usecase.TurnAlarmOffUseCase
import com.trm.alarmist.core.domain.usecase.TurnAlarmOnOnDateUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnSnoozeUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmScheduleUseCase
import com.trm.alarmist.core.domain.usecase.UpdateGroupOnOffUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
  factoryOf(::AddAlarmUseCase)
  factoryOf(::EditAlarmUseCase)
  factoryOf(::DeleteAlarmUseCase)
  factoryOf(::ToggleAlarmOnOffUseCase)
  factoryOf(::UpdateGroupOnOffUseCase)
  factoryOf(::UpdateAlarmScheduleUseCase)
  factoryOf(::UpdateAlarmOnDismissUseCase)
  factoryOf(::UpdateAlarmOnSnoozeUseCase)
  factoryOf(::GetGroupedAlarmsFlowUseCase)
  factoryOf(::GetAlarmsInGroupFlowUseCase)
  factoryOf(::GetAndResetMissedAlarmsOnBootUseCase)
  factoryOf(::GetAlarmsScheduledOnDateFlowUseCase)
  factoryOf(::GetTodayAlarmsUseCase)
  factoryOf(::GetNextTodayAlarmUseCase)
  factoryOf(::GetScheduledAlarmCountsForDateRangeUseCase)
  factoryOf(::IsAlarmScheduledToFireAtDateTimeUseCase)
  factoryOf(::TurnAlarmOffOnDateUseCase)
  factoryOf(::TurnAlarmOnOnDateUseCase)
  factoryOf(::ToggleAlarmOnOffOnDateUseCase)
  factoryOf(::TurnAlarmOffUseCase)
  factoryOf(::AddGroupUseCase)
  factoryOf(::EditGroupUseCase)
  factoryOf(::DeleteGroupUseCase)
}
