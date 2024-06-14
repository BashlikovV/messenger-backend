package by.bashlikovvv.di

import by.bashlikovvv.data.AuthorizationServiceImpl
import by.bashlikovvv.data.local.UserService
import by.bashlikovvv.data.remote.WebSocketsService
import by.bashlikovvv.domain.service.AuthenticationService
import by.bashlikovvv.domain.service.FCMService
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val dataModule = module {
    single<Database> {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = ""
        )
    }

    single<UserService> {
        UserService(get(), get())
    }

    single<WebSocketsService> {
        WebSocketsService()
    }

    single<AuthenticationService> {
        AuthorizationServiceImpl()
    }

    single<FCMService> {
        by.bashlikovvv.data.remote.FCMService(get(), get())
    }
}