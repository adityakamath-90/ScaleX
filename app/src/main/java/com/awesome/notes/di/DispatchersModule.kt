import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

//@Module
//@InstallIn(SingletonComponent::class)
internal object DispatchersModule {

    //@Provides
    //@Dispatcher(IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    //@Provides
    //@Dispatcher(Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}



