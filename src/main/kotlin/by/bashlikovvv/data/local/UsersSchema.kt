package by.bashlikovvv.data.local

import by.bashlikovvv.di.DigestFunction
import by.bashlikovvv.util.UUIDSerializer
import by.bashlikovvv.util.dbQuery
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class ExposedUser(
    @Serializable(UUIDSerializer::class)
    @SerialName("id") val id: UUID = UUID.randomUUID(),
    @SerialName("password") val password: String,
    @SerialName("phone") val phone: String,
    @SerialName("fcm_token") val fcmToken: String? = null
)

class UserService(
    database: Database,
    private val digestFunction: DigestFunction
) {
    object Users : Table() {
        val id = uuid("id").autoGenerate()
        val password = this.binary("password", length = 256)
        val phone = varchar("phone", length = 13).uniqueIndex()
        val fcmToken = varchar("fcm_token", length = 256)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun create(user: ExposedUser): UUID = dbQuery {
        Users.insert {
            it[id] = UUID.randomUUID()
            it[password] = digestFunction(user.password)
            it[phone] = user.phone
            it[fcmToken] = user.fcmToken ?: ""
        }[Users.id]
    }

    suspend fun verify(phone: String, password: String): UUID? {
        return dbQuery {
            val user = Users.select { Users.phone eq phone }
                .map {
                    ExposedUser(
                        id = it[Users.id],
                        password = (it[Users.password].contentEquals(digestFunction(password))).toString(),
                        phone = it[Users.phone],
                        fcmToken = it[Users.fcmToken]
                    )
                }
                .singleOrNull()

            if (user?.password == "true") {
                user.id
            } else {
                null
            }
        }
    }

    suspend fun setFCMToken(id: UUID, token: String) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[fcmToken] = token
            }
        }
    }

    suspend fun getFCMToken(id: UUID): String? =
        dbQuery {
            Users.select { Users.id eq id }
                .singleOrNull()
                ?.get(Users.fcmToken)
        }

    suspend fun read(): List<ExposedUser> =
        dbQuery {
            Users.selectAll()
                .map {
                    ExposedUser(
                        id = it[Users.id],
                        password = it[Users.password].toString(),
                        phone = it[Users.phone],
                        fcmToken = it[Users.fcmToken]
                    )
                }
        }
}

