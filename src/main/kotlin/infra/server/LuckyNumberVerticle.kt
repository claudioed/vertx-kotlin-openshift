package infra.server

import domain.service.NumberCounter
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

class LuckyNumberVerticle : AbstractVerticle() {

    override fun start(startFuture: Future<Void>?) {
        vertx.eventBus().consumer<Any>("lucky-numbers") { message -> handleMessage(message) }

    }

    private fun handleMessage(message: Message<Any>){
        val numbers = JsonObject(message.body().toString())
        numbers.getJsonArray("numbers").stream().forEach {
            run {
                NumberCounter.count(it as Int)
                vertx.eventBus().send("lucky", it.toString())
            }
        }
    }

}