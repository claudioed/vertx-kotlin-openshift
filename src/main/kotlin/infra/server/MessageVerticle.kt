package infra.server

import domain.UuidMessage
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
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
    }

    val handlerNewMessage = Handler<RoutingContext> { req ->
        val message = UuidMessage(UUID.randomUUID().toString())
        vertx.eventBus().publish("new-message",Json.encodePrettily(message))
        req.response().endWithJson(message)
    }

    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
    }

}