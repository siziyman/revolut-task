package revolut.home.task

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import revolut.home.task.account.CreateAccountRequest
import revolut.home.task.common.Currency
import revolut.home.task.transaction.SubmitTransactionRequest
import revolut.home.task.transaction.TransactionDTO
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail


private const val endpointUrl = "/transaction"

object TransactionSpec : Spek(
        {
            val gson = Gson()
            var embeddedServer: EmbeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
            var client: HttpClient = HttpClient.create(embeddedServer.url)
            val idSet = setOf<Int>().toMutableSet()
            describe("Transaction test suite") {
                beforeEachTest {
                    embeddedServer.close()
                    embeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
                    client = HttpClient.create(embeddedServer.url)
                    //create a couple of "filled" accounts, remove previous ones from set
                    idSet.clear()
                    val id1 = createAccount(client, BigDecimal.TEN)
                    idSet.add(id1)
                    val id2 = createAccount(client, BigDecimal.TEN)
                    idSet.add(id2)
                }

                it("Submit transaction") {
                    val accIterator = idSet.iterator()
                    val requestBody = SubmitTransactionRequest(
                            accIterator.next().toLong(), accIterator.next().toLong(), BigDecimal.ONE)
                    val request = HttpRequest.POST<SubmitTransactionRequest>(endpointUrl, requestBody)
                    val httpResponse = client.toBlocking().exchange(request, String::class.java).body()
                    val responseDTO = gson.fromJson<TransactionDTO>(httpResponse, TransactionDTO::class.java)
                    assertNotNull(responseDTO)
                    assertNotNull(responseDTO.timestamp)
                    assertTrue(responseDTO.id > 0)
                    assertEquals(requestBody.sender, responseDTO.sender)
                    assertEquals(requestBody.recipient, responseDTO.recipient)
                    assertEquals(requestBody.amount, responseDTO.amount)
                }

                it("Submit invalid transaction (not enough money)") {
                    val accIterator = idSet.iterator()
                    assertInvalidTransactionFails(client, accIterator.next().toLong(), accIterator.next().toLong(),
                                                  BigDecimal(10000))
                }

                it("Submit invalid transaction (nonexistent sender)") {
                    val accIterator = idSet.iterator()
                    assertInvalidTransactionFails(client, Long.MAX_VALUE, accIterator.next().toLong(), BigDecimal(10))
                }

                it("Submit invalid transaction (nonexistent receiver)") {
                    val accIterator = idSet.iterator()
                    assertInvalidTransactionFails(client, accIterator.next().toLong(), Long.MAX_VALUE, BigDecimal(10))
                }

                it("Get transaction") {
                    val accIterator = idSet.iterator()
                    val transactionToSubmit = SubmitTransactionRequest(
                            accIterator.next().toLong(), accIterator.next().toLong(), BigDecimal.ONE)
                    val submitResponseBody = submitTransaction(client, gson, transactionToSubmit)
                    assertNotNull(submitResponseBody)
                    val response =
                            client.toBlocking().retrieve("$endpointUrl/${submitResponseBody.id}", String::class.java)
                    // considering there is a test for transaction submission, that test working should be an indication that this is "safe" call
                    assertEquals(gson.fromJson<TransactionDTO>(response, TransactionDTO::class.java),
                                 submitResponseBody)
                }

                it("Get all transactions") {
                    val accIterator = idSet.iterator()
                    val from = accIterator.next()
                    val to = accIterator.next()
                    val transactions = setOf<TransactionDTO>().toMutableSet()
                    for (i in 1..5) {
                        val submittedTransaction = submitTransaction(client, gson, SubmitTransactionRequest(
                                from.toLong(), to.toLong(), BigDecimal.valueOf(i % 2L + 1)))
                        transactions.add(submittedTransaction)
                    }
                    val transactionsString = client.toBlocking().retrieve("/transaction")
                    val token = object : TypeToken<List<TransactionDTO>>() {}.type
                    val receivedTransactions = gson.fromJson<List<TransactionDTO>>(transactionsString, token)
                    assertTrue(transactions.containsAll(receivedTransactions))
                    assertEquals(transactions.size, receivedTransactions.size)
                }
                afterEachTest {
                    client.close()
                    embeddedServer.close()
                }
            }
        }
)

private fun submitTransaction(client: HttpClient,
                              gson: Gson,
                              requestBody: SubmitTransactionRequest): TransactionDTO {
    val request = HttpRequest.POST<SubmitTransactionRequest>(endpointUrl, requestBody)
    val submitResponse = client.toBlocking().exchange(request, String::class.java)
    return gson.fromJson<TransactionDTO>(submitResponse.body(), TransactionDTO::class.java)
}

private fun assertInvalidTransactionFails(client: HttpClient,
                                          sender: Long, receiver: Long,
                                          amount: BigDecimal) {
    val requestBody = SubmitTransactionRequest(
            sender, receiver, amount)
    val request = HttpRequest.POST<SubmitTransactionRequest>(endpointUrl, requestBody)
    try {
        client.toBlocking().exchange(request, Map::class.java)
        fail("Exception expected")
    } catch (e: HttpClientResponseException) {
        println(e.message)
        assertEquals(HttpStatus.BAD_REQUEST, e.status)
    }
}

private fun createAccount(client: HttpClient, balance: BigDecimal): Int {
    val requestBody = CreateAccountRequest(balance, Currency.EUR)
    val request = HttpRequest.POST<CreateAccountRequest>("/account", requestBody)
    val response = client.toBlocking().exchange(request, Map::class.java).body()
    assertNotNull(response)
    assertTrue(response["id"] is Int)
    return response["id"] as Int
}