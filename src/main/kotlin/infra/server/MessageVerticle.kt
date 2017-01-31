package infra.server

import domain.UuidMessage
import domain.service.LuckyNumberService
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import java.util.*

/**
 * Main Verticle
 */
class MessageVerticle : AbstractVerticle() {

    override fun start(startFuture: Future<Void>?) {
        val router = createRouter()

        vertx.createHttpServer()
                .requestHandler { router.accept(it) }
                .listen(config().getInteger("http.port", 8005)) { result ->
                    if (result.succeeded()) {
                        startFuture?.complete()
                    } else {
                        startFuture?.fail(result.cause())
                    }
                }

    }

    private fun createRouter() = Router.router(vertx).apply {
        get("/message").handler(handlerNewMessage)
        get("/lucky-numbers").handler(handlerLuckyNumber)
    }

    val handlerNewMessage = Handler<RoutingContext> { req ->
        val message = UuidMessage(UUID.randomUUID().toString())
        vertx.eventBus().publish("new-message",Json.encodePrettily(message))
        req.response().endWithJson(message)
    }

    val handlerLuckyNumber = Handler<RoutingContext> { req ->
        val luckyNumbers = LuckyNumberService.luckyNumber()
        val jsonObject = JsonObject().put("numbers", luckyNumbers)
        vertx.eventBus().publish("lucky-numbers",jsonObject)
        req.response().endWithJson(jsonObject)
    }

    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
    }

}