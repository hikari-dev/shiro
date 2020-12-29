package dev.hikari.quartz

import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory

object Scheduler {

    private val scheduler = StdSchedulerFactory().scheduler

    /**
     * Visit the url below to learn about cron expressions
     * @see <a href="https://github.com/quartz-scheduler/quartz/blob/master/docs/tutorials/crontrigger.md#CronTriggersTutorial-Introduction">CronTrigger Tutorial</a>
     */
    fun startSchedule() {
        val job = JobBuilder.newJob(MorningTask::class.java)
            .withIdentity("MorningTask")
            .build()
        val trigger = TriggerBuilder.newTrigger()
            .withIdentity("MorningTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("00 00 8 * * ?"))
            .build()
        scheduler.scheduleJob(job, trigger)
        scheduler.start()
    }
}