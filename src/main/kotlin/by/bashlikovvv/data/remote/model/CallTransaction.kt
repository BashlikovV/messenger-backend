package by.bashlikovvv.data.remote.model

import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Транзакция вызова - уникальный идентификатор для обмена данными между пользователями
 * перед началом звонка.
 * Время жизни транзакции - 1 минута
 * */
data class CallTransaction(
    val callerId: UUID,
    val calledId: UUID,
    val transactionIdentifier: UUID = UUID.randomUUID(),
    private val onRemoveAction: (CallTransaction) -> Unit
) {
    private var isScheduled: AtomicBoolean = AtomicBoolean(false)

    private var timer = Timer()

    //Living time of transaction - 1 minute
    fun scheduleLivingTime() {
        if (isScheduled.get()) {
            timer.cancel()
            timer = Timer()
            isScheduled.set(false)
        }
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    onRemoveAction(this@CallTransaction)
                }
            },
            LIVING_TIME_IN_MILLS
        )
        isScheduled.set(true)
    }

    companion object {
        const val LIVING_TIME_IN_MILLS: Long = 60 * 1000
    }
}