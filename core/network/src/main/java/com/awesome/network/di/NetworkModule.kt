
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import java.util.concurrent.PriorityBlockingQueue
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    // Provide a fixed thread pool CoroutineDispatcher based on available processors
    @Provides
    @Singleton
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()
    }

    // Provide the PriorityTaskExecutor as a Singleton
    @Provides
    @Singleton
    fun providePriorityTaskExecutor(dispatcher: CoroutineDispatcher): PriorityTaskExecutor {
        return PriorityTaskExecutor(
            taskQueue = PriorityBlockingQueue(1) { task1: NetworkTask<*>, task2: NetworkTask<*> ->
                task2.priority.ordinal - task1.priority.ordinal
            },
            taskScope = CoroutineScope(SupervisorJob() + dispatcher)
        )
    }
}
