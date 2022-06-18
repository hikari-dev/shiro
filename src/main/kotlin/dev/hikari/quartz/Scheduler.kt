package dev.hikari.quartz

import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory


private val scheduler = StdSchedulerFactory().scheduler

/**
 * Visit the page below to learn about cron expressions.
 * @see <a href="https://github.com/quartz-scheduler/quartz/blob/master/docs/tutorials/crontrigger.md">CronTrigger Tutorial</a>
 */
fun startSchedule() {
    val morningJob = JobBuilder
        .newJob(MorningTask::class.java)
        .withIdentity("MorningTask")
        .build()
    val morningTrigger = TriggerBuilder
        .newTrigger()
        .withIdentity("MorningTrigger")
        .withSchedule(CronScheduleBuilder.cronSchedule("00 00 08 * * ?"))
        .build()
    val moYuReminderJob = JobBuilder
        .newJob(MoYuReminderTask::class.java)
        .withIdentity("MoYuTask")
        .build()
    val moYuTrigger = TriggerBuilder
        .newTrigger()
        .withIdentity("MoYuTrigger")
        .withSchedule(CronScheduleBuilder.cronSchedule("00 00 15 * * ?"))
        .build()
    val dailyNewsJob = JobBuilder
        .newJob(DailyNewsTask::class.java)
        .withIdentity("DailyNewsTask")
        .build()
    val dailyNewsTrigger = TriggerBuilder
        .newTrigger()
        .withIdentity("DailyNewsTrigger")
        .withSchedule(CronScheduleBuilder.cronSchedule("00 00 08 * * ?"))
        .build()
    scheduler.scheduleJobs(
        mapOf(
//            morningJob to setOf(morningTrigger),
//            moYuReminderJob to setOf(moYuTrigger),
            dailyNewsJob to setOf(dailyNewsTrigger),
        ), false
    )
    scheduler.start()
}
