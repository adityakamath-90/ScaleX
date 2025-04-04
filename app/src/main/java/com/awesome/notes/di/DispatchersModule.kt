import com.awesome.notes.Dispatcher
import com.awesome.notes.DispatcherType.IO
import com.awesome.notes.DispatcherType.Default
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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



