package revolut.home.task

import com.google.gson.Gson
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import revolut.home.task.account.AccountDTO
import revolut.home.task.account.CreateAccountRequest
import revolut.home.task.common.Currency
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

val testCreateAccountRequest = CreateAccountRequest(BigDecimal(100), Currency.EUR)

private const val endpointUrl = "/account"

object AccountSpec : Spek(
        {
            describe("Account test suite") {
                val gson = Gson()
                var embeddedServer: EmbeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
                var client: HttpClient = HttpClient.create(embeddedServer.url)

                beforeEachTest {
                    embeddedServer.close()
                    embeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
                    client = HttpClient.create(embeddedServer.url)
                }

                it("List accounts for empty DB") {
                    val response = client.toBlocking().retrieve(endpointUrl)
                    assertEquals("[]", response)
                }

                it("Create account") {
                    val request = HttpRequest.POST(endpointUrl, testCreateAccountRequest)
                    val body = client.toBlocking().exchange(request, String::class.java).body()
                    val createdAccount = gson.fromJson<AccountDTO>(body, AccountDTO::class.java)
                    assertEquals(testCreateAccountRequest.balance, createdAccount.balance)
                    assertEquals(testCreateAccountRequest.currency, createdAccount.currency)
                }

                it("Create invalid account") {
                    val request = HttpRequest.POST(endpointUrl, CreateAccountRequest(BigDecimal(-1000), Currency.GBP))
                    // this is slightly messy, but I do want to check both status and the fact of failure (via checking exception thrown)
                    try {
                        client.toBlocking().exchange(request, Map::class.java)
                        fail("Exception expected; got non-error response")
                    } catch (e: HttpClientResponseException) {
                        assertEquals(HttpStatus.BAD_REQUEST, e.status)
                    }
                }

                it("Get account") {
                    val id = createDefaultAccount(client, gson)
                    val request = HttpRequest.GET<Map<String, String>>("$endpointUrl/$id")
                    val body = client.toBlocking().retrieve(request, String::class.java)
                    val receivedAccount = gson.fromJson<AccountDTO>(body, AccountDTO::class.java)
                    assertEquals(id, receivedAccount.id)
                    assertEquals(testCreateAccountRequest.balance, receivedAccount.balance)
                    assertEquals(testCreateAccountRequest.currency, receivedAccount.currency)
                }

                it("Get invalid account (malformed)") {
                    try {
                        client.toBlocking().retrieve("$endpointUrl/incorrectString")
                        fail("Exception expected; got non-error response")
                    } catch (e: HttpClientResponseException) {
                        assertEquals(HttpStatus.BAD_REQUEST, e.status)
                    }
                }

                it("Get invalid account (negative ID)") {
                    try {
                        client.toBlocking().retrieve("$endpointUrl/-10")
                        fail("Exception expected; got non-error response")
                    } catch (e: HttpClientResponseException) {
                        assertEquals(HttpStatus.BAD_REQUEST, e.status)
                    }
                }

                afterEachTest {
                    client.close()
                    embeddedServer.close()
                }
            }
        }
)

private fun createDefaultAccount(client: HttpClient, gson: Gson): Long {
    val request = HttpRequest.POST(endpointUrl, testCreateAccountRequest)
    val body = client.toBlocking().exchange(request, String::class.java).body()
    assertNotNull(body)
    val receivedAccount = gson.fromJson<AccountDTO>(body, AccountDTO::class.java)
    return receivedAccount.id
}