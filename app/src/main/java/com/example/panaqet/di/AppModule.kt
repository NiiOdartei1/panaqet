package com.example.panaqet.di

import android.content.Context
import androidx.room.Room
import com.example.panaqet.data.local.CartDao
import com.example.panaqet.data.local.PanaQetDatabase
import com.example.panaqet.data.remote.AffiliateApi
import com.example.panaqet.data.remote.AuthApi
import com.example.panaqet.data.remote.AuthInterceptor
import com.example.panaqet.data.remote.PaymentApi
import com.example.panaqet.data.remote.ProductApi
import com.example.panaqet.data.remote.SellerApi
import com.example.panaqet.data.remote.BuyerApi
import com.example.panaqet.data.remote.MessageApi
import com.example.panaqet.data.repository.AffiliateRepositoryImpl
import com.example.panaqet.data.repository.AuthRepositoryImpl
import com.example.panaqet.data.repository.CartRepositoryImpl
import com.example.panaqet.data.repository.ProductRepositoryImpl
import com.example.panaqet.data.repository.SellerRepositoryImpl
import com.example.panaqet.data.repository.BuyerRepositoryImpl
import com.example.panaqet.data.repository.MessageRepositoryImpl
import com.example.panaqet.domain.repository.AffiliateRepository
import com.example.panaqet.domain.repository.AuthRepository
import com.example.panaqet.domain.repository.CartRepository
import com.example.panaqet.domain.repository.ProductRepository
import com.example.panaqet.domain.repository.SellerRepository
import com.example.panaqet.domain.repository.BuyerRepository
import com.example.panaqet.domain.repository.MessageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://panaqet-production.up.railway.app/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi {
        return retrofit.create(ProductApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAffiliateApi(retrofit: Retrofit): AffiliateApi {
        return retrofit.create(AffiliateApi::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentApi(retrofit: Retrofit): PaymentApi {
        return retrofit.create(PaymentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSellerApi(retrofit: Retrofit): SellerApi {
        return retrofit.create(SellerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBuyerApi(retrofit: Retrofit): BuyerApi {
        return retrofit.create(BuyerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMessageApi(retrofit: Retrofit): MessageApi {
        return retrofit.create(MessageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PanaQetDatabase {
        return Room.databaseBuilder(
            context,
            PanaQetDatabase::class.java,
            "panaqet_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCartDao(db: PanaQetDatabase): CartDao {
        return db.cartDao()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi, authInterceptor: AuthInterceptor): AuthRepository {
        return AuthRepositoryImpl(api, authInterceptor)
    }

    @Provides
    @Singleton
    fun provideProductRepository(api: ProductApi): ProductRepository {
        return ProductRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideCartRepository(dao: CartDao): CartRepository {
        return CartRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideAffiliateRepository(api: AffiliateApi): AffiliateRepository {
        return AffiliateRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideSellerRepository(api: SellerApi): SellerRepository {
        return SellerRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideBuyerRepository(api: BuyerApi): BuyerRepository {
        return BuyerRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideMessageRepository(api: MessageApi): MessageRepository {
        return MessageRepositoryImpl(api)
    }
}
