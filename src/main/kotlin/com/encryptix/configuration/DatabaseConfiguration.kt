import com.encryptix.database.Messages
import com.encryptix.database.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Configure database
 *
 */
fun Application.configureDatabase() {
    val url = System.getenv("POSTGRES_URL")
    val user = System.getenv("POSTGRES_USER")
    val password = System.getenv("POSTGRES_PASSWORD")

    try {
        Database.connect(url, driver = "org.postgresql.Driver", user = user, password = password)

    } catch (e: Exception) {
        println(e)
    }

    transaction {
        SchemaUtils.create(Users, Messages)
        //dont really need this but fuck it for now
        addLogger(StdOutSqlLogger)
    }
}